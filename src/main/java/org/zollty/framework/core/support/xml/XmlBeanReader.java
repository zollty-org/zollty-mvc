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
 * Create by ZollTy on 2013-9-15 (http://blog.zollty.com, zollty@163.com)
 */
package org.zollty.framework.core.support.xml;

import static org.zollty.framework.core.support.xml.XmlNodeConstants.BEAN_ELEMENT;
import static org.zollty.framework.core.support.xml.XmlNodeConstants.IMPORT_ELEMENT;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zollty.framework.core.support.AbstractBeanReader;
import org.zollty.framework.core.support.BeanDefinition;
import org.zollty.framework.core.support.xml.parser.XmlNodeParserFactory;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.ResourceContext;
import org.zollty.framework.util.dom.DefaultDom;
import org.zollty.framework.util.dom.Dom;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.NestedRuntimeException;

/**
 * @author zollty
 * @since 2013-9-15
 */
public class XmlBeanReader extends AbstractBeanReader {

    private static final Logger LOG = LogFactory.getLogger(XmlBeanReader.class);

    private Dom dom = new DefaultDom();

    private ResourceContext beanXmlResourceContext;

    private ClassLoader beanClassLoader;

    private Set<String> existed = new HashSet<String>();

    public XmlBeanReader(ResourceContext beanXmlResourceContext) {
        this.beanXmlResourceContext = beanXmlResourceContext;
        this.beanClassLoader = MvcUtils.ClassUtil.getDefaultClassLoader();
    }

    public XmlBeanReader(ResourceContext beanXmlResourceContext, ClassLoader beanClassLoader) {
        this.beanXmlResourceContext = beanXmlResourceContext;
        this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : MvcUtils.ClassUtil
                .getDefaultClassLoader());
    }

    private void init() {
        beanDefinitions = new ArrayList<BeanDefinition>();
        String beanXmlFileLocation = beanXmlResourceContext.getLocation();
        if (beanXmlFileLocation == null || !beanXmlFileLocation.endsWith(".xml")) {
            LOG.warn("the beanXmlFileLocation [{}] is invalidate! XmlBeanReader just ignore it.",
                    beanXmlFileLocation);
            return;
        }

        // 得到所有bean节点
        List<Element> beansList = new ArrayList<Element>();
        parseXml(beansList);
        // 迭代beans列表
        if (beansList != null && !beansList.isEmpty()) {
            for (Element ele : beansList) {
                beanDefinitions.add((BeanDefinition) XmlNodeParserFactory.getXmlBeanDefinition(ele,
                        dom, beanClassLoader));
            }
        }
    }

    @Override
    public List<BeanDefinition> loadBeanDefinitions() {
        init();
        return beanDefinitions;
    }

    private void parseXml(List<Element> beansList) {
        recursiveDomParse(beansList, beanXmlResourceContext.getLocation());
        existed = null;
    }

    private void recursiveDomParse(List<Element> beansList, String beanXmlFileLocation) {
        InputStream in = null;
        try {
            in = MvcUtils.ResourceUtil.getResourceInputStream(beanXmlFileLocation,
                    beanXmlResourceContext.getClassLoader(),
                    beanXmlResourceContext.getServletContext());
        }
        catch (IOException e) {
            MvcUtils.IOUtil.closeIO(in);
            throw new NestedRuntimeException(e);
        }

        // 用于判断循环引用
        existed.add(beanXmlFileLocation);

        // 获得Xml文档对象
        Document doc = dom.getDocument(in);
        if (doc != null) {
            // 得到根节点
            Element root = dom.getRoot(doc);
            // 得到所有bean节点
            List<Element> list = dom.elements(root, BEAN_ELEMENT);
            beansList.addAll(list);

            // 得到所有import节点
            List<Element> importList = dom.elements(root, IMPORT_ELEMENT);
            if (importList != null) {
                for (Element ele : importList) {
                    if (ele.hasAttribute("resource")) {
                        String resourceLocation = ele.getAttribute("resource");
                        // 要检查是否循环引用，比如之前有一个test1.xml引入test2.xml，
                        // 而test2.xml又引入test1.xml或者test2.xml
                        if (MvcUtils.StringUtil.isNotBlank(resourceLocation)
                                && !existed.contains(resourceLocation)) {
                            recursiveDomParse(beansList, resourceLocation);
                        }
                        else {
                            LOG.warn("import element [{}] in [{}] is blank or duplicate",
                                    resourceLocation, beanXmlFileLocation);
                        }
                    }
                }
            }
        }
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ResourceContext getBeanXmlResourceContext() {
        return beanXmlResourceContext;
    }

    public void setBeanXmlResourceContext(ResourceContext beanXmlResourceContext) {
        this.beanXmlResourceContext = beanXmlResourceContext;
    }

}