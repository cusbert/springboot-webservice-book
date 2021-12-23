package com.practice.book.domain.posts;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    private static final String TITLE = "TITLE";
    private static final String CONTENT = "CONTENT";
    private static final String AUTHOR = "AUTHOR";

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글_목록_조회() {

        // given
        postsRepository.save(Posts.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR).build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(TITLE);
        assertThat(posts.getContent()).isEqualTo(CONTENT);
    }

    @Test
    public void BaseTimeEntity_등록() {

        // given
        LocalDateTime now = LocalDateTime.of(2021, 12, 23, 0, 0, 0);

        postsRepository.save(Posts.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR).build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);

        System.out.println(">>> createDate = " + posts.getCreatedDate());
        System.out.println(">>> modifiedDate = " + posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);
    }
}