package com.practice.book.web.dto;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트() {

        // given
        String name = "test";
        int amount = 1000;

        // when
        HelloResponseDto dto = new HelloResponseDto(name, amount);

        // then
        assertThat(dto.getName(), equalTo(name));
        assertThat(dto.getAmount(), equalTo(amount));
    }
}