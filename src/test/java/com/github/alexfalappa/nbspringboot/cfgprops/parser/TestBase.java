package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.ParsingResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class to factor out parsing methods.
 *
 * @author Alessandro Falappa
 */
@Tag("LexerTests")
public class TestBase {

    final protected CfgPropsParboiled parser = Parboiled.createParser(CfgPropsParboiled.class);
    final protected ReportingParseRunner reportingRunner = new ReportingParseRunner(parser.cfgProps());
    final protected TracingParseRunner tracingRunner = new TracingParseRunner(parser.cfgProps());

    @BeforeEach
    public void clearParser() {
        parser.reset();
    }

    protected void parseNoMatch(String input) {
        ParsingResult<?> result = reportingRunner.run(input);
        if (result.matched) {
            listPropsOrdered(parser.getParsedProps());
            result = tracingRunner.run(input);
        }
        assertFalse(result.matched);
    }

    protected void parseMatch(String input) {
        ParsingResult<?> result = reportingRunner.run(input);
        if (result.matched) {
            final Properties pp = parser.getParsedProps();
            if (!pp.isEmpty()) {
                listPropsOrdered(pp);
            }
        } else {
            result = tracingRunner.run(input);
            for (ParseError pe : result.parseErrors) {
            }
        }
        assertTrue(result.matched);
        assertFalse(result.hasErrors());
    }

    protected String readResource(String name) throws IOException, URISyntaxException {
        byte[] encoded = Files.readAllBytes(Paths.get(TestBase.class.getResource(name).toURI()));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    protected void listPropsOrdered(Properties p) {
        if (p.isEmpty()) {
        } else {
            TreeSet<Object> sortedKeys = new TreeSet<>(p.keySet());
            for (Object k : sortedKeys) {
            }
        }
    }

}
