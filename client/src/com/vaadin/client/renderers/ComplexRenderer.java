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

import java.util.Collection;
import java.util.Collections;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Visibility;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widget.escalator.FlyweightCell;

/**
 * Base class for renderers that needs initialization and destruction logic
 * (override {@link #init(FlyweightCell) and #destroy(FlyweightCell) } and event
 * handling (see {@link #onBrowserEvent(Cell, NativeEvent)},
 * {@link #getConsumedEvents()} and {@link #onActivate()}.
 * 
 * <p>
 * Also provides a helper method for hiding the cell contents by overriding
 * {@link #setContentVisible(FlyweightCell, boolean)}
 * 
 * @since
 * @author Vaadin Ltd
 */
public abstract class ComplexRenderer<T> implements Renderer<T> {

    /**
     * Called at initialization stage. Perform any initialization here e.g.
     * attach handlers, attach widgets etc.
     * 
     * @param cell
     *            The cell. Note that the cell is not to be stored outside of
     *            the method as the cell install will change. See
     *            {@link FlyweightCell}
     */
    public abstract void init(FlyweightCell cell);

    /**
     * Called after the cell is deemed to be destroyed and no longer used by the
     * Grid. Called after the cell element is detached from the DOM.
     * 
     * @param cell
     *            The cell. Note that the cell is not to be stored outside of
     *            the method as the cell install will change. See
     *            {@link FlyweightCell}
     */
    public void destroy(FlyweightCell cell) {
        // Implement if needed
    }

    /**
     * Returns the events that the renderer should consume. These are also the
     * events that the Grid will pass to
     * {@link #onBrowserEvent(Cell, NativeEvent)} when they occur.
     * 
     * @return a list of consumed events
     * 
     * @see com.google.gwt.dom.client.BrowserEvents
     */
    public Collection<String> getConsumedEvents() {
        return Collections.emptyList();
    }

    /**
     * Called whenever a registered event is triggered in the column the
     * renderer renders.
     * <p>
     * The events that triggers this needs to be returned by the
     * {@link #getConsumedEvents()} method.
     * <p>
     * Returns boolean telling if the event has been completely handled and
     * should not cause any other actions.
     * 
     * @param cell
     *            Object containing information about the cell the event was
     *            triggered on.
     * 
     * @param event
     *            The original DOM event
     * @return true if event should not be handled by grid
     */
    public boolean onBrowserEvent(Cell cell, NativeEvent event) {
        return false;
    }

    /**
     * Used by Grid to toggle whether to show actual data or just an empty
     * placeholder while data is loading. This method is invoked whenever a cell
     * changes between data being available and data missing.
     * <p>
     * Default implementation hides content by setting visibility: hidden to all
     * elements inside the cell. Text nodes are left as is - renderers that add
     * such to the root element need to implement explicit support hiding them.
     * 
     * @param cell
     *            The cell
     * @param hasData
     *            Has the cell content been loaded from the data source
     * 
     */
    public void setContentVisible(FlyweightCell cell, boolean hasData) {
        Element cellElement = cell.getElement();
        for (int n = 0; n < cellElement.getChildCount(); n++) {
            Node node = cellElement.getChild(n);
            if (Element.is(node)) {
                Element e = Element.as(node);
                if (hasData) {
                    e.getStyle().clearVisibility();
                } else {
                    e.getStyle().setVisibility(Visibility.HIDDEN);
                }
            }
        }
    }

    /**
     * Called when the cell is activated by pressing <code>enter</code>, double
     * clicking or performing a double tap on the cell.
     * 
     * @param cell
     *            the activated cell
     * @return <code>true</code> if event was handled and should not be
     *         interpreted as a generic gesture by Grid.
     */
    public boolean onActivate(Cell cell) {
        return false;
    }

    /**
     * Called when the renderer is deemed to be destroyed and no longer used by
     * the Grid.
     */
    public void destroy() {
        // Implement if needed
    }
}
