package org.litesoft.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.litesoft.annotations.PackageFriendlyForTesting;

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
     * produces <code>pretty</code> form <B>with native Line Endings, either
     * CR only (unusual), CR+LF, or LF only)</B>; using the <code>JavaTimeModule</code>
     * enhanced <code>ObjectMapper</code> and converts the Checked Exception to
     * a <code>JsonException</code> runtime exception.
     */
    public static String prettyWriteValueAsString( Object value ) {
        return writeValueAsString( prettyWriter(), value );
    }

    /**
     * Proxy for <code>ObjectMapper.writeValueAsString( Object )</code> that
     * produces <code>pretty</code> form <B>with LF only Line Endings</B>;
     * using the <code>JavaTimeModule</code> enhanced <code>ObjectMapper</code>
     * and converts the Checked Exception to a <code>JsonException</code>
     * runtime exception.
     */
    public static String prettyWriteValueAsStringOnlyNL( Object value ) {
        return forceNLonly( NEW_LINE, writeValueAsString( prettyWriter(), value ) );
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

    private final static String NEW_LINE;

    static {
        String ls;
        try {
            ls = System.getProperty( "line.separator" );
        }
        catch ( Throwable t ) {
            ls = "\n"; // fallback when security manager denies access
        }
        NEW_LINE = ls;
    }

    static final String NEW_LINE_LF = "\n";
    static final String NEW_LINE_CR = "\r";
    static final String NEW_LINE_CRLF = "\r\n";

    static final char LF = '\n';
    static final char CR = '\r';

    @PackageFriendlyForTesting
    @SuppressWarnings("SameParameterValue")
    static String forceNLonly( String osNewLine, String orig ) {
        if ( NEW_LINE_LF.equals( osNewLine ) ) {
            return orig;
        }
        if ( NEW_LINE_CR.equals( osNewLine ) ) {
            return orig.replace( CR, LF );
        }
        if ( !NEW_LINE_CRLF.equals( osNewLine ) ) {
            byte[] bytes = NEW_LINE.getBytes();
            StringBuilder sb = new StringBuilder( "Unknown new line form: " );
            char prefix = '[';
            for ( byte zByte : bytes ) {
                int entry = ((int)zByte & 255);
                sb.append( prefix ).append( entry );
                prefix = ',';
            }
            throw new Error( sb.append( ']' ).toString() );
        }
        // remove CRs
        int at = orig.indexOf( CR );
        if ( at == -1 ) {
            return orig;
        }
        StringBuilder sb = new StringBuilder( orig.length() );
        int from = 0;
        do {
            if ( at > from ) {
                sb.append( orig, from, at );
            }
            from = at + 1;
            if ( orig.length() <= from ) {
                return sb.toString();
            }
        } while ( -1 != (at = orig.indexOf( CR, from )) );
        return sb.append( orig, from, orig.length() ).toString();
    }
}
