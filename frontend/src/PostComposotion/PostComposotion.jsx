import React, { useState, useRef } from 'react';
import { X } from 'lucide-react';
import Header from './post_composotion_header';
import FeelingButton from './feeling_button';
import Upload_button from './upload_button';
import MediaReviewer from './media_review';
import TextEditor from './text_editor';
import SubmitButton from './submit_button';

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
    formData.append("upload_preset", "dyk7gqqw");   // your preset name


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

  const handleSubmit = () => {
    if (!postText.trim() && mediaFiles.length === 0) return;

    const uploadedUrls = mediaFiles.map((m) => m.uploadedUrl);

    alert(`
      Post Submitted! 
      Text: ${postText}
      Feeling: ${feeling ?? "None"}
      Uploaded URLs:
      ${uploadedUrls.join("\n")}
    `);

    setPostText("");
    setMediaFiles([]);
    
    setFeeling(null); 
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
