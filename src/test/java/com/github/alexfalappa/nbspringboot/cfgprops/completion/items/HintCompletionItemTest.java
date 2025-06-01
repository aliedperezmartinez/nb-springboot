package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.modules.editor.completion.CompletionResultSetImpl;
import org.netbeans.modules.editor.completion.CompletionSpiPackageAccessor;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.springframework.boot.configurationmetadata.ValueHint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HintCompletionItemTest {

    @Mock
    private CompletionResultSetImpl completionResultSetImpl;

    @Test
    public void testCreateDocumentationTaskNull() {
        final HintCompletionItemImpl instance = new HintCompletionItemImpl(new ValueHint(), 0, 0);

        assertNull(instance.createDocumentationTask());
    }

    @Test
    public void testCreateDocumentationTask() throws InterruptedException, InvocationTargetException {
        final CompletionResultSet completionResultSet = CompletionSpiPackageAccessor.get()
            .createCompletionResultSet(completionResultSetImpl);
        when(completionResultSetImpl.isFinished()).thenReturn(Boolean.TRUE, Boolean.FALSE);

        final HintCompletionItemImpl instance = new HintCompletionItemImpl(buildHint("value", "description"), 0, 0);

        final AsyncCompletionTask result = (AsyncCompletionTask) instance.createDocumentationTask();

        SwingUtilities.invokeAndWait(() -> result.query(completionResultSet));
        /* The previous line was supposed to run asynchronously,
           but when all tests are executed the verificationss fail.
           Doesn't happen when the test alone or the test file alone is executed.
        */
        Thread.sleep(5000);
        verify(completionResultSetImpl).setDocumentation(argThat(argument -> {
            return argument.getText().equals("<b>value</b><br/>description");
        }));
        verify(completionResultSetImpl).finish();
    }

    @Test
    public void testGetInsertPrefix() {
        final String value = "value";
        final HintCompletionItemImpl instance = new HintCompletionItemImpl(buildHint(value), 0, 0);

        final CharSequence result = instance.getInsertPrefix();

        assertEquals(value, result);
    }

    @Test
    public void testGetSortText() {
        final String value = "value";
        final HintCompletionItemImpl instance = new HintCompletionItemImpl(buildHint(value), 0, 0);

        final CharSequence result = instance.getSortText();

        assertEquals(value, result);
    }

    @Test
    public void testGetText() {
        final HintCompletionItemImpl instance = new HintCompletionItemImpl(buildHint("<p>STRING</p>"), 0, 0);

        final CharSequence result = instance.getText();

        assertEquals("&lt;p&gt;STRING&lt;/p&gt;", result);
    }

    @Test
    public void testGetSortPriority() {
        final HintCompletionItemImpl instance = new HintCompletionItemImpl(new ValueHint(), 0, 0);

        int result = instance.getSortPriority();

        assertEquals(0, result);
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

    private static class HintCompletionItemImpl extends HintCompletionItem {

        public HintCompletionItemImpl(ValueHint hint, int caretOffset, int dotOffset) {
            super(hint, caretOffset, dotOffset);
        }

        @Override
        public void defaultAction(JTextComponent component) {
        }

        @Override
        protected ImageIcon getIcon() {
            return null;
        }
    }

}
