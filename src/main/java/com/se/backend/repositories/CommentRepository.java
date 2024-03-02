package com.se.backend.repositories;

import com.se.backend.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
//    @Query("SELECT new com.se.backend.projection.CommentDTO(c) FROM Comment c")
//    List<CommentDTO> findAllDTO();
}
