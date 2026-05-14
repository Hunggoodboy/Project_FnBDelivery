package com.fnb.backend.service;

import com.fnb.backend.dto.Request.OrderRequestDTO;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.entity.Orders;
import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.OrdersRepository;
import com.fnb.backend.repository.UsersRepository;
import com.fnb.backend.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.fnb.backend.dto.Response.OrderResponseDTO;

@Service
@AllArgsConstructor

public class OrderService {
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public ApiResponse createOrders(List<OrderRequestDTO> requests) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("Anh chin mời công chúa iu đăng nhập đã rui đặt bánh nhéee");
        }
        StringBuilder emailContent = new StringBuilder("Thông báo: Công chúa vừa đặt đơn hàng mới!\n\n");
        requests.stream()
                .forEach(request -> {
                    Users user = usersRepository.findById(currentUserId)
                                                .orElseThrow(() -> new RuntimeException("Anh chin mời công chúa Trần Lê Khánh Chi iu của anh đăng nhập đã rui đặt bánh nhéee"));
                    Orders orders = Orders.builder()
                            .users(user)
                            .customerName(request.getCustomerName())
                            .phoneNumber(request.getPhoneNumber())
                            .address(request.getAddress())
                            .note(request.getNote())
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
        sendEmailNotification("Thông báo đơn hàng mới từ Bakery", emailContent.toString());
        return ApiResponse.builder()
                .success(true)
                          .build();
    }

    private void sendEmailNotification(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("email_nhan_thong_bao@gmail.com"); // Email anh muốn nhận thông báo
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
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
