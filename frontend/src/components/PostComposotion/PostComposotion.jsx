import React, { useState, useRef } from 'react';
import { X } from 'lucide-react';

// Mock components - replace with your actual imports
const Header = () => <div style={{ padding: '1rem', background: '#fff', borderBottom: '1px solid #e5e7eb' }}>Header</div>;
const FeelingButton = ({ setFeeling, feeling }) => (
  <button onClick={() => setFeeling(feeling === 'Happy' ? 'None' : 'Happy')} style={{ padding: '0.5rem 1rem', borderRadius: '0.5rem', border: '1px solid #d1d5db' }}>
    Feeling: {feeling}
  </button>
);
const Upload_button = ({ handleFileInput, fileInputRef }) => (
  <>
    <input type="file" ref={fileInputRef} onChange={handleFileInput} multiple accept="image/*,video/*" style={{ display: 'none' }} />
    <button onClick={() => fileInputRef.current?.click()} style={{ padding: '0.5rem 1rem', borderRadius: '0.5rem', border: '1px solid #d1d5db' }}>
      Upload Media
    </button>
  </>
);
const TextEditor = ({ setPostText, postText }) => (
  <textarea
    value={postText}
    onChange={(e) => setPostText(e.target.value)}
    placeholder="What's on your mind?"
    style={{ width: '100%', minHeight: '120px', padding: '1rem', borderRadius: '0.5rem', border: '1px solid #d1d5db' }}
  />
);
const MediaReviewer = ({ mediaFiles, setMediaFiles }) => (
  <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
    {mediaFiles.map((media) => (
      <div key={media.id} style={{ position: 'relative', width: '100px', height: '100px' }}>
        {media.type === 'image' ? (
          <img src={media.localUrl} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '0.5rem' }} />
        ) : (
          <video src={media.localUrl} style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '0.5rem' }} />
        )}
        <button
          onClick={() => setMediaFiles((prev) => prev.filter((m) => m.id !== media.id))}
          style={{ position: 'absolute', top: '0.25rem', right: '0.25rem', background: 'rgba(0,0,0,0.5)', color: '#fff', border: 'none', borderRadius: '50%', width: '24px', height: '24px', cursor: 'pointer' }}
        >
          <X size={16} />
        </button>
        {media.uploadedUrl === null && <div style={{ position: 'absolute', bottom: '0.25rem', left: '0.25rem', fontSize: '0.75rem', color: '#fff', background: 'rgba(0,0,0,0.5)', padding: '0.25rem', borderRadius: '0.25rem' }}>Uploading...</div>}
      </div>
    ))}
  </div>
);
const SubmitButton = ({ mediaFiles, postText, handleSubmit }) => (
  <button
    onClick={handleSubmit}
    disabled={!postText.trim() && mediaFiles.length === 0}
    style={{ padding: '0.75rem 2rem', background: '#3b82f6', color: '#fff', borderRadius: '0.5rem', border: 'none', cursor: 'pointer', fontSize: '1rem', fontWeight: '600', opacity: !postText.trim() && mediaFiles.length === 0 ? 0.5 : 1 }}
  >
    Post
  </button>
);

// 🔥 SavePost function - replace with your actual API endpoint
const SavePost = async (postData) => {
  try {
    const response = await fetch('/api/posts', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(postData),
    });
    
    if (!response.ok) {
      throw new Error('Failed to save post');
    }
    
    const data = await response.json();
    console.log('Post saved successfully:', data);
    return data;
  } catch (error) {
    console.error('Error saving post:', error);
    throw error;
  }
};

export default function PostComposer() {
  const [postText, setPostText] = useState('');
  const [mediaFiles, setMediaFiles] = useState([]);
  const [feeling, setFeeling] = useState("None");
  const fileInputRef = useRef(null);

  const handleFileInput = (e) => {
    const files = Array.from(e.target.files);
    handleFiles(files);
  };

  // 🔥 Upload to Cloudinary
  const uploadFileToCloudinary = async (file) => {
    const url = `https://api.cloudinary.com/v1_1/dqhdiihx4/auto/upload`;
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "dyk7gqqw");

    const response = await fetch(url, {
      method: "POST",
      body: formData,
    });

    const data = await response.json();
    return data.secure_url;
  };

  const handleFiles = async (files) => {
    const validFiles = files.filter(
      (file) =>
        file.type.startsWith("image/") || file.type.startsWith("video/")
    );

    const previews = validFiles.map((file) => ({
      id: Math.random().toString(36).substr(2, 9),
      file,
      localUrl: URL.createObjectURL(file),
      uploadedUrl: null,
      type: file.type.startsWith("image/") ? "image" : "video",
    }));

    setMediaFiles((prev) => [...prev, ...previews]);

    for (let fileObj of previews) {
      const cloudUrl = await uploadFileToCloudinary(fileObj.file);
      setMediaFiles((prev) =>
        prev.map((m) => (m.id === fileObj.id ? { ...m, uploadedUrl: cloudUrl } : m))
      );
    }
  };

  const handleSubmit = async () => {
    if (!postText.trim() && mediaFiles.length === 0) return;

    // Wait for any pending uploads
    if (mediaFiles.some(m => m.uploadedUrl === null)) {
      alert('Please wait for all media to finish uploading');
      return;
    }

    try {
      const postData = {
        userId: 1,
        content: postText,
        feeling: feeling !== "None" ? feeling : null,
        attachments: mediaFiles.map((m) => ({
          typeOfAttachment: m.type === "image" ? "Image" : "Video",
          attachmentURL: m.uploadedUrl,
        })),
      };

      await SavePost(postData);

      alert('Post submitted successfully!');
      
      // Clear form
      setPostText("");
      setMediaFiles([]);
      setFeeling("None");
    } catch (error) {
      alert('Failed to submit post. Please try again.');
    }
  };

  return (
    <div
      style={{
        height: "100vh",
        width: "100vw",
        margin: 0,
        padding: 0,
        background: "linear-gradient(to bottom right, #eff6ff, #ffffff)",
        overflow: "hidden",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <Header />
      <div
        style={{
          flex: 1,
          overflowY: "auto",
          padding: "2rem",
          display: "flex",
          flexDirection: "column",
          gap: "1.8rem",
        }}
      >
        <div style={{ display: "flex", gap: "1rem", alignItems: "center", flexWrap: "wrap" }}>
          <FeelingButton setFeeling={setFeeling} feeling={feeling} />
          <Upload_button handleFileInput={handleFileInput} fileInputRef={fileInputRef} />
        </div>

        <TextEditor setPostText={setPostText} postText={postText} />

        {mediaFiles.length > 0 && (
          <MediaReviewer mediaFiles={mediaFiles} setMediaFiles={setMediaFiles} />
        )}

        <SubmitButton mediaFiles={mediaFiles} postText={postText} handleSubmit={handleSubmit} />
      </div>
    </div>
  );
}