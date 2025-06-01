package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationmetadata.ValueHint;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeyCompletionItemTest {

    @Mock
    private Component source;
    @Mock
    private StyledDocument doc;
    @Mock
    private JTextComponent component;
    @Mock
    private Element lineElement;

    @Test
    public void testDefaultAction() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);

        final KeyCompletionItem instance = new KeyCompletionItem(buildHint("value"), 0, 0);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "value=", null);
    }

    @Test
    public void testDefaultActionOverwrite() throws BadLocationException {
        final String value = "value";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key=value");

        final KeyCompletionItem instance = new KeyCompletionItem(buildHint(value), 0, 0);
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

        final KeyCompletionItem instance = new KeyCompletionItem(buildHint(value), 0, 0);
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

        final KeyCompletionItem instance = new KeyCompletionItem(buildHint(value), 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "value=", null);
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
