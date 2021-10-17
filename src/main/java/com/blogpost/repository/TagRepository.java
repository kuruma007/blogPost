package com.blogpost.repository;

import com.blogpost.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Transactional
    @Query(value = "SELECT t FROM Tag t WHERE t.name LIKE %?1%")
    Tag findTagWithName(@Param("name") String name);
}
