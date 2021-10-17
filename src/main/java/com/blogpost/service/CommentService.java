package com.blogpost.service;

import com.blogpost.model.Comment;
import com.blogpost.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void saveComment(Comment comment){
        commentRepository.save(comment);
    }

    public List<Comment> findAllByPostId(Long postId){
        return commentRepository.findAllByPostId(postId);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public Comment get(Long id) {
        return commentRepository.findById(id).get();
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.getById(commentId);
    }

    public void deleteCommentByPostId(Long postId) {
        commentRepository.deleteById(postId);
    }

    public Comment getCommentById(Long commentId) {
       return commentRepository.getCommentById(commentId);
    }

    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }
}
