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
package com.vaadin.tests.components.grid.basicfeatures.server;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridCellStyleGeneratorTest extends GridBasicFeaturesTest {
    @Test
    public void testStyleNameGeneratorScrolling() throws Exception {
        openTestURL();

        selectRowStyleNameGenerator(GridBasicFeatures.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(GridBasicFeatures.CELL_STYLE_GENERATOR_SPECIAL);

        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell3_2 = getGridElement().getCell(3, 2);

        Assert.assertTrue(hasCssClass(row2, "row2"));
        Assert.assertTrue(hasCssClass(cell3_2, "Column_2"));

        // Scroll down and verify that the old elements don't have the
        // stylename any more

        // Carefully chosen offset to hit an index % 4 without cell style
        getGridElement().getRow(352);

        Assert.assertFalse(hasCssClass(row2, "row2"));
        Assert.assertFalse(hasCssClass(cell3_2, "Column_2"));
    }

    @Test
    public void testDisableStyleNameGenerator() throws Exception {
        openTestURL();

        selectRowStyleNameGenerator(GridBasicFeatures.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(GridBasicFeatures.CELL_STYLE_GENERATOR_SPECIAL);

        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell3_2 = getGridElement().getCell(3, 2);

        Assert.assertTrue(hasCssClass(row2, "row2"));
        Assert.assertTrue(hasCssClass(cell3_2, "Column_2"));

        // Disable the generator and check again
        selectRowStyleNameGenerator(GridBasicFeatures.ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(GridBasicFeatures.CELL_STYLE_GENERATOR_NONE);

        Assert.assertFalse(hasCssClass(row2, "row2"));
        Assert.assertFalse(hasCssClass(cell3_2, "Column_2"));
    }

    @Test
    public void testChangeStyleNameGenerator() throws Exception {
        openTestURL();

        selectRowStyleNameGenerator(GridBasicFeatures.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(GridBasicFeatures.CELL_STYLE_GENERATOR_SPECIAL);

        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell3_2 = getGridElement().getCell(3, 2);

        Assert.assertTrue(hasCssClass(row2, "row2"));
        Assert.assertTrue(hasCssClass(cell3_2, "Column_2"));

        // Change the generator and check again
        selectRowStyleNameGenerator(GridBasicFeatures.ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(GridBasicFeatures.CELL_STYLE_GENERATOR_PROPERTY_TO_STRING);

        // Old styles removed?
        Assert.assertFalse(hasCssClass(row2, "row2"));
        Assert.assertFalse(hasCssClass(cell3_2, "Column_2"));

        // New style present?
        Assert.assertTrue(hasCssClass(cell3_2, "Column-2"));
    }

    private void selectRowStyleNameGenerator(String name) {
        selectMenuPath("Component", "State", "Row style generator", name);
    }

    private void selectCellStyleNameGenerator(String name) {
        selectMenuPath("Component", "State", "Cell style generator", name);
    }
}
