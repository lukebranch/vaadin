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
package com.vaadin.client.renderers;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widget.grid.GridUtil;
import com.vaadin.client.widgets.Grid;

/**
 * An abstract superclass for renderers that render clickable widgets. Click
 * handlers can be added to a renderer to listen to click events emitted by all
 * widgets rendered by the renderer.
 * 
 * @param <T>
 *            the presentation (column) type
 * @param <W>
 *            the widget type
 * 
 * @since
 * @author Vaadin Ltd
 */
public abstract class ClickableRenderer<T, W extends Widget> extends
        WidgetRenderer<T, W> implements ClickHandler {

    /**
     * A handler for {@link RendererClickEvent renderer click events}.
     * 
     * @param <R>
     *            the row type of the containing Grid
     * 
     * @see {@link ButtonRenderer#addClickHandler(RendererClickHandler)}
     */
    public interface RendererClickHandler<R> extends EventHandler {

        /**
         * Called when a rendered button is clicked.
         * 
         * @param event
         *            the event representing the click
         */
        void onClick(RendererClickEvent<R> event);
    }

    /**
     * An event fired when a widget rendered by a ClickableWidgetRenderer
     * subclass is clicked.
     * 
     * @param <R>
     *            the row type of the containing Grid
     */
    @SuppressWarnings("rawtypes")
    public static class RendererClickEvent<R> extends
            MouseEvent<RendererClickHandler> {

        @SuppressWarnings("unchecked")
        static final Type<RendererClickHandler> TYPE = new Type<RendererClickHandler>(
                BrowserEvents.CLICK, new RendererClickEvent());

        private Cell cell;

        private R row;

        private RendererClickEvent() {
        }

        /**
         * Returns the cell of the clicked button.
         * 
         * @return the cell
         */
        public Cell getCell() {
            return cell;
        }

        /**
         * Returns the data object corresponding to the row of the clicked
         * button.
         * 
         * @return the row data object
         */
        public R getRow() {
            return row;
        }

        @Override
        public Type<RendererClickHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void dispatch(RendererClickHandler handler) {

            EventTarget target = getNativeEvent().getEventTarget();

            if (!Element.is(target)) {
                return;
            }

            Element e = Element.as(target);
            Grid<R> grid = (Grid<R>) GridUtil.findClosestParentGrid(e);

            cell = GridUtil.findCell(grid, e);
            row = grid.getDataSource().getRow(cell.getRow());

            handler.onClick(this);
        }
    }

    private HandlerManager handlerManager;

    /**
     * {@inheritDoc}
     * <p>
     * <em>Implementation note:</em> It is the implementing method's
     * responsibility to add {@code this} as a click handler of the returned
     * widget, or a widget nested therein, in order to make click events
     * propagate properly to handlers registered via
     * {@link #addClickHandler(RendererClickHandler) addClickHandler}.
     */
    @Override
    public abstract W createWidget();

    /**
     * Adds a click handler to this button renderer. The handler is invoked
     * every time one of the widgets rendered by this renderer is clicked.
     * <p>
     * Note that the row type of the click handler must match the row type of
     * the containing Grid.
     * 
     * @param handler
     *            the click handler to be added
     */
    public HandlerRegistration addClickHandler(RendererClickHandler<?> handler) {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager.addHandler(RendererClickEvent.TYPE, handler);
    }

    @Override
    public void onClick(ClickEvent event) {
        /*
         * The handler manager is lazily instantiated so it's null iff
         * addClickHandler is never called.
         */
        if (handlerManager != null) {
            DomEvent.fireNativeEvent(event.getNativeEvent(), handlerManager);
        }
    }
}
