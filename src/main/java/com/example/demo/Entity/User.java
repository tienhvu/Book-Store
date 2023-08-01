package com.example.demo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Size(min = 2, max = 30,message = "Họ tên phải có tối thiểu 2 ký tự!")
    private String name;
    @Column(nullable = false)
    @Size(min = 6, max = 15, message = "Tên đăng nhập phải từ 6 đến 15 ký tự!")
    private String username;
    @Column(nullable = false,length = 30)
    @Email
    private String email;
    @Column(nullable = false)
    @Size(min = 6, max = 15,message = "Mật khẩu phải từ 6 đến 15 ký tự!")
    private String password;
    @Column(nullable = false)
    @Size(min = 10, max = 15,message = "Số điện thoại cần tối thiểu 10 chữ số!")
    private String phonenumber;
    @Column
    private String role;



    public User() {
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getRole() {
        return role;
    }

}
