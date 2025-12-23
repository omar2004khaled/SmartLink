package com.example.auth.controller;

import java.util.List;  // Updated package path

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;  // Updated package path
import org.springframework.data.domain.Pageable;  // Updated package path
import org.springframework.data.domain.Sort;  // Updated package path
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;  // Updated package path
import org.springframework.web.bind.annotation.GetMapping;  // Updated package path
import org.springframework.web.bind.annotation.PathVariable;  // Updated package path
import org.springframework.web.bind.annotation.PostMapping;  // Updated package path
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.PostDTO;
import com.example.auth.dto.ReportDTO;
import com.example.auth.enums.ReportCategory;
import com.example.auth.service.PostService.PostService;
import com.example.auth.service.ReportService.ReportService;

@RestController
@RequestMapping("/Post")
@CrossOrigin(origins = "${app.frontend.base-url}")
public class PostRestController {
    private PostService postService;
    private ReportService reportService;
    @Autowired
    public PostRestController(PostService postService, ReportService reportService) {
        this.postService = postService;
        this.reportService = reportService;
    }
    @GetMapping("/all")
    public List<PostDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size,
                                                           @RequestParam(defaultValue = "PostId") String sortBy,
                                                           @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return postService.findAll(pageable);
    }
    @GetMapping("/{id}")
    public PostDTO findById(@PathVariable Long id) {
        return postService.findById(id);
    }
    @PostMapping("/add")
    public PostDTO addPost(@RequestBody PostDTO postDTO) {
        return postService.save(postDTO);
    }
    @PutMapping("/update/{id}")
    public PostDTO updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        if (postDTO==null) return null;
        return postService.updatePost(id, postDTO);
    }
    @DeleteMapping("/delete/{id}")
    public void deletePost(@PathVariable Long id) {
        postService.deleteById(id);
    }

    @PostMapping("/{postId}/report")
    public ReportDTO reportPost(@PathVariable Long postId, @RequestParam Long reporterId,
                                @RequestParam ReportCategory category,
                                @RequestParam(required = false) String description) {
        return reportService.createReport(postId, reporterId, category, description);
    }

    @GetMapping("/{postId}/reports")
    public List<ReportDTO> getReportsForPost(@PathVariable Long postId) {
        return reportService.getReportsByPostId(postId);
    }

    @GetMapping("/{postId}/report-count")
    public long getReportCount(@PathVariable Long postId) {
        return reportService.getReportCountForPost(postId);
    }

    @GetMapping("/{postId}/has-reported")
    public boolean hasUserReported(@PathVariable Long postId, @RequestParam Long reporterId) {
        return reportService.hasUserReportedPost(postId, reporterId);
    }

}
