package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

/**
 * Syntax test suite for BootCfgParser: basic scenario.
 *
 * @author Alessandro Falappa
 */
//@Ignore
public class BasicTest extends TestBase {

    @Test
    public void testEmpty() throws URISyntaxException, IOException {
        parseMatch("");
    }

    @Test
    public void testComment1() throws URISyntaxException, IOException {
        parseMatch("# pound sign comment");
    }

    @Test
    public void testComment2() throws URISyntaxException, IOException {
        parseMatch("! exclamation mark comment");
    }

    @Test
    public void testKeyOnly() throws URISyntaxException, IOException {
        parseMatch("key");
    }

    @Test
    public void testSingleEqualEmpty() throws URISyntaxException, IOException {
        parseMatch("key=");
    }

    @Test
    public void testSingleEqualValued() throws URISyntaxException, IOException {
        parseMatch("key=val");
    }

    @Test
    public void testSingleEqualDotted() throws URISyntaxException, IOException {
        parseMatch("prefix.key=val");
    }

    @Test
    public void testSingleEqualHyphen() throws URISyntaxException, IOException {
        parseMatch("my-key=val");
    }

    @Test
    public void testSingleEqualUnderscore() throws URISyntaxException, IOException {
        parseMatch("my_key=val");
    }

    @Test
    public void testSingleColonEmpty() throws URISyntaxException, IOException {
        parseMatch("key:");
    }

    @Test
    public void testSingleColonValued() throws URISyntaxException, IOException {
        parseMatch("key:val");
    }

    @Test
    public void testSingleDottedColon() throws URISyntaxException, IOException {
        parseMatch("prefix.middle.key:val");
    }

    @Test
    public void testSingleColonHyphen() throws URISyntaxException, IOException {
        parseMatch("my-key:val");
    }

    @Test
    public void testSingleColonUnderscore() throws URISyntaxException, IOException {
        parseMatch("my_key:val");
    }

    @Test
    public void testSingleWhitespace1() throws URISyntaxException, IOException {
        parseMatch(" \t key =\tval ");
    }

    @Test
    public void testSingleWhitespace2() throws URISyntaxException, IOException {
        parseMatch(" \t key =\t val");
    }

    @Test
    public void testSingleWhitespace3() throws URISyntaxException, IOException {
        parseMatch(" \t key  =val");
    }

    @Test
    public void testSingleWhitespace4() throws URISyntaxException, IOException {
        parseMatch(" \t key=val");
    }
}
