package com.blogpost.repository;

import com.blogpost.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT DISTINCT p FROM Post p " +
            "WHERE p.title LIKE %?1%"+"OR p.content LIKE %?1%"+
            "OR p.author LIKE %?1%")
    Page<Post> findAll(String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM Post p WHERE p.published_at = ?1",nativeQuery = true)
    List<Post> findPostByPublishedAt(String publishedDate);

    Collection<? extends Long> getPostByAuthorId(Long authorId);

    @Query(value = "SELECT * FROM Post p WHERE p.published_at = ?1",nativeQuery = true)
    List<Post> findAllPostById(Long postIds);
}
