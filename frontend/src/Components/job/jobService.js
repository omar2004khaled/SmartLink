
import { API_BASE_URL, CLOUDINARY_UPLOAD_URL } from '../../config';

const uploadToCloudinary = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', 'dyk7gqqw');
    
    const res = await fetch(CLOUDINARY_UPLOAD_URL, {
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
    const response = await fetch(`${API_BASE_URL}/apply/post`, {
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
