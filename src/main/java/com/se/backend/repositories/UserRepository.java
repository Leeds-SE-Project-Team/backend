package com.se.backend.repositories;


import com.se.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
//    @Query("SELECT new com.se.backend.projection.UserDTO(u) FROM User u")
//    List<CommentDTO> findAllDTO();
}
