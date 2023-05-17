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

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void registerUser(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        userRepository.save(user);
    }
    public void save(User user) {
        userRepository.save(user);
    }

    public void saveById(long userId) {
        User user = userRepository.findById(userId).get();
        userRepository.save(user);
    }


    public User findById(long userId){
        User user = userRepository.findById(userId).get();
        return user;
    }

    public List<User> findAll(){
        return (List<User>) userRepository.findAll();
    }


    public User findUserByEmail(String email) {
        return null;
    }

    public ResponseEntity<User> updateUser(Long user_id, User user) {
        try{
            Optional<User> userOptional = userRepository.findById(user_id);
            User userEntity = userOptional.get();

            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setPassword(user.getPassword());
            userEntity.setEmail(user.getEmail());
            userEntity.setPhoneNumber(user.getPhoneNumber());
            userEntity.setGender(user.getGender());
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

    public RequestResponse saveUser(User user) {
        if (this.findUserByEmail(user.getEmail()) != null && this.findUserByEmail(user.getEmail()).size() == 0){
            try {
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

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
                userToUpdate.setPhoneNumber(user.getPhoneNumber());
                userToUpdate.setGender(user.getGender());
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
        User user = (User) userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return (UserDetails) user;
    }
}
