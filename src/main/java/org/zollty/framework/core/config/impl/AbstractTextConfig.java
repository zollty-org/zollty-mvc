package org.zollty.framework.core.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.zollty.framework.core.Const;
import org.zollty.framework.core.config.ConfigTools;
import org.zollty.framework.util.MvcUtils;
import org.zollty.util.IOUtils;
import org.zollty.util.NestedRuntimeException;

/**
 * TextConfig设计的初衷，就是想免去xml的繁琐配置，故TextConfig只支持注解形式的bean，不支持xml形式的bean定义
 */
public abstract class AbstractTextConfig extends AbstractFileConfig {
    
    public AbstractTextConfig() {
        super(Const.DEFAULT_CONFIG_LOCATION_PROP);
        loadConfig();
    }
    
    public AbstractTextConfig(String configLocation) {
        super(configLocation);
        loadConfig();
    }
    
    public AbstractTextConfig(String configLocation, ClassLoader classLoader) {
        super(configLocation, classLoader);
        loadConfig();
    }
    
    public abstract InputStream getResourceInputStream() throws IOException;
    
    
    private void loadConfig() {
        
        String configPath = getConfigLocation();
        if( configPath == null || !configPath.endsWith(".properties") ) {
            throw new IllegalArgumentException("config location assume be a *.properties file but get: " + configPath);
        }

        InputStream in = null;
        try {
            in = getResourceInputStream();
        }
        catch (IOException e) {
            IOUtils.closeIO(in);
            throw new NestedRuntimeException(e);
        }

        Properties props = MvcUtils.ResourceUtil.getProperties(in);
        Map<String, String> propsMap = MvcUtils.ResourceUtil.covertProperties2Map(props);

        String scanPackage = propsMap.get("scan-package");
        if (scanPackage != null) {
            // 得到所有scan节点
            this.setScanningPackages(MvcUtils.StringSplitUtil.splitIgnoreEmpty(scanPackage, ','));
        }
        else {
            this.setScanningPackages(new String[0]);
        }
        this.setViewPath(propsMap.get("view-path"));
        this.setEncoding(propsMap.get("view-encoding"));

        String prefix = propsMap.get("no-intercept-prefix");
        String suffix = propsMap.get("no-intercept-suffix");
        if (MvcUtils.StringUtil.isNotEmpty(prefix)) {
            this.setExcludePrefixes(ConfigTools.parseExcludePrefix(prefix.toString()));
        }
        if (MvcUtils.StringUtil.isNotEmpty(suffix)) {
            this.setExcludeSuffixes(ConfigTools.parseExcludeSuffix(suffix.toString()));
        }
    }
     
}
