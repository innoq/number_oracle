package com.innoq.numbergame.baseapi;

@SuppressWarnings("serial")
public class BadAttemptException extends Exception {
    public BadAttemptException(String mes) {
        super(mes);
    }
}
