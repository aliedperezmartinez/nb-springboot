package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

public abstract class BaseCompletiomItem implements CompletionItem {

    private boolean overwrite;

    @Override
    public void processKeyEvent(KeyEvent evt) {
        // detect if Ctrl + Enter is pressed
        overwrite = evt.getKeyCode() == KeyEvent.VK_ENTER && (evt.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;
    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(getText(), getTextRight(), graphics, font);
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent component) {
        return false;
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getRenderText(), getTextRight(), g, defaultFont, getDefaultColor(selected), width, height, selected);
    }

    protected String getRenderText() {
        return getText();
    }

    protected boolean isOverwrite() {
        return overwrite;
    }

    protected String getTextRight() {
        return null;
    }

    protected Color getDefaultColor(boolean selected) {
        return selected ? UIManager.getColor("List.selectionForeground") : UIManager.getColor("List.foreground");
    }

    protected abstract String getText();

    protected abstract ImageIcon getIcon();

    protected static abstract class BaseAsyncCompletionQuery extends AsyncCompletionQuery {

        @Override
        protected void query(CompletionResultSet completionResultSet, Document document, int i) {
            completionResultSet.setDocumentation(createCompletionDocumentation());
            completionResultSet.finish();
        }

        protected abstract CompletionDocumentation createCompletionDocumentation();

    }

}
