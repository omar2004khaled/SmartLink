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

export const fetchAllApplications = async () => {
  // try {
  //   const response = await fetch('http://localhost:8080/applications/all');
  //   if (!response.ok) throw new Error('Failed to fetch applications');
  //   return await response.json();
  // } catch (error) {
  //   console.error('Error fetching applications:', error);
  //   return [];
  // }
  return [
    {
      id: 1,
      name: 'John Doe',
      email: 'john.doe@example.com',
      cvUrl: 'https://res.cloudinary.com/dqhdiihx4/raw/upload/v1766347003/databases_sheet_2_tubfsp.pdf',
      coverLetter: 'I am very interested in this position and believe my skills align perfectly with your requirements.',
      status: 'PENDING',
      createdAt: '2024-01-15T10:30:00'
    },
    {
      id: 2,
      name: 'Jane Smith',
      email: 'jane.smith@example.com',
      cvUrl: 'https://res.cloudinary.com/demo/image/upload/sample.pdf',
      coverLetter: 'With 5 years of experience in the field, I am confident I can contribute significantly to your team.',
      status: 'REVIEWED',
      createdAt: '2024-01-14T14:20:00'
    },
    {
      id: 3,
      name: 'Mike Johnson',
      email: 'mike.j@example.com',
      cvUrl: 'https://res.cloudinary.com/demo/image/upload/sample.pdf',
      coverLetter: 'I have a proven track record of success and would love to bring my expertise to your organization.',
      status: 'ACCEPTED',
      createdAt: '2024-01-13T09:15:00'
    }
  ];
};
