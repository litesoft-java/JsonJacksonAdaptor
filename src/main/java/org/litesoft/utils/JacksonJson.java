package org.litesoft.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson Json support that ensures that the <code>JavaTimeModule</code> is included!
 */
@SuppressWarnings("unused")
public class JacksonJson {
    /**
     * <code>OBJECT_MAPPER</code> field gives raw access to the <code>JavaTimeModule</code>
     * enhanced <code>ObjectMapper</code>
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule( new JavaTimeModule() );
    private static final ObjectWriter PRETTY_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    /**
     * Proxy for <code>ObjectMapper.readValue( String, class )</code> that uses the
     * <code>JavaTimeModule</code> enhanced <code>ObjectMapper</code> and converts
     * the Checked Exception to a <code>JsonException</code> runtime exception.
     */
    public static <T> T readValue( String content, Class<T> valueType ) {
        try {
            return OBJECT_MAPPER.readValue( content, valueType );
        }
        catch ( Exception e ) {
            throw new JsonException( e );
        }
    }

    /**
     * Proxy for <code>ObjectMapper.writeValueAsString( Object )</code> that
     * produces <code>compact</code> form; using the <code>JavaTimeModule</code>
     * enhanced <code>ObjectMapper</code> and converts the Checked Exception to
     * a <code>JsonException</code> runtime exception.
     */
    public static String compactWriteValueAsString( Object value ) {
        return writeValueAsString( compactWriter(), value );
    }

    /**
     * Proxy for <code>ObjectMapper.writeValueAsString( Object )</code> that
     * produces <code>pretty</code> form; using the <code>JavaTimeModule</code>
     * enhanced <code>ObjectMapper</code> and converts the Checked Exception to
     * a <code>JsonException</code> runtime exception.
     */
    public static String prettyWriteValueAsString( Object value ) {
        return writeValueAsString( prettyWriter(), value );
    }

    /**
     * @return non-Null <code>ObjectWriter</code> for 'compact' output (w/o pretty printing).
     */
    public static ObjectWriter compactWriter() {
        return OBJECT_MAPPER.writer();
    }

    /**
     * @return non-Null <code>ObjectWriter</code> for 'pretty' output (with pretty printing).
     */
    public static ObjectWriter prettyWriter() {
        return PRETTY_WRITER;
    }

    /**
     * @return non-Null <code>ObjectReader</code>.
     */
    public static ObjectReader reader() {
        return OBJECT_MAPPER.reader();
    }

    private static String writeValueAsString( ObjectWriter writer, Object value ) {
        try {
            return writer.writeValueAsString( value );
        }
        catch ( Exception e ) {
            throw new JsonException( e );
        }
    }
}
