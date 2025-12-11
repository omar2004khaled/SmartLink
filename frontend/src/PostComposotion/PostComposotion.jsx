import React, { useState, useRef } from 'react';
import { X } from 'lucide-react';
import Header from './post_composotion_header';
import FeelingButton from './feeling_button';
import Upload_button from './upload_button';
import MediaReviewer from './media_review';
import TextEditor from './text_editor';
import SubmitButton from './submit_button';
import { SavePost , userIdFromLocalStorage} from '../FetchData/FetchData';
import { Link, useNavigate } from 'react-router-dom';

export default function PostComposer() {
  const [postText, setPostText] = useState('');
  const [mediaFiles, setMediaFiles] = useState([]);
  const [feeling, setFeeling] = useState("None");
  const navigate = useNavigate();

  const fileInputRef = useRef(null);

  const handleFileInput = (e) => {
    const files = Array.from(e.target.files);
    handleFiles(files);
  };

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

    if (mediaFiles.length > 0) {
      mediaFiles.forEach(async (m) => {
        if (m.uploadedUrl === null) {
          const cloudUrl = await uploadFileToCloudinary(m.file);
          setMediaFiles((prev) =>
            prev.map((media) => (media.id === m.id ? { ...media, uploadedUrl: cloudUrl } : media))
          );
        }
      });
    }

    const uploadedUrls = mediaFiles.map((m) => m.uploadedUrl);

    SavePost({
      userId: userIdFromLocalStorage(),
      content: postText,
      attachments: [
        ...mediaFiles.map((m) => ({
          typeOfAttachment: m.type === "image" ? "Image" : "Video",
          attachmentURL: m.uploadedUrl,
        }))
      ],
    });
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
    navigate('/home');
  };

  return (
    <div style={{ minHeight: "100vh", background: "#f8fafc", padding: "20px" }}>
      <div style={{ maxWidth: "600px", margin: "0 auto", background: "white", borderRadius: "8px", padding: "24px", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
        <Header />
        
        <div style={{ marginTop: "20px", display: "flex", flexDirection: "column", gap: "16px" }}>
          <TextEditor setPostText={setPostText} postText={postText} />
          
          <div style={{ display: "flex", gap: "12px" }}>
            <FeelingButton setFeeling={setFeeling} feeling={feeling} />
            <Upload_button handleFileInput={handleFileInput} fileInputRef={fileInputRef} />
          </div>

          {mediaFiles.length > 0 && (
            <MediaReviewer mediaFiles={mediaFiles} setMediaFiles={setMediaFiles} />
          )}

          <SubmitButton mediaFiles={mediaFiles} postText={postText} handleSubmit={handleSubmit} />
        </div>
      </div>
    </div>
  );
}
