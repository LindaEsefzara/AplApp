package com.Linda.AplApp.Repository;

import com.Linda.AplApp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);
    UserDetails findByUserName(String userName);



    int countAllByRole(String role);

    List<User> findByRoleAndEmailNotOrderByIdDesc(String role, String email);

}
