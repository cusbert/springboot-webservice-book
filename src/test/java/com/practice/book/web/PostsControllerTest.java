package com.practice.book.web;

import com.practice.book.domain.posts.Posts;
import com.practice.book.domain.posts.PostsRepository;
import com.practice.book.web.dto.PostsSaveRequestDto;
import com.practice.book.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsControllerTest {

    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String AUTHOR = "AUTHOR";

    public static final String EXPECTED_TITLE = "TITLE2";
    public static final String EXPECTED_CONTENT = "CONTENT2";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Post_등록_성공() {

        // given
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(CONTENT);

    }

    @Test
    public void Post_수정_성공() throws Exception {

        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build());

        Long updatedId = savedPosts.getId();

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(EXPECTED_TITLE)
                .content(EXPECTED_CONTENT)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updatedId;
        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(EXPECTED_TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(EXPECTED_CONTENT);
    }

    @Test
    public void Post_상세정보_조회_성공() throws Exception {

        // given
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build());

        Long updatedId = savedPosts.getId();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updatedId;

        // when
        ResponseEntity<String> responseEntity =  restTemplate.getForEntity(url, String.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(CONTENT);
    }
}