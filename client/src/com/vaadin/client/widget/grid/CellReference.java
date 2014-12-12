/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.widget.grid;

import com.vaadin.client.widgets.Grid;

/**
 * A data class which contains information which identifies a cell in a
 * {@link Grid}.
 * <p>
 * Since this class follows the <code>Flyweight</code>-pattern any instance of
 * this object is subject to change without the user knowing it and so should
 * not be stored anywhere outside of the method providing these instances.
 * 
 * @param <T>
 *            the type of the row object containing this cell
 */
public class CellReference<T> {
    private int columnIndex;
    private Grid.Column<?, T> column;
    private final RowReference<T> rowReference;

    public CellReference(RowReference<T> rowReference) {
        this.rowReference = rowReference;
    }

    /**
     * Sets the identifying information for this cell.
     * 
     * @param rowReference
     *            a reference to the row that contains this cell
     * @param columnIndex
     *            the index of the column
     * @param column
     *            the column object
     */
    public void set(int columnIndex, Grid.Column<?, T> column) {
        this.columnIndex = columnIndex;
        this.column = column;
    }

    /**
     * Gets the grid that contains the referenced cell.
     * 
     * @return the grid that contains referenced cell
     */
    public Grid<T> getGrid() {
        return rowReference.getGrid();
    }

    /**
     * Gets the row index of the row.
     * 
     * @return the index of the row
     */
    public int getRowIndex() {
        return rowReference.getRowIndex();
    }

    /**
     * Gets the row data object.
     * 
     * @return the row object
     */
    public T getRow() {
        return rowReference.getRow();
    }

    /**
     * Gets the index of the column.
     * 
     * @return the index of the column
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Gets the column objects.
     * 
     * @return the column object
     */
    public Grid.Column<?, T> getColumn() {
        return column;
    }

    /**
     * Gets the value of the cell.
     * 
     * @return the value of the cell
     */
    public Object getValue() {
        return getColumn().getValue(getRow());
    }
}