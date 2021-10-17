package com.blogpost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAndTag {
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Post post;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Tag> tags;
}
