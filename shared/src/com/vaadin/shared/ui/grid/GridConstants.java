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

import java.io.Serializable;

/**
 * Container class for common constants and default values used by the Grid
 * component.
 * 
 * @since
 * @author Vaadin Ltd
 */
public final class GridConstants implements Serializable {

    /**
     * Default padding in pixels when scrolling programmatically, without an
     * explicitly defined padding value.
     */
    public static final int DEFAULT_PADDING = 0;

    /**
     * Delay before a long tap action is triggered. Number in milliseconds.
     */
    public static final int LONG_TAP_DELAY = 500;

    /**
     * The threshold in pixels a finger can move while long tapping.
     */
    public static final int LONG_TAP_THRESHOLD = 3;
}
