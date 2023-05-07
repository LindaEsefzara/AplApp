package com.Linda.AplApp.Service;

import com.Linda.AplApp.Entity.RequestResponse;
import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findUserByEmail(String email){
        List<User> users = this.userRepository.findByEmail(email);
        if (users.size() == 0){
            return new ArrayList<>();
        } else {
            boolean exists = false;
            for (User user: users) {
                if (user.getEmail().equals(email)){
                    exists = true;
                }
            }
            if (exists){
                return users;
            } else {
                return new ArrayList<>();
            }
        }
    }

    public RequestResponse saveUser(User user) {
        if (this.findUserByEmail(user.getEmail()) != null && this.findUserByEmail(user.getEmail()).size() == 0){
            try {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                user.setActive(1);
                userRepository.save(user);
                return RequestResponse.builder().responseCode(0).data(user).message("success").build();
            } catch (Exception ignored){
                LoggerFactory.getLogger(LoggerFactory.class).error("SAVE USER ERROR", ignored);
                return RequestResponse.builder().responseCode(1).data(user).message("Try again something went wrong").build();
            }
        } else {
            return RequestResponse.builder().responseCode(1).data(user).message("Email adress is allready on use please try again").build();
        }
    }

    public int getUsersCount(String role){
        return userRepository.countAllByRole(role);
    }

    public List<User> getUsersByRoleNotLoggedIn(String role, String email){
        return userRepository.findByRoleAndEmailNotOrderByIdDesc(role, email);
    }

    public User getUserById(long id){
        return userRepository.getOne(id);
    }

    public RequestResponse updateUserDetails(User user){
        User userToUpdate = this.getUserById(user.getId());
        if (userToUpdate != null){
            try {
                userToUpdate.setEmail(user.getEmail());
                userToUpdate.setFirstName(user.getFirstName());
                userToUpdate.setLastName(user.getLastName());
                userRepository.save(userToUpdate);
                return RequestResponse.builder().responseCode(0).data(userToUpdate).message("success").build();
            } catch (Exception ignored){
                LoggerFactory.getLogger(LoggerFactory.class).error("UPDATE USER ERROR", ignored);
                return RequestResponse.builder().responseCode(1).data(user).message("Try again something went wrong").build();
            }
        } else {
            return RequestResponse.builder().responseCode(1).data(null).message("Try again something went wrong").build();
        }
    }

    public RequestResponse updateUserStatus(User user){
        User userToChangeStatus = this.getUserById(user.getId());
        if (userToChangeStatus != null){
            try {
                userToChangeStatus.setActive(user.getActive());
                userRepository.save(userToChangeStatus);
                return RequestResponse.builder().responseCode(0).data(userToChangeStatus).message("success").build();
            } catch (Exception ignored){
                LoggerFactory.getLogger(LoggerFactory.class).error("UPDATE USER ERROR", ignored);
                return RequestResponse.builder().responseCode(1).data(user).message("Try again something went wrong").build();
            }
        } else {
            return RequestResponse.builder().responseCode(1).data(null).message("Try again something went wrong").build();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUsername(username);
    }
}
