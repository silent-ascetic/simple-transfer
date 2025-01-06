package com.passer.simpletransfer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class SimpleTransferApplicationTests {

    @Test
    void contextLoads() {
        File file = new File("");

        System.out.println(file.getAbsolutePath());
    }

}
