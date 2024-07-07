package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImplementation (UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository= userRepository;
        this.passwordEncoder= passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
return (UserDetails) userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("user not found"));
    }



}
