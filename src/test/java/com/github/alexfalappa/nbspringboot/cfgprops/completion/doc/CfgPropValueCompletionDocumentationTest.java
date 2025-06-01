package com.github.alexfalappa.nbspringboot.cfgprops.completion.doc;

import java.net.URL;
import javax.swing.Action;
import org.junit.jupiter.api.Test;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.springframework.boot.configurationmetadata.ValueHint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CfgPropValueCompletionDocumentationTest {

    @Test
    public void testGetText() {
        final CfgPropValueCompletionDocumentation instance = new CfgPropValueCompletionDocumentation(buildHint("value"));

        final String result = instance.getText();

        assertEquals("<b>value</b>", result);
    }

    @Test
    public void testGetTextDescription() {
        final CfgPropValueCompletionDocumentation instance = new CfgPropValueCompletionDocumentation(buildHint("value", "<description>&stuff"));

        final String result = instance.getText();

        assertEquals("<b>value</b><br/>&lt;description&gt;&amp;stuff", result);
    }

    @Test
    public void testGetURL() {
        final CfgPropValueCompletionDocumentation instance = new CfgPropValueCompletionDocumentation(new ValueHint());

        final URL result = instance.getURL();

        assertNull(result);
    }

    @Test
    public void testResolveLink() {
        final CfgPropValueCompletionDocumentation instance = new CfgPropValueCompletionDocumentation(new ValueHint());

        final CompletionDocumentation result = instance.resolveLink("string");

        assertNull(result);
    }

    @Test
    public void testGetGotoSourceAction() {
        final CfgPropValueCompletionDocumentation instance = new CfgPropValueCompletionDocumentation(new ValueHint());

        final Action result = instance.getGotoSourceAction();

        assertNull(result);
    }

    private static ValueHint buildHint(final String value, final String description) {
        final ValueHint hint = buildHint(value);
        hint.setDescription(description);
        return hint;
    }

    private static ValueHint buildHint(final String value) {
        final ValueHint hint = new ValueHint();
        hint.setValue(value);
        return hint;
    }
}
