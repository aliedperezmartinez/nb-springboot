package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import com.github.alexfalappa.nbspringboot.PrefConstants;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.modules.editor.completion.CompletionResultSetImpl;
import org.netbeans.modules.editor.completion.CompletionSpiPackageAccessor;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.NbPreferences;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.Hints;
import org.springframework.boot.configurationmetadata.ValueHint;
import org.springframework.boot.configurationmetadata.ValueProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CfgPropCompletionItemTest {

    @Mock
    private Component source;
    @Mock
    private JTextComponent component;
    @Mock
    private ConfigurationMetadataProperty configurationMeta;
    @Mock
    private CompletionResultSetImpl completionResultSetImpl;
    @Mock
    private Hints hints;
    @Mock
    private StyledDocument doc;
    @Mock
    private Element lineElement;

    @Test
    public void testType() {
        when(configurationMeta.getType()).thenReturn("java.lang.String");

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        assertEquals("String", instance.getType());
    }

    @Test
    public void testDefaultAction() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id=", null);
    }

    @Test
    public void testDefaultActionOverwrite() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key=value");

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id", null);
    }

    @Test
    public void testDefaultActionOverwriteColon() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key:value");

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @Test
    public void testDefaultActionOverwriteOther() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("value");
        when(lineElement.getEndOffset()).thenReturn(4);

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @Test
    public void testDefaultActionOverwriteOtherCanCompleteValueValueHints() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("value");
        when(lineElement.getEndOffset()).thenReturn(4);
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getValueHints()).thenReturn(List.of(new ValueHint()));

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @Test
    public void testDefaultActionOverwriteOtherCanCompleteValueValueProviders() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("value");
        when(lineElement.getEndOffset()).thenReturn(4);
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getValueHints()).thenReturn(List.of());
        when(hints.getValueProviders()).thenReturn(List.of(new ValueProvider()));

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @Test
    public void testDefaultActionOverwriteOtherCanCompleteValueNotCompletableType() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.lang.String");
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("value");
        when(lineElement.getEndOffset()).thenReturn(4);
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getValueHints()).thenReturn(List.of());
        when(hints.getValueProviders()).thenReturn(List.of(new ValueProvider()));

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "java.util.List<java.lang.Boolean>",
        "java.util.Set<java.lang.Boolean>",
        "java.util.List<org.springframework.core.io.Resource>",
        "java.util.Set<org.springframework.core.io.Resource>",
        "java.util.List<java.nio.charset.Charset>",
        "java.util.Set<java.nio.charset.Charset>",
        "java.util.List<java.util.Locale>",
        "java.util.Set<java.util.Locale>"})
    public void testDefaultActionOverwriteSequenceCanCompleteValueCompletableType(String type) throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn(type);
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("value");
        when(lineElement.getEndOffset()).thenReturn(4);
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getValueHints()).thenReturn(List.of());
        when(hints.getValueProviders()).thenReturn(List.of());
        NbPreferences.forModule(PrefConstants.class)
            .put(PrefConstants.PREF_ARRAY_NOTATION, Boolean.FALSE.toString());

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "java.lang.Boolean",
        "java.nio.charset.Charset",
        "java.util.Locale",
        "org.springframework.core.io.Resource",
        "org.springframework.util.MimeType"})
    public void testDefaultActionOverwriteCanCompleteValueCompletableType(String type) throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn(type);
        when(configurationMeta.getId()).thenReturn("id");
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("value");
        when(lineElement.getEndOffset()).thenReturn(4);
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getValueHints()).thenReturn(List.of());
        when(hints.getValueProviders()).thenReturn(List.of());

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id=", null);
    }

    @Test
    public void testDefaultActionTypeMap() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.util.Map");
        when(configurationMeta.getId()).thenReturn("id");

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id.", null);
    }

    @Test
    public void testDefaultActionTypeMapCanCompleteKeyKeyHints() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.util.Map");
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getKeyHints()).thenReturn(List.of(new ValueHint()));

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id.", null);
    }
    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "java.util.Set", "java.lang.String[]"})

    @Test
    public void testDefaultActionTypeMapCanCompleteKeyEmptyProviders() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.util.Map");
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getKeyHints()).thenReturn(List.of());
        when(hints.getKeyProviders()).thenReturn(List.of(new ValueProvider()));

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id.", null);
    }

    @Test
    public void testDefaultActionTypeMapCanCompleteKeyNotCompletableType() throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn("java.util.Map");
        when(configurationMeta.getId()).thenReturn("id");
        when(configurationMeta.getHints()).thenReturn(hints);
        when(hints.getKeyHints()).thenReturn(List.of());
        when(hints.getKeyProviders()).thenReturn(List.of());

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id.", null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "java.util.Set", "java.lang.String[]"})
    public void testDefaultActionTypeSequence(String type) throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn(type);
        when(configurationMeta.getId()).thenReturn("id");
        NbPreferences.forModule(PrefConstants.class)
            .put(PrefConstants.PREF_ARRAY_NOTATION, Boolean.FALSE.toString());

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id=", null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "java.util.Set", "java.lang.String[]"})
    public void testDefaultActionTypeSequencePreferArray(String type) throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn(type);
        when(configurationMeta.getId()).thenReturn("id");
        when(component.getCaretPosition()).thenReturn(10);
        NbPreferences.forModule(PrefConstants.class)
            .put(PrefConstants.PREF_ARRAY_NOTATION, Boolean.TRUE.toString());

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.defaultAction(component);

        verify(doc).remove(0, 0);
        verify(doc).insertString(0, "id[]=", null);
        verify(component).setCaretPosition(8);
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.util.List", "java.util.Set", "java.lang.String[]"})
    public void testDefaultActionTypeSequencePreferArrayNoEqualSign(String type) throws BadLocationException {
        when(component.getDocument()).thenReturn(doc);
        when(configurationMeta.getType()).thenReturn(type);
        when(configurationMeta.getId()).thenReturn("id");
        when(component.getCaretPosition()).thenReturn(10);
        when(doc.getParagraphElement(anyInt())).thenReturn(lineElement);
        when(doc.getText(anyInt(), anyInt())).thenReturn("key=value");
        NbPreferences.forModule(PrefConstants.class)
            .put(PrefConstants.PREF_ARRAY_NOTATION, Boolean.TRUE.toString());

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        instance.processKeyEvent(keyEventOverwrite());

        instance.defaultAction(component);

        verify(doc).remove(0, 3);
        verify(doc).insertString(0, "id[]", null);
        verify(component).setCaretPosition(9);
    }

    @Test
    public void testProcessKeyEvent() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.processKeyEvent(new KeyEvent(source, 0, 0, 0, KeyEvent.VK_0, '0'));

        assertFalse(instance.isOverwrite());
    }

    @Test
    public void testProcessKeyEventCtrlEnter() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        instance.processKeyEvent(keyEventOverwrite());

        assertTrue(instance.isOverwrite());
    }

    @Test
    public void testCreateDocumentationTask() throws InterruptedException, InvocationTargetException {
        final CompletionResultSet completionResultSet = CompletionSpiPackageAccessor.get()
            .createCompletionResultSet(completionResultSetImpl);
        when(completionResultSetImpl.isFinished()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(configurationMeta.getHints()).thenReturn(hints);

        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        final AsyncCompletionTask result = (AsyncCompletionTask) instance.createDocumentationTask();

        SwingUtilities.invokeAndWait(() -> result.query(completionResultSet));
        /* The previous line was supposed to run asynchronously,
           but when all tests are executed the verificationss fail.
           Doesn't happen when the test alone or the test file alone is executed.
        */
        Thread.sleep(5000);
        verify(completionResultSetImpl).setDocumentation(argThat(argument -> {
            return argument.getText().equals("<b>null</b>");
        }));
        verify(completionResultSetImpl).finish();
    }

    @Test
    public void testCreateToolTipTask() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        assertNull(instance.createToolTipTask());
    }

    @Test
    public void testInstantSubstitution() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        assertFalse(instance.instantSubstitution(component));
    }

    @Test
    public void testGetSortPriorityDeprecated_DeprecatedLast() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        when(configurationMeta.isDeprecated()).thenReturn(Boolean.TRUE);

        int result = instance.getSortPriority();
        assertEquals(1, result);
    }

    @Test
    public void testGetSortPriorityNotDeprecated_DeprecatedLast() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);
        when(configurationMeta.isDeprecated()).thenReturn(Boolean.FALSE);

        int result = instance.getSortPriority();
        assertEquals(0, result);
    }

    @Test
    public void testGetSortPriorityDeprecated_NotDeprecatedLast() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, false);
        when(configurationMeta.isDeprecated()).thenReturn(Boolean.TRUE);

        int result = instance.getSortPriority();
        assertEquals(0, result);
    }

    @Test
    public void testGetSortPriorityNotDeprecated_NotDeprecatedLast() {
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, false);
        when(configurationMeta.isDeprecated()).thenReturn(Boolean.FALSE);

        int result = instance.getSortPriority();
        assertEquals(0, result);
    }

    @Test
    public void testGetSortText() {
        final String id = "id";
        when(configurationMeta.getId()).thenReturn(id);
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        final CharSequence result = instance.getSortText();

        assertEquals(id, result);
    }

    @Test
    public void testGetInsertPrefix() {
        final String id = "id";
        when(configurationMeta.getId()).thenReturn(id);
        final CfgPropCompletionItem instance = new CfgPropCompletionItem(configurationMeta, 0, 0, true);

        CharSequence result = instance.getInsertPrefix();

        assertEquals(id, result);
    }

    private KeyEvent keyEventOverwrite() {
        return new KeyEvent(source, 0, 0, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '0');
    }

}
