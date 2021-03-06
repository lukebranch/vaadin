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

package com.vaadin.shared.ui.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

/**
 * The shared state for the {@link com.vaadin.ui.components.grid.Grid} component
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridState extends AbstractComponentState {

    /**
     * A description of which of the three bundled SelectionModels is currently
     * in use.
     * <p>
     * Used as a data transfer object instead of the client/server ones, because
     * they don't know about each others classes.
     * 
     * @see com.vaadin.ui.components.grid.Grid.SelectionMode
     * @see com.vaadin.client.ui.grid.Grid.SelectionMode
     */
    public enum SharedSelectionMode {
        /**
         * Representation of a single selection mode
         * 
         * @see com.vaadin.ui.components.grid.Grid.SelectionMode#SINGLE
         * @see com.vaadin.client.ui.grid.Grid.SelectionMode#SINGLE
         */
        SINGLE,

        /**
         * Representation of a multiselection mode
         * 
         * @see com.vaadin.ui.components.grid.Grid.SelectionMode#MULTI
         * @see com.vaadin.client.ui.grid.Grid.SelectionMode#MULTI
         */
        MULTI,

        /**
         * Representation of a no-selection mode
         * 
         * @see com.vaadin.ui.components.grid.Grid.SelectionMode#NONE
         * @see com.vaadin.client.ui.grid.Grid.SelectionMode#NONE
         */
        NONE;
    }

    /**
     * The default value for height-by-rows for both GWT widgets
     * {@link com.vaadin.ui.components.grid Grid} and
     * {@link com.vaadin.client.ui.grid.Escalator Escalator}
     */
    public static final double DEFAULT_HEIGHT_BY_ROWS = 10.0d;

    /**
     * The key in which a row's data can be found
     * 
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, String)
     */
    public static final String JSONKEY_DATA = "d";

    /**
     * The key in which a row's own key can be found
     * 
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, String)
     */
    public static final String JSONKEY_ROWKEY = "k";

    /**
     * The key in which a row's generated style can be found
     * 
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, String)
     */
    public static final String JSONKEY_ROWSTYLE = "rs";

    /**
     * The key in which a generated styles for a row's cells can be found
     * 
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, String)
     */
    public static final String JSONKEY_CELLSTYLES = "cs";

    /**
     * Columns in grid.
     */
    public List<GridColumnState> columns = new ArrayList<GridColumnState>();

    /**
     * Column order in grid.
     */
    public List<String> columnOrder = new ArrayList<String>();

    public GridStaticSectionState header = new GridStaticSectionState();

    public GridStaticSectionState footer = new GridStaticSectionState();

    /** The number of frozen columns */
    public int frozenColumnCount = 0;

    /** The height of the Grid in terms of body rows. */
    @DelegateToWidget
    public double heightByRows = DEFAULT_HEIGHT_BY_ROWS;

    /** The mode by which Grid defines its height. */
    @DelegateToWidget
    public HeightMode heightMode = HeightMode.CSS;

    // instantiated just to avoid NPEs
    public List<String> selectedKeys = new ArrayList<String>();

    public SharedSelectionMode selectionMode;

    /** Keys of the currently sorted columns */
    public String[] sortColumns = new String[0];

    /** Directions for each sorted column */
    public SortDirection[] sortDirs = new SortDirection[0];

    /** The enabled state of the editor row */
    public boolean editorRowEnabled = false;

    /** Whether row data might contain generated row styles */
    public boolean hasRowStyleGenerator;
    /** Whether row data might contain generated cell styles */
    public boolean hasCellStyleGenerator;

}
