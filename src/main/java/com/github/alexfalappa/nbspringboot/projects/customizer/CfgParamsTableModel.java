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

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import static java.util.stream.Collectors.toList;

/**
 * An editable {@code JTable} model holding {@link CfgOverride} objects.
 * <p>
 * Backed by a {@link LinkedList}.
 *
 * @author Alessandro Falappa
 */
public class CfgParamsTableModel extends AbstractTableModel {

    List<CfgOverride> overrides = new LinkedList<>();

    @Override
    public int getRowCount() {
        return overrides.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CfgOverride co = overrides.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> co.enabled;
            case 1 -> co.name;
            case 2 -> co.value;
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> "En.";
            case 1 -> "Name";
            case 2 -> "Value";
            default -> super.getColumnName(columnIndex);
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Boolean.class;
            case 1, 2 -> String.class;
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < overrides.size()) {
            CfgOverride co = overrides.get(rowIndex);
            switch (columnIndex) {
                case 0 -> {
                    if (aValue instanceof Boolean aBoolean) {
                        co.enabled = aBoolean;
                    }
                }
                case 1 -> {
                    if (aValue instanceof String string) {
                        co.name = string;
                    }
                }
                case 2 -> {
                    if (aValue instanceof String string) {
                        co.value = string;
                    }
                }
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public List<CfgOverride> getOverrides() {
        return overrides;
    }

    public List<CfgOverride> getEnabledOverrides() {
        return overrides.stream()
            .filter(ov -> ov.enabled)
            .collect(toList());
    }

    public CfgOverride getOverrideAt(int idx) {
        return overrides.get(idx);
    }

    public void addOverride(CfgOverride override) {
        overrides.add(override);
        final int numOverrides = overrides.size();
        fireTableRowsInserted(numOverrides - 1, numOverrides - 1);
    }

    public CfgOverride removeOverride(int selRow) {
        final CfgOverride removed = overrides.remove(selRow);
        fireTableRowsDeleted(selRow, selRow);
        return removed;
    }

    public void removeAllOverrides() {
        int oldSize = overrides.size();
        overrides.clear();
        fireTableRowsDeleted(0, oldSize - 1);
    }

}
