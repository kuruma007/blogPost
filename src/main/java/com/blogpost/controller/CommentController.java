package com.blogpost.controller;

import com.blogpost.model.*;
import com.blogpost.service.CommentService;
import com.blogpost.service.UserService;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostController postController;

    @RequestMapping(value = "/savecomment/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> saveComment(@PathVariable("id") Long postId,
                              @RequestBody Comment comment,
                              Model model) {
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
        comment.setPostId(postId);
        commentService.saveComment(comment);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/comment/edit/{commentId}")
    public String editComment(@PathVariable("commentId") Long commentId,
                              @RequestParam("postId") Long postId,
                              Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("editComment", comment);
        return postController.getSelectedPost(postId, model);
    }

    @PutMapping("/update/{commentId}/{postId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId,
                                @PathVariable("postId") Long postId,
                                @RequestBody Comment comment,
                                Model model) {
        User user = userService.getCurrentUser();
        comment.setId(commentService.getCommentById(commentId).getId());
        Long id = comment.getPostId();
        comment.setUpdatedAt(Timestamp.from(Instant.now()).toString());

        commentService.createComment(comment);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteComment/{commentId}/{postId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId,
                                                         @PathVariable("postId") Long postId,
                                                         Model model) {

        commentService.deleteCommentByPostId(commentId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
