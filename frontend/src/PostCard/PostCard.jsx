import React, { useState, useEffect } from 'react';
import UserHeader from './UserHeader';
import Content from './Content';
import Attachment from './Attachment';
import Footer from './Footer';
import CommentsPanel from './CommentsPanel';
import ReportModal from './ReportModal';
import { SaveComment, GetComments, GetUserInfo, userIdFromLocalStorage, UpdatePost, DeletePost } from '../FetchData/FetchData';
import './PostCard.css';
import { API_BASE_URL, CLOUDINARY_UPLOAD_URL } from '../config';

function PostItem({ post }) {
  const [showComments, setShowComments] = useState(false);
  const [showAll, setShowAll] = useState(false);
  const [loadingComments, setLoadingComments] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [loadingUserInfo, setLoadingUserInfo] = useState(false);
  const [commentUsersInfo, setCommentUsersInfo] = useState({});
  const [errorMessage, setErrorMessage] = useState(null);

  const [comments, setComments] = useState([]);

  const [commentText, setCommentText] = useState('');
  const [attachedFile, setAttachedFile] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState('');
  const [editAttachment, setEditAttachment] = useState(null);
  const [editAttachmentUrl, setEditAttachmentUrl] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);


  const showError = (message) => {
    setErrorMessage(message);
    setTimeout(() => {
      setErrorMessage(null);
    }, 5000);
  };

  // Remove the useEffect that sets comments from post.comments

  useEffect(() => {
    if (isEditing) {
      setEditContent(post?.content || '');
      setEditAttachmentUrl(post?.attachment || null);
      setEditAttachment(null);
    }
  }, [isEditing, post]);

  useEffect(() => {
    const fetchUserInfo = async () => {
      const userId = post?.userId;
      if (!userId) return;

      setLoadingUserInfo(true);
      try {
        const info = await GetUserInfo(userId);
        if (info) {
          setUserInfo(info);
        }
      } catch (err) {
        console.error('Error fetching user info:', err);
      } finally {
        setLoadingUserInfo(false);
      }
    };

    fetchUserInfo();
  }, [post?.userId]);

  useEffect(() => {
    return () => {
      if (attachedFile && attachedFile.preview) URL.revokeObjectURL(attachedFile.preview);
    };
  }, [attachedFile]);

  // Auto-fetch comments when post loads
  useEffect(() => {
    const postId = post?.id || post?.postId;
    if (postId) {
      fetchCommentsFromServer();
    }
  }, [post?.id, post?.postId]);

  const uploadToCloudinary = async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('upload_preset', 'dyk7gqqw');

      const res = await fetch(CLOUDINARY_UPLOAD_URL, {
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

  const fetchCommentsFromServer = async () => {
    const postId = post?.id || post?.postId;
    if (!post || !postId) {
      console.error('Cannot fetch comments: post or postId is missing', { post, postId });
      return;
    }
    setLoadingComments(true);
    try {
      const data = await GetComments(postId, 0, 10);

      const userIds = data.map(c => c.userId).filter(Boolean);
      const uniqueUserIds = [...new Set(userIds.filter(id => id && !commentUsersInfo[id]))];
      const userInfoPromises = uniqueUserIds.map(userId => GetUserInfo(userId));
      const userInfoResults = await Promise.all(userInfoPromises);
      const fetchedUserInfo = {};
      uniqueUserIds.forEach((userId, index) => {
        if (userInfoResults[index]) {
          fetchedUserInfo[userId] = userInfoResults[index];
        }
      });
      if (Object.keys(fetchedUserInfo).length > 0) {
        setCommentUsersInfo(prev => ({ ...prev, ...fetchedUserInfo }));
      }
      const allUserInfo = { ...commentUsersInfo, ...fetchedUserInfo };

      const mapped = data.map((c) => {
        const userInfo = c.userId ? allUserInfo[c.userId] : null;
        return {
          commentId: c.commentId,
          userId: c.userId,
          author: userInfo?.fullName || (c.userId ? `User${c.userId}` : 'Anonymous'),
          text: c.text,
          avatar: '/src/PostCard/avatar.png',
          time: 'some time',
          attachment: c.url || undefined,
        };
      });
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

    if (attachedFile) {
      showError('Only one attachment is allowed. Remove the current attachment first.');
      e.target.value = null;
      return;
    }

    const preview = URL.createObjectURL(f);
    setAttachedFile({ file: f, preview, uploading: true, uploadedUrl: null });
    e.target.value = null;

    const uploadedUrl = await uploadToCloudinary(f);
    setAttachedFile((prev) => ({
      ...prev,
      uploading: false,
      uploadedUrl: uploadedUrl || null,
    }));

    if (uploadedUrl) {
      console.log('Uploaded attachment URL:', uploadedUrl);
    } else {
      showError('Attachment upload failed');
    }
  };

  const handleRemoveAttachment = () => {
    if (attachedFile && attachedFile.preview) URL.revokeObjectURL(attachedFile.preview);
    setAttachedFile(null);
  };

  const saveCommentToServer = async (commentDTO) => {
    const res = await fetch(`${API_BASE_URL}/comment/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(commentDTO),
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || 'Failed to save comment');
    }
    const id = await res.json();
    return id;
  };

  const handleSendComment = async () => {
    const text = commentText.trim();
    if (!text && !attachedFile) return;
    const postId = post?.id || post?.postId;
    if (!postId) {
      console.error('Cannot send comment: post ID is missing', { post });
      showError('Error: Post ID is missing. Cannot send comment.');
      return;
    }

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
      userId: userIdFromLocalStorage(),
      text: text || null,
      url: attachmentUrl || null,
      type: attachmentUrl ? 'Image' : null,
      postId: postId
    };

    try {
      console.log('Submitting comment for post:', postId, 'Comment DTO:', commentDTO);
      const id = await SaveComment(commentDTO);
      setCommentText('');
      handleRemoveAttachment();
      await fetchCommentsFromServer();
    } catch (err) {
      console.error('Error saving comment', err);
      showError('Failed to save comment: ' + (err.message || 'Unknown error'));
    }
  };

  const toggleComments = () => {
    setShowComments(!showComments);
  };

  const closeComments = () => setShowComments(false);

  const toggleInline = async () => {
    const newVal = !inlineVisible;
    setInlineVisible(newVal);
    if (newVal) {
      await fetchCommentsFromServer();
    }
  };

  const handleDeletePost = async () => {
    const postId = post?.id || post?.postId;
    if (!postId) {
      showError('Error: Post ID is missing.');
      return;
    }

    if (!window.confirm('Are you sure you want to delete this post? This action cannot be undone.')) {
      return;
    }

    try {
      await DeletePost(postId);
      showError('Post deleted successfully!');
      window.location.reload();
    } catch (err) {
      console.error('Error deleting post:', err);
      showError('Failed to delete post: ' + (err.message || 'Unknown error'));
    }
  };

  const handleEditPost = () => {
    setIsEditing(true);
    // Scroll to the post card for better UX
    setTimeout(() => {
      const postCard = document.querySelector(`[data-post-id="${post?.id || post?.postId}"]`);
      if (postCard) {
        postCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }, 100);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditContent('');
    setEditAttachment(null);
    setEditAttachmentUrl(null);
  };

  const handleEditFileChange = async (e) => {
    const f = e.target.files && e.target.files[0];
    if (!f) return;

    const preview = URL.createObjectURL(f);
    setEditAttachment({ file: f, preview, uploading: true, uploadedUrl: null });
    e.target.value = null;

    const uploadedUrl = await uploadToCloudinary(f);
    setEditAttachment((prev) => ({
      ...prev,
      uploading: false,
      uploadedUrl: uploadedUrl || null,
    }));

    if (uploadedUrl) {
      setEditAttachmentUrl(uploadedUrl);
      console.log('Uploaded attachment URL:', uploadedUrl);
    } else {
      showError('Attachment upload failed');
    }
  };

  const handleRemoveEditAttachment = () => {
    if (editAttachment && editAttachment.preview) {
      URL.revokeObjectURL(editAttachment.preview);
    }
    setEditAttachment(null);
    setEditAttachmentUrl(null);
  };

  const handleSaveEdit = async () => {
    const postId = post?.id || post?.postId;
    if (!postId) {
      showError('Error: Post ID is missing.');
      return;
    }

    if (!editContent.trim() && !editAttachmentUrl) {
      showError('Please enter some content or add an attachment.');
      return;
    }

    if (editAttachment && editAttachment.uploading) {
      await new Promise((resolve) => {
        const check = () => {
          if (!editAttachment.uploading) return resolve();
          setTimeout(check, 200);
        };
        check();
      });
    }

    setIsSaving(true);
    try {
      const originalAttachment = post?.attachment || null;
      const finalAttachmentUrl = editAttachmentUrl;
      const attachmentChanged = finalAttachmentUrl !== originalAttachment;
      const contentChanged = editContent.trim() !== (post?.content || '');
      if (attachmentChanged) {
        const attachments = finalAttachmentUrl ? [{
          attachmentURL: finalAttachmentUrl,
          typeOfAttachment: 'Image',
          AttachId: null
        }] : [];
        const attachmentDTO = {
          id: postId,
          content: null,
          attachments: attachments.length > 0 ? attachments : null,
          createdAt: post.createdAt || null
        };
        console.log('Updating post attachments:', attachmentDTO);
        await UpdatePost(postId, attachmentDTO);
      }

      if (contentChanged || attachmentChanged) {
        const contentDTO = {
          id: postId,
          content: editContent.trim() || null,
          userId: post.userId,
          attachments: null,
          createdAt: post.createdAt || null
        };
        console.log('Updating post content:', contentDTO);
        await UpdatePost(postId, contentDTO);
      }

      showError('Post updated successfully!');
      setIsEditing(false);
      window.location.reload();
    } catch (err) {
      console.error('Error updating post:', err);
      showError('Failed to update post: ' + (err.message || 'Unknown error'));
    } finally {
      setIsSaving(false);
    }
  };

  const formatRelativeTime = (dateString) => {
    if (!dateString) return 'just now';

    try {
      const date = new Date(dateString);
      const now = new Date();
      const diffInSeconds = Math.floor((now - date) / 1000);

      // Less than a minute
      if (diffInSeconds < 60) {
        return 'just now';
      }
      const diffInMinutes = Math.floor(diffInSeconds / 60);
      if (diffInMinutes < 60) {
        return `${diffInMinutes} ${diffInMinutes === 1 ? 'minute' : 'minutes'} ago`;
      }
      const diffInHours = Math.floor(diffInMinutes / 60);
      if (diffInHours < 24) {
        return `${diffInHours} ${diffInHours === 1 ? 'hour' : 'hours'} ago`;
      }
      const diffInDays = Math.floor(diffInHours / 24);
      if (diffInDays < 7) {
        return `${diffInDays} ${diffInDays === 1 ? 'day' : 'days'} ago`;
      }

      // Weeks
      const diffInWeeks = Math.floor(diffInDays / 7);
      if (diffInWeeks < 4) {
        return `${diffInWeeks} ${diffInWeeks === 1 ? 'week' : 'weeks'} ago`;
      }

      // Months
      const diffInMonths = Math.floor(diffInDays / 30);
      if (diffInMonths < 12) {
        return `${diffInMonths} ${diffInMonths === 1 ? 'month' : 'months'} ago`;
      }

      // Years
      const diffInYears = Math.floor(diffInDays / 365);
      return `${diffInYears} ${diffInYears === 1 ? 'year' : 'years'} ago`;

    } catch (error) {
      console.error('Error formatting date:', error);
      return 'recently';
    }
  };

  return (
    <div className="post-card" data-post-id={post?.id || post?.postId}>
      {/* Error Message Display */}
      {errorMessage && (
        <div className="error-message">
          <span>{errorMessage}</span>
          <button onClick={() => setErrorMessage(null)} className="error-close">×</button>
        </div>
      )}

      <UserHeader
        username={userInfo?.fullName || post.username || 'User'}
        userId={post.userId}
        time={post.time ? formatRelativeTime(post.time) : 'just now'}
        bio={userInfo ? (userInfo.role ? `${userInfo.role}${userInfo.email ? ` • ${userInfo.email}` : ''}` : userInfo.email || '') : ''}
        avatarUrl={null}
        onDelete={handleDeletePost}
        onUpdate={handleEditPost}
        onReport={() => setShowReportModal(true)}
        postId={post?.id || post?.postId}
        userType={userInfo?.userType || post.userType}
        onError={showError}
      />
      {isEditing ? (
        <div className="post-edit-mode">
          <textarea
            className="post-edit-textarea"
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            placeholder="What's on your mind?"
            rows={4}
            autoFocus
          />
          <div className="post-edit-attachment">
            {editAttachmentUrl && !editAttachment && (
              <div className="edit-attachment-preview">
                <img src={editAttachmentUrl} alt="current attachment" />
                <button
                  className="remove-attach"
                  onClick={handleRemoveEditAttachment}
                  aria-label="Remove attachment"
                >
                  ×
                </button>
              </div>
            )}

            {editAttachment && (
              <div className="edit-attachment-preview">
                <img src={editAttachment.preview} alt="new attachment preview" />
                {editAttachment.uploading && <div className="uploading-indicator">Uploading…</div>}
                <button
                  className="remove-attach"
                  onClick={handleRemoveEditAttachment}
                  aria-label="Remove attachment"
                >
                  ×
                </button>
              </div>
            )}

            {!editAttachmentUrl && !editAttachment && (
              <label className="attach-button" title="Attach image">
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleEditFileChange}
                  disabled={!!editAttachment}
                />
                📎 Attach Image
              </label>
            )}
          </div>

          <div className="post-edit-actions">
            <button
              className="cancel-btn"
              onClick={handleCancelEdit}
              disabled={isSaving}
            >
              Cancel
            </button>
            <button
              className="save-btn"
              onClick={handleSaveEdit}
              disabled={isSaving || (!editContent.trim() && !editAttachmentUrl)}
            >
              {isSaving ? 'Saving...' : 'Save'}
            </button>
          </div>
        </div>
      ) : (
        <>
          <Content content={post.content ?? ''} />

          {/* Display ALL attachments */}
          {post.attachments && post.attachments.length > 0 && (
            <div className="post-attachments-container">
              {post.attachments.map((attachment, index) => (
                <Attachment
                  key={`attachment-${post.id}-${index}`}
                  attachment={attachment}
                  index={index}
                  totalAttachments={post.attachments.length}
                />
              ))}
            </div>
          )}
        </>
      )}

      <Footer onCommentClick={toggleComments} postId={post?.id || post?.postId} userId={userIdFromLocalStorage()} />

      {/* Show comments based on showComments state */}
      {showComments && comments.length > 0 && (
        <div className="inline-comments">
          {loadingComments && <div className="no-comments">Loading comments…</div>}
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

      {/* Show only 1 most recent comment when comments are hidden */}
      {!showComments && comments.length > 0 && (
        <div className="inline-comments">
          {loadingComments && <div className="no-comments">Loading comments…</div>}
          {!loadingComments && comments.slice(0, 1).map((c, i) => (
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
          {!loadingComments && comments.length > 1 && (
            <div className="view-all-comments" onClick={toggleComments} style={{
              padding: '8px 12px',
              color: '#666',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: 500
            }}>
              View all {comments.length} comments
            </div>
          )}
        </div>
      )}

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

      {showReportModal && (
        <ReportModal
          postId={post?.id || post?.postId}
          onClose={() => setShowReportModal(false)}
          onReportSuccess={() => {
            setShowReportModal(false);
            // Optionally show a success message or refresh
          }}
        />
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