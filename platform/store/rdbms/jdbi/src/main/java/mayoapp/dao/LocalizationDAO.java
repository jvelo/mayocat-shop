/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.Locale;
import java.util.UUID;

import org.mayocat.model.Localized;
import org.mayocat.store.rdbms.dbi.argument.JsonArgumentAsJsonArgumentFactory;
import org.mayocat.store.rdbms.dbi.binder.BindJson;
import org.mayocat.store.rdbms.dbi.binder.BindToString;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;

@RegisterArgumentFactory({ JsonArgumentAsJsonArgumentFactory.class })
public interface LocalizationDAO<E extends Localized>
{
    // FIXME
    // Support for batching translation (see commented signature below) does not work. It fails at the JDBC level with
    // "A result was returned by the statement, when none was expected."
    //
    //@SqlBatch("select upsert_translation(:entity_id, :locale, :entity)")
    //void createOrUpdateTranslations(@Bind("entity_id") List<UUID> id,
    //    @BindToString("locale") List<Locale> locale, @BindJson("entity") List<Object> json);

    @SqlUpdate("select upsert_translation(:entity_id, :locale, :entity)")
    void createOrUpdateTranslation(@Bind("entity_id") UUID id,
            @BindToString("locale") Locale locale, @BindJson("entity") Object json);
}
