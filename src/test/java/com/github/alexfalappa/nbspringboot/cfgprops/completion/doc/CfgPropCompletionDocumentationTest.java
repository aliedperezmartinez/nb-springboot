package com.github.alexfalappa.nbspringboot.cfgprops.completion.doc;

import java.net.URL;
import javax.swing.Action;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.Deprecation;
import org.springframework.boot.configurationmetadata.Hints;
import org.springframework.boot.configurationmetadata.ValueHint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CfgPropCompletionDocumentationTest {

    @Mock
    private ConfigurationMetadataProperty configurationMeta;

    @Test
    public void testGetText() {
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b>", result);
    }

    @Test
    public void testGetTextType() {
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getType()).thenReturn("java.util.List<java.lang.String>");
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/>java.util.List&lt;java.lang.String&gt;", result);
    }

    @Test
    public void testGetTextDeprecation() {
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getDeprecation()).thenReturn(new Deprecation());
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/><br/><b><i>Deprecated</i></b>", result);
    }

    @Test
    public void testGetTextDeprecationDeprecated() {
        when(configurationMeta.getId()).thenReturn("id");
        final Deprecation deprecation = new Deprecation();
        deprecation.setLevel(Deprecation.Level.ERROR);
        when(configurationMeta.getDeprecation()).thenReturn(deprecation);
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/><br/><b><i>REMOVED</i></b>", result);
    }

    @Test
    public void testGetTextDeprecationReason() {
        when(configurationMeta.getId()).thenReturn("id");
        final Deprecation deprecation = new Deprecation();
        deprecation.setReason("reason");
        when(configurationMeta.getDeprecation()).thenReturn(deprecation);
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/><br/><b><i>Deprecated:</i></b> reason", result);
    }

    @Test
    public void testGetTextDeprecationReplacement() {
        when(configurationMeta.getId()).thenReturn("id");
        final Deprecation deprecation = new Deprecation();
        deprecation.setReplacement("replacement");
        when(configurationMeta.getDeprecation()).thenReturn(deprecation);
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals(
            "<b>id</b><br/><br/><b><i>Deprecated</i></b><br/><i>Replaced by:</i> <code>replacement</code>",
            result);
    }

    @Test
    public void testGetTextDefaultValue() {
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getDefaultValue()).thenReturn("value");
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/><br/><i>Default:</i> value", result);
    }

    @Test
    public void testGetTextDefaultValueArray() {
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getDefaultValue()).thenReturn(new String[]{"value"});
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/><br/><i>Default:</i> [value]", result);
    }

    @Test
    public void testGetTextDescription() {
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getDescription()).thenReturn("description");
        when(configurationMeta.getHints()).thenReturn(new Hints());
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals("<b>id</b><br/><br/>description", result);
    }

    @Test
    public void testGetTextListOfValues() {
        when(configurationMeta.getId()).thenReturn("id");
        final Hints hints = new Hints();
        final ValueHint hint = new ValueHint();
        hint.setValue("value");
        hint.setDescription("description");
        hints.getValueHints().add(hint);
        when(configurationMeta.getHints()).thenReturn(hints);
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final String result = instance.getText();

        assertEquals(
            "<b>id</b><br/><br/><table><tr><td><i>Value</i></td><td><i>Description</i></td></tr><tr><td>value</td><td>description</th></tr></table>",
            result);
    }

    @Test
    public void testGetURL() {
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final URL result = instance.getURL();

        assertEquals(
            "http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#common-application-properties",
            result.toExternalForm());
    }

    @Test
    public void testResolveLink() {
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final CompletionDocumentation result = instance.resolveLink("string");

        assertNull(result);
    }

    @Test
    public void testGetGotoSourceAction() {
        final CfgPropCompletionDocumentation instance = new CfgPropCompletionDocumentation(configurationMeta);

        final Action result = instance.getGotoSourceAction();

        assertNull(result);
    }

}
