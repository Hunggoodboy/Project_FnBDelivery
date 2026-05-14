package com.fnb.backend.controller;

import com.fnb.backend.dto.UserSessionDTO;
import com.fnb.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Inject AuthenticationManager để báo cho Spring Security biết
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request){
        String phone_number = credentials.get("phone_number");
        String password = credentials.get("password");

        try {
            // 1. Xác thực qua Spring Security
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(phone_number, password);
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Lưu vào Session để Spring Security nhớ
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            // 3. Logic cũ của bạn: Lấy thông tin user và lưu DTO vào session
            UserSessionDTO userSessionDTO = userService.login(phone_number, password);
            session.setAttribute("user", userSessionDTO);
            session.setAttribute("UserID", userSessionDTO.getId());

            return ResponseEntity.ok(userSessionDTO);

        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401).body("Tài khoản hoặc mật khẩu không chính xác!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        UserSessionDTO user = (UserSessionDTO) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}