package com.telegram.utility;

public class textFieldValidation {
    public static boolean checkNullAndNonEmpty(String text){
        return text == null || text.length() == 0;
    }
}
