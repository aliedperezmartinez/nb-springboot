package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

/**
 * Syntax test suite for BootCfgParser: intermediate scenario.
 *
 * @author Alessandro Falappa
 */
//@Ignore
public class IntermediateTest extends TestBase {

    @Test
    public void testEmptyLines() throws URISyntaxException, IOException {
        parseMatch("""


                   \t
                   \f""");
    }

    @Test
    public void testCommentLines() throws URISyntaxException, IOException {
        parseMatch("""
                   # pound sign comment
                   ! exclamation mark comment
                    \t # pound sign with initial whitespace
                     \t! exclamation mark with initial whitespace""");
    }

    @Test
    public void testCommentAndEmptyLines() throws URISyntaxException, IOException {
        parseMatch("""
                   # pound sign comment
                   
                   ! exclamation mark comment
                   """);
    }

    @Test
    public void testKeysOnly() throws URISyntaxException, IOException {
        parseMatch("""
                   key1
                   key2""");
    }

    @Test
    public void testSpaceValue() throws URISyntaxException, IOException {
        parseMatch("key=val with spaces");
    }

    @Test
    public void testTabKey() throws URISyntaxException, IOException {
        parseMatch("key\\twith\\ttabs=val");
    }

    @Test
    public void testTabValue() throws URISyntaxException, IOException {
        parseMatch("key=val\\twith\\ttabs");
    }

    @Test
    public void testLinefeedKey() throws URISyntaxException, IOException {
        parseMatch("key\\nwith\\nlinefeeds=val");
    }

    @Test
    public void testLinefeedValue() throws URISyntaxException, IOException {
        parseMatch("key=val\\nwith\\nlinefeeds");
    }

    @Test
    public void testEscapedSpaceKey() throws URISyntaxException, IOException {
        parseMatch("key\\ with\\ spaces=val");
    }

    @Test
    public void testEscapedSpaceValue() throws URISyntaxException, IOException {
        parseMatch("key=val\\ with\\ spaces");
    }

    @Test
    public void testEscapedBackslashKey() throws URISyntaxException, IOException {
        parseMatch("key\\\\with\\\\slashes=val");
    }

    @Test
    public void testEscapedBackslashValue() throws URISyntaxException, IOException {
        parseMatch("key=val\\\\with\\\\slashes");
    }

    @Test
    public void testEscapedCommentKey() throws URISyntaxException, IOException {
        parseMatch("key\\#with\\!comment=value");
    }

    @Test
    public void testEscapedCommentValue() throws URISyntaxException, IOException {
        parseMatch("key=value\\#with\\!escaped comment signs");
    }

    @Test
    public void testEscapedEqualKey() throws URISyntaxException, IOException {
        parseMatch("a\\=key=val");
    }

    @Test
    public void testEscapedEqualValue() throws URISyntaxException, IOException {
        parseMatch("key=a\\=val");
    }

    @Test
    public void testEqualValue() throws URISyntaxException, IOException {
        parseMatch("key=a=val");
    }

    @Test
    public void testEscapedColonKey() throws URISyntaxException, IOException {
        parseMatch("a\\:key:val");
    }

    @Test
    public void testEscapedColonValue() throws URISyntaxException, IOException {
        parseMatch("key:a\\:val");
    }

    @Test
    public void testColonValue() throws URISyntaxException, IOException {
        parseMatch("key:a:val");
    }

    @Test
    public void testUnicodeKey() throws URISyntaxException, IOException {
        parseMatch("key\\u00a9=value");
    }

    @Test
    public void testUnicodeValue() throws URISyntaxException, IOException {
        parseMatch("key=value\\u00A9");
    }

    @Test
    public void testUnusualValue() throws URISyntaxException, IOException {
        parseMatch("key=[({+})]£$%&@-_^\"*/'`|;,.`ç°§*<>");
    }

    @Test
    public void testUnusualKey() throws URISyntaxException, IOException {
        parseMatch("({+})£$%&@-_^\"*/'`|;,`ç°§*<>=value");
    }

    @Test
    public void testMultipleMixedSep() throws URISyntaxException, IOException {
        parseMatch("""
                   key1:val1
                   key2=val2""");
    }

    @Test
    public void testArrayNotation() throws URISyntaxException, IOException {
        parseMatch(" \t array[12] =\tval1");
    }

    @Test
    public void testMultipleArrayNotation() throws URISyntaxException, IOException {
        parseMatch("""
                    \t array[0] =\tval1
                    prefix.array[1]=val2""");
    }

    @Test
    public void testMapNotation() throws URISyntaxException, IOException {
        parseMatch(" \t map[key] =\tval1");
    }

    @Test
    public void testMultipleMapNotation() throws URISyntaxException, IOException {
        parseMatch("""
                    \t map[one] =\tval1
                    prefix.map[two]=val2""");
    }

}
