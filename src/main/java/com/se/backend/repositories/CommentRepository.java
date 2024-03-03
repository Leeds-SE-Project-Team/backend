package com.se.backend.repositories;

import com.se.backend.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    //    @Query("SELECT new com.se.backend.projection.CommentDTO(c) FROM Comment c")
//    List<CommentDTO> findAllDTO();
//    @Query("SELECT new com.se.backend.projection.CommentDTO(c) FROM Comment c WHERE c.parent is null")
//    List<Comment> findAllDTO();
    @Query("SELECT c FROM Comment c WHERE c.tour.id = ?1")
    List<Comment> findAllByTourId(Long id);
}
