package com.practice.book.web;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.book.domain.posts.Posts;
import com.practice.book.domain.posts.PostsRepository;
import com.practice.book.web.dto.PostsSaveRequestDto;
import com.practice.book.web.dto.PostsUpdateRequestDto;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";
    public static final String AUTHOR = "AUTHOR";

    public static final String EXPECTED_TITLE = "TITLE2";
    public static final String EXPECTED_CONTENT = "CONTENT2";
    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void Post_등록_성공() throws Exception {

        // given
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
            .title(TITLE)
            .content(CONTENT)
            .author(AUTHOR)
            .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(requestDto)))
            .andExpect(status().isOk());

        // then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(CONTENT);

    }

    @Test
    @WithMockUser(roles = "USER")
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
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(requestDto)))
            .andExpect(status().isOk());

        // then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(EXPECTED_TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(EXPECTED_CONTENT);
    }

    @WithMockUser(roles = "USER")
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
        mockMvc.perform(get(url))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.content").value(CONTENT));
    }

}