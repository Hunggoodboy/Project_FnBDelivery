package com.fnb.backend.service;

import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.UsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Trong trường hợp này, 'username' truyền vào chính là số điện thoại từ AuthController
        Users user = usersRepository.findByPhoneNumber(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with phone number: " + username);
        }
        
        String role = user.getRole() != null ? user.getRole() : "USER";
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new User(user.getUsername(), 
                        user.getPassword(), 
                        Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
