import { Upload } from 'lucide-react';
export default function Upload_button({fileInputRef,handleFileInput}){
    return <>
    <button
                onClick={() => fileInputRef.current?.click()}
                style={{
                  padding: ".9rem 1.2rem",
                  borderRadius: "0.75rem",
                  background: "#eef2ff",
                  border: "2px solid #c7d2fe",
                  fontSize: "1rem",
                  display: "flex",
                  alignItems: "center",
                  gap: ".4rem",
                  cursor: "pointer",
                  whiteSpace: "nowrap",
                }}
              >
                <Upload size={20} color="#4f46e5" />
                Add Media
              </button>
    
              {/* Hidden Input */}
              <input
                ref={fileInputRef}
                type="file"
                multiple
                accept="image/*,video/*"
                onChange={handleFileInput}
                style={{ display: "none" }}
              />
    </>
}