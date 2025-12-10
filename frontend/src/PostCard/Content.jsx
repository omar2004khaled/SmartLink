import React from "react";

function Content({ content = "" }){
    const paragraphs = String(content).split(/\r?\n/).filter(Boolean);

    return (
        <div className="post-content">
            {paragraphs.map((p, i) => (
                <p key={i} className="post-content-text">{p}</p>
            ))}
        </div>
    );
}

export default Content;