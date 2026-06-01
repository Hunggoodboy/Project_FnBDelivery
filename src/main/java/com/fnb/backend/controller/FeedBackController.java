package com.fnb.backend.controller;

import com.fnb.backend.dto.Request.FeedBackEditRequest;
import com.fnb.backend.dto.Request.FeedBackRequest;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.entity.FeedBack;
import com.fnb.backend.service.FeedBackService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
public class FeedBackController {
    private final FeedBackService feedBackService;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody FeedBackRequest feedBackRequest){
        return ResponseEntity.ok(feedBackService.saveFeedBack(feedBackRequest));
    }
    @GetMapping("/product")
    public ResponseEntity<?> getFeedBack(@RequestParam Long id){
        return ResponseEntity.ok(feedBackService.getFeedBackByProductId(id));
    }
    @GetMapping("/detail")
    public ResponseEntity<?> getFeedBackDetail(@RequestParam Long id){
        return ResponseEntity.ok(feedBackService.getFeedBackById(id));
    }
    @PutMapping("/edit")
    public ResponseEntity<?> edit(@RequestBody FeedBackEditRequest request){
        return ResponseEntity.ok(feedBackService.editFeedBack(request));
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long id){
        return ResponseEntity.ok(feedBackService.deleteFeedBack(id));
    }
}
