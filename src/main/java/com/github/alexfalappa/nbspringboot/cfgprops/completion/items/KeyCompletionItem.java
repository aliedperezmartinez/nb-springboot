/*
 * Copyright 2019 the original author or authors.
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.netbeans.api.editor.completion.Completion;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.springframework.boot.configurationmetadata.ValueHint;


/**
 * The implementation of {@code CompletionItem} for Spring Boot key hints.
 * <p>
 * Uses an {@code ValueHint} to render the completion item and spawn the documentation display.
 *
 * @author Alessandro Falappa
 */
public class KeyCompletionItem extends HintCompletionItem {

    private static final Logger logger = Logger.getLogger(KeyCompletionItem.class.getName());
    private static final ImageIcon fieldIcon = new ImageIcon(ImageUtilities.loadImage(
            "com/github/alexfalappa/nbspringboot/cfgprops/completion/springboot-key.png"));

    public KeyCompletionItem(ValueHint hint, int dotOffset, int caretOffset) {
        super(hint, caretOffset, dotOffset);
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        final ValueHint hint = getHint();
        logger.log(Level.FINER, "Accepted key value hint: {0}", hint.toString());
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            // calculate the amount of chars to remove (by default from dot up to caret position)
            int lenToRemove = caretOffset - dotOffset;
            int equalSignIndex = -1;
            int colonIndex = -1;
            if (isOverwrite()) {
                // NOTE: the editor removes by itself the word at caret when ctrl + enter is pressed
                // the document state here is different from when the completion was invoked thus we have to
                // find again the offset of the equal sign in the line
                Element lineElement = doc.getParagraphElement(caretOffset);
                String line = doc.getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset());
                equalSignIndex = line.indexOf('=');
                colonIndex = line.indexOf(':');
                if (equalSignIndex >= 0) {
                    // from dot to equal sign
                    lenToRemove = lineElement.getStartOffset() + equalSignIndex - dotOffset;
                } else if (colonIndex >= 0) {
                    // from dot to colon
                    lenToRemove = lineElement.getStartOffset() + colonIndex - dotOffset;
                }
            }
            // remove characters from dot then insert new text
            doc.remove(dotOffset, lenToRemove);
            if (equalSignIndex < 0 && colonIndex < 0) {
                logger.log(Level.FINER, "Adding equal sign and continuing completion");
                doc.insertString(dotOffset, hint.getValue().toString().concat("="), null);
            } else {
                logger.log(Level.FINER, "Finish completion with no added chars");
                doc.insertString(dotOffset, hint.getValue().toString(), null);
                Completion.get().hideAll();
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected ImageIcon getIcon() {
        return fieldIcon;
    }

}
