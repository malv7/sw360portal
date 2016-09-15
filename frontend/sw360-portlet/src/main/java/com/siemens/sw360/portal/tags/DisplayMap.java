/*
 * Copyright (c) Bosch Software Innovations GmbH 2016.
 * Part of the SW360 Portal Project.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.siemens.sw360.portal.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Map;

/**
 * This displays a map
 *
 * @author birgit.heydenreich@tngtech.com
 */
public class DisplayMap extends SimpleTagSupport {

    private Map<String, String> value;
    private Map<String, String> autoFillValue;

    public void setValue(Map<String, String> value) {
        this.value = value;
    }

    public void setAutoFillValue(Map<String, String> autoFillValue) {
        this.autoFillValue = autoFillValue;
    }

    public void doTag() throws JspException, IOException {
        Map<String, String> fullValue;

        if (value == null) {
            fullValue = autoFillValue;
        } else {
            fullValue = value;
        }

        if (null != fullValue && !fullValue.isEmpty()) {
            String result = getMapAsString(fullValue);
            getJspContext().getOut().print(result);
        }
    }

    public static String getMapAsString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul style=\"list-style-type: none; padding:0; margin:0;\">");
        map.entrySet().stream().forEach(e -> sb.append(
                "<li><b>" + e.getKey() + "</b> " + e.getValue() + "</li>"
        ));
        sb.append("</ul>");
        return sb.toString();
    }
}