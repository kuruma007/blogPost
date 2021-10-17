package com.blogpost.service;

import com.blogpost.model.Post;
import com.blogpost.repository.PostRepository;
import com.blogpost.repository.PostTagRepository;
import com.blogpost.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostTagRepository postTagRepository;

    public List<Post> allPost() {
        return postRepository.findAll();
    }

    public void savePost(Post post) {
        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()).toString());
        postRepository.save(post);
    }

    public Post get(Long id) {
        return postRepository.findById(id).get();
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    public Post getPostById(Long id) {
        return postRepository.getById(id);
    }

    public Page<Post> viewPostWithPagination(int pageNo, int pageSize, String sortField, String sortDirection,
                                            String keyword) {
       Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
               Sort.by(sortField).ascending() : Sort.by(sortField).descending() ;
       Pageable pageable =  PageRequest.of(pageNo -1, pageSize, sort);
       if(keyword != null){
           return  postRepository.findAll(keyword,pageable);
       }
       return postRepository.findAll(pageable);
   }

    public List<Post> filterPosts(Optional<Long[]> authorId,
                                  Optional<Long[]> tagId,
                                  Optional<String> date) throws ParseException {
        List<Long> authorIds;
        List<Long> tagIds;
        List<Post> posts = new ArrayList<>();
        String postDate = "";

        if(date.isPresent()){
            postDate = date.get();
        }

        if (authorId.isPresent() && tagId.isPresent()) {
            authorIds = Arrays.asList(authorId.get());
            tagIds = Arrays.asList(tagId.get());
            List<Long> postsByTagId = getAllPostIdByTagIds(tagIds);
            List<Long> postIdByAuthorId = getPostIdByAuthor(authorIds);
            List<Long> postIds = new ArrayList<>();

            for (Long postId : postsByTagId) {
                if (postIdByAuthorId.contains(postId)) {
                    postIds.add(postId);
                }
            }
            posts = getPostByPostIds(postIds, postDate);

        } else {
            if (authorId.isPresent()) {
                authorIds = Arrays.asList(authorId.get());
                List<Long> postIdByAuthorId = getPostIdByAuthor(authorIds);
                posts = getPostByPostIds(postIdByAuthorId, postDate);

            } else if (tagId.isPresent()) {
                tagIds = Arrays.asList(tagId.get());
                List<Long> postsByTagId = getAllPostIdByTagIds(tagIds);
                posts = getPostByPostIds(postsByTagId, postDate);
            } else {
                if(postDate != ""){
                    posts = postRepository.findPostByPublishedAt(postDate);
                }
            }
        }
        return posts;
    }

    public List<Long> getAllPostIdByTagIds(List<Long> tagIds) {
        List<Long> postId = new ArrayList<>();
        for (Long tagId : tagIds) {
            postId.addAll(postTagRepository.getPostIdByTagId(tagId));
        }
        return postId;
    }

    public List<Long> getPostIdByAuthor(List<Long> authorIds) {
        List<Long> postIds = new ArrayList<>();
        for (Long authorId : authorIds) {
            postIds.addAll(postRepository.getPostByAuthorId(authorId));
        }
        return postIds;
    }

    public List<Post> getPostByPostIds(List<Long> postIds ,String dateString) throws ParseException {
        Set<Post> postSet = new HashSet<>();
        if(!dateString.equals("")) {
            List<Post> posts = postRepository.findPostByPublishedAt(dateString);
            if(posts!=null){
                for(Post post:posts){
                    postSet.add(post);
                }
            }
        }
        List<Post> postList = getAllPostsById(postIds);
        if(postList!=null){
            for(Post post:postList){
                postSet.add(post);
            }
        }

        if(postSet!=null){
            for(Post post:postSet){
                postList.add(post);
            }
        }
        return postList;
    }

    public List<Post> getAllPostsById(List<Long> postIds){
        Set<Post>  postSet = new HashSet<>();
        if(postIds!=null){
            for(Long id:postIds){
                List<Post> posts = postRepository.findAllPostById(id);
                if(posts != null){
                    for(Post post:posts){
                        postSet.add(post);
                    }
                }
            }
        }
        List<Post> posts = new ArrayList<>();
        if(postSet!=null){
            for(Post post:postSet){
                posts.add(post);
            }
        }
        return  posts;
    }
}
