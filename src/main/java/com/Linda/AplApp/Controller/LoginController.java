package com.Linda.AplApp.Controller;

import com.Linda.AplApp.Configuration.UserRegistrationValidator;
import com.Linda.AplApp.Configuration.WebMvcConfig;
import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Entity.UserRoles;
import com.Linda.AplApp.Repository.UserRepository;
import com.Linda.AplApp.Service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LoginController {

    private final UserRepository userRepository;
    private final WebMvcConfig webMvcConfig;
    private final UserService userService;
    private final UserRegistrationValidator registrationValidator;

    public LoginController(UserRepository userRepository, WebMvcConfig webMvcConfig, UserService userService,
            UserRegistrationValidator registrationValidator) {
        this.userRepository = userRepository;
        this.webMvcConfig = webMvcConfig;
        this.userService = userService;
        this.registrationValidator = registrationValidator;
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout.html";
    }

    @GetMapping("/register")
    public String registerUser() {

        return "register.html";
    }

    @CrossOrigin
    @PostMapping("/register")
    public String registerUser(@Validated User user, BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "register.html";
        }

        String role = String.valueOf(user.getAuthorities());

        switch (role) {
            case "Admin" -> user.setAuthorities((List<SimpleGrantedAuthority>) UserRoles.ADMIN.getGrantedAuthorities());

            case "User" -> user.setAuthorities((List<SimpleGrantedAuthority>) UserRoles.USER.getGrantedAuthorities());
        }
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setEmail(user.getEmail());
        user.setPassword(webMvcConfig.bCryptPasswordEncoder().encode(user.getPassword()));
        user.setAuthorities(user.getAuthorities());
        user.setGender(user.getGender());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        System.out.println(user);
        userRepository.save(user);

        return "redirect:/login";
    }

   @RequestMapping(value = "/login/success", method = RequestMethod.GET)
    public String loginSuccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            LoggerFactory.getLogger(LoginController.class).warn("AUTH :: " + auth.getAuthorities().toString());
            Object[] authorities = auth.getAuthorities().toArray();
            String authy = null;
            if (authorities.length == 1) {
                for (Object authority : authorities) {
                    authy = String.valueOf(authority);
                }
            }
            if (authy != null && authy.equals("student")) {
                return "/studentHome";
            } else if (authy != null && authy.equals("teacher")) {
                return "/teacherHome";
            }
        }

        LoggerFactory.getLogger(LoginController.class).error("AUTH ERROR :: Auth is null");
        return "/logout";
    }

   /* @GetMapping("/login/success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null) {
            String authy = authentication.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            if (authy != null) {
                if (authy.equals("USER")) {
                    return "redirect:/studentHome.html";
                } else if (authy.equals("ADMIN")) {
                    return "redirect:/teacherHome.html";
                }
            }
        }

        LoggerFactory.getLogger(LoginController.class).error("AUTH ERROR :: Auth is null");
        return "redirect:/logout";
    }*/
    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password,
            @RequestParam("role") String role) {

        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Role: " + role);

        if (role.equals("student")) {
            return "/studentHome.html";
        } else if (role.equals("teacher")) {
            return "/teacherHome.html";
        }

        return "/login";
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
