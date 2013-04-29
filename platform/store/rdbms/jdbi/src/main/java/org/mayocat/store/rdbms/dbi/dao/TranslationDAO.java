package org.mayocat.store.rdbms.dbi.dao;

import java.util.List;
import java.util.UUID;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;

public interface TranslationDAO
{
    @SqlUpdate
    (
        "INSERT INTO translation (id, entity_id, field) VALUES (:id, :entity_id, :field)"
    )
    void createTranslation(@Bind("id") UUID id, @Bind("entity_id") UUID entityId, @Bind("field") String field);

    @SqlBatch
    (
        "INSERT INTO translation_<type> (translation_id, locale, text) VALUES (:translation_id, :locale, :text)"
    )
    void insertTranslations(@Define("type") String type, @Bind("translation_id") List<UUID> id,
        @Bind("locale") List<String> locale, @Bind("text") List<String> text);
}
