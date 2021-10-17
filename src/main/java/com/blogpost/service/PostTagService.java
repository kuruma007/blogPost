package com.blogpost.service;

import com.blogpost.model.PostTags;
import com.blogpost.model.Tag;
import com.blogpost.repository.PostTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostTagService {
    @Autowired
    private PostTagRepository postTagRepository;

    public List<PostTags> findPostTagsByTags(List<Tag> tags){
        List<PostTags> postTags = new ArrayList<>();
        for(Tag tag : tags){
            postTags.addAll(postTagRepository.findPostIdByTagId(tag.getId()));
        }
        return postTags;
    }
}
