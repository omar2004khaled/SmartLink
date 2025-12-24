package com.example.auth.service.GeminiService;
import com.example.auth.dto.CompanyDTO;
import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.dto.JobDTO.SemiJobDto;
import com.example.auth.dto.ProfileDtos.SemiEducationDto;
import com.example.auth.dto.ProfileDtos.SkillDto;
import com.example.auth.entity.ProfileEntities.Education;
import com.example.auth.entity.ProfileEntities.Skill;
import com.example.auth.repository.JobRepository;
import com.example.auth.repository.ProfileRepositories.EducationRepository;
import com.example.auth.repository.ProfileRepositories.SkillRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.example.auth.entity.Job;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Getter
@Setter
public class GeminiService {
    SkillRepository skillRepository;
    EducationRepository educationRepository;
    JsonUtils jsonUtils;
    JobRepository jobRepository;
    List<SkillDto> personSkills;
    List<SemiEducationDto> personEducation;
    List<SemiJobDto> semiJobs;
    List<Job> jobs;
    List<CompanyDTO> companyNames ;

    @Value("${Gemini_key}")
    private String apiKey;
    @Autowired
    public GeminiService(SkillRepository skillRepository, EducationRepository educationRepository, JobRepository jobRepository) {
        this.skillRepository = skillRepository;
        this.educationRepository = educationRepository;
        this.jobRepository = jobRepository;
        this.personSkills =  new ArrayList<>();
        this.personEducation= new ArrayList<>();
        this.jobs= new ArrayList<>();
        this.jsonUtils = new JsonUtils();
        this.semiJobs= new ArrayList<>();
    }
    public void getPersonSkillsAndEducation(long id) {
        this.personSkills.clear();
        this.personEducation.clear();
        List<Skill> skills= skillRepository.findSkillNameOfSkill(id);
        for (Skill skill : skills) {
            SkillDto skillDto = new SkillDto();
            skillDto.setId(skill.getSkillId());
            skillDto.setSkillName(skill.getSkillName());
            skillDto.setProficiency(skill.getProficiency());
            personSkills.add(skillDto);
        }
        List<Education> educations= educationRepository.findDescriptionAndFieldOfEducation(id);
        for (Education education : educations){
            SemiEducationDto semiEducationDto = new SemiEducationDto();
            semiEducationDto.setId(education.getEducationId());
            semiEducationDto.setFieldOfStudy(education.getFieldOfStudy());
            semiEducationDto.setDescription(education.getDescription());
            personEducation.add(semiEducationDto);
        }
    }
    public void getJobs() {
        this.semiJobs.clear();
        this.jobs = jobRepository.findAllCurrentJobs(Instant.now());
        for (Job job : jobs){
            CompanyDTO companyDTO = new CompanyDTO();
            SemiJobDto jobDto = new SemiJobDto();
            jobDto.setJobId(job.getJobId());
            CompanyDTO.CompanyDTOBuilder builder = CompanyDTO.builder().userId(job.getJobId()).companyName(job.getCompany().getFullName());
            companyDTO = builder.build();
            jobDto.setTitle(job.getTitle());
            jobDto.setDescription(job.getDescription());
            jobDto.setExperienceLevel(job.getExperienceLevel());
            this.semiJobs.add(jobDto);
        }

    }
    private List<Long> parseJobIdsFromResponse(String responseText) {
        try {
            // Extract the JSON part from the response (removing markdown code blocks if present)
            String jsonResponse = responseText
                .replaceAll("```json?", "")  // Remove markdown code block markers
                .replaceAll("^\\s*", "")       // Remove leading whitespace
                .replaceAll("\\s*$", "");      // Remove trailing whitespace
            
            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<Map<String, Object>>> result = mapper.readValue(
                jsonResponse, 
                new TypeReference<Map<String, List<Map<String, Object>>>>() {}
            );
            return result.get("ranked_jobs").stream()
                .map(job -> Long.parseLong(job.get("job_id").toString()))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini API response", e);
        }
    }
    
    private List<JobResponse> reorderJobs(List<Long> orderedJobIds) {
        List<JobResponse> reorderedJobs = new ArrayList<>();
        if (orderedJobIds == null || orderedJobIds.isEmpty()) {
            for (Job job : jobs){
                reorderedJobs.add(jsonUtils.mapper(job));
            }
            return reorderedJobs;
        }
        Map<Long, Job> jobMap = this.jobs.stream()
            .collect(Collectors.toMap(Job::getJobId, job -> job));
        for (Long id : orderedJobIds) {
            Job job = jobMap.get(id);
            if (job != null) {
                reorderedJobs.add(jsonUtils.mapper(job ));
            }
        }
        for (Job job : this.jobs) {
            if (!orderedJobIds.contains(job.getJobId())) {
                reorderedJobs.add(jsonUtils.mapper(job));
            }
        }
        return reorderedJobs;
    }
    public List<JobResponse> callGoogleApi(Long profileId) {
        try {
            this.getPersonSkillsAndEducation(profileId);
            this.getJobs();
            String Prompt = "You are given a user profile and a list of jobs.\n" +
                    "\n" +
                    "INPUT:\n" +
                    "\n" +
                    "USER_PROFILE:\n" +
                    "{\n" +
                    "  \"skills\": " + JsonUtils.toJsonString(personSkills) + ",\n" +
                    "  \"education\": " + JsonUtils.toJsonString(personEducation) + "\n" +
                    "}\n\n" +
                    "JOBS:\n" +
                    JsonUtils.toJsonString(semiJobs) +"\n\n"+
                    "TASK:\n" +
                    "\n" +
                    "For EACH job, compute the following scores in range [0, 1]:\n" +
                    "\n" +
                    "1) embedding_similarity:\n" +
                    "   Semantic similarity between:\n" +
                    "   - (skills_text + education_text)\n" +
                    "   - job_description\n" +
                    "\n" +
                    "2) skill_match:\n" +
                    "   Extract skills from job_description.\n" +
                    "   Extract skills from skills_text.\n" +
                    "   skill_match =\n" +
                    "   (number of job skills found in user skills)\n" +
                    "   /\n" +
                    "   (total number of job skills)\n" +
                    "\n" +
                    "3) education_match:\n" +
                    "   Compare education_text with job requirements.\n" +
                    "   education_match =\n" +
                    "   1.0 if level is sufficient and field is relevant\n" +
                    "   0.5 if field is relevant but level is lower\n" +
                    "   0.0 otherwise\n" +
                    "\n" +
                    "FINAL SCORE (MUST BE USED EXACTLY):\n" +
                    "\n" +
                    "final_score =\n" +
                    "0.5 * embedding_similarity\n" +
                    "+ 0.3 * skill_match\n" +
                    "+ 0.2 * education_match\n" +
                    "\n" +
                    "OUTPUT:\n" +
                    "Return the jobs sorted by final_score descending.\n" +
                    "\n" +
                    "OUTPUT FORMAT (JSON ONLY):\n" +
                    "\n" +
                    "{\n" +
                    "  \"ranked_jobs\": [\n" +
                    "    {\n" +
                    "      \"job_id\": number,\n" +
                    "      \"title\": string,\n" +
                    "      \"embedding_similarity\": number,\n" +
                    "      \"skill_match\": number,\n" +
                    "      \"education_match\": number,\n" +
                    "      \"final_score\": number\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n" +
                    "NOTE that i want the JSON file directly so do not add ```json``` or `````` or any other markdown code block markers";
            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                Prompt,
                null
            );
            String responseText = response.text();
            List<Long> orderedJobIds = parseJobIdsFromResponse(responseText);
            return reorderJobs(orderedJobIds);
            
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
            return reorderJobs(new ArrayList<>());// Return original list in case of error
        }


    }
}
