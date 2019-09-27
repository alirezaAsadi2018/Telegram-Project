package com.telegram.controllers;

import org.junit.Test;

public class TestMainController {
    @Test
    public void testMain(){
        System.out.println(this.getClass().getResource("fxmls/" + "firstPage" + ".fxml"));
    }
}
