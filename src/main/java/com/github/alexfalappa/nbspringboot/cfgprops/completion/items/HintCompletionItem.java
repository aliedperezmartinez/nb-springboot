package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import com.github.alexfalappa.nbspringboot.Utils;
import com.github.alexfalappa.nbspringboot.cfgprops.completion.doc.CfgPropValueCompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.springframework.boot.configurationmetadata.ValueHint;

public abstract class HintCompletionItem extends BaseCompletiomItem {

    private final ValueHint hint;
    protected final int caretOffset;
    protected final int dotOffset;

    public HintCompletionItem(ValueHint hint, int caretOffset, int dotOffset) {
        this.hint = hint;
        this.caretOffset = caretOffset;
        this.dotOffset = dotOffset;
    }

    @Override
    public CompletionTask createDocumentationTask() {
        if (hint.getDescription() != null) {
            return new AsyncCompletionTask(new ValueHintAsyncCompletionQuery(hint));
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getInsertPrefix() {
        return hint.getValue().toString();
    }

    @Override
    public CharSequence getSortText() {
        return hint.getValue().toString();
    }

    @Override
    protected String getText() {
        return Utils.simpleHtmlEscape(hint.getValue().toString());
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    public ValueHint getHint() {
        return hint;
    }

    protected static class ValueHintAsyncCompletionQuery extends BaseCompletiomItem.BaseAsyncCompletionQuery {

        private final ValueHint hint;

        public ValueHintAsyncCompletionQuery(ValueHint hint) {
            this.hint = hint;
        }

        @Override
        protected CompletionDocumentation createCompletionDocumentation() {
            return new CfgPropValueCompletionDocumentation(hint);
        }
    }
}
