package com.blogpost.repository;

import com.blogpost.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    Comment getCommentById(Long commentId);

    @Query(value = "SELECT c FROM Comments c WHERE c.Id = ?1",nativeQuery = true)
    List<Comment> getCommentByPostId(Long postId);
}
