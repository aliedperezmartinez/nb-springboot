package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.parboiled.MatcherContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JavaIdPartMatcherTest {

    @Mock
    private MatcherContext context;

    @Test
    public void testIsSingleCharMatcher() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.isSingleCharMatcher();

        assertTrue(result);
    }

    @Test
    public void testCanMatchEmpty() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.canMatchEmpty();

        assertFalse(result);
    }

    @Test
    public void testIsStarterCharSpace() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.isStarterChar(' ');

        assertFalse(result);
    }

    @Test
    public void testIsStarterCharLetter() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.isStarterChar('a');

        assertTrue(result);
    }

    @Test
    public void testIsStarterCharDigit() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.isStarterChar('1');

        assertTrue(result);
    }

    @Test
    public void testIsStarterCharUnderscore() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.isStarterChar('_');

        assertTrue(result);
    }

    @Test
    public void testIsStarterCharSign() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.isStarterChar('-');

        assertFalse(result);
    }

    @Test
    public void testGetStarterChar() {
        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final char result = instance.getStarterChar();

        assertEquals('a', result);
    }

    @Test
    public void testMatchNotAcceptableChar() {
        when(context.getCurrentChar()).thenReturn('-');

        final JavaIdPartMatcher instance = new JavaIdPartMatcher();

        final boolean result = instance.match(context);

        assertFalse(result);
        verifyNoMoreInteractions(context);
    }

}
