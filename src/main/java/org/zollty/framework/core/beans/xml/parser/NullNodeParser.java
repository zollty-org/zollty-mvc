package org.zollty.framework.core.beans.xml.parser;

import org.w3c.dom.Element;
import org.zollty.framework.core.beans.xml.ManagedValue;
import org.zollty.framework.util.dom.Dom;

public class NullNodeParser implements XmlNodeParser {

    @Override
    public Object parse(Element ele, Dom dom) {
        
        return new ManagedValue(null);
    }

}
