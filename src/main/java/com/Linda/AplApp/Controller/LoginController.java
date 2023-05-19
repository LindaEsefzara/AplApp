package com.Linda.AplApp.Controller;

import com.Linda.AplApp.Configuration.WebMvcConfig;
import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Entity.UserRoles;
import com.Linda.AplApp.Repository.UserRepository;
import com.Linda.AplApp.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping("/user")
public class LoginController {

    private final UserRepository userRepository;
    private final  WebMvcConfig webMvcConfig;
    private final UserService userService;

    public LoginController(UserRepository userRepository, WebMvcConfig webMvcConfig, UserService userService) {
        this.userRepository = userRepository;
        this.webMvcConfig = webMvcConfig;
        this.userService = userService;
    }


    @GetMapping(value="/login")
    public String login() {
        return "login.html";
    }
   /*@PostMapping(value = "/login")
    public String loginUser(){
        return "teacher/teacherHome.html";
    }*/

    @GetMapping(value="/logout")
    public String logout() {
        return "logout.html";
    }

    @GetMapping("/register")
    public String displayRegisterUser(User user) {

        return "register.html";
    }

    @PostMapping("/register")
    public String registerUser(User user, BindingResult result, Model model) {

        if (result.hasErrors()) {

            return "register";
        }

        String role = String.valueOf(user.getAuthorities());

        switch (role) {
            case "Admin" ->  user.setAuthorities(UserRoles.ADMIN.getGrantedAuthorities());

            case "User" ->  user.setAuthorities(UserRoles.ADMIN.getGrantedAuthorities());
        }
        user.setUserName(user.getUserName());
        user.setEmail(user.getEmail());
        user.setPassword(webMvcConfig.bCryptPasswordEncoder().encode(user.getPassword()));
        user.setAuthorities(user.getAuthorities());
        user.setGender(user.getGender());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        // IF no errors
        System.out.println(user);
        userRepository.save(user);
        //model.addAttribute("user", userModel);

        return "login.html";
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> showUsers() {
        return userService.showUsers();
    }

    @DeleteMapping("/id")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.findById(id);
    }

    @PutMapping("/id")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody final User user) {
        return userService.updateUser(id, user);
    }
}
