/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zollty.dbk.support.xml;

import org.zollty.dbk.support.SqlValue;

/**
 * Subinterface of {@link org.zollty.dbk.support.SqlValue}
 * that supports passing in XML data to specified column and adds a
 * cleanup callback, to be invoked after the value has been set and
 * the corresponding statement has been executed.
 *
 * @author Thomas Risberg
 * @since 2.5.5
 * @see org.zollty.dbk.support.SqlValue
 */
public interface SqlXmlValue extends SqlValue {

}
