package com.example.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ProfileRepositories.JobSeekerProfileRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.CommentRepo;
import com.example.auth.entity.Post;
import com.example.auth.service.EmailService;
import com.example.auth.entity.Report;
import com.example.auth.repository.ReportRepository;
import com.example.auth.enums.ReportCategory;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final PostRepository postRepository;
    private final CommentRepo commentRepository;
    private final ReportRepository reportRepository;

    public AdminController(UserRepository userRepository, EmailService emailService, JobSeekerProfileRepository jobSeekerProfileRepository, PostRepository postRepository, CommentRepo commentRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard!";
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String userType) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> users;
        
        if (!search.isEmpty() || !userType.isEmpty()) {
            users = userRepository.findUsersWithFilters(search, userType, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", users.getContent());
        response.put("totalElements", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("currentPage", users.getNumber());
        response.put("size", users.getSize());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        List<User> allUsers = userRepository.findAll();
        
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(User::isEnabled).count();
        long adminUsers = allUsers.stream().filter(user -> "ADMIN".equals(user.getRole())).count();
        long companyUsers = allUsers.stream().filter(user -> "COMPANY".equals(user.getUserType())).count();
        long jobSeekerUsers = allUsers.stream().filter(user -> "JOB_SEEKER".equals(user.getUserType())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("adminUsers", adminUsers);
        stats.put("companyUsers", companyUsers);
        stats.put("jobSeekerUsers", jobSeekerUsers);
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/send-email/{userId}")
    public ResponseEntity<?> sendEmailToUser(@PathVariable Long userId, @RequestBody Map<String, String> request, Authentication auth) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }
        
        User user = userOptional.get();
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }
        
        // Send email using EmailService
        boolean emailSent = emailService.sendAdminNotification(user.getEmail(), auth.getName(), message);
        
        if (emailSent) {
            return ResponseEntity.ok("Email sent to " + user.getEmail() + " successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to send email to " + user.getEmail());
        }
    }

    @PostMapping("/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN') and @userRepository.findByEmail(authentication.name).orElse(new com.example.auth.entity.User()).email == 'BigBoss@example.com'")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId, Authentication auth) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("User is already an ADMIN");
        }

        if (!user.isEnabled()) {
            return ResponseEntity.badRequest().body("User must verify email before promotion");
        }

        user.setRole("ADMIN");
        userRepository.save(user);
        

        return ResponseEntity.ok("User " + user.getEmail() + " promoted to ADMIN successfully!");
    }

    @PostMapping("/demote/{userId}")
    @PreAuthorize("hasRole('ADMIN') and @userRepository.findByEmail(authentication.name).orElse(new com.example.auth.entity.User()).email == 'BigBoss@example.com'")
    public ResponseEntity<?> demoteToUser(@PathVariable Long userId, Authentication auth) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        if ("BigBoss@example.com".equals(user.getEmail())) {
            return ResponseEntity.badRequest().body("Cannot demote super admin");
        }

        if ("USER".equals(user.getRole())) {
            return ResponseEntity.badRequest().body("User is already a regular USER");
        }

        user.setRole("USER");
        userRepository.save(user);
        

        return ResponseEntity.ok("User " + user.getEmail() + " demoted to USER successfully!");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        return ResponseEntity.ok(userOptional.get());
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') and @userRepository.findByEmail(authentication.name).orElse(new com.example.auth.entity.User()).email == 'BigBoss@example.com'")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, Authentication auth) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found with ID: " + userId);
            }

            User user = userOptional.get();
            
            if ("BigBoss@example.com".equals(user.getEmail())) {
                return ResponseEntity.badRequest().body("Cannot delete super admin");
            }

            jobSeekerProfileRepository.findByUser_Id(userId).ifPresent(jobSeekerProfileRepository::delete);
            
            // Log the action
            System.out.println("AUDIT LOG: Admin " + auth.getName() + " deleted user " + user.getEmail() + " (ID: " + userId + ") at " + java.time.LocalDateTime.now());
            
            userRepository.delete(user);
            return ResponseEntity.ok("User " + user.getEmail() + " deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete user: User has associated profile data that must be removed first.");
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        
        ResponseEntity<?> authCheck = checkAuthentication(auth);
        if (authCheck != null) return authCheck;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postId").descending());
        Page<Post> posts = postRepository.findAll(pageable);
        
        // Enrich posts with user email information
        List<Map<String, Object>> enrichedPosts = posts.getContent().stream().map(post -> {
            Map<String, Object> postData = new HashMap<>();
            postData.put("postId", post.getPostId());
            postData.put("content", post.getContent());
            postData.put("userId", post.getUserId());
            postData.put("createdAt", post.getCreatedAt());
            
            // Get user email
            Optional<User> userOptional = userRepository.findById(post.getUserId());
            if (userOptional.isPresent()) {
                postData.put("userEmail", userOptional.get().getEmail());
                postData.put("userName", userOptional.get().getFullName());
            } else {
                postData.put("userEmail", "Unknown User");
                postData.put("userName", "Unknown User");
            }
            
            return postData;
        }).toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("posts", enrichedPosts);
        response.put("totalElements", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("currentPage", posts.getNumber());
        response.put("size", posts.getSize());
        
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> checkAuthentication(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication required");
            error.put("message", "Please log in to access admin features");
            return ResponseEntity.status(401).body(error);
        }
        
        boolean hasAdminRole = auth.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!hasAdminRole) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied");
            error.put("message", "Admin privileges required");
            return ResponseEntity.status(403).body(error);
        }
        
        return null;
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication auth) {
        ResponseEntity<?> authCheck = checkAuthentication(auth);
        if (authCheck != null) return authCheck;
        
        try {
            Optional<Post> postOptional = postRepository.findById(postId);

            if (postOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Post not found with ID: " + postId);
            }

            Post post = postOptional.get();
            
            // Delete related comments first
            commentRepository.deleteByPostId(postId);
            
            // Log the action
            System.out.println("AUDIT LOG: Admin " + auth.getName() + " deleted post ID: " + postId + " at " + java.time.LocalDateTime.now());
            
            postRepository.delete(post);
            return ResponseEntity.ok("Post deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete post: " + e.getMessage());
        }
    }

    // Report Management Endpoints

    @GetMapping("/reports")
    public ResponseEntity<?> getReportedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String category,
            Authentication auth) {

        ResponseEntity<?> authCheck = checkAuthentication(auth);
        if (authCheck != null) return authCheck;

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        List<Report> reports;
        if (!status.isEmpty() && !category.isEmpty()) {
            // Filter by both status and category
            reports = reportRepository.findAll().stream()
                .filter(r -> status.equals(r.getStatus()) &&
                           (category.isEmpty() || category.equals(r.getReportCategory().toString())))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .skip((long) page * size)
                .limit(size)
                .toList();
        } else if (!status.isEmpty()) {
            reports = reportRepository.findAll().stream()
                .filter(r -> status.equals(r.getStatus()))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .skip((long) page * size)
                .limit(size)
                .toList();
        } else if (!category.isEmpty()) {
            reports = reportRepository.findAll().stream()
                .filter(r -> category.equals(r.getReportCategory().toString()))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .skip((long) page * size)
                .limit(size)
                .toList();
        } else {
            reports = reportRepository.findAll().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .skip((long) page * size)
                .limit(size)
                .toList();
        }

        // Return individual reports with post and author information
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Report report : reports) {
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("id", report.getReportId());
            reportData.put("category", report.getReportCategory().toString());
            reportData.put("description", report.getDescription());
            reportData.put("timestamp", report.getTimestamp());
            reportData.put("status", report.getStatus());

            // Add reporter info
            Optional<User> reporter = userRepository.findById(report.getReporterId());
            if (reporter.isPresent()) {
                Map<String, Object> reporterInfo = new HashMap<>();
                reporterInfo.put("id", reporter.get().getId());
                reporterInfo.put("name", reporter.get().getFullName());
                reporterInfo.put("email", reporter.get().getEmail());
                reportData.put("reporter", reporterInfo);
            }

            // Add post info
            Optional<Post> postOptional = postRepository.findById(report.getPostId());
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                Map<String, Object> postInfo = new HashMap<>();
                postInfo.put("id", post.getPostId());
                postInfo.put("content", post.getContent());
                postInfo.put("createdAt", post.getCreatedAt());

                // Add post author info
                Optional<User> author = userRepository.findById(post.getUserId());
                if (author.isPresent()) {
                    Map<String, Object> authorInfo = new HashMap<>();
                    authorInfo.put("id", author.get().getId());
                    authorInfo.put("name", author.get().getFullName());
                    authorInfo.put("email", author.get().getEmail());
                    postInfo.put("author", authorInfo);
                } else {
                    // Author not found
                    Map<String, Object> authorInfo = new HashMap<>();
                    authorInfo.put("id", null);
                    authorInfo.put("name", "Unknown User");
                    authorInfo.put("email", "N/A");
                    postInfo.put("author", authorInfo);
                }

                reportData.put("post", postInfo);
            } else {
                // Post not found (may have been deleted)
                Map<String, Object> postInfo = new HashMap<>();
                postInfo.put("id", report.getPostId());
                postInfo.put("content", "This post has been deleted or is no longer available.");
                postInfo.put("createdAt", null);

                Map<String, Object> authorInfo = new HashMap<>();
                authorInfo.put("id", null);
                authorInfo.put("name", "Unknown User");
                authorInfo.put("email", "N/A");
                postInfo.put("author", authorInfo);

                reportData.put("post", postInfo);
            }

            result.add(reportData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", result);
        response.put("totalElements", result.size());
        response.put("totalPages", (result.size() + size - 1) / size);
        response.put("currentPage", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/{reportId}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> request,
            Authentication auth) {

        ResponseEntity<?> authCheck = checkAuthentication(auth);
        if (authCheck != null) return authCheck;

        String action = request.get("action"); // APPROVE_POST, REMOVE_POST, WARN_USER
        String adminNote = request.getOrDefault("adminNote", "");

        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Report not found");
        }

        Report report = reportOptional.get();
        report.setStatus("RESOLVED");
        reportRepository.save(report);

        // Log admin action
        System.out.println("AUDIT LOG: Admin " + auth.getName() + " resolved report " + reportId +
                          " with action: " + action + " at " + java.time.LocalDateTime.now());

        // Handle different actions
        String message = "Report resolved successfully";
        switch (action) {
            case "REMOVE_POST":
                Optional<Post> postOptional = postRepository.findById(report.getPostId());
                if (postOptional.isPresent()) {
                    commentRepository.deleteByPostId(report.getPostId());
                    postRepository.delete(postOptional.get());
                    message = "Post removed successfully";
                }
                break;
            case "WARN_USER":
                // Send warning email to post author
                Optional<Post> post = postRepository.findById(report.getPostId());
                if (post.isPresent()) {
                    Optional<User> author = userRepository.findById(post.get().getUserId());
                    if (author.isPresent()) {
                        emailService.sendAdminNotification(
                            author.get().getEmail(),
                            "Admin",
                            "Your post has been reported and reviewed. Please review our community guidelines to avoid future violations. " + adminNote
                        );
                        message = "Warning sent to user";
                    }
                }
                break;
            case "APPROVE_POST":
            default:
                message = "Post approved - no action taken";
                break;
        }

        return ResponseEntity.ok(message);
    }

    @GetMapping("/reports/stats")
    public ResponseEntity<?> getReportStats(Authentication auth) {
        ResponseEntity<?> authCheck = checkAuthentication(auth);
        if (authCheck != null) return authCheck;

        List<Report> allReports = reportRepository.findAll();
        long totalReports = allReports.size();
        long pendingReports = allReports.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long resolvedReports = allReports.stream().filter(r -> "RESOLVED".equals(r.getStatus())).count();

        // High priority reports (SEXUAL_HARASSMENT, HATE_SPEECH)
        long highPriorityReports = allReports.stream()
            .filter(r -> "PENDING".equals(r.getStatus()))
            .filter(r -> r.getReportCategory() == ReportCategory.SEXUAL_HARASSMENT ||
                        r.getReportCategory() == ReportCategory.HATE_SPEECH)
            .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReports", totalReports);
        stats.put("pendingReports", pendingReports);
        stats.put("resolvedReports", resolvedReports);
        stats.put("highPriorityReports", highPriorityReports);

        return ResponseEntity.ok(stats);
    }
}