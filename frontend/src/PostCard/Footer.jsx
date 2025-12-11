import React, { useState, useRef, useEffect } from 'react';
import { MessageCircle, Forward, ThumbsUp, Heart, Laugh, Smile, Frown, Angry } from 'lucide-react';
import './PostCard.css';
import { toggleReaction, getReactionCounts, getTopReactions, getTotalReactions, getUserReaction } from '../FetchData/FetchData';

const REACTIONS = [
    { key: 'LIKE', Icon: ThumbsUp, label: 'Like', color: '#3b82f6' },
    { key: 'LOVE', Icon: Heart, label: 'Love', color: '#ef4444' },
    { key: 'HAHA', Icon: Laugh, label: 'HAHA', color: '#f59e0b' },
    { key: 'WOW', Icon: Smile, label: 'WOW', color: '#eab308' },
    { key: 'SAD', Icon: Frown, label: 'SAD', color: '#8b5cf6' },
    { key: 'ANGRY', Icon: Angry, label: 'ANGRY', color: '#dc2626' },
];
    const getReactionIcon = (reactionType) => {
        const reaction = REACTIONS.find(r => r.key === reactionType);
        return reaction ? reaction.Icon : ThumbsUp;
    };

    const getReactionColor = (reactionType) => {
        const reaction = REACTIONS.find(r => r.key === reactionType);
        return reaction ? reaction.color : '#3b82f6';
    };
function Footer({ onCommentClick, onToggleInline , postId , userId}) {
    const [selectedReaction, setSelectedReaction] = useState(null);
    const [showReactions, setShowReactions] = useState(false);
    const [reactionCounts, setReactionCounts] = useState({});
    const [topReactions, setTopReactions] = useState([]);
    const [totalReactions, setTotalReactions] = useState(0);
    const [loading, setLoading] = useState(false);
    const reactionsRef = useRef(null);
    useEffect(() => {
        const loadReactionData = async () => {
            if (!postId) return;
            try {
                setLoading(true);
                if (userId) {
                    const userReaction = await getUserReaction(postId, userId);
                    setSelectedReaction(userReaction);
                }
                const counts = await getReactionCounts(postId);
                setReactionCounts(counts);
                const topReacts = await getTopReactions(postId, 3);
                setTopReactions(topReacts);
                const total = await getTotalReactions(postId);
                setTotalReactions(total);
            } catch (error) {
                console.error('Error loading reaction data:', error);
            } finally {
                setLoading(false);
            }
        };
        loadReactionData();
    }, [postId, userId]);

    useEffect(() => {
        function handleClickOutside(e) {
            if (reactionsRef.current && !reactionsRef.current.contains(e.target)) {
                setShowReactions(false);
            }
        }
        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, []);

    const currentReaction = REACTIONS.find(r => r.key === selectedReaction) || REACTIONS[0];
    const CurrentIcon = selectedReaction ? currentReaction.Icon : REACTIONS[0].Icon;
    const currentColor = currentReaction?.color || '#0f172a';
    const currentLabel = selectedReaction ? currentReaction?.label : 'Like';

    const handleReactionSelect = async (reactionKey) => {
        if (!postId || !userId) {
            console.error('Cannot react: postId or userId is missing');
            return;
        }
        
        try {
            const result = await toggleReaction(postId, userId, reactionKey);
            setSelectedReaction(result.reactionType);
            const counts = await getReactionCounts(postId);
            setReactionCounts(counts);
            const topReacts = await getTopReactions(postId, 3);
            setTopReactions(topReacts);
            const total = await getTotalReactions(postId);
            setTotalReactions(total);
        } catch (error) {
            console.error('Error toggling reaction:', error);
        } finally {
            setShowReactions(false);
        }
    };
    const renderTopReactions = () => {
        if (topReactions.length === 0 || totalReactions === 0) {
            return null;
        }
        
        return (
            <div className="top-reactions-bar">
                <div className="reaction-icons">
                    {topReactions.map((reaction, index) => {
                        const ReactionIcon = getReactionIcon(reaction.type);
                        const color = getReactionColor(reaction.type);
                        return (
                            <div key={index} className="reaction-icon-wrapper" title={`${reaction.type}: ${reaction.count}`}>
                                <ReactionIcon 
                                    size={18} 
                                    color={color} 
                                    fill="transparent"
                                />
                            </div>
                        );
                    })}
                </div>
                {totalReactions > 0 && (
                    <span className="reaction-count">
                        {totalReactions}
                    </span>
                )}
            </div>
        );
    };

    return (
        <div className="post-footer-container">

            {renderTopReactions()}
            <div className="post-card-footer">
                <div
                    className="reaction-container"
                    ref={reactionsRef}
                    onMouseEnter={() => setShowReactions(true)}
                >
                    <button
                        className={`reaction-button ${selectedReaction ? 'selected' : ''}`}
                        onClick={() => setShowReactions(s => !s)}
                        disabled={loading}
                        style={selectedReaction ? { 
                            color: currentColor,
                            borderColor: `${currentColor}40`,
                            backgroundColor: `${currentColor}10`
                        } : {}}
                    >
                        <CurrentIcon className="reaction-icon" size={24} color={selectedReaction ? currentColor: 'currentColor'} />
                        <span className="reaction-label">{currentLabel}</span>
                    </button>

                    {showReactions && (
                        <div 
                            className="reactions-picker"
                            onMouseEnter={() => setShowReactions(true)}
                            onMouseLeave={() => setShowReactions(false)}
                        >
                            {REACTIONS.map(reaction => {
                                const ReactionIcon = reaction.Icon;
                                const isSelected = selectedReaction === reaction.key;
                                const count = reactionCounts[reaction.key] || 0;
                                return (
                                    <button
                                        key={reaction.key}
                                        className={`reaction-option ${isSelected ? 'selected' : ''}`}
                                        onClick={() => handleReactionSelect(reaction.key)}
                                        title={`${reaction.label} (${count})`}
                                        type="button"
                                        style={isSelected ? { 
                                            color: reaction.color,
                                            borderColor: `${reaction.color}40`,
                                            backgroundColor: `${reaction.color}15`
                                        } : {}}
                                    >
                                        <div className="reaction-option-content">
                                            <ReactionIcon size={32} color={isSelected ? reaction.color : 'currentColor'} />
                                            {count > 0 && (
                                                <span className="reaction-count-badge">{count}</span>
                                            )}
                                        </div>
                                    </button>
                                );
                            })}
                        </div>
                    )}
                </div>

                <div style={{ display: 'flex', gap: 12, flex: 1 }}>
                    <button
                        className="comment-icon-button"
                        onClick={(e) => {
                            e.preventDefault();
                            if (typeof onToggleInline === 'function') {
                                onToggleInline();
                            } else if (typeof onCommentClick === 'function') {
                                onCommentClick(true);
                            }
                        }}
                        aria-label="Comment"
                    >
                        <MessageCircle />
                        <span>Comment</span>
                    </button>

                    <button className="share-icon-button">
                        <Forward />
                        Share
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Footer;