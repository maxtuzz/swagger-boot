package com.maxtuzz.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * User 'user' entity
 * Author: Max Tuzzolino
 */

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Length(max = 50)
    @Transient
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String passwordHash;

    @NotNull
    private String name;

    protected User() { }

    public User(String email,
                String password,
                String name) {

        this.email = email;
        this.name = name;
        this.setPassword(password);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public void setEmail(String email) {
        // we want to save emails as lower case
        this.email = email.toLowerCase();
    }

    // prevent the or password from being sent in responses
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    // allow password input
    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
        // Generate a password hash to save in the DB
        this.passwordHash = BCrypt.hashpw(getPassword(), BCrypt.gensalt());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compare the input plain text password with our saved hash
     * @param password  the plain text password from the user
     * @return  does the password match
     */
    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.passwordHash);
    }
}