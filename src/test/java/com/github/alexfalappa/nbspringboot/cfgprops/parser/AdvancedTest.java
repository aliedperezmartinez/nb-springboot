package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

/**
 * Syntax test suite for BootCfgParser: advanced scenario.
 *
 * @author Alessandro Falappa
 */
//@Ignore
public class AdvancedTest extends TestBase {

    @Test
    public void testSingleEqualContinuation() throws URISyntaxException, IOException {
        parseMatch("""
                   key=val\\
                   on next line""");
    }

    @Test
    public void testSingleEqualContinuationSlash() throws URISyntaxException, IOException {
        parseMatch("""
                    anotherkey = slash before\\\\\\
                   continuation""");
    }

    @Test
    public void testSingleEqualContinuationSpace() throws URISyntaxException, IOException {
        parseMatch("""
                   key2=value\\
                       with space at start of continuation""");
    }

    @Test
    public void testMultipleEqualContinuation() throws URISyntaxException, IOException {
        parseMatch("""
                   key1=first\\
                   value
                   key2=second\\
                   value""");
    }

}
