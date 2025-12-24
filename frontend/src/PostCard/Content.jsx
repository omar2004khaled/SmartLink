import React from "react";

function Content({ content = "" }){
    // Preserve simple newlines by splitting into paragraphs
    const paragraphs = String(content).split(/\r?\n/).filter(Boolean);

    return (
        <div className="post-content">
            {paragraphs.length === 0 ? (
                <p className="post-content-text">No content</p>
            ) : (
                paragraphs.map((p, i) => (
                    <p key={i} className="post-content-text">{p}</p>
                ))
            )}
        </div>
    );
}

export default Content;
