/*
 * Copyright 2016 the original author or authors.
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
package com.github.alexfalappa.nbspringboot.projects.customizer;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLEditorKit;

import org.openide.util.NbPreferences;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;

import com.github.alexfalappa.nbspringboot.PrefConstants;
import com.github.alexfalappa.nbspringboot.Utils;
import com.github.alexfalappa.nbspringboot.projects.service.api.SpringBootService;

import static com.github.alexfalappa.nbspringboot.PrefConstants.PREF_DEPR_ERROR_SHOW;
import static com.github.alexfalappa.nbspringboot.PrefConstants.PREF_DEPR_SORT_LAST;
import static java.awt.event.MouseEvent.BUTTON1;

/**
 * Dialog for choosing one Spring Boot configuration property in an ordered list of those available in the project.
 * <p>
 * Queries a {@link SpringBootService} for the configuration properties names.
 *
 * @author Alessandro Falappa
 */
public class CfgPropsDialog extends javax.swing.JDialog {

    private boolean okPressed = false;
    private final TreeSet<ConfigurationMetadataProperty> sortedProps;
    private final boolean bDeprErrorShow;

    /**
     * Creates new dialog CfgPropsDialog.
     *
     * @param parent the parent dialog.
     */
    public CfgPropsDialog(java.awt.Dialog parent) {
        super(parent, true);
        initComponents();
        // retrieve some flads from prefs
        final Preferences prefs = NbPreferences.forModule(PrefConstants.class);
        final boolean bDeprLast = prefs.getBoolean(PREF_DEPR_SORT_LAST, true);
        bDeprErrorShow = prefs.getBoolean(PREF_DEPR_ERROR_SHOW, true);
        // setup props sorting
        this.sortedProps = new TreeSet<>(new ConfigurationMetadataComparator(bDeprLast));
        // setup property list
        lCfgProps.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                final ConfigurationMetadataProperty selectedValue = lCfgProps.getSelectedValue();
                if (selectedValue != null) {
                    tpDetails.setText(Utils.cfgPropDetailsHtml(selectedValue));
                    tpDetails.setCaretPosition(0);
                }
            }
        });
        // set default button
        rootPane.setDefaultButton(bOk);
        // close dialog with ESC key
        final ActionListener escAction = ae -> {
            CfgPropsDialog.this.setVisible(false);
        };
        rootPane.registerKeyboardAction(escAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void loadCfgProps(SpringBootService bootService) {
        if (bootService == null) {
            return;
        }
        sortedProps.addAll(bootService.queryPropertyMetadata(null));
        filterProps(null);
        // setup filtering
        new ListFilterer(txFilter);
        pack();
    }

    public boolean okPressed() {
        return okPressed;
    }

    public String getSelectedPropName() {
        return lCfgProps.getSelectedValue().getId();
    }

    /** This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
     * this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bCancel = new javax.swing.JButton();
        bOk = new javax.swing.JButton();
        splitter = new javax.swing.JSplitPane();
        scroller1 = new javax.swing.JScrollPane();
        lCfgProps = new javax.swing.JList<>();
        scroller2 = new javax.swing.JScrollPane();
        tpDetails = new javax.swing.JTextPane();
        txFilter = new javax.swing.JTextField();
        lFilter = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getBundle(CfgPropsDialog.class).getString("CfgPropsDialog.title")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bCancel, org.openide.util.NbBundle.getBundle(CfgPropsDialog.class).getString("CfgPropsDialog.bCancel.text")); // NOI18N
        bCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bOk, org.openide.util.NbBundle.getBundle(CfgPropsDialog.class).getString("CfgPropsDialog.bOk.text")); // NOI18N
        bOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOkActionPerformed(evt);
            }
        });

        splitter.setBorder(null);
        splitter.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitter.setContinuousLayout(true);

        lCfgProps.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lCfgProps.setCellRenderer(new ConfigurationMetadataCellRenderer());
        lCfgProps.setVisibleRowCount(16);
        lCfgProps.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lCfgPropsMouseClicked(evt);
            }
        });
        scroller1.setViewportView(lCfgProps);

        splitter.setTopComponent(scroller1);

        tpDetails.setEditorKit(new HTMLEditorKit());
        tpDetails.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        tpDetails.setMinimumSize(new java.awt.Dimension(42, 63));
        tpDetails.setPreferredSize(new java.awt.Dimension(42, 63));
        scroller2.setViewportView(tpDetails);

        splitter.setRightComponent(scroller2);

        txFilter.setColumns(25);
        txFilter.setText(org.openide.util.NbBundle.getMessage(CfgPropsDialog.class, "CfgPropsDialog.txFilter.text")); // NOI18N
        txFilter.setToolTipText(org.openide.util.NbBundle.getMessage(CfgPropsDialog.class, "CfgPropsDialog.txFilter.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lFilter, org.openide.util.NbBundle.getMessage(CfgPropsDialog.class, "CfgPropsDialog.lFilter.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitter, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(bCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bOk))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txFilter)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bCancel, bOk});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitter)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bOk)
                    .addComponent(bCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOkActionPerformed
        okPressed = true;
        bCancelActionPerformed(null);
    }//GEN-LAST:event_bOkActionPerformed

    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_bCancelActionPerformed

    private void lCfgPropsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lCfgPropsMouseClicked
        if (evt.getButton() == BUTTON1 && evt.getClickCount() > 1) {
            bOkActionPerformed(null);
        }
    }//GEN-LAST:event_lCfgPropsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bOk;
    private javax.swing.JList<ConfigurationMetadataProperty> lCfgProps;
    private javax.swing.JLabel lFilter;
    private javax.swing.JScrollPane scroller1;
    private javax.swing.JScrollPane scroller2;
    private javax.swing.JSplitPane splitter;
    private javax.swing.JTextPane tpDetails;
    private javax.swing.JTextField txFilter;
    // End of variables declaration//GEN-END:variables

    private void filterProps(String filter) {
        DefaultListModel<ConfigurationMetadataProperty> dlmCfgProps = new DefaultListModel<>();
        for (ConfigurationMetadataProperty item : sortedProps) {
            if (filter == null || item.getId().contains(filter)) {
                if (Utils.isErrorDeprecated(item)) {
                    if (bDeprErrorShow) {
                        dlmCfgProps.addElement(item);
                    }
                } else {
                    dlmCfgProps.addElement(item);
                }
            }
        }
        lCfgProps.setModel(dlmCfgProps);
        if (!dlmCfgProps.isEmpty()) {
            lCfgProps.setSelectedIndex(0);
        }
    }

    private static class ConfigurationMetadataCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ConfigurationMetadataProperty prop) {
                if (prop.isDeprecated()) {
                    setText(String.format("<html><s>%s", prop.getId()));
                    if (Utils.isErrorDeprecated(prop)) {
                        setForeground(UIManager.getColor("nb.errorForeground"));
                    }
                } else {
                    setText(prop.getId());
                }
            }
            return this;
        }

    }

    private static class ConfigurationMetadataComparator implements Comparator<ConfigurationMetadataProperty> {

        private final boolean sortDeprLast;

        public ConfigurationMetadataComparator(boolean sortDeprLast) {
            this.sortDeprLast = sortDeprLast;
        }

        @Override
        public int compare(ConfigurationMetadataProperty p1, ConfigurationMetadataProperty p2) {
            if (!sortDeprLast) {
                return p1.getId().compareTo(p2.getId());
            } else {
                boolean d1 = p1.isDeprecated();
                boolean d2 = p2.isDeprecated();
                if (d1 && !d2) {
                    return 1;
                } else if (d2 && !d1) {
                    return -1;
                } else {
                    return p1.getId().compareTo(p2.getId());
                }
            }
        }

    }

    private class ListFilterer extends KeyAdapter implements DocumentListener {

        private final JTextField txtField;

        public ListFilterer(JTextField txtField) {
            this.txtField = txtField;
            txtField.getDocument().addDocumentListener(this);
            txtField.addKeyListener(this);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            doFilter();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            doFilter();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            doFilter();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            final int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ESCAPE) {
                final String filter = txtField.getText();
                if (filter == null || filter.isEmpty()) {
                    // close dialog if filter textfield is empty
                    setVisible(false);
                } else {
                    // clear textfield
                    txtField.setText(null);
                    e.consume();
                }
            } else {
                final int size = lCfgProps.getModel().getSize();
                // short circuit if empty size
                if (size == 0) {
                    return;
                }
                int selIdx = lCfgProps.getSelectedIndex();
                final int pageStep = lCfgProps.getVisibleRowCount();
                // calculate new index
                switch (keyCode) {
                    case KeyEvent.VK_PAGE_UP -> {
                        selIdx -= pageStep;
                        // clamp to [0 : size-1] range
                        if (selIdx < 0) {
                            selIdx = 0;
                        } else if (selIdx >= size) {
                            selIdx = size - 1;
                        }
                    }
                    case KeyEvent.VK_UP -> {
                        selIdx -= 1;
                        // wrap if needed
                        if (selIdx < 0) {
                            selIdx += size;
                        } else if (selIdx >= size) {
                            selIdx -= size;
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        selIdx += 1;
                        // wrap if needed
                        if (selIdx < 0) {
                            selIdx += size;
                        } else if (selIdx >= size) {
                            selIdx -= size;
                        }
                    }
                    case KeyEvent.VK_PAGE_DOWN -> {
                        selIdx += pageStep;
                        // clamp to [0 : size-1] range
                        if (selIdx < 0) {
                            selIdx = 0;
                        } else if (selIdx >= size) {
                            selIdx = size - 1;
                        }
                    }
                }
                // move properties list selection and scroll to it
                lCfgProps.setSelectedIndex(selIdx);
                lCfgProps.scrollRectToVisible(lCfgProps.getCellBounds(selIdx, selIdx));
            }
        }

        private void doFilter() {
            String text = txFilter.getText().toLowerCase();
            if (!text.isEmpty()) {
                filterProps(text);
            } else {
                filterProps(null);
            }
        }

    }
}
