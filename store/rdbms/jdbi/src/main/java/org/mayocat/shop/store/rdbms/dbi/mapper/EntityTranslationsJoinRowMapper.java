package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.util.Locale;
import java.util.Map;

import org.mayocat.shop.store.rdbms.dbi.jointype.EntityTranslationJoinRow;

public class EntityTranslationsJoinRowMapper extends BaseMapMapper<EntityTranslationJoinRow>
{
    protected EntityTranslationJoinRow mapInternal(int index, Map<String, Object> data)
    {
        EntityTranslationJoinRow entry = new EntityTranslationJoinRow();
        entry.setEntityData(data);

        extractTranslation(data, entry);

        return entry;
    }

    private void extractTranslation(Map<String, Object> data, EntityTranslationJoinRow entry)
    {
        if (data.get("translation.field") != null) {
            entry.setField((String) data.get("translation.field"));
            if (data.get("translation_medium.text") != null) {
                entry.setText((String) data.get("translation_medium.text"));
            } else if (data.get("translation_small.text") != null) {
                entry.setText((String) data.get("translation_small.text"));
            }

            if (data.get("translation_medium.locale") != null) {
                entry.setLocale(parseLocale((String) data.get("translation_medium.locale")));
            } else if (data.get("translation_small.locale") != null) {
                entry.setLocale(parseLocale((String) data.get("translation_small.locale")));
            }
        }
    }

    private Locale parseLocale(String localeAsText)
    {
        if (localeAsText.indexOf('_') < 0) {
            // Language code only
            return new Locale(localeAsText);
        } else {
            // Language and country
            String language = localeAsText.split("_")[0];
            String country = localeAsText.split("_")[1];
            return new Locale(language, country);
        }
    }
}
