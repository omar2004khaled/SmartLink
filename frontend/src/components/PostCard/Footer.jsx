import React, { useState, useRef, useEffect } from 'react';
import { MessageCircle, Forward, ThumbsUp, Heart, Laugh, Smile, Frown, Angry } from 'lucide-react';
import './PostCard.css';

const REACTIONS = [
    { key: 'like', Icon: ThumbsUp, label: 'Like', color: '#3b82f6' }, // Blue
    { key: 'love', Icon: Heart, label: 'Love', color: '#ef4444' }, // Red
    { key: 'haha', Icon: Laugh, label: 'HAHA', color: '#f59e0b' }, // Amber
    { key: 'wow', Icon: Smile, label: 'WOW', color: '#eab308' }, // Yellow
    { key: 'sad', Icon: Frown, label: 'SAD', color: '#8b5cf6' }, // Violet
    { key: 'angry', Icon: Angry, label: 'ANGRY', color: '#dc2626' }, // Red
];

function Footer({ onCommentClick, onToggleInline }) {
    const [selectedReaction, setSelectedReaction] = useState(null);
    const [showReactions, setShowReactions] = useState(false);
    const reactionsRef = useRef(null);

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

    const handleReactionSelect = (key) => {
        if (key === selectedReaction) {
            setSelectedReaction(null);
        } else {
            setSelectedReaction(key);
        }
        setShowReactions(false);
    };

    return (
        <div className="post-card-footer ">
            <div
                className="reaction-container"
                ref={reactionsRef}
                onMouseEnter={() => setShowReactions(true)}
            >
                <button
                    className={`reaction-button ${selectedReaction ? 'selected' : ''}`}
                    onClick={() => setShowReactions(s => !s)}
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
                            return (
                                <button
                                    key={reaction.key}
                                    className={`reaction-option ${isSelected ? 'selected' : ''}`}
                                    onClick={() => handleReactionSelect(reaction.key)}
                                    title={reaction.label}
                                    type="button"
                                    style={isSelected ? { 
                                        color: reaction.color,
                                        borderColor: `${reaction.color}40`,
                                        backgroundColor: `${reaction.color}15`
                                    } : {}}
                                >
                                    <ReactionIcon size={32}
                                       
                                        color={isSelected ? reaction.color : 'currentColor'}
                                    />
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
    );
}

export default Footer;