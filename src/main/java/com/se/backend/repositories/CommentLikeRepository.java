package com.se.backend.repositories;

import com.se.backend.models.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, JpaSpecificationExecutor<CommentLike> {
    List<CommentLike> findAllByCommentId(Long commentId);

    List<CommentLike> findAllByUserId(Long userId);

    List<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);
}
