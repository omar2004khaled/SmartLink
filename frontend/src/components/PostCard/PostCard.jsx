import React, { useState, useEffect } from 'react';
import UserHeader from './UserHeader';
import Content from './Content';
import Attachment from './Attachment';
import Footer from './Footer';
import CommentsPanel from './CommentsPanel';
import './PostCard.css';

// PostItem handles one post card and its composer/modal
function PostItem({ post }) {
  const [showComments, setShowComments] = useState(false);
  const [showAll, setShowAll] = useState(false);
  const [inlineVisible, setInlineVisible] = useState(false);
  const [loadingComments, setLoadingComments] = useState(false);

  const initialComments = post?.comments || commentsSeed;
  const [comments, setComments] = useState(initialComments);

  const [commentText, setCommentText] = useState('');
  const [attachedFile, setAttachedFile] = useState(null);

  useEffect(() => {
    setComments(post?.comments || commentsSeed);
  }, [post]);

  useEffect(() => {
    return () => {
      if (attachedFile && attachedFile.preview) URL.revokeObjectURL(attachedFile.preview);
    };
  }, [attachedFile]);

  // Upload helper for Cloudinary (unsigned preset)
  const uploadToCloudinary = async (file) => {
    try {
      const url = `https://api.cloudinary.com/v1_1/dqhdiihx4/auto/upload`;
      const formData = new FormData();
      formData.append('file', file);
      formData.append('upload_preset', 'dyk7gqqw');

      const res = await fetch(url, {
        method: 'POST',
        body: formData,
      });

      const data = await res.json();
      return data.secure_url;
    } catch (err) {
      console.error('Upload failed', err);
      return null;
    }
  };

  // Fetch comments from backend for this post (pageNo 0)
  const fetchCommentsFromServer = async () => {
    if (!post || !post.id) return;
    setLoadingComments(true);
    try {
      const res = await fetch(`/comment/getAll/${post.id}/0`);
      if (!res.ok) throw new Error('Failed to fetch comments');
      const data = await res.json();
      // data is List<CommentDTO> with fields: commentId, userId, text, url, type, postId
      const mapped = data.map((c) => ({
        commentId: c.commentId,
        author: c.userId ? `User${c.userId}` : 'Anonymous',
        text: c.text,
        avatar: '/src/PostCard/avatar.png',
        time: 'some time',
        attachment: c.url || undefined,
      }));
      setComments(mapped);
    } catch (err) {
      console.error('Error loading comments', err);
    } finally {
      setLoadingComments(false);
    }
  };

  const handleFileChange = async (e) => {
    const f = e.target.files && e.target.files[0];
    if (!f) return;

    // only allow one attachment at a time
    if (attachedFile) {
      alert('Only one attachment is allowed. Remove the current attachment first.');
      e.target.value = null;
      return;
    }

    const preview = URL.createObjectURL(f);
    setAttachedFile({ file: f, preview, uploading: true, uploadedUrl: null });
    e.target.value = null;

    // upload immediately
    const uploadedUrl = await uploadToCloudinary(f);
    setAttachedFile((prev) => ({
      ...prev,
      uploading: false,
      uploadedUrl: uploadedUrl || null,
    }));

    if (uploadedUrl) {
      console.log('Uploaded attachment URL:', uploadedUrl);
    } else {
      try { alert('Attachment upload failed'); } catch(e) {}
    }
  };

  const handleRemoveAttachment = () => {
    if (attachedFile && attachedFile.preview) URL.revokeObjectURL(attachedFile.preview);
    setAttachedFile(null);
  };

  // Save comment to backend
  const saveCommentToServer = async (commentDTO) => {
    const res = await fetch('/comment/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(commentDTO),
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || 'Failed to save comment');
    }
    // server returns Long id
    const id = await res.json();
    return id;
  };

  const handleSendComment = async () => {
    const text = commentText.trim();
    if (!text && !attachedFile) return;

    if (attachedFile && attachedFile.uploading) {
      await new Promise((resolve) => {
        const check = () => {
          if (!attachedFile.uploading) return resolve();
          setTimeout(check, 200);
        };
        check();
      });
    }

    const attachmentUrl = attachedFile ? (attachedFile.uploadedUrl || attachedFile.preview) : undefined;

    const commentDTO = {
      commentId: null,
      userId: 1,
      text: text || null,
      url: attachmentUrl || null,
      type: attachmentUrl ? 'image' : null,
      postId: post.id,
    };

    try {
      const id = await saveCommentToServer(commentDTO);
      const newComment = {
        commentId: id,
        author: `You`,
        text: commentDTO.text || (attachmentUrl ? 'Shared an attachment' : ''),
        avatar: '/src/PostCard/avatar.png',
        time: 'now',
        attachment: attachmentUrl,
      };

      setComments((c) => [newComment, ...c]);
      setCommentText('');
      handleRemoveAttachment();
    } catch (err) {
      console.error('Error saving comment', err);
      alert('Failed to save comment');
    }
  };

  const openComments = (showAllFlag = false) => {
    setShowAll(!!showAllFlag);
    setShowComments(true);
  };

  const closeComments = () => setShowComments(false);

  const toggleInline = async () => {
    const newVal = !inlineVisible;
    setInlineVisible(newVal);
    if (newVal) {
      // load from server when opening inline
      await fetchCommentsFromServer();
    }
  };

  return (
    <div className="post-card">
      <UserHeader username={post.username ?? 'User'} time={post.time ?? 'just now'} />
      <Content content={post.content ?? ''} />

      {post.attachment && (
        <Attachment attachment={post.attachment} onRemove={post.onRemove ?? (() => {})} />
      )}

      <Footer onCommentClick={() => openComments(true)} onToggleInline={toggleInline} />

      {/* inline comments list */}
      {inlineVisible && (
        <div className="inline-comments">
          {loadingComments && <div className="no-comments">Loading comments…</div>}
          {!loadingComments && comments.length === 0 && <div className="no-comments">No comments yet</div>}
          {!loadingComments && comments.map((c, i) => (
            <div key={c.commentId ?? i} className="comment-item">
              <img src={c.avatar || '/src/PostCard/avatar.png'} className="comment-avatar" alt="avatar" />
              <div className="comment-body">
                <div className="comment-author">{c.author || 'Anonymous'}</div>
                <div className="comment-text">{c.text}</div>
                {c.attachment && (
                  <div style={{ marginTop: 8 }}>
                    <img src={c.attachment} alt="attachment" style={{ maxWidth: '100%', borderRadius: 8 }} />
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* composer below actions */}
      <div className="comment-composer">
        <img src="/src/PostCard/avatar.png" alt="me" className="composer-avatar" />
        <div className="composer-body">
          <textarea
            className="composer-input"
            placeholder="Write a comment..."
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
            rows={2}
          />

          <div className="composer-actions">
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <label className={`attach-button ${attachedFile ? 'disabled' : ''}`} title={attachedFile ? 'Remove attachment to add another' : 'Attach image'}>
                <input type="file" accept="image/*" onChange={handleFileChange} disabled={!!attachedFile} />
                📎
              </label>

              {attachedFile && (
                <div className="attach-preview">
                  <img src={attachedFile.preview} alt="attachment preview" />
                  {attachedFile.uploading && <div className="uploading-indicator">Uploading…</div>}
                  <button className="remove-attach" onClick={handleRemoveAttachment} aria-label="Remove">×</button>
                </div>
              )}
            </div>

            <div className="composer-send">
              <button className="send-btn" onClick={handleSendComment} disabled={attachedFile && attachedFile.uploading}>Send</button>
            </div>
          </div>
        </div>
      </div>

      {showComments && (
        <CommentsPanel comments={comments} onClose={closeComments} showAll={showAll} />
      )}
    </div>
  );
}

export default function PostCard({ post, Posts }) {
  const postsArray = Array.isArray(Posts) ? Posts : (post ? [post] : []);

  return (
    <>
      {postsArray.map((p, idx) => (
        <PostItem key={p.id ?? idx} post={p} />
      ))}
    </>
  );
}