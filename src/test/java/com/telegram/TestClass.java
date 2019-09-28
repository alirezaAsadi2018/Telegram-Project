package com.telegram;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestClass {
    @Test
    public void testLogger() throws IOException {
        Logger logger = LoggerFactory.getLogger(TestClass.class);
        logger.error("This is Alireza");
    }
}
