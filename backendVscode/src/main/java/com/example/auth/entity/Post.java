package com.example.auth.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Posts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostId")
    private BigInteger PostId;

    @Column(name = "UserId" , nullable = false)
    private BigInteger UserId;

    @Column(name = "content" , nullable = false , length = 2500)
    private String content  ;

    @Column(name = "CreatedAt")
    private Timestamp CreatedAt ;
}