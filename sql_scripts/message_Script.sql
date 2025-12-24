CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_message_sender 
        FOREIGN KEY (sender_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_message_receiver 
        FOREIGN KEY (receiver_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE
)