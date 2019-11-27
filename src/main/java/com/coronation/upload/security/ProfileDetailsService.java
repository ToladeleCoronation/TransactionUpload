package com.coronation.upload.security;

import com.coronation.upload.domain.User;
import com.coronation.upload.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Toyin on 2/17/19.
 */

@Service
public class ProfileDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userDao;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new ProfileDetails(user);
    }
}
