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

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

import com.github.alexfalappa.nbspringboot.projects.service.impl.HintSupport;
import javax.swing.ImageIcon;

/**
 * The implementation of {@code CompletionItem} for file objects.
 *
 * @author Alessandro Falappa
 */
public class FileObjectCompletionItem extends BaseCompletiomItem {

    private static final Logger logger = Logger.getLogger(FileObjectCompletionItem.class.getName());
    private final int caretOffset;
    private final FileObject fileObj;
    private final int dotOffset;

    public FileObjectCompletionItem(FileObject fileObj, int dotOffset, int caretOffset) {
        this.fileObj = fileObj;
        this.dotOffset = dotOffset;
        this.caretOffset = caretOffset;
    }

    @Override
    public String getText() {
        return fileObj.getNameExt();
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Accepted file object completion: {0}", FileUtil.getFileDisplayName(fileObj));
        }
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            // calculate the amount of chars to remove (by default from dot up to caret position)
            int lenToRemove = caretOffset - dotOffset;
            if (isOverwrite()) {
                // NOTE: the editor removes by itself the word at caret when ctrl + enter is pressed
                // the document state here is different from when the completion was invoked thus we have to
                // find again the offset of the equal sign in the line
                Element lineElement = doc.getParagraphElement(caretOffset);
                String line = doc.getText(lineElement.getStartOffset(), lineElement.getEndOffset() - lineElement.getStartOffset());
                int equalSignIndex = line.indexOf('=');
                int colonIndex = line.indexOf(':');
                int commaIndex = line.indexOf(',', dotOffset - lineElement.getStartOffset());
                if (equalSignIndex >= 0 && dotOffset < equalSignIndex) {
                    // from dot to equal sign
                    lenToRemove = lineElement.getStartOffset() + equalSignIndex - dotOffset;
                } else if (colonIndex >= 0 && dotOffset < colonIndex) {
                    // from dot to colon
                    lenToRemove = lineElement.getStartOffset() + colonIndex - dotOffset;
                } else if (commaIndex >= 0) {
                    // from dot to comma
                    lenToRemove = lineElement.getStartOffset() + commaIndex - dotOffset;
                } else {
                    // from dot to end of line (except line terminator)
                    lenToRemove = lineElement.getEndOffset() - 1 - dotOffset;
                }
            }
            // remove characters from dot then insert new text
            doc.remove(dotOffset, lenToRemove);
            if (fileObj.isRoot()) {
                logger.log(Level.FINER, "Adding filesystem root and continuing completion");
                doc.insertString(dotOffset, getText(), null);
            } else if (fileObj.isFolder()) {
                logger.log(Level.FINER, "Adding folder and continuing completion");
                doc.insertString(dotOffset, getText().concat("/"), null);
            } else {
                logger.log(Level.FINER, "Adding file and finishing completion");
                doc.insertString(dotOffset, getText(), null);
                Completion.get().hideAll();
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected ImageIcon getIcon() {
        return HintSupport.getIconFor(FileUtil.toFile(fileObj));
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public int getSortPriority() {
        return fileObj.isFolder() ? 0 : 1;
    }

    @Override
    public CharSequence getSortText() {
        return getText();
    }

    @Override
    public CharSequence getInsertPrefix() {
        return getText();
    }

}
