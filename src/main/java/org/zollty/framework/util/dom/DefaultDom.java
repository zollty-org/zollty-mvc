/* 
 * Copyright (C) 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * Create by ZollTy on 2013-6-16 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.util.dom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;

/**
 * 
 * @author zollty
 * @since 2013-6-16
 */
public class DefaultDom implements Dom {

    private Logger log = LogFactory.getLogger(DefaultDom.class);

    private DocumentBuilderFactory dbf;
    private DocumentBuilder db; // dom解析器

    public DefaultDom() {
        dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            log.error(e);
        }
    }

    @Override
    public Document getDocument(InputStream is) {
        Document doc = null;
        try {
            doc = db.parse(is);
        }
        catch (SAXException e) {
            log.error(e);
        }
        catch (IOException e) {
            log.error(e);
        } finally {
            if (is != null)
                try {
                    is.close();
                }
                catch (IOException e) {
                }
        }
        return doc;
    }

    @Override
    public Element getRoot(Document doc) {
        return doc.getDocumentElement();
    }

    @Override
    public List<Element> elements(Element e) {
        return elements(e, null);
    }

    @Override
    public List<Element> elements(Element e, String name) {
        List<Element> eList = new ArrayList<Element>();

        NodeList nodeList = e.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (name != null) {
                    if (node.getNodeName().equals(name))
                        eList.add((Element) node);
                }
                else {
                    eList.add((Element) node);
                }
            }
        }
        return eList;
    }

    @Override
    public Element element(Element e, String name) {
        NodeList element = e.getElementsByTagName(name);
        if (element != null && e.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) element.item(0);
        }
        return null;
    }

    @Override
    public String getTextValue(Element valueEle) {
        if (valueEle != null) {
            StringBuilder sb = new StringBuilder();
            NodeList nl = valueEle.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node item = nl.item(i);
                if ((item instanceof CharacterData && !(item instanceof Comment))
                        || item instanceof EntityReference) {
                    sb.append(item.getNodeValue());
                }
            }
            return sb.toString().trim();
        }
        return null;
    }

}