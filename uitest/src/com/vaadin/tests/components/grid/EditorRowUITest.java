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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class EditorRowUITest extends MultiBrowserTest {

    @Test
    public void testEditorRow() {
        setDebug(true);
        openTestURL();

        assertFalse("Sanity check",
                isElementPresent(PasswordFieldElement.class));

        openEditorRow(5);
        assertFalse("Remove this when grid is fixed",
                isElementPresent(PasswordFieldElement.class));
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();

        openEditorRow(10);

        assertTrue("Edtor row should be opened with a password field",
                isElementPresent(PasswordFieldElement.class));

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));
    }

    private void openEditorRow(int rowIndex) {
        GridElement grid = $(GridElement.class).first();

        GridCellElement cell = grid.getCell(rowIndex, 1);

        new Actions(driver).moveToElement(cell).doubleClick().build().perform();
    }

}
