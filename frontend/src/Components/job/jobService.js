
import { API_BASE_URL, CLOUDINARY_UPLOAD_URL } from '../../config';
const uploadToCloudinary = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', 'ml_default'); // Changed to signed preset
    
    // Determine resource type and endpoint
    const isPdf = file.type === 'application/pdf';
    const resourceType = isPdf ? 'raw' : 'image';
    
    const endpoint = `https://api.cloudinary.com/v1_1/dqhdiihx4/${resourceType}/upload`;
    
    const res = await fetch(endpoint, {
      method: 'POST',
      body: formData,
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error('Upload failed:', errorText);
      throw new Error(`Upload failed: ${res.status}`);
    }

    const data = await res.json();
    console.log('Upload response:', data);
    console.log('Secure URL:', data.secure_url);
    return data.secure_url;
  } catch (err) {
    console.error('Upload error:', err);
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
    const payload = {
      // id: null, // Optional - let backend generate
      name: applicationData.name || null,
      email: applicationData.email || null,
      userId: localStorage.getItem('userId'),
      jobId: jobId,
      status: "PENDING",
      cvURL: cvUrl,
      coverLetter: applicationData.coverLetter,
      // createdAt: null // Optional - let backend set
    };


    const response = await fetch(`${API_BASE_URL}/apply/post`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Server error:', errorText);
      throw new Error(`Failed to submit application: ${response.status}`);
    }

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Error submitting application:', error);
    throw error;
  }
};