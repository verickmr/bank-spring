package org.example.exeption;

public class BusinessRuleException extends RuntimeException{
    public BusinessRuleException(String message) {
        super(message);
    }

}
