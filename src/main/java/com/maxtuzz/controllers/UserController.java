package com.maxtuzz.controllers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.maxtuzz.domain.entities.User;
import com.maxtuzz.domain.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User Controller
 * Author: Max Tuzzolino
 */

@RestController
@RequestMapping("/user")
@Api(tags = "User", description = "User APIs")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @ApiOperation(value = "Get chef details",
            notes = "Simply returns a small payload of User information based on their user identity number.",
            response = User.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable long id) {
        User user = userRepository.findOne(id);

        return (user != null)
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>("User with identifier " + id + " not found.", HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Logged in User details",
            notes = "Returns a the information of logged in user (from JWT).",
            response = User.class)
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<?> getByToken(HttpServletRequest request) {
        User user = (User)request.getAttribute("user");

        return (user != null)
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Check if email exists",
            notes = "Returns a boolean value true or false if a email exists or not. " +
                    "Used to check if a email is taken or not during the login process.",
            response = EmailExistsResponse.class)
    @RequestMapping(value = "/exists", method = RequestMethod.GET)
    public ResponseEntity<?> emailCheck(@ApiParam("User's email address") @RequestParam(value = "email") String email) {
        User user = userRepository.findByEmail(email.toLowerCase());
        return new ResponseEntity<>(new EmailExistsResponse(user != null), HttpStatus.OK);
    }

    @ApiOperation(value = "Search for user",
            notes = "This API will search and return user objects based on input param. The search is case insensitive.",
            response = User[].class)
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> search(@ApiParam("User name search query") @RequestParam(value="name") String name) {
        List<User> users = userRepository.findChefs(name);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Email exists response class
     */
    private static class EmailExistsResponse {
        private boolean exists;

        EmailExistsResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }
    }
}
