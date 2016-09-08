package com.maxtuzz.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.maxtuzz.domain.entities.User;
import com.maxtuzz.domain.repositories.UserRepository;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Authentication requests
 * Author: Max Tuzzolino
 */

@RestController
@RequestMapping("/auth")
@Api(tags = "Authentication", description = "Registration and Login APIs")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @ApiOperation(value = "Register",
            notes = "This api will create a new account, and issue a new JWT client to be returned to user.",
            response = TokenResponse.class)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.save(new User(
                req.email.toLowerCase(),
                req.password,
                req.name)
        );

        return new ResponseEntity<>(new TokenResponse(generateToken(user.getId())), HttpStatus.OK);
    }


    @ApiOperation(value = "Login",
            notes = "Issue a token for an already registered user.",
            response = TokenResponse.class)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(req.email);

        if (user == null || !user.authenticate(req.password)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new TokenResponse(generateToken(user.getId())), HttpStatus.OK);
    }

    /**
     * Sign chef identifier
     */
    private String generateToken(Long id) {
        return Jwts.builder()
                .setSubject(Long.toString(id))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * TokenResponse helper class
     */
    private static final class TokenResponse {
        private final String token;

        public TokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }

    /**
     * LoginRequest class model
     */
    private static final class LoginRequest {
        @NotNull
        private String email;
        @NotNull
        public String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email.toLowerCase();
        }
    }

    /**
     * Register request class model
     */
    private static final class RegisterRequest {
        @NotNull
        public String email;
        @NotNull
        public String name;
        @NotNull
        public String password;
    }
}

