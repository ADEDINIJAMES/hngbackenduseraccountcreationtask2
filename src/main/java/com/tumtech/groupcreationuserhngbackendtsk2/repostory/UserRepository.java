package com.tumtech.groupcreationuserhngbackendtsk2.repostory;

import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
Optional<Users> findByEmail (String email);
Boolean existsByEmail(String email);
}
