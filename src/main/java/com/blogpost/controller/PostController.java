package com.blogpost.controller;

import com.blogpost.model.*;
import com.blogpost.repository.CommentRepository;
import com.blogpost.service.*;
import com.blogpost.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {
    public static final Integer Post_PER_PAGE = 10;

    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostTagService postTagService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @GetMapping("/login")
    public String blogPost(){
        return "login";
    }

    @RequestMapping("/")
    public ResponseEntity<Page<Post>> viewHomePage(@Param("keyword") String keyword, Model model) {
        return new ResponseEntity<>(findPostsUsingPagination(1, "publishedAt",
                "asc", keyword, model), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public String getSelectedPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        User user = userService.getCurrentUser();
        List<Comment> listComment = commentRepository.getCommentByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("listComment", listComment);
        model.addAttribute("user", user);

        return "post";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthentication(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception{
        try {
            authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        }catch (BadCredentialsException badCredentialsException){
            throw new Exception("Incorrect email or password");
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getEmail());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping(value = "/save")
    public ResponseEntity<?> savePost(@RequestBody PostAndTag postAndTag,
                                      PostTags postTags) {
        Post post = postAndTag.getPost();
        List<Tag> tags = postAndTag.getTags();
        try {
            post.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
            post.setPublishedAt(new Timestamp(System.currentTimeMillis()).toString());
            post.setIsPublished(true);
            post.setAuthor("user.getName()");

            int contentLength = (post.getContent().length());
            if (contentLength < 50) {
                post.setExcerpt(post.getContent().substring(0, contentLength / 2));
            } else {
                post.setExcerpt(post.getContent());
            }
            List<Tag> tagList = tags;
            for (Tag tag : tagList) {
                Tag tagObject = new Tag();
                if (tagService.checkTagWithName(tag.getName())) {
                    tagList.add(tagService.getTagByName(tag.getName()));
                } else {
                    tagObject.setName(tag.getName());
                    tagObject.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
                    tagList.add(tagObject);
                }
           }
            post.setTags(tagList);
            postService.savePost(post);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        }catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@PathVariable(name = "id") Long id, @RequestBody Post post){
        post.setId(postService.get(id).getId());

        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()).toString());

        int contentLength =  (post.getContent().length()) ;
        if(contentLength < 50){
            post.setExcerpt(post.getContent().substring(0,contentLength/2));
        }
        else{
            post.setExcerpt(post.getContent().substring(0,50));
        }
        postService.savePost(post);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping(name = "/post/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<PostAndComments> openPost(@PathVariable(name = "id") Long id, Model model){
        try {
            Post post = postService.getPostById(id);
            List<Comment> commentList = commentService.findAllByPostId(id);

            PostAndComments postAndComments = new PostAndComments(post, commentList);

            return new ResponseEntity<>(postAndComments, HttpStatus.OK);
        }catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView shownEditPostForm(@PathVariable(name = "id") Long id) {
        ModelAndView modalAndView = new ModelAndView("editpost");
        Post post = postService.get(id);
        modalAndView.addObject("post", post);
        return modalAndView;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable(name = "id") Long id) {
        postService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/filter")
    public List<Post> getFilteredPosts(@RequestParam Optional<Long[]> authorId,
                                   @RequestParam Optional<Long[]> tagId,
                                   @RequestParam Optional<String> date,
                                   @Param("keyword") String keyword,
                                   Model model) throws ParseException {
        List<Post> posts = null;

        try {
            posts = this.postService.filterPosts(authorId, tagId, date);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("No post found!!");
        }
        findPostsUsingPagination(1, "publishedAt","asc", keyword, model);
        model.addAttribute("posts", posts);
        return postService.filterPosts(authorId, tagId, date);
    }

    @GetMapping("/page/{pageNo}")
    public Page<Post> findPostsUsingPagination(@PathVariable (value = "pageNo") int pageNo,
                         @RequestParam(value = "sortField", defaultValue="publishedAt") String sortField ,
                         @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                         @Param("keyword") String keyword,
                         Model model){
        Page<Post> page = postService.viewPostWithPagination(pageNo, Post_PER_PAGE, sortField, sortDirection, keyword);
        List<Post> postList = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("postList", postList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("users",userService.getAllUser());
        model.addAttribute("tags",tagService.allTag());
        return postService.viewPostWithPagination(pageNo, Post_PER_PAGE, sortField, sortDirection, keyword);
    }

//    public static boolean hasRole(String roleName) {
//        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
//                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
//    }
}
