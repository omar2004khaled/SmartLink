package com.example.auth.controller;

import com.example.auth.dto.JobDTO.JobResponse;
import com.example.auth.service.GeminiService.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/gemini")
public class GeminiController {
    private final GeminiService geminiService;
    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }
    @GetMapping("/getRelevantJobs/{profileId}")
    public CompletableFuture<ResponseEntity<?>> getJobs(@PathVariable Long profileId) {
        return CompletableFuture.supplyAsync( () -> {
            try{
                List<JobResponse> jobs = geminiService.callGoogleApi(profileId);
                return ResponseEntity.ok(jobs);
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "error", "Failed to process request",
                                "message", e.getMessage()
                        ));
            }
        }).completeOnTimeout(
                    ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                            .body(Map.of(
                                    "error", "Request timeout",
                                    "message", "The request took too long to process"
                            )),
                    120, TimeUnit.SECONDS
            );
    }
}
