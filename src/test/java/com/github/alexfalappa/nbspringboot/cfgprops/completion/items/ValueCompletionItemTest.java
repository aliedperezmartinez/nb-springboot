package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.modules.editor.completion.CompletionResultSetImpl;
import org.netbeans.modules.editor.completion.CompletionSpiPackageAccessor;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.springframework.boot.configurationmetadata.ValueHint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValueCompletionItemTest {

    @Mock
    private Component source;
    @Mock
    private StyledDocument doc;
    @Mock
    private JTextComponent component;
    @Mock
    private CompletionResultSetImpl completionResultSetImpl;
    @Mock
    private Element lineElement;

    @Test
    public void testGetText() {
        final ValueCompletionItem instance = new ValueCompletionItem(buildHint("<a&b>"), 0, 0);

        final String result = instance.getText();

        assertEquals("&lt;a&amp;b&gt;", result);
    }

    @Test
    public void testGetTextRight() {
        final ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        assertNull(instance.getTextRight());
    }

    @Test
    public void testDefaultAction() throws BadLocationException {
        final String value = "value";
        when(component.getDocument()).thenReturn(doc);

        ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, value, null);
    }

    @Test
    public void testDefaultActionOverwrite() throws BadLocationException {
        final String value = "value";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key=value");

        ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, value, null);
    }

    @Test
    public void testDefaultActionOverwriteColon() throws BadLocationException {
        final String value = "value";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key:value");

        ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, value, null);
    }

    @Test
    public void testDefaultActionOverwriteComma() throws BadLocationException {
        final String value = "value";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key,value");

        ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, value, null);
    }

    @Test
    public void testDefaultActionOverwriteOther() throws BadLocationException {
        final String value = "value";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key");
        when(lineElement.getEndOffset()).thenReturn(4);

        ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, value, null);
    }

    @Test
    public void testProcessKeyEvent() {
        final KeyEvent evt = new KeyEvent(source, 0, 0, 0, KeyEvent.VK_0, '0');

        ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        instance.processKeyEvent(evt);

        assertFalse(instance.isOverwrite());
    }

    @Test
    public void testProcessKeyEventCtrlEnter() {
        final ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        instance.processKeyEvent(keyEventOverwrite());

        assertTrue(instance.isOverwrite());
    }

    @Test
    public void testCreateDocumentationTaskNull() {
        final ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        assertNull(instance.createDocumentationTask());
    }

    @Test
    public void testCreateDocumentationTask() throws InterruptedException, InvocationTargetException {
        final CompletionResultSet completionResultSet = CompletionSpiPackageAccessor.get()
            .createCompletionResultSet(completionResultSetImpl);
        when(completionResultSetImpl.isFinished()).thenReturn(Boolean.TRUE, Boolean.FALSE);

        final ValueCompletionItem instance = new ValueCompletionItem(buildHint("value", "description"), 0, 0);

        final AsyncCompletionTask result = (AsyncCompletionTask) instance.createDocumentationTask();

        SwingUtilities.invokeAndWait(() -> result.query(completionResultSet));
        /* The previous line was supposed to run asynchronously,
           but when all tests are executed the verificationss fail.
           Doesn't happen when the test alone or the test file alone is executed.
        */
        Thread.sleep(5000);
        verify(completionResultSetImpl).setDocumentation(ArgumentMatchers.argThat(argument -> {
            final String text = argument.getText();
            return text.equals("<b>value</b><br/>description");
        }));
        verify(completionResultSetImpl).finish();
    }

    @Test
    public void testCreateToolTipTask() {
        final ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        assertNull(instance.createToolTipTask());
    }

    @Test
    public void testInstantSubstitution() {
        final ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        assertFalse(instance.instantSubstitution(component));
    }

    @Test
    public void testGetSortPriority() {
        final ValueCompletionItem instance = new ValueCompletionItem(new ValueHint(), 0, 0);

        int result = instance.getSortPriority();

        assertEquals(0, result);
    }

    @Test
    public void testGetSortText() {
        final String value = "value";
        final ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);

        final CharSequence result = instance.getSortText();

        assertEquals(value, result);
    }

    @Test
    public void testGetInsertPrefix() {
        final String value = "value";
        final ValueCompletionItem instance = new ValueCompletionItem(buildHint(value), 0, 0);

        final CharSequence result = instance.getInsertPrefix();

        assertEquals(value, result);
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

    private KeyEvent keyEventOverwrite() {
        return new KeyEvent(source, 0, 0, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '0');
    }

}
