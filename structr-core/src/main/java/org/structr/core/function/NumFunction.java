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
package org.structr.core.function;

import org.structr.common.error.FrameworkException;
import org.structr.schema.action.ActionContext;
import org.structr.schema.action.Function;

/**
 *
 */
public class NumFunction extends Function<Object, Object> {

	public static final String ERROR_MESSAGE_NUM = "Usage: ${num(string)}. Example: ${num(this.numericalStringValue)}";

	@Override
	public String getName() {
		return "num()";
	}

	@Override
	public Object apply(final ActionContext ctx, final Object caller, final Object[] sources) throws FrameworkException {

		try {
			if (!arrayHasMinLengthAndAllElementsNotNull(sources, 1)) {
				
				return null;
			}

			try {
				return getDoubleOrNull(sources[0]);

			} catch (Throwable t) {

				logException(caller, t, sources);
				return null;

			}

		} catch (final IllegalArgumentException e) {

			logParameterError(caller, sources, ctx.isJavaScriptContext());
			return usage(ctx.isJavaScriptContext());

		}
	}


	@Override
	public String usage(boolean inJavaScriptContext) {
		return ERROR_MESSAGE_NUM;
	}

	@Override
	public String shortDescription() {
		return "Converts the given string to a floating-point number";
	}

}
