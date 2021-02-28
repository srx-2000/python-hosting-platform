package com.srx.spring.development.Exception;

public class CommandException extends RuntimeException {
//    private static final long serialVersionUID = -2854427558805142493L;
    public CommandException(String message) {
        super(message);
    }
}
