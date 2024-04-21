package com.dalv.bksims.services.user;

import com.dalv.bksims.exceptions.EntityNotFoundException;
import com.dalv.bksims.models.entities.user.User;
import com.dalv.bksims.models.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepo;

    public User findOneUserByCode(String code) {
        User user = userRepo.findByCode(code).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User with code " + code + " not found");
        }
        return user;
    }
}
