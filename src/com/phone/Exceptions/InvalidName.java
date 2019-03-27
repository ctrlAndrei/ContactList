package com.phone.Exceptions;

public class InvalidName extends Exception {
    public InvalidName(){
        super("should contain characters");
    }
}
