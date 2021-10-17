package com.blogpost.repository;

import com.blogpost.model.PostTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTags,Long> {

    @Query("SELECT pt.postId FROM PostTags pt WHERE pt.tagId = ?1")
    List<PostTags> findPostIdByTagId(Long tagId);

    @Query("SELECT pt.postId FROM PostTags pt WHERE pt.tagId = ?1")
    List<Long> getPostIdByTagId(Long tagId);
}
