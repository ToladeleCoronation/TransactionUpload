package com.coronation.upload.exception;

/**
 * Created by Toyin on 4/9/19.
 */
public class DataEncryptionException extends Exception {
    public DataEncryptionException(String message) {
        super(message);
    }

    public DataEncryptionException(Throwable cause) {
        super(cause);
    }
}
