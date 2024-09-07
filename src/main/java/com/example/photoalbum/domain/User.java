package com.example.photoalbum.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user",schema = "photo_album",uniqueConstraints = {@UniqueConstraint(columnNames = "user_id")})
public class User {
    @Id
    @Column(name = "user_id",unique = true, nullable = false)
    private String userId;

    @Column(name = "user_name",unique = false, nullable = false)
    private String userName;

    @Column(name = "password",unique = false, nullable = false)
    private String password;

    @Column(name = "email",unique = false, nullable = true)
    private String email;

    @Column(name = "created_at",unique = false, nullable = true)
    @CreationTimestamp
    private Date created_at;

    @Column(name = "login_at", unique = false, nullable = true)
    private LocalDateTime login_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();
    public User(){

    }

    public void updateLoginTime(){
        this.login_at = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getLogin_at() {
        return login_at;
    }

    public void setLogin_at(LocalDateTime login_at) {
        this.login_at = login_at;
    }
}
