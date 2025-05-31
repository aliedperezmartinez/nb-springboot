package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.lang.model.element.ElementKind;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.completion.CompletionTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JavaTypeCompletionItemTest {

    @Mock
    private Component source;
    @Mock
    private JTextComponent component;
    @Mock
    private KeyEvent evt;
    @Mock
    private BaseDocument document;
    @Mock
    private StyledDocument doc;
    @Mock
    private Element lineElement;

    @Test
    public void testGetText() {
        final String name = "name";
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);

        final String result = instance.getText();

        assertEquals(name, result);
    }

    @Test
    public void testGetTextRight() {
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        final String result = instance.getTextRight();

        assertNull(result);
    }

    @Test
    public void testDefaultAction() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        final String name = "name";

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, name, null);
    }

    @Test
    public void testDefaultActionKeyCompletion() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "name=", null);
    }

    @Test
    public void testDefaultActionOverwrite() throws BadLocationException {
        final String name = "name";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key=value");

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, name, null);
    }

    @Test
    public void testDefaultActionOverwriteColon() throws BadLocationException {
        final String name = "name";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key:value");

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, name, null);
    }

    @Test
    public void testDefaultActionOverwriteComma() throws BadLocationException {
        final String name = "name";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key,value");

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, name, null);
    }

    @Test
    public void testDefaultActionOverwriteOther() throws BadLocationException {
        final String name = "name";
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key");
        when(lineElement.getEndOffset()).thenReturn(4);

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, name, null);
    }

    @Test
    public void testProcessKeyEvent() {
        when(evt.getID()).thenReturn(KeyEvent.KEY_TYPED);
        when(evt.getKeyChar()).thenReturn('a');
        when(evt.getKeyCode()).thenReturn(KeyEvent.VK_A);
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        instance.processKeyEvent(evt);

        verify(evt, never()).consume();
        assertFalse(instance.isOverwrite());
    }

    @Test
    public void testProcessKeyEventCtrlEnter() {
        when(evt.getID()).thenReturn(KeyEvent.KEY_TYPED);
        when(evt.getKeyChar()).thenReturn('a');
        when(evt.getKeyCode()).thenReturn(KeyEvent.VK_ENTER);
        when(evt.getModifiersEx()).thenReturn(KeyEvent.CTRL_DOWN_MASK);

        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        instance.processKeyEvent(evt);

        verify(evt, never()).consume();
        assertTrue(instance.isOverwrite());
    }

    @Test
    public void testProcessKeyEventCompletePackage() throws BadLocationException {
        when(evt.getID()).thenReturn(KeyEvent.KEY_TYPED);
        when(evt.getKeyChar()).thenReturn('.');
        when(evt.getKeyCode()).thenReturn(KeyEvent.VK_PERIOD);
        when(evt.getSource()).thenReturn(component);
        when(component.getDocument()).thenReturn(document);
        doAnswer(invocation -> {
            final Runnable r = invocation.getArgument(0, Runnable.class);
            r.run();
            return null;
        })
        .when(document).runAtomic(any(Runnable.class));
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.PACKAGE, 0, 0);

        instance.processKeyEvent(evt);

        verify(evt).consume();
        verify(document).remove(0, 0);
        verify(document).insertString(0, "name.", null);
        assertFalse(instance.isOverwrite());
    }

    @Test
    public void testCreateDocumentationTask() {
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        final CompletionTask result = instance.createDocumentationTask();

        assertNull(result);
    }

    @Test
    public void testCreateToolTipTask() {
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        final CompletionTask result = instance.createToolTipTask();

        assertNull(result);
    }

    @Test
    public void testInstantSubstitution() {
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        final boolean result = instance.instantSubstitution(component);

        assertFalse(result);
    }

    @Test
    public void testGetSortPriority() {
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.METHOD, 0, 0);

        final int result = instance.getSortPriority();

        assertEquals(2, result);
    }

    @Test
    public void testGetSortPriorityPackage() {
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem("name", ElementKind.PACKAGE, 0, 0);

        final int result = instance.getSortPriority();

        assertEquals(1, result);
    }

    @Test
    public void testGetSortText() {
        final String name = "name";
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);

        final CharSequence result = instance.getSortText();

        assertEquals(name, result);
    }

    @Test
    public void testGetInsertPrefix() {
        final String name = "name";
        final JavaTypeCompletionItem instance = new JavaTypeCompletionItem(name, ElementKind.METHOD, 0, 0);

        final CharSequence result = instance.getInsertPrefix();

        assertEquals(name, result);
    }

    private KeyEvent keyEventOverwrite() {
        return new KeyEvent(source, 0, 0, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '0');
    }

}
