package com.Linda.AplApp.Service;

import com.Linda.AplApp.Entity.RequestResponse;
import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    public void save(User user) {
        userRepository.save(user);
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
                return RequestResponse.builder().responseCode(1).data(user).message("Sorry, error encountered while saving details. Please try again").build();
            }
        } else {
            return RequestResponse.builder().responseCode(1).data(user).message("Sorry, this email address is already in use by another user. please try using another email address").build();
        }
    }
    public void saveById(long id) {
        User user = userRepository.findById(id).get();
        userRepository.save(user);
    }


    public User findById(long id){
        User user = userRepository.findById(id).get();
        return user;
    }

    public List<User> findAll(){
        return (List<User>) userRepository.findAll();
    }

    public List<User> userSearcher(String userName, String email) {
        if
        (userName != null && email != null) {
            return findAll();

        }else if
        (userName == null && email !=null) {
            return findAll();

        } else return new ArrayList<>();

    }

    public User findUserByEmail(String email) {
        return null;
    }

    public ResponseEntity<User> updateUser(Long id, User user) {
        try{
            Optional<User> userOptional = userRepository.findById(id);
            User userEntity = userOptional.get();

            userEntity.setUserName(user.getUserName());
            userEntity.setPassword(user.getPassword());
            userEntity.setEmail(user.getEmail());
            userEntity.setPhoneNumber(user.getPhoneNumber());
            userEntity.setGender(user.getGender());
            userEntity.setAuthorities(user.getAuthorities());
            userRepository.save(userEntity);
            return new ResponseEntity<>(userEntity, HttpStatus.ACCEPTED);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<User>> showUsers() {
        try {
            return ResponseEntity.ok((List<User>) this.userRepository.findAll());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName);
    }

}
