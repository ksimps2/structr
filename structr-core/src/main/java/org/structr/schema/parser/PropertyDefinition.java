/**
 * Copyright (C) 2010-2017 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.schema.parser;

import org.structr.schema.SchemaHelper.Type;

/**
 *
 *
 */
public interface PropertyDefinition {

	public String getPropertyName();
	public Type getPropertyType();
	public String getRawSource();
	public String getSource();
	public String getDbName();
	public String getFormat();
	public boolean isNotNull();
	public boolean isCompound();
	public boolean isUnique();
	public boolean isIndexed();
	public String getDefaultValue();
	public String getContentType();
	public String getReadFunction();
	public String getWriteFunction();
}
