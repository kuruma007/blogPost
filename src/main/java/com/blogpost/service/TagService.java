package com.blogpost.service;

import com.blogpost.model.Tag;
import com.blogpost.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public boolean checkTagWithName(String tagName){
        Tag tags = tagRepository.findTagWithName(tagName);
        if(tags != null){
            return true;
        }
        return false;
    }

    public Tag getTagByName(String name){
        return tagRepository.findTagWithName(name);
    }

    public List<Tag> allTag() {
        return tagRepository.findAll();
    }

    public void save(Tag tag) {
        tagRepository.save(tag);
    }

    public Tag get(Long id) {
        return tagRepository.findById(id).get();
    }

    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    public void saveTag(Tag tag){
        tagRepository.save(tag);
    }

    public  List<Tag> findTagById(List<Long> tagId){
        List<Tag> tags = new ArrayList<>();
        for(Long tagIds : tagId){
            tags.add(tagRepository.getOne(tagIds));
        }
        return  tags;
    }
}
