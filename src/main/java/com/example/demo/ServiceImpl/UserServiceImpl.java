package com.example.demo.ServiceImpl;

import com.example.demo.Entity.Book;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findUserByUsername(String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        return userOptional.orElse(null);
    }

    @Override
    public Boolean checkPasswordUser(String email, String password) {
        User user = userRepo.findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public Boolean checkUserByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public Boolean checkRole(String role) {
        if (role != null && role.equals("1")) {
            return true;  // admin
        } else {
            return false; // user
        }

    }

    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserById(long id) {
        Optional<User> optional = userRepo.findById(id);
        User user = null;
        if(optional.isPresent()){
           user = optional.get();
        }else{
            throw new RuntimeException("Không tìm thấy user với id :: "+ id);
        }
        return user;

    }


}

