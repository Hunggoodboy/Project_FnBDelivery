package com.fnb.backend.service;

import com.fnb.backend.dto.Request.FavouriteRequestRequest;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.entity.FavouriteRequest;
import com.fnb.backend.repository.FavouriteRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class FavouriteService {
    private final FavouriteRequestRepository favouriteRequestRepository;
    private final MailService mailService;

    public ApiResponse saveFavouriteRequest(FavouriteRequestRequest request){
        FavouriteRequest favourite = FavouriteRequest.builder()
                .product(request.getProduct())
                .note(request.getNote())
                .type(request.getType())
                .createTime(LocalDateTime.now())
                .build();
        favouriteRequestRepository.save(favourite);
        mailService.sendEmail("Bạn có yêu cầu sở thích mới từ công chúa \n" +
                "Tên sản phẩm : " + request.getProduct() + "\n" +
                "Ghi chú : " + request.getNote());
        return ApiResponse.builder()
                .success(true)
                .message("Chúc mừng iem công chúa iu đã gửi sản phẩm yêu thích tới anh ạ, anh đã tiếp nhận ạ")
                .build();
    }
}
