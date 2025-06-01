package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BaseCompletiomItemTest {

    @Mock
    private Component source;
    @Mock
    private JTextComponent component;
    @Mock
    private CompletionDocumentation completionDocumentation;
    @Mock
    private CompletionResultSet completionResultSet;
    @Mock
    private Document document;

    @Test
    public void testProcessKeyEvent() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        instance.processKeyEvent(new KeyEvent(source, 0, 0, 0, KeyEvent.VK_0, '0'));

        assertFalse(instance.isOverwrite());
    }

    @Test
    public void testProcessKeyEventCtrlEnter() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        instance.processKeyEvent(keyEventOverwrite());

        assertTrue(instance.isOverwrite());
    }

    @Test
    public void testCreateToolTipTask() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        assertNull(instance.createToolTipTask());
    }

    @Test
    public void testInstantSubstitution() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        assertFalse(instance.instantSubstitution(component));
    }

    @Test
    public void testGetTextRight() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        assertNull(instance.getTextRight());
    }

    @Test
    public void testGetDefaultColorSelected() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        final Color result = instance.getDefaultColor(true);

        assertEquals(UIManager.getColor("List.selectionForeground"), result);
    }

    @Test
    public void testGetDefaultColorNotSelected() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        final Color result = instance.getDefaultColor(false);

        assertEquals(UIManager.getColor("List.foreground"), result);
    }

    @Test
    public void testGetRenderText() {
        final BaseCompletiomItem instance = new BaseCompletiomItemImpl();

        final String result = instance.getRenderText();

        assertEquals("text", result);
    }

    @Test
    public void testBaseAsyncCompletionQuery() {
        final TestAsyncCompletionQueryImpl query =  new TestAsyncCompletionQueryImpl(completionDocumentation);

        query.doQuery(completionResultSet, document, 0);

        verify(completionResultSet).setDocumentation(completionDocumentation);
        verify(completionResultSet).finish();
    }

    private KeyEvent keyEventOverwrite() {
        return new KeyEvent(source, 0, 0, KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '0');
    }

    private static class BaseCompletiomItemImpl extends BaseCompletiomItem {

        @Override
        protected String getText() {
            return "text";
        }

        @Override
        public void defaultAction(JTextComponent component) {
        }

        @Override
        public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        }

        @Override
        public CompletionTask createDocumentationTask() {
            return null;
        }

        @Override
        public int getSortPriority() {
            return 0;
        }

        @Override
        public CharSequence getSortText() {
            return "";
        }

        @Override
        public CharSequence getInsertPrefix() {
            return "";
        }

        @Override
        protected ImageIcon getIcon() {
            return null;
        }
    }

    private static class TestAsyncCompletionQueryImpl extends BaseCompletiomItem.BaseAsyncCompletionQuery {

        private final CompletionDocumentation completionDocumentation;

        public TestAsyncCompletionQueryImpl(CompletionDocumentation completionDocumentation) {
            this.completionDocumentation = completionDocumentation;
        }

        public void doQuery(CompletionResultSet completionResultSet, Document document, int i) {
            query(completionResultSet, document, i);
        }

        @Override
        protected CompletionDocumentation createCompletionDocumentation() {
            return completionDocumentation;
        }
    }

}
