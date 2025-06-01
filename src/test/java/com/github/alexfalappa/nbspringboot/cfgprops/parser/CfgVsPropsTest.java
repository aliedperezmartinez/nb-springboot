package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.parboiled.errors.ParseError;
import org.parboiled.support.ParsingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test comparing {@code java.util.Properties} loading versus parsing.
 *
 * @author Alessandro Falappa
 */
//@Ignore
public class CfgVsPropsTest extends TestBase {

    @Test
    public void testCompareProps() throws IOException, URISyntaxException {
        try (InputStream is = getClass().getResourceAsStream("/load.properties")) {
            Properties loaded = new Properties();
            loaded.load(is);
            listPropsOrdered(loaded);
            final String strFile = readResource("/load.properties");
            ParsingResult pr = reportingRunner.run(strFile);
            final Properties parsed = parser.getParsedProps();
            listPropsOrdered(parsed);
            if (!pr.matched) {
                for (Object err : reportingRunner.getParseErrors()) {
                    ParseError pe = (ParseError) err;
                }
            }
            assertTrue(pr.matched, "Failed parsing");
            assertEquals(loaded.size(), parsed.size(), "Different loaded/parsed sizes");
            for (Map.Entry<Object, Object> entry : loaded.entrySet()) {
                assertTrue(parsed.containsKey(entry.getKey()),
                        String.format("Missing key %s in parsed", entry.getKey()));
                assertEquals(
                        entry.getValue(),
                        parsed.get(entry.getKey().toString()),
                        String.format("Different loaded-parsed values for key %s", entry.getKey()));
            }
        }
    }

    public void testWriteProps() throws IOException {
        Properties p = new Properties();
        p.setProperty("key", "value");
        p.setProperty("a=key", "value");
        p.setProperty("the#key", "value");
        p.setProperty("one!key", "value");
        p.setProperty("my key", "value");
        p.setProperty("anoth:key", "value");
        p.setProperty("key1", "the value");
        p.setProperty("key2", "a#value");
        p.setProperty("key3", "one!value");
        p.setProperty("key4", "my=value");
        p.setProperty("key5", "anoth:value");
        p.setProperty("spaces", "a value with spaces");
        p.setProperty("slashes", "a\\value\\with\\slashes");
        p.setProperty("linefeed", "a value\nwith line\nfeeds");
        p.setProperty("unicode", "©àèìòù");
        try (OutputStream os = Files.newOutputStream(Paths.get("write.properties"))) {
            p.store(os, "This is a comment");
        }
    }

}
