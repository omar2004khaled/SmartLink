import React from "react";
function Attachment({ attachment={ id: 1, type: "Image", url: "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcScd4KbQbrRlYFXgzyOeNl_n_4T0zeGHyGBpglz_l2pnjHkhqWce3wTqwvPWWwi9aDR12Gye8Pco68ehhs"}, onRemove }) {
    return (
        <div className="attachment-item">
        {attachment.type === "Image" && (
            <img src={attachment.url} alt="attachment" className="attachment-image" />
        )}
        {attachment.type === "Video" && (
            <video controls className="attachment-video">
            <source src={attachment.url} type="video/mp4" />
            Your browser does not support the video tag.
            </video>
        )}
        {/* <button className="remove-attachment-button" onClick={() => onRemove(attachment.id)}>
            Remove
        </button> */}
        </div>
    );
} 
export default Attachment;