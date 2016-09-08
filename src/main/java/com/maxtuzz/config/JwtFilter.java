package com.maxtuzz.config;

import com.maxtuzz.domain.entities.User;
import com.maxtuzz.domain.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt security layer to filter requests with or without access tokens
 * Created by maxtuzz on 22/06/16.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER = "bearer ";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.toLowerCase().startsWith(BEARER)) {

            try {
                String token = authHeader.substring(BEARER.length());
                String userId = Jwts.parser()
                        .setSigningKey(jwtSecret)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();

                // Get user from token details
                User user = userRepository.findOne(Long.parseLong(userId));

                if (user == null)
                    throw new SecurityException("Unauthorized");

                // Set attribute to user details
                req.setAttribute("user", user);

            } catch (Exception e) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Continue filter chain
            chain.doFilter(req, res);
        } else if (req.getMethod().equals("OPTIONS")) {
            chain.doFilter(req, res);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Define which paths should be ignored by this filter
        return request.getRequestURI().startsWith("/auth")
                || request.getRequestURI().startsWith("/user/exists")
                || request.getRequestURI().startsWith("/swagger")
                || request.getRequestURI().startsWith("/webjars")
                || request.getRequestURI().startsWith("/v2");
    }
}
