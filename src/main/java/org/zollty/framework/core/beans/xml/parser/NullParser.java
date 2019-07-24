package org.zollty.framework.core.beans.xml.parser;

import org.w3c.dom.Element;
import org.zollty.framework.core.beans.xml.value.ManagedValue;
import org.zollty.framework.util.dom.DomParser;

class NullParser implements ElementParser {

    @Override
    public Object parse(Element ele, DomParser dom) {
        
        return new ManagedValue(null);
    }

}
