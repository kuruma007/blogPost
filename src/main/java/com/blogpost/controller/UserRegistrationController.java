package com.blogpost.controller;

import com.blogpost.service.UserService;
import com.blogpost.web.dto.UserRegistrationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRegistrationController {

    private UserService userService;

    public UserRegistrationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUserAccount(@RequestBody UserRegistrationDto userRegistrationDto){
        userService.save(userRegistrationDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
