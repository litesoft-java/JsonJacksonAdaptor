package org.litesoft.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonJsonTest {
    @Test
    void forceNLonly_LF() {
        String input = "this\ntest\n";
        assertSame(input, JacksonJson.forceNLonly( JacksonJson.NEW_LINE_LF, input ));
    }

    @Test
    void forceNLonly_CR() {
        String input = "this\rtest\rtail\r";
        String rtput = "this\ntest\ntail\n";
        assertEquals(rtput, JacksonJson.forceNLonly( JacksonJson.NEW_LINE_CR, input ));
    }

    @Test
    void forceNLonly_CRLF() {
        String input = "this\r\ntest\r\ntail\r";
        String rtput = "this\ntest\ntail";
        assertEquals(rtput, JacksonJson.forceNLonly( JacksonJson.NEW_LINE_CRLF, input ));
    }
}