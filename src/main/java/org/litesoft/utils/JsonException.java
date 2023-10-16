package org.litesoft.utils;

/**
 * Runtime exception for wrapping a Json Checked Exception.
 */
public class JsonException extends RuntimeException {
    public JsonException( Exception cause ) {
        super( cause );
    }
}
