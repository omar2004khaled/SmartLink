package com.example.auth.entity;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostId")
    private Long postId;

    @Column(name = "UserId" , nullable = false)
    private Long UserId;

    @Column(name = "content" , nullable = false , length = 2500)
    private String content  ;

    @Column(name = "CreatedAt")
    private Timestamp CreatedAt ;

    public Post(Long userId, String content) {
        this.UserId = userId;
        this.content = content;
    }
}