package com.fnb.backend.service;
import com.fnb.backend.dto.Request.FeedBackEditRequest;
import com.fnb.backend.dto.Request.FeedBackRequest;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.dto.Response.FeedBackResponse;
import com.fnb.backend.entity.FeedBack;
import com.fnb.backend.entity.Product;
import com.fnb.backend.repository.FeedBackRepository;
import com.fnb.backend.repository.IngredientRepository;
import com.fnb.backend.repository.NutritionRepository;
import com.fnb.backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeedBackService {
    private final ProductRepository productRepository;
    private final FeedBackRepository feedBackRepository;
    private final MailService  mailService;
    public ApiResponse saveFeedBack(FeedBackRequest feedBackRequest) {
        FeedBack feedBack = FeedBack.builder()
                .image(feedBackRequest.getImage())
                .content(feedBackRequest.getContent())
                .star(feedBackRequest.getStar())
                .createTime(java.time.LocalDateTime.now())
                .build();
        Product product = productRepository.findById(feedBackRequest.getProductId()).orElseThrow(() -> new RuntimeException("Không thấy sản phẩm"));
        feedBack.setProduct(product);
        feedBackRepository.save(feedBack);
        mailService.sendEmail(feedBack.getContent() + "cho món ăn" + product.getName());
        return ApiResponse.builder()
                .message("Chúc mừng công chúa đã gửi feedback thành công")
                .success(true)
                .build();
    }
    public ApiResponse editFeedBack(FeedBackEditRequest feedBackRequest) {
        FeedBack feedBack = feedBackRepository.findById(feedBackRequest.getFeedBackId()).orElseThrow(() -> new RuntimeException("Không thấy feedBack"));
        feedBack.setImage(feedBackRequest.getImage());
        feedBack.setContent(feedBackRequest.getContent());
        feedBack.setStar(feedBackRequest.getStar());
        feedBack.setUpdateTime(java.time.LocalDateTime.now());
        feedBackRepository.save(feedBack);
        return ApiResponse.builder()
                .success(true)
                .message("Chúc mừng công chúa đã sửa feedback thành công ạaa")
                .build();
    }
    public ApiResponse deleteFeedBack(Long id) {
        feedBackRepository.deleteById(id);
        return ApiResponse.builder()
                .message("Hụ Hụ seo công chúa lại xoá feedback ché ọ")
                .success(true)
                .build();
    }
    public List<FeedBackResponse> getFeedBackByProductId(Long productId) {
        return feedBackRepository.findByProductIdOrderByCreateTimeDesc(productId)
                .stream()
                .map(feedBack -> {
                    return FeedBackResponse.builder()
                            .id(feedBack.getId())
                            .image(feedBack.getImage())
                            .content(feedBack.getContent())
                            .star(feedBack.getStar())
                            .createTime(feedBack.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());
    }
    public FeedBackResponse getFeedBackById(Long id) {
        FeedBack current =  feedBackRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có feedback"));
        return FeedBackResponse.builder()
                .id(current.getId())
                .star(current.getStar())
                .content(current.getContent())
                .image(current.getImage())
                .createTime(current.getCreateTime())
                .build();
    }
}
