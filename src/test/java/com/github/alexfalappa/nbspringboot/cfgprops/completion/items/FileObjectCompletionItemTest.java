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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileObjectCompletionItemTest {

    @Mock
    private FileObject fileObj;
    @Mock
    private Component source;
    @Mock
    private JTextComponent component;
    @Mock
    private StyledDocument doc;
    @Mock
    private Element lineElement;

    @Test
    public void testGetText() {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        final String result = instance.getText();
        assertEquals(fileName, result);
    }

    @Test
    public void testGetTextRight() {
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        assertNull(instance.getTextRight());
    }

    @Test
    public void testDefaultAction() throws BadLocationException, FileStateInvalidException {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(component.getDocument()).thenReturn(doc);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, fileName, null);
    }

    @Test
    public void testDefaultActionRootFile() throws BadLocationException, FileStateInvalidException {
        final String fileName = "/";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(fileObj.isRoot()).thenReturn(Boolean.TRUE);
        when(component.getDocument()).thenReturn(doc);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, fileName, null);
    }

    @Test
    public void testDefaultActionDirectory() throws BadLocationException, FileStateInvalidException {
        final String fileName = "dir";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(fileObj.isFolder()).thenReturn(Boolean.TRUE);
        when(component.getDocument()).thenReturn(doc);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "dir/", null);
    }

    @Test
    public void testDefaultActionOverwrite() throws BadLocationException, FileStateInvalidException {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key=value");

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, fileName, null);
    }

    @Test
    public void testDefaultActionOverwriteColon() throws BadLocationException, FileStateInvalidException {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key:value");

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, fileName, null);
    }

    @Test
    public void testDefaultActionOverwriteComma() throws BadLocationException, FileStateInvalidException {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key,value");

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, fileName, null);
    }

    @Test
    public void testDefaultActionOverwriteOther() throws BadLocationException, FileStateInvalidException {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);
        when(component.getDocument()).thenReturn(doc);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key");
        when(lineElement.getEndOffset()).thenReturn(4);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, fileName, null);
    }

    @Test
    public void testProcessKeyEvent() {
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        instance.processKeyEvent(new KeyEvent(source, 0, 0, 0, KeyEvent.VK_0, '0'));

        assertFalse(instance.isOverwrite());
    }

    @Test
    public void testProcessKeyEventCtrlEnter() {
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        instance.processKeyEvent(keyEventOverwrite());

        assertTrue(instance.isOverwrite());
    }

    @Test
    public void testCreateDocumentationTask() {
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        assertNull(instance.createDocumentationTask());
    }

    @Test
    public void testCreateToolTipTask() {
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        assertNull(instance.createToolTipTask());
    }

    @Test
    public void testInstantSubstitution() {
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        assertFalse(instance.instantSubstitution(component));
    }

    @Test
    public void testGetSortPriorityDirectory() {
        when(fileObj.isFolder()).thenReturn(Boolean.TRUE);
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        int result = instance.getSortPriority();
        assertEquals(0, result);
    }

    @Test
    public void testGetSortPriorityNotDirectory() {
        when(fileObj.isFolder()).thenReturn(Boolean.FALSE);
        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        int result = instance.getSortPriority();
        assertEquals(1, result);
    }

    @Test
    public void testGetSortText() {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        final CharSequence result = instance.getSortText();
        assertEquals(fileName, result);
    }

    @Test
    public void testGetInsertPrefix() {
        final String fileName = "file.ext";
        when(fileObj.getNameExt()).thenReturn(fileName);

        final FileObjectCompletionItem instance = new FileObjectCompletionItem(fileObj, 0, 0);

        final CharSequence result = instance.getInsertPrefix();
        assertEquals(fileName, result);
    }

    private KeyEvent keyEventOverwrite() {
        return new KeyEvent(source, 0, 0, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '0');
    }
}
