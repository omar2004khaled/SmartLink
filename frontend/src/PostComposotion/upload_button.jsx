import { Upload } from 'lucide-react';
export default function Upload_button({fileInputRef,handleFileInput}){
    return <>
    <button
                onClick={() => fileInputRef.current?.click()}
                style={{
                  padding: "8px 12px",
                  borderRadius: "6px",
                  background: "#f9fafb",
                  border: "1px solid #d1d5db",
                  fontSize: "14px",
                  display: "flex",
                  alignItems: "center",
                  gap: "6px",
                  cursor: "pointer",
                }}
              >
                <Upload size={16} />
                Media
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