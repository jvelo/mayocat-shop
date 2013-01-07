package org.mayocat.shop.store.rdbms.dbi.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;

public interface TranslationDAO
{
    @GetGeneratedKeys
    @SqlUpdate
    (
        "INSERT INTO translation (entity_id, field) VALUES (:entity_id, :field)"
    )
    Long createTranslation(@Bind("entity_id") Long id, @Bind("field") String field);

    @SqlBatch
    (
        "INSERT INTO translation_<type> (translation_id, locale, text) VALUES (:translation_id, :locale, :text)"
    )
    void insertTranslations(@Define("type") String type, @Bind("translation_id") List<Long> id,
        @Bind("locale") List<String> locale, @Bind("text") List<String> text);
}
