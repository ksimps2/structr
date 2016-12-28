/**
 * Copyright (C) 2010-2016 Structr GmbH
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
package org.structr.core.common;

import java.util.Locale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.structr.common.StructrTest;
import org.structr.common.error.FrameworkException;
import org.structr.core.entity.Localization;
import org.structr.core.graph.NodeAttribute;
import org.structr.core.graph.Tx;
import org.structr.core.script.Scripting;
import org.structr.schema.action.ActionContext;

/**
 *
 * @author Christian Morgner
 */
public class LocalizationTest extends StructrTest {

	@Test
	public void testLocalizationWithoutDomain() {

		// create
		try (final Tx tx = app.tx()) {

			app.create(Localization.class,
				new NodeAttribute<>(Localization.name, "test1"),
				new NodeAttribute<>(Localization.locale, "de"),
				new NodeAttribute<>(Localization.localizedName, "localized1")
			);

			app.create(Localization.class,
				new NodeAttribute<>(Localization.name, "test2"),
				new NodeAttribute<>(Localization.locale, "de"),
				new NodeAttribute<>(Localization.domain, "ExistingDomain"),
				new NodeAttribute<>(Localization.localizedName, "localized2")
			);

			app.create(Localization.class,
				new NodeAttribute<>(Localization.name, "test3"),
				new NodeAttribute<>(Localization.locale, "de_DE"),
				new NodeAttribute<>(Localization.localizedName, "localized3")
			);

			app.create(Localization.class,
				new NodeAttribute<>(Localization.name, "test4"),
				new NodeAttribute<>(Localization.locale, "de_DE"),
				new NodeAttribute<>(Localization.domain, "ExistingDomain"),
				new NodeAttribute<>(Localization.localizedName, "localized4")
			);

			tx.success();

		} catch (FrameworkException fex) {
			fail("Unexpected exception.");
		}

		// test1
		try (final Tx tx = app.tx()) {

			final ActionContext ctx = new ActionContext(securityContext);

			ctx.setLocale(Locale.GERMAN);

			assertEquals("Invalid localization result", "localized1", Scripting.evaluate(ctx, null, "${localize('test1', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "localized1", Scripting.evaluate(ctx, null, "${localize('test1', 'NonexistingDomain')}"));
			assertEquals("Invalid localization result", "localized2", Scripting.evaluate(ctx, null, "${localize('test2', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "test2",      Scripting.evaluate(ctx, null, "${localize('test2', 'NonexistingDomain')}"));
			assertEquals("Invalid localization result", "localized3", Scripting.evaluate(ctx, null, "${localize('test3', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "localized3", Scripting.evaluate(ctx, null, "${localize('test3', 'NonexistingDomain')}"));
			//assertEquals("Invalid localization result", "localized4", Scripting.evaluate(ctx, null, "${localize('test4', 'ExistingDomain')}"));
			//assertEquals("Invalid localization result", "test4",      Scripting.evaluate(ctx, null, "${localize('test4', 'NonexistingDomain')}"));

			tx.success();

		} catch (FrameworkException fex) {
			fail("Unexpected exception.");
		}

		// test2
		try (final Tx tx = app.tx()) {

			final ActionContext ctx = new ActionContext(securityContext);

			ctx.setLocale(Locale.GERMANY);

			assertEquals("Invalid localization result", "localized1", Scripting.evaluate(ctx, null, "${localize('test1', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "localized1", Scripting.evaluate(ctx, null, "${localize('test1', 'NonexistingDomain')}"));
			assertEquals("Invalid localization result", "localized2", Scripting.evaluate(ctx, null, "${localize('test2', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "test2",      Scripting.evaluate(ctx, null, "${localize('test2', 'NonexistingDomain')}"));
			assertEquals("Invalid localization result", "localized3", Scripting.evaluate(ctx, null, "${localize('test3', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "localized3", Scripting.evaluate(ctx, null, "${localize('test3', 'NonexistingDomain')}"));
			assertEquals("Invalid localization result", "localized4", Scripting.evaluate(ctx, null, "${localize('test4', 'ExistingDomain')}"));
			assertEquals("Invalid localization result", "test4",      Scripting.evaluate(ctx, null, "${localize('test4', 'NonexistingDomain')}"));

			tx.success();

		} catch (FrameworkException fex) {
			fail("Unexpected exception.");
		}
	}

}