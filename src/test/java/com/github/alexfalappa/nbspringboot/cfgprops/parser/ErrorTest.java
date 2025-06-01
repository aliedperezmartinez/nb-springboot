package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

/**
 * Syntax test suite for BootCfgParser: error scenario.
 *
 * @author Alessandro Falappa
 */
//@Ignore
public class ErrorTest extends TestBase {

    @Test
    public void testInvalidUnicodeKey() throws URISyntaxException, IOException {
        parseNoMatch("key\\u00g9=value");
    }

    @Test
    public void testInvalidUnicodeValue() throws URISyntaxException, IOException {
        parseNoMatch("key=value\\u0GA9");
    }

    @Test
    public void testSpaceInKey() throws URISyntaxException, IOException {
        parseNoMatch(" \t space key :\tval1");
    }

    @Test
    public void testOnlyOpeningBracket() {
        parseNoMatch(" \t key[0 :\tval1");
    }

    @Test
    public void testOnlyClosingBracket() {
        parseNoMatch(" \t key0] :\tval1");
    }
}
