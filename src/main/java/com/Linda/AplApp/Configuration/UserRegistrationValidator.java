package com.Linda.AplApp.Configuration;

import com.Linda.AplApp.Entity.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserRegistrationValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required", "Email is required.");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", "Password is required.");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role", "field.required", "Role is required.");

            // Add additional validation logic if needed
        }
    }


