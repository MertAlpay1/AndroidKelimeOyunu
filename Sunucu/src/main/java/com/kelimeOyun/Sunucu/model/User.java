
package com.kelimeOyun.Sunucu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int totalGames;

    @Column(nullable = false)
    private int wonGames;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    
    
    
    /*
    package com.kelimeOyun.Sunucu.model;

public class LoginResponse {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

    */

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public int getWonGames() {
        return wonGames;
    }

    public void setWonGames(int wonGames) {
        this.wonGames = wonGames;
    }
    
    
    
    
    
}
