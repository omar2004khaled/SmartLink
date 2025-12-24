export const fetchApplications = async (jobId) => {
  try {
    const response = await fetch(`http://localhost:8080/apply/${jobId}`);
    if (!response.ok) throw new Error('Failed to fetch applications');
    return await response.json();
  } catch (error) {
    console.error('Error fetching applications:', error);
    return [];
  }
};

