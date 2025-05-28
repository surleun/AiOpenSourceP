package com.sweetspot.server.comment.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId);
    Long countByCommentId(Long commentId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}