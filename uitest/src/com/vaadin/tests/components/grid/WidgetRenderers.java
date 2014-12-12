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

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderer.ButtonRenderer;
import com.vaadin.ui.renderer.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderer.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderer.ImageRenderer;
import com.vaadin.ui.renderer.ProgressBarRenderer;

public class WidgetRenderers extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();

        container.addContainerProperty(ProgressBarRenderer.class, Double.class,
                null);
        container
                .addContainerProperty(ButtonRenderer.class, String.class, null);
        container.addContainerProperty(ImageRenderer.class, Resource.class,
                null);

        final Item item = container.getItem(container.addItem());

        item.getItemProperty(ProgressBarRenderer.class).setValue(0.3);
        item.getItemProperty(ButtonRenderer.class).setValue("Click");
        item.getItemProperty(ImageRenderer.class).setValue(
                new ThemeResource("window/img/close.png"));

        final Grid grid = new Grid(container);

        grid.setId("test-grid");
        grid.setSelectionMode(SelectionMode.NONE);

        grid.getColumn(ProgressBarRenderer.class).setRenderer(
                new ProgressBarRenderer());

        grid.getColumn(ButtonRenderer.class).setRenderer(
                new ButtonRenderer(new RendererClickListener() {
                    @Override
                    public void click(RendererClickEvent event) {
                        item.getItemProperty(ButtonRenderer.class).setValue(
                                "Clicked!");
                    }
                }));

        grid.getColumn(ImageRenderer.class).setRenderer(
                new ImageRenderer(new RendererClickListener() {

                    @Override
                    public void click(RendererClickEvent event) {
                        item.getItemProperty(ImageRenderer.class).setValue(
                                new ThemeResource("window/img/maximize.png"));
                    }
                }));

        addComponent(grid);

        addComponent(new Button("Change column order",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.setColumnOrder(ImageRenderer.class,
                                ProgressBarRenderer.class, ButtonRenderer.class);
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        return "Tests the functionality of widget-based renderers";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13334);
    }
}
