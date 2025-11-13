package com.backend.hypershop.repository;

import com.backend.hypershop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByMobile(String phone);
    Optional<User> findByEmail(String email);


}
