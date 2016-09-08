package com.maxtuzz.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.maxtuzz.domain.entities.User;

import java.util.List;

/**
 * Main chef 'user' repository for data level access
 * Author: Max Tuzzolino
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByNameContainingIgnoreCase(String name);

    default List<User> findChefs(String name) {
        return findByNameContainingIgnoreCase(name);
    }

    // One-to-one matching
    User findByEmail(String email);
}
