import React from "react";
function Attachment({ attachment, onRemove = null }) {
    if (!attachment || !attachment.attachmentURL) return null;
    
    const type = String(attachment.typeOfAttachment || '').toLowerCase();

    return (
        <div className="attachment-item">
            {type === "image" && (
                <img 
                    src={attachment.attachmentURL} 
                    alt="attachment" 
                    className="attachment-image" 
                    loading="lazy" 
                    style={{ maxWidth: '100%', height: 'auto', borderRadius: '8px' }}
                />
            )}

            {type === "video" && (
                <video 
                    controls 
                    className="attachment-video"
                    style={{ maxWidth: '100%', height: 'auto', borderRadius: '8px' }}
                >
                    <source src={attachment.attachmentURL} type="video/mp4" />
                    Your browser does not support the video tag.
                </video>
            )}

            {typeof onRemove === 'function' && (
                <button className="remove-attachment-button" onClick={() => onRemove(attachment.attachId)}>
                    Remove
                </button>
            )}
        </div>
    );
}

export default Attachment;