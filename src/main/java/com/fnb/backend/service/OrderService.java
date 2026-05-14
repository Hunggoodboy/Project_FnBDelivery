package com.fnb.backend.service;

import com.fnb.backend.dto.Request.OrderRequestDTO;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.entity.Orders;
import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.OrdersRepository;
import com.fnb.backend.repository.UsersRepository;
import com.fnb.backend.utils.SecurityUtils;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.fnb.backend.dto.Response.OrderResponseDTO;
import com.resend.*;


@Service
@RequiredArgsConstructor

public class OrderService {
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final JavaMailSender mailSender;

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${USERNAME_EMAIL}")
    private String myEmail;

    @Transactional
    public ApiResponse createOrders(List<OrderRequestDTO> requests) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Anh chin mời công chúa iu đăng nhập đã rui đặt bánh nhéee");
        }
        Users user = usersRepository.findById(currentUserId)
                                    .orElseThrow(() -> new RuntimeException("Người dùng chưa đăng ký"));
        StringBuilder emailContent = new StringBuilder("Thông báo: Công chúa vừa đặt đơn hàng mới!\n\n");
        requests.stream()
                .forEach(request -> {
                    Orders orders = Orders.builder()
                            .users(user)
                            .customerName(request.getCustomerName())
                            .phoneNumber(request.getPhoneNumber())
                            .address(request.getAddress())
                            .paymentMethod(request.getPaymentMethod())
                            .totalPrice(request.getTotalPrice())
                            .status("Pending")
                            .note(request.getNote())
                            .createdAt(LocalDateTime.now())
                            .build();
                    ordersRepository.save(orders);
                    emailContent.append("- Món: ").append(request.getNote()).append("\n")
                                .append("- Giá: ").append(request.getTotalPrice()).append(" cái ôm\n")
                                .append("- Địa chỉ: ").append(request.getAddress()).append("\n\n");
                });
        sendEmail(emailContent.toString());
        return ApiResponse.builder()
                .success(true)
                          .build();
    }

    public void sendEmail(String text) {
        // Cho vào luồng ngầm để web không bị đơ
        new Thread(() -> {
            try {
                Resend resend = new Resend(resendApiKey);

                // Dùng CreateEmailOptions thay vì SendEmailRequest
                CreateEmailOptions params = CreateEmailOptions.builder()
                                                              .from("Tiệm Bánh Báo Đơn <onboarding@resend.dev>")
                                                              .to(myEmail)
                                                              .text(text)
                                                              .subject("Đơn hàng mới từ công chúa iu")
                                                              .html(null)
                                                              .build();

                // Dùng CreateEmailResponse thay vì SendEmailResponse
                CreateEmailResponse data = resend.emails().send(params);
                System.out.println("Đã gửi email thành công! ID: " + data.getId());

            } catch (ResendException e) {
                System.err.println("Lỗi gửi mail qua Resend: " + e.getMessage());
            }
        }).start();
    }
    public List<OrderResponseDTO> getAllOrders() {
        return ordersRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getTodayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        List<OrderResponseDTO> dtos =  ordersRepository.findByCreatedAtBetween(startOfDay, endOfDay)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        Collections.reverse(dtos);
        return dtos;
    }

    private OrderResponseDTO mapToResponseDTO(Orders order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .paymentMethod(order.getPaymentMethod())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
