package com.Linda.AplApp.Configuration;

public class FormValidationUtil {
    public boolean isEmptyInput(String input){
        if (input != null){
            input = input.trim();
            return !(!input.isEmpty() && !input.equals(" "));
        } else {
            return true;
        }
    }
}
