package com.example.demo.Service;

import com.example.demo.Entity.Book;
import com.example.demo.Entity.User;

import java.util.List;

public interface UserService {

    void saveUser(User user);
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    Boolean checkPasswordUser(String email, String password);
    Boolean checkUserByEmail(String email);
    Boolean checkRole(String role);
    List<User> findAllUsers();
    User getUserById(long id);
}
