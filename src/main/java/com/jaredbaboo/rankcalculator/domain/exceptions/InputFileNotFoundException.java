package com.jaredbaboo.rankcalculator.domain.exceptions;

import java.io.IOException;

public class InputFileNotFoundException extends RuntimeException{
    public InputFileNotFoundException(String s, IOException e) {
        super(s, e);
    }
}
