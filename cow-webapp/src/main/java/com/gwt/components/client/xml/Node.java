/**
 * Approved for Public Release: 10-4800. Distribution Unlimited.
 * Copyright 2011 The MITRE Corporation,
 * Licensed under the Apache License,
 * Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

/*
Simple XML Parser for GWT
Copyright (C) 2006 musachy http://gwt.components.googlepages.com/

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
*/

package com.gwt.components.client.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {

  private Map attributes = new HashMap();
  private ArrayList childNodes = new ArrayList();
  private String type;
  private String name;
  private String value;
  private Document document;
  private Node parent;

  Node(String nodeType, String nodeName, String nodeValue, Document ownerDocument) {
    this.type = nodeType;
    this.name = nodeName;
    this.value = nodeValue;
    this.document = ownerDocument;
  }

  public void appendChild(Node child) {
    child.setParent(this);
    childNodes.add(child);
  }

  public String getAttribute(String name) {
    return (String)attributes.get(name);
  }

  public void setAttribute(String name, String value) {
    attributes.put(name, value);
  }

  public Node getParent() {
    return parent;
  }

  private void setParent(Node parent) {
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ArrayList getChildNodes() {
    return childNodes;
  }

  public Document getDocument() {
    return document;
  }
} 