/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexfalappa.nbspringboot.cfgprops.completion.items;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.Hints;

import com.github.alexfalappa.nbspringboot.PrefConstants;
import com.github.alexfalappa.nbspringboot.Utils;
import com.github.alexfalappa.nbspringboot.cfgprops.completion.doc.CfgPropCompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionDocumentation;

import static com.github.alexfalappa.nbspringboot.Utils.shortenJavaType;
import static com.github.alexfalappa.nbspringboot.Utils.simpleHtmlEscape;

/**
 * The implementation of {@code CompletionItem} for Spring Boot configuration property names.
 * <p>
 * Uses a {@code ConfigurationMetadataProperty} to render the completion item and spawn the documentation display.
 *
 * @author Aggelos Karalias
 * @author Alessandro Falappa
 */
public class CfgPropCompletionItem extends BaseCompletiomItem {

    private static final Logger logger = Logger.getLogger(CfgPropCompletionItem.class.getName());
    private static final ImageIcon fieldIcon = new ImageIcon(ImageUtilities.loadImage(
            "com/github/alexfalappa/nbspringboot/cfgprops/completion/springboot-property.png"));
    private final ConfigurationMetadataProperty configurationMeta;
    private final int caretOffset;
    private final int propStartOffset;
    private final String type;
    private final boolean sortDeprLast;

    public CfgPropCompletionItem(ConfigurationMetadataProperty configurationMeta, int propStartOffset, int caretOffset,
            boolean sortDeprLast) {
        this.configurationMeta = configurationMeta;
        if (configurationMeta.getType() != null) {
            type = simpleHtmlEscape(shortenJavaType(configurationMeta.getType()));
        } else {
            type = null;
        }
        this.propStartOffset = propStartOffset;
        this.caretOffset = caretOffset;
        this.sortDeprLast = sortDeprLast;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        logger.log(Level.FINER, "Accepted name completion: {0}", configurationMeta.getId());
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            // calculate the amount of chars to remove (by default from property start up to caret position)
            int lenToRemove = caretOffset - propStartOffset;
            int equalSignIndex = -1;
            final boolean overwrite = isOverwrite();
            if (overwrite) {
                // NOTE: the editor removes by itself the word at caret when ctrl + enter is pressed
                // the document state here is different from when the completion was invoked thus we have to
                // find again the offset of the equal sign in the line
                Element lineElement = doc.getParagraphElement(caretOffset);
                String line = doc.getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset());
                equalSignIndex = line.indexOf('=');
                int colonIndex = line.indexOf(':');
                if (equalSignIndex >= 0) {
                    // from property start to equal sign
                    lenToRemove = lineElement.getStartOffset() + equalSignIndex - propStartOffset;
                } else if (colonIndex >= 0) {
                    // from property start to colon
                    lenToRemove = lineElement.getStartOffset() + colonIndex - propStartOffset;
                } else {
                    // from property start to end of line (except line terminator)
                    lenToRemove = lineElement.getEndOffset() - 1 - propStartOffset;
                }
            }
            // remove characters from the property name start offset
            doc.remove(propStartOffset, lenToRemove);
            // add some useful chars depending on data type and presence of successive equal signs
            final String dataType = configurationMeta.getType();
            final boolean isSequence = dataType.contains("List") || dataType.contains("Set") || dataType.contains("[]");
            final boolean preferArray = NbPreferences.forModule(PrefConstants.class)
                    .getBoolean(PrefConstants.PREF_ARRAY_NOTATION, false);
            final boolean needEqualSign = !(overwrite && equalSignIndex >= 0);
            StringBuilder sb = new StringBuilder(getText());
            boolean continueCompletion = false;
            int goBack = 0;
            if (dataType.contains("Map")) {
                sb.append(".");
                continueCompletion = canCompleteKey();
            } else if (isSequence) {
                if (preferArray) {
                    sb.append("[]");
                    goBack = 1;
                    if (needEqualSign) {
                        sb.append("=");
                        goBack++;
                    }
                } else {
                    if (needEqualSign) {
                        sb.append("=");
                        continueCompletion = canCompleteValue();
                    }
                }
            } else if (needEqualSign) {
                sb.append("=");
                continueCompletion = canCompleteValue();
            }
            doc.insertString(propStartOffset, sb.toString(), null);
            if (goBack != 0) {
                jtc.setCaretPosition(jtc.getCaretPosition() - goBack);
            }
            // optinally close the code completion box
            if (!continueCompletion) {
                Completion.get().hideAll();
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected Color getDefaultColor(boolean selected) {
        return selected? UIManager.getColor("List.selectionForeground"): getDeprecatedColor();
    }

    @Override
    protected ImageIcon getIcon() {
        return fieldIcon;
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return new AsyncCompletionTask(new AsyncCompletionQueryImpl(configurationMeta));
    }

    @Override
    public int getSortPriority() {
        return (configurationMeta.isDeprecated() && sortDeprLast) ? 1 : 0;
    }

    @Override
    public CharSequence getSortText() {
        return getText();
    }

    @Override
    public CharSequence getInsertPrefix() {
        return getText();
    }

    @Override
    protected String getText() {
        return configurationMeta.getId();
    }

    @Override
    protected String getTextRight() {
        return type;
    }

    @Override
    protected String getRenderText() {
        final String text = getText();
        if (configurationMeta.isDeprecated()) {
            return "<s>" + text + "</s>";
        }
        return text;
    }

    private boolean canCompleteKey() {
        final Hints hints = configurationMeta.getHints();
        if (hints == null) {
            return false;
        }
        if (!hints.getKeyHints().isEmpty()) {
            return true;
        }
        if (!hints.getKeyProviders().isEmpty()) {
            return true;
        }
        return isCompletableType(configurationMeta.getType());
    }

    private boolean canCompleteValue() {
        final Hints hints = configurationMeta.getHints();
        if (hints == null) {
            return false;
        }
        if (!hints.getValueHints().isEmpty()) {
            return true;
        }
        if (!hints.getValueProviders().isEmpty()) {
            return true;
        }
        return isCompletableType(configurationMeta.getType());
    }

    private Color getDeprecatedColor() {
        return Utils.isErrorDeprecated(configurationMeta)
            ? UIManager.getColor("nb.errorForeground")
            : UIManager.getColor("List.foreground");
    }

    private static boolean isCompletableType(String dataType) {
        return switch (dataType) {
            case "java.lang.Boolean",
                "java.nio.charset.Charset",
                "java.util.Locale",
                "org.springframework.core.io.Resource",
                "org.springframework.util.MimeType",
                "java.util.List<java.lang.Boolean>",
                "java.util.Set<java.lang.Boolean>",
                "java.util.List<org.springframework.core.io.Resource>",
                "java.util.Set<org.springframework.core.io.Resource>",
                "java.util.List<java.nio.charset.Charset>",
                "java.util.Set<java.nio.charset.Charset>",
                "java.util.List<java.util.Locale>",
                "java.util.Set<java.util.Locale>"
                -> true;
// TODO try to interpret the targetType as an enum
//                try {
//                    Object[] enumvals = cp.getClassLoader(true).loadClass(dataType).getEnumConstants();
//                    return enumvals != null;
//                } catch (ClassNotFoundException ex) {
//                    return false;
//                }
            default -> false;
        };
    }

    private static class AsyncCompletionQueryImpl extends BaseAsyncCompletionQuery {

        private final ConfigurationMetadataProperty configurationMeta;

        public AsyncCompletionQueryImpl(ConfigurationMetadataProperty configurationMeta) {
            this.configurationMeta = configurationMeta;
        }

        @Override
        protected CompletionDocumentation createCompletionDocumentation() {
            return new CfgPropCompletionDocumentation(configurationMeta);
        }
    }

}
