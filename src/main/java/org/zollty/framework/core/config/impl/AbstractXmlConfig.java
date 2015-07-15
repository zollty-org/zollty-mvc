package org.zollty.framework.core.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zollty.framework.core.Const;
import org.zollty.framework.core.config.ConfigTools;
import org.zollty.framework.core.config.InitByConfig;
import org.zollty.framework.util.MvcUtils;
import org.zollty.framework.util.dom.DefaultDom;
import org.zollty.framework.util.dom.Dom;
import org.zollty.log.LogFactory;
import org.zollty.log.Logger;
import org.zollty.util.IOUtils;
import org.zollty.util.NestedRuntimeException;

public abstract class AbstractXmlConfig extends AbstractFileConfig {
    
    private Logger logger = LogFactory.getLogger(AbstractXmlConfig.class);
    
    private Dom dom;
    
    public AbstractXmlConfig() {
        super(Const.DEFAULT_CONFIG_LOCATION_XML);
        this.dom = new DefaultDom();
        loadConfig();
    }
    
    
    public AbstractXmlConfig(String configLocation) {
        super(configLocation);
        this.dom = new DefaultDom();
        loadConfig();
    }
    
    public AbstractXmlConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
        this.dom = new DefaultDom();
        loadConfig();
    }
    
    public AbstractXmlConfig(String configLocation, Dom dom) {
        super(configLocation);
        this.dom = dom;
        loadConfig();
    }
    
    public AbstractXmlConfig(String configLocation, ClassLoader classLoader, Dom dom) {
        super(configLocation, classLoader);
        this.dom = dom;
        loadConfig();
    }
    
    public abstract InputStream getResourceInputStream() throws IOException;

    
    private void loadConfig() {
        String configPath = getConfigLocation();
        if( configPath == null || !configPath.endsWith(".xml") ) {
            throw new IllegalArgumentException("config location assume be a xml file but get: " + configPath);
        }

        InputStream in = null;
        try {
            in = getResourceInputStream();
        }
        catch (IOException e) {
            IOUtils.closeIO(in);
            throw new NestedRuntimeException(e);
        }

        // 获得Xml文档对象
        Document doc = dom.getDocument( in );
        // 得到根节点
        Element root = dom.getRoot(doc);
        
        // 得到所有scan节点
        List<Element> scanList = dom.elements(root, "component-scan");

        if (scanList != null) {
            List<String> paths = new LinkedList<String>();
            for (int i = 0; i < scanList.size(); i++) {
                Element ele = scanList.get(i);
                String path = ele.getAttribute("base-package");
                if( MvcUtils.StringUtil.isNotEmpty(path) )
                    paths.add(path);
            }
            this.setScanningPackages(paths.toArray(new String[0]));
        } else {
            this.setScanningPackages(new String[0]);
        }

        Element mvc = dom.element(root, "mvc");
        if (mvc != null) {
            String viewPath = mvc.getAttribute("view-path");
            String encoding = mvc.getAttribute("view-encoding");
            logger.info("mvc viewPath ["+viewPath+"] encoding [" + encoding + "]");
            
            if( MvcUtils.StringUtil.isNotBlank(viewPath))
                this.setViewPath(viewPath);
            if( MvcUtils.StringUtil.isNotBlank(encoding))
                this.setEncoding(encoding);
        }
        
        List<Element> nointers = dom.elements(root, "no-intercept");
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        String str = null;
        for (Element nointc : nointers) {
            str = nointc.getAttribute("prefix");
            if(MvcUtils.StringUtil.isNotEmpty(str))
                prefix.append(str).append(',');
            str = nointc.getAttribute("suffix");
            if(MvcUtils.StringUtil.isNotEmpty(str))
                suffix.append(str).append(',');
        }
        if( MvcUtils.StringUtil.isNotEmpty(prefix) ) {
            this.setExcludePrefixes(ConfigTools.parseExcludePrefix(prefix.toString()));
        }
        if( MvcUtils.StringUtil.isNotEmpty(suffix) ) {
            this.setExcludeSuffixes(ConfigTools.parseExcludeSuffix(suffix.toString()));
        }
        
        Element logger = dom.element(root, "logger");
        if( null != logger ){
            String logName = logger.getAttribute("class");
            String level = logger.getAttribute("level");
            if( null != logName ){
                this.setLogLevel(level);
                InitByConfig.initLogFactory(logName, level);
            }
        }
        
        Element errorPage = dom.element(root, "errorPage");
        if( null != errorPage ){
            String path = errorPage.getAttribute("path");
            if( null != path ){
                this.setErrorPagePath(path);
            }
        }
    }
    
    
    public void setDom(Dom dom) {
        this.dom = dom;
    }
    
    public Dom getDom(){
        return dom;
    }
     
}
