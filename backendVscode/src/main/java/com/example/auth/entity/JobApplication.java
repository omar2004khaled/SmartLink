package com.example.auth.entity;

import com.example.auth.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "job_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus applicationStatus;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applier_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;
    @Column(name = "cv_erl")
    String cvURL;
    @Column(name = "cover_letter")
    String coverLetter;
    @ElementCollection
    @CollectionTable(
            name = "job_application_comments",
            joinColumns = @JoinColumn(name = "application_id")
    )
    @Column(name = "comment")
    private List<String> comments;
}
