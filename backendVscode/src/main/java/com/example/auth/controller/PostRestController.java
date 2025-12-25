package com.example.auth.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.PostDTO;
import com.example.auth.dto.ReportDTO;
import com.example.auth.enums.ReportCategory;
import com.example.auth.service.PostService.PostService;
import com.example.auth.service.PostSortingService;
import com.example.auth.service.RedisService;
import com.example.auth.service.ReportService.ReportService;

@RestController
@RequestMapping("/Post")
@CrossOrigin(origins = "${app.frontend.base-url}")
public class PostRestController {
    private PostService postService;
    private ReportService reportService;
    private RedisService redisService;
    private PostSortingService postSortingService;

    @Autowired
    public PostRestController(PostService postService, ReportService reportService,
                              RedisService redisService, PostSortingService postSortingService) {
        this.postService = postService;
        this.reportService = reportService;
        this.redisService = redisService;
        this.postSortingService = postSortingService;
    }

    @GetMapping("/all")
    public List<PostDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "7") int size,
                                 @RequestParam(defaultValue = "PostId") String sortBy,
                                 @RequestParam(defaultValue = "true") boolean ascending,
                                 @RequestParam(required = false) Long viewerId) {
        if (viewerId == null) {
            Sort sort = ascending ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            return postService.findAll(pageable);
        }

        final String feedKey = "feed:" + viewerId;
        long start = (long) page * size;
        long end = start + size - 1;

        if (!redisService.hasKey(feedKey)) {
            postSortingService.execute(0, viewerId);
        }
        Set<Object> memberIds = redisService.getSortedSetRange(feedKey, start, end);

        if (memberIds == null || memberIds.isEmpty()) {
            postSortingService.execute(0, viewerId);
            memberIds = redisService.getSortedSetRange(feedKey, start, end);
            if (memberIds == null) memberIds = Set.of();
        }

        List<PostDTO> result = new ArrayList<>();
        boolean triggeredNextPage = false;

        for (Object mem : memberIds) {
            if (mem == null) continue;
            String s = String.valueOf(mem);
            if (s.startsWith("PAGE:")) {
                try {
                    String pageStr = s.substring(5);
                    int nextPage = Integer.parseInt(pageStr);
                    postSortingService.execute(nextPage, viewerId);
                    triggeredNextPage = true;
                } catch (Exception ex) {
                }
                continue;
            }
            try {
                Long postId = Long.parseLong(s);
                PostDTO dto = postService.findById(postId);
                if (dto != null) {
                    result.add(dto);
                }
            } catch (Exception ex) {
            }
        }

        if (triggeredNextPage) {
            memberIds = redisService.getSortedSetRange(feedKey, start, end);
            result.clear();
            for (Object mem : memberIds) {
                if (mem == null) continue;
                String s = String.valueOf(mem);
                if (s.startsWith("PAGE:")) continue;
                try {
                    Long postId = Long.parseLong(s);
                    PostDTO dto = postService.findById(postId);
                    if (dto != null) result.add(dto);
                } catch (Exception ex) {

                }
            }
        }

        return result;
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
        if (postDTO == null) return null;
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
