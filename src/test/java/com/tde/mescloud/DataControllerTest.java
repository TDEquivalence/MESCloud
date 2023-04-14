package com.tde.mescloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DataControllerTest {

    @Autowired
    DataController dataController;

    @Test
    void healh() {
        assertEquals("HEALTH CHECK OK!", dataController.healthCheck());
    }
}
