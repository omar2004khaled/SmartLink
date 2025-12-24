import React from "react";
function Attachment({ attachment = { AttachId: 1, typeOfAttachment: "image", attachmentURL: "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcScd4KbQbrRlYFXgzyOeNl_n_4T0zeGHyGBpglz_l2pnjHkhqWce3wTqwvPWWwi9aDR12Gye8Pco68ehhs" }, onRemove = null }) {
    const type = String(attachment.typeOfAttachment || '').toLowerCase();

    return (
        <div className="attachment-item">
            {type === "image" && (
                <img src={attachment.attachmentURL} alt={attachment.alt || 'attachment'} className="attachment-image" loading="lazy" />
            )}

            {type === "video" && (
                <video controls className="attachment-video">
                    <source src={attachment.attachmentURL} type="video/mp4" />
                    Your browser does not support the video tag.
                </video>
            )}
        </div>
    );
}

export default Attachment;
