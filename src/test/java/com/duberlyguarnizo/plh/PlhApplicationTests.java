package com.duberlyguarnizo.plh;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class PlhApplicationTests {

    @Test
    @DisplayName("SB Context is not null")
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }

}
