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
package com.vaadin.tests.components.grid.basicfeatures;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SortEvent;
import com.vaadin.event.SortEvent.SortListener;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.SortDirection;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Grid.RowStyleGenerator;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderer.DateRenderer;
import com.vaadin.ui.renderer.HtmlRenderer;
import com.vaadin.ui.renderer.NumberRenderer;

/**
 * Tests the basic features like columns, footers and headers
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridBasicFeatures extends AbstractComponentTest<Grid> {

    public static final String ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4 = "Row numbers for 3/4";
    public static final String ROW_STYLE_GENERATOR_NONE = "None";
    public static final String ROW_STYLE_GENERATOR_ROW_NUMBERS = "Row numbers";
    public static final String CELL_STYLE_GENERATOR_NONE = "None";
    public static final String CELL_STYLE_GENERATOR_PROPERTY_TO_STRING = "Property to string";
    public static final String CELL_STYLE_GENERATOR_SPECIAL = "Special for 1/4 Column 1";
    private static final int MANUALLY_FORMATTED_COLUMNS = 5;
    public static final int COLUMNS = 12;
    public static final int ROWS = 1000;

    private int columnGroupRows = 0;
    private IndexedContainer ds;
    private Grid grid;

    @Override
    @SuppressWarnings("unchecked")
    protected Grid constructComponent() {

        // Build data source
        ds = new IndexedContainer() {
            @Override
            public List<Object> getItemIds(int startIndex, int numberOfIds) {
                log("Requested items " + startIndex + " - "
                        + (startIndex + numberOfIds));
                return super.getItemIds(startIndex, numberOfIds);
            }
        };

        {
            int col = 0;
            for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; col++) {
                ds.addContainerProperty(getColumnProperty(col), String.class,
                        "");
            }

            ds.addContainerProperty(getColumnProperty(col++), Integer.class,
                    Integer.valueOf(0));
            ds.addContainerProperty(getColumnProperty(col++), Date.class,
                    new Date());
            ds.addContainerProperty(getColumnProperty(col++), String.class, "");

            // Random numbers
            ds.addContainerProperty(getColumnProperty(col++), Integer.class, 0);
            ds.addContainerProperty(getColumnProperty(col++), Integer.class, 0);

        }

        {
            Random rand = new Random();
            rand.setSeed(13334);
            long timestamp = 0;
            for (int row = 0; row < ROWS; row++) {
                Item item = ds.addItem(Integer.valueOf(row));
                int col = 0;
                for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; col++) {
                    item.getItemProperty(getColumnProperty(col)).setValue(
                            "(" + row + ", " + col + ")");
                }
                item.getItemProperty(getColumnProperty(1)).setReadOnly(true);

                item.getItemProperty(getColumnProperty(col++)).setValue(
                        Integer.valueOf(row));
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        new Date(timestamp));
                timestamp += 91250000; // a bit over a day, just to get
                                       // variation
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        "<b>" + row + "</b>");

                // Random numbers
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        rand.nextInt());
                // Random between 0 - 5 to test multisorting
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        rand.nextInt(5));
            }
        }

        // Create grid
        Grid grid = new Grid(ds);

        {
            int col = grid.getContainerDataSource().getContainerPropertyIds()
                    .size()
                    - MANUALLY_FORMATTED_COLUMNS;
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new NumberRenderer(new DecimalFormat("0,000.00",
                            DecimalFormatSymbols.getInstance(new Locale("fi",
                                    "FI")))));
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new DateRenderer(new SimpleDateFormat("dd.MM.yy HH:mm")));
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new HtmlRenderer());
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new NumberRenderer());
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new NumberRenderer());
        }

        // Create footer
        grid.appendFooterRow();
        grid.setFooterVisible(false);

        // Add footer values (header values are automatically created)
        for (int col = 0; col < COLUMNS; col++) {
            grid.getFooterRow(0).getCell(getColumnProperty(col))
                    .setText("Footer " + col);
        }

        // Set varying column widths
        for (int col = 0; col < COLUMNS; col++) {
            grid.getColumn(getColumnProperty(col)).setWidth(100 + col * 50);
        }

        grid.addSortListener(new SortListener() {
            @Override
            public void sort(SortEvent event) {

                log("SortOrderChangeEvent: isUserOriginated? "
                        + event.isUserOriginated());
            }
        });

        grid.setSelectionMode(SelectionMode.NONE);

        grid.getEditorRowField(getColumnProperty(3)).setReadOnly(true);

        createGridActions();

        createColumnActions();

        createPropertyActions();

        createHeaderActions();

        createFooterActions();

        createRowActions();

        createEditorRowActions();

        addHeightActions();

        createClickAction("Column 1 starts with \"(23\"", "Filter",
                new Command<Grid, Void>() {
                    @Override
                    public void execute(Grid grid, Void value, Object data) {
                        ds.addContainerFilter(new Filter() {

                            @Override
                            public boolean passesFilter(Object itemId, Item item)
                                    throws UnsupportedOperationException {
                                return item.getItemProperty("Column 1")
                                        .getValue().toString()
                                        .startsWith("(23");
                            }

                            @Override
                            public boolean appliesToProperty(Object propertyId) {
                                return propertyId.equals("Column 1");
                            }
                        });
                    }
                }, null);

        this.grid = grid;
        return grid;
    }

    protected void createGridActions() {
        LinkedHashMap<String, String> primaryStyleNames = new LinkedHashMap<String, String>();
        primaryStyleNames.put("v-grid", "v-grid");
        primaryStyleNames.put("v-escalator", "v-escalator");
        primaryStyleNames.put("my-grid", "my-grid");

        createMultiClickAction("Primary style name", "State",
                primaryStyleNames, new Command<Grid, String>() {

                    @Override
                    public void execute(Grid grid, String value, Object data) {
                        grid.setPrimaryStyleName(value);

                    }
                }, primaryStyleNames.get("v-grid"));

        LinkedHashMap<String, SelectionMode> selectionModes = new LinkedHashMap<String, Grid.SelectionMode>();
        selectionModes.put("single", SelectionMode.SINGLE);
        selectionModes.put("multi", SelectionMode.MULTI);
        selectionModes.put("none", SelectionMode.NONE);
        createSelectAction("Selection mode", "State", selectionModes, "none",
                new Command<Grid, Grid.SelectionMode>() {
                    @Override
                    public void execute(Grid grid, SelectionMode selectionMode,
                            Object data) {
                        grid.setSelectionMode(selectionMode);
                    }
                });

        LinkedHashMap<String, Integer> selectionLimits = new LinkedHashMap<String, Integer>();
        selectionLimits.put("2", Integer.valueOf(2));
        selectionLimits.put("1000", Integer.valueOf(1000));
        selectionLimits.put("Integer.MAX_VALUE",
                Integer.valueOf(Integer.MAX_VALUE));
        createSelectAction("Selection limit", "State", selectionLimits, "1000",
                new Command<Grid, Integer>() {
                    @Override
                    public void execute(Grid grid, Integer limit, Object data) {
                        if (!(grid.getSelectionModel() instanceof MultiSelectionModel)) {
                            grid.setSelectionMode(SelectionMode.MULTI);
                        }

                        ((MultiSelectionModel) grid.getSelectionModel())
                                .setSelectionLimit(limit.intValue());
                    }
                });

        LinkedHashMap<String, List<SortOrder>> sortableProperties = new LinkedHashMap<String, List<SortOrder>>();
        for (Object propertyId : ds.getSortableContainerPropertyIds()) {
            sortableProperties.put(propertyId + ", ASC", Sort.by(propertyId)
                    .build());
            sortableProperties.put(propertyId + ", DESC",
                    Sort.by(propertyId, SortDirection.DESCENDING).build());
        }
        createSelectAction("Sort by column", "State", sortableProperties,
                "Column 9, ascending", new Command<Grid, List<SortOrder>>() {
                    @Override
                    public void execute(Grid grid, List<SortOrder> sortOrder,
                            Object data) {
                        grid.setSortOrder(sortOrder);
                    }
                });

        createBooleanAction("Reverse Grid Columns", "State", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        List<Object> ids = new ArrayList<Object>();
                        ids.addAll(ds.getContainerPropertyIds());
                        if (!value) {
                            c.setColumnOrder(ids.toArray());
                        } else {
                            Object[] idsArray = new Object[ids.size()];
                            for (int i = 0; i < ids.size(); ++i) {
                                idsArray[i] = ids.get((ids.size() - 1) - i);
                            }
                            c.setColumnOrder(idsArray);
                        }
                    }
                });

        LinkedHashMap<String, CellStyleGenerator> cellStyleGenerators = new LinkedHashMap<String, CellStyleGenerator>();
        LinkedHashMap<String, RowStyleGenerator> rowStyleGenerators = new LinkedHashMap<String, RowStyleGenerator>();
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_NONE, null);
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_ROW_NUMBERS,
                new RowStyleGenerator() {
                    @Override
                    public String getStyle(RowReference rowReference) {
                        return "row" + rowReference.getItemId();
                    }
                });
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4,
                new RowStyleGenerator() {
                    @Override
                    public String getStyle(RowReference rowReference) {
                        int rowIndex = ((Integer) rowReference.getItemId())
                                .intValue();

                        if (rowIndex % 4 == 0) {
                            return null;
                        } else {
                            return "row" + rowReference.getItemId();
                        }
                    }
                });
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_NONE, null);
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_PROPERTY_TO_STRING,
                new CellStyleGenerator() {
                    @Override
                    public String getStyle(CellReference cellReference) {
                        return cellReference.getPropertyId().toString()
                                .replace(' ', '-');
                    }
                });
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_SPECIAL,
                new CellStyleGenerator() {
                    @Override
                    public String getStyle(CellReference cellReference) {
                        int rowIndex = ((Integer) cellReference.getItemId())
                                .intValue();
                        Object propertyId = cellReference.getPropertyId();
                        if (rowIndex % 4 == 1) {
                            return null;
                        } else if (rowIndex % 4 == 3
                                && "Column 1".equals(propertyId)) {
                            return null;
                        }
                        return propertyId.toString().replace(' ', '_');
                    }
                });

        createSelectAction("Row style generator", "State", rowStyleGenerators,
                CELL_STYLE_GENERATOR_NONE,
                new Command<Grid, RowStyleGenerator>() {
                    @Override
                    public void execute(Grid grid, RowStyleGenerator generator,
                            Object data) {
                        grid.setRowStyleGenerator(generator);
                    }
                });

        createSelectAction("Cell style generator", "State",
                cellStyleGenerators, CELL_STYLE_GENERATOR_NONE,
                new Command<Grid, CellStyleGenerator>() {
                    @Override
                    public void execute(Grid grid,
                            CellStyleGenerator generator, Object data) {
                        grid.setCellStyleGenerator(generator);
                    }
                });

        LinkedHashMap<String, Integer> frozenOptions = new LinkedHashMap<String, Integer>();
        for (int i = -1; i <= COLUMNS; i++) {
            frozenOptions.put(String.valueOf(i), Integer.valueOf(i));
        }
        createSelectAction("Frozen column count", "State", frozenOptions, "0",
                new Command<Grid, Integer>() {
                    @Override
                    public void execute(Grid c, Integer value, Object data) {
                        c.setFrozenColumnCount(value.intValue());
                    }
                });
    }

    protected void createHeaderActions() {
        createCategory("Header", null);

        createBooleanAction("Visible", "Header", true,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.setHeaderVisible(value);
                    }
                });

        LinkedHashMap<String, String> defaultRows = new LinkedHashMap<String, String>();
        defaultRows.put("Top", "Top");
        defaultRows.put("Bottom", "Bottom");
        defaultRows.put("Unset", "Unset");

        createMultiClickAction("Default row", "Header", defaultRows,
                new Command<Grid, String>() {

                    @Override
                    public void execute(Grid grid, String value, Object data) {
                        HeaderRow defaultRow = null;
                        if (value.equals("Top")) {
                            defaultRow = grid.getHeaderRow(0);
                        } else if (value.equals("Bottom")) {
                            defaultRow = grid.getHeaderRow(grid
                                    .getHeaderRowCount() - 1);
                        }
                        grid.setDefaultHeaderRow(defaultRow);
                    }

                }, defaultRows.get("Top"));

        createClickAction("Prepend row", "Header", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.prependHeaderRow();
            }

        }, null);
        createClickAction("Append row", "Header", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.appendHeaderRow();
            }

        }, null);

        createClickAction("Remove top row", "Header",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeHeaderRow(0);
                    }

                }, null);
        createClickAction("Remove bottom row", "Header",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeHeaderRow(grid.getHeaderRowCount() - 1);
                    }

                }, null);
    }

    protected void createFooterActions() {
        createCategory("Footer", null);

        createBooleanAction("Visible", "Footer", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.setFooterVisible(value);
                    }
                });

        createClickAction("Prepend row", "Footer", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.prependFooterRow();
            }

        }, null);
        createClickAction("Append row", "Footer", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.appendFooterRow();
            }

        }, null);

        createClickAction("Remove top row", "Footer",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeFooterRow(0);
                    }

                }, null);
        createClickAction("Remove bottom row", "Footer",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeFooterRow(grid.getFooterRowCount() - 1);
                    }

                }, null);
    }

    protected void createColumnActions() {
        createCategory("Columns", null);

        for (int c = 0; c < COLUMNS; c++) {
            final int index = c;
            createCategory(getColumnProperty(c), "Columns");

            createClickAction("Add / Remove", getColumnProperty(c),
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            String columnProperty = getColumnProperty((Integer) data);
                            if (grid.getColumn(columnProperty) == null) {
                                grid.addColumn(columnProperty);
                            } else {
                                grid.removeColumn(columnProperty);
                            }
                        }
                    }, null, c);

            createBooleanAction("Sortable", getColumnProperty(c), true,
                    new Command<Grid, Boolean>() {

                        @Override
                        public void execute(Grid grid, Boolean value,
                                Object columnIndex) {
                            Object propertyId = getColumnProperty((Integer) columnIndex);
                            Column column = grid.getColumn(propertyId);
                            column.setSortable(value);
                        }
                    }, c);

            createCategory("Column " + c + " Width", getColumnProperty(c));

            createClickAction("Auto", "Column " + c + " Width",
                    new Command<Grid, Integer>() {

                        @Override
                        public void execute(Grid grid, Integer value,
                                Object columnIndex) {
                            Object propertyId = getColumnProperty((Integer) columnIndex);
                            Column column = grid.getColumn(propertyId);
                            column.setWidthUndefined();
                        }
                    }, -1, c);

            createClickAction("25.5px", "Column " + c + " Width",
                    new Command<Grid, Void>() {
                        @Override
                        @SuppressWarnings("boxing")
                        public void execute(Grid grid, Void value,
                                Object columnIndex) {
                            grid.getColumns().get((Integer) columnIndex)
                                    .setWidth(25.5);
                        }
                    }, null, c);

            for (int w = 50; w < 300; w += 50) {
                createClickAction(w + "px", "Column " + c + " Width",
                        new Command<Grid, Integer>() {

                            @Override
                            public void execute(Grid grid, Integer value,
                                    Object columnIndex) {
                                Object propertyId = getColumnProperty((Integer) columnIndex);
                                Column column = grid.getColumn(propertyId);
                                column.setWidth(value);
                            }
                        }, w, c);
            }

            LinkedHashMap<String, GridStaticCellType> defaultRows = new LinkedHashMap<String, GridStaticCellType>();
            defaultRows.put("Text Header", GridStaticCellType.TEXT);
            defaultRows.put("Html Header ", GridStaticCellType.HTML);
            defaultRows.put("Widget Header", GridStaticCellType.WIDGET);

            createMultiClickAction("Header Type", getColumnProperty(c),
                    defaultRows, new Command<Grid, GridStaticCellType>() {

                        @Override
                        public void execute(Grid grid,
                                GridStaticCellType value, Object columnIndex) {
                            final Object propertyId = getColumnProperty((Integer) columnIndex);
                            final HeaderCell cell = grid.getDefaultHeaderRow()
                                    .getCell(propertyId);
                            switch (value) {
                            case TEXT:
                                cell.setText("Text Header");
                                break;
                            case HTML:
                                cell.setHtml("HTML Header");
                                break;
                            case WIDGET:
                                cell.setComponent(new Button("Button Header",
                                        new ClickListener() {

                                            @Override
                                            public void buttonClick(
                                                    ClickEvent event) {
                                                log("Button clicked!");
                                            }
                                        }));
                            default:
                                break;
                            }
                        }

                    }, c);

            defaultRows = new LinkedHashMap<String, GridStaticCellType>();
            defaultRows.put("Text Footer", GridStaticCellType.TEXT);
            defaultRows.put("Html Footer", GridStaticCellType.HTML);
            defaultRows.put("Widget Footer", GridStaticCellType.WIDGET);

            createMultiClickAction("Footer Type", getColumnProperty(c),
                    defaultRows, new Command<Grid, GridStaticCellType>() {

                        @Override
                        public void execute(Grid grid,
                                GridStaticCellType value, Object columnIndex) {
                            final Object propertyId = getColumnProperty((Integer) columnIndex);
                            final FooterCell cell = grid.getFooterRow(0)
                                    .getCell(propertyId);
                            switch (value) {
                            case TEXT:
                                cell.setText("Text Footer");
                                break;
                            case HTML:
                                cell.setHtml("HTML Footer");
                                break;
                            case WIDGET:
                                cell.setComponent(new Button("Button Footer",
                                        new ClickListener() {

                                            @Override
                                            public void buttonClick(
                                                    ClickEvent event) {
                                                log("Button clicked!");
                                            }
                                        }));
                            default:
                                break;
                            }
                        }

                    }, c);
        }
    }

    private static String getColumnProperty(int c) {
        return "Column " + c;
    }

    protected void createPropertyActions() {
        createCategory("Properties", null);

        createBooleanAction("Prepend property", "Properties", false,
                new Command<Grid, Boolean>() {
                    private final Object propertyId = new Object();

                    @Override
                    public void execute(Grid c, Boolean enable, Object data) {
                        if (enable.booleanValue()) {
                            ds.addContainerProperty(propertyId, String.class,
                                    "property value");
                            grid.getColumn(propertyId).setHeaderCaption(
                                    "new property");
                            grid.setColumnOrder(propertyId);
                        } else {
                            ds.removeContainerProperty(propertyId);
                        }
                    }
                }, null);
    }

    protected void createRowActions() {
        createCategory("Body rows", null);

        class NewRowCommand implements Command<Grid, String> {
            private final int index;

            public NewRowCommand() {
                this(0);
            }

            public NewRowCommand(int index) {
                this.index = index;
            }

            @Override
            public void execute(Grid c, String value, Object data) {
                Item item = ds.addItemAt(index, new Object());
                for (int i = 0; i < COLUMNS; i++) {
                    Class<?> type = ds.getType(getColumnProperty(i));
                    if (String.class.isAssignableFrom(type)) {
                        Property<String> itemProperty = getProperty(item, i);
                        itemProperty.setValue("newcell: " + i);
                    } else if (Integer.class.isAssignableFrom(type)) {
                        Property<Integer> itemProperty = getProperty(item, i);
                        itemProperty.setValue(Integer.valueOf(i));
                    } else {
                        // let the default value be taken implicitly.
                    }
                }
            }

            private <T extends Object> Property<T> getProperty(Item item, int i) {
                @SuppressWarnings("unchecked")
                Property<T> itemProperty = item
                        .getItemProperty(getColumnProperty(i));
                return itemProperty;
            }
        }
        final NewRowCommand newRowCommand = new NewRowCommand();

        createClickAction("Add 18 rows", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        for (int i = 0; i < 18; i++) {
                            newRowCommand.execute(c, value, data);
                        }
                    }
                }, null);

        createClickAction("Add first row", "Body rows", newRowCommand, null);

        createClickAction("Add second row", "Body rows", new NewRowCommand(1),
                null);

        createClickAction("Remove first row", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        ds.removeItem(firstItemId);
                    }
                }, null);

        createClickAction("Remove 18 first rows", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        for (int i = 0; i < 18; i++) {
                            Object firstItemId = ds.getIdByIndex(0);
                            ds.removeItem(firstItemId);
                        }
                    }
                }, null);

        createClickAction("Modify first row (getItemProperty)", "Body rows",
                new Command<Grid, String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        Item item = ds.getItem(firstItemId);
                        for (int i = 0; i < COLUMNS; i++) {
                            Property<?> property = item
                                    .getItemProperty(getColumnProperty(i));
                            if (property.getType().equals(String.class)) {
                                ((Property<String>) property)
                                        .setValue("modified: " + i);
                            }
                        }
                    }
                }, null);

        createClickAction("Modify first row (getContainerProperty)",
                "Body rows", new Command<Grid, String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        for (Object containerPropertyId : ds
                                .getContainerPropertyIds()) {
                            Property<?> property = ds.getContainerProperty(
                                    firstItemId, containerPropertyId);
                            if (property.getType().equals(String.class)) {
                                ((Property<String>) property)
                                        .setValue("modified: "
                                                + containerPropertyId);
                            }
                        }
                    }
                }, null);

        createBooleanAction("Select first row", "Body rows", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid grid, Boolean select, Object data) {
                        final Object firstItemId = grid
                                .getContainerDataSource().firstItemId();
                        if (select.booleanValue()) {
                            grid.select(firstItemId);
                        } else {
                            grid.deselect(firstItemId);
                        }
                    }
                });

        createClickAction("Remove all rows", "Body rows",
                new Command<Grid, String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        ds.removeAllItems();
                    }
                }, null);
    }

    protected void createEditorRowActions() {
        createBooleanAction("Enabled", "Editor row", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        c.setEditorRowEnabled(value);
                    }
                });

        createClickAction("Edit item 5", "Editor row",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.editItem(5);
                    }
                }, null);

        createClickAction("Edit item 100", "Editor row",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.editItem(100);
                    }
                }, null);
        createClickAction("Save", "Editor row", new Command<Grid, String>() {
            @Override
            public void execute(Grid c, String value, Object data) {
                try {
                    c.saveEditorRow();
                } catch (CommitException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, null);
        createClickAction("Cancel edit", "Editor row",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.cancelEditorRow();
                    }
                }, null);
    }

    @SuppressWarnings("boxing")
    protected void addHeightActions() {
        createCategory("Height by Rows", "Size");

        createBooleanAction("HeightMode Row", "Size", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid c, Boolean heightModeByRows,
                            Object data) {
                        c.setHeightMode(heightModeByRows ? HeightMode.ROW
                                : HeightMode.CSS);
                    }
                }, null);

        addActionForHeightByRows(1d / 3d);
        addActionForHeightByRows(2d / 3d);

        for (double i = 1; i < 5; i++) {
            addActionForHeightByRows(i);
            addActionForHeightByRows(i + 1d / 3d);
            addActionForHeightByRows(i + 2d / 3d);
        }

        Command<Grid, String> sizeCommand = new Command<Grid, String>() {
            @Override
            public void execute(Grid grid, String height, Object data) {
                grid.setHeight(height);
            }
        };

        createCategory("Height", "Size");
        // header 20px + scrollbar 16px = 36px baseline
        createClickAction("86px (no drag scroll select)", "Height",
                sizeCommand, "86px");
        createClickAction("96px (drag scroll select limit)", "Height",
                sizeCommand, "96px");
        createClickAction("106px (drag scroll select enabled)", "Height",
                sizeCommand, "106px");
    }

    private void addActionForHeightByRows(final Double i) {
        DecimalFormat df = new DecimalFormat("0.00");
        createClickAction(df.format(i) + " rows", "Height by Rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.setHeightByRows(i);
                    }
                }, null);
    }

    @Override
    protected Integer getTicketNumber() {
        return 12829;
    }

    @Override
    protected Class<Grid> getTestClass() {
        return Grid.class;
    }

}
