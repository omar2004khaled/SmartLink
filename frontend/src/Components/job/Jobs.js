const GRAPHQL_ENDPOINT = 'http://localhost:8080/graphql';

const GET_JOBS_QUERY = `
  query GetJobs($filter: JobFilter) {
    allJobs(filter: $filter) {
      jobId
      title
      description
      experienceLevel
      jobType
      locationType
      jobLocation
      salaryMin
      salaryMax
    }
  }
`;

export const fetchJobs = async (filters = {}) => {
  try {
    const filter = {};
    
    if (filters.title) filter.title = filters.title;
    if (filters.location) filter.jobLocation = filters.location;
    if (filters.type) filter.locationType = filters.type.toUpperCase();
    if (filters.level) filter.experienceLevel = filters.level.toUpperCase();
    if (filters.jobType) filter.jobType = filters.jobType.replace('-', '_').toUpperCase();
    if (filters.minSalary) filter.minSalary = parseInt(filters.minSalary);
    if (filters.maxSalary) filter.maxSalary = parseInt(filters.maxSalary);

    console.log('Sending filter:', filter);

    const response = await fetch(GRAPHQL_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ 
        query: GET_JOBS_QUERY,
        variables: { filter }
      }),
    });
    
    const result = await response.json();
    console.log('GraphQL Response:', result);
    
    if (result.errors) {
      console.error('GraphQL Errors:', result.errors);
      return [];
    }
    
    return result.data?.allJobs || [];
    
  } catch (error) {
    console.error('Error fetching jobs:', error);
    return [];
  }
};
