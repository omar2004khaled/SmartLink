
const uploadToCloudinary = async (file) => {
  try {
    const url = `https://api.cloudinary.com/v1_1/dqhdiihx4/auto/upload`;
    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', 'dyk7gqqw');
    
    const res = await fetch(url, {
      method: 'POST',
      body: formData,
    });

    const data = await res.json();
    console.log('Cloudinary upload response:', data.secure_url);
    return data.secure_url;
  } catch (err) {
    console.error('Upload failed', err);
    return null;
  }
};

export const submitApplication = async (jobId, applicationData) => {
  try {
    let cvUrl = null;
    
    if (applicationData.cv) {
      cvUrl = await uploadToCloudinary(applicationData.cv);
      if (!cvUrl) {
        throw new Error('Failed to upload CV');
      }
    }
    const token = localStorage.getItem('authToken');
    const response = await fetch('http://localhost:8080/apply/post', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        userId:localStorage.getItem('userId'),
        jobId: jobId,
        status:"PENDING",
        cvUrl: cvUrl,
        coverLetter: applicationData.coverLetter
      }),
    });

    if (response.redirected) {
      window.location.href = response.url;
      return;
    }

    if (!response.ok) {
      throw new Error('Failed to submit application');
    }

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Error submitting application:', error);
    throw error;
  }
};
