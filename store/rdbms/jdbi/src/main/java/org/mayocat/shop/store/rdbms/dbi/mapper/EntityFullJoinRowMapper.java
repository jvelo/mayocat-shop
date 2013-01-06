package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.util.Locale;
import java.util.Map;

import org.mayocat.shop.store.rdbms.dbi.dao.jointype.EntityFullJoinRow;

public class EntityFullJoinRowMapper extends BaseMapMapper<EntityFullJoinRow>
{

    protected EntityFullJoinRow mapInternal(int index, Map<String, Object> data)
    {
        EntityFullJoinRow entry = new EntityFullJoinRow();
        entry.setEntityData(data);

        extractTranslation(data, entry);

        return entry;
    }

    private void extractTranslation(Map<String, Object> data, EntityFullJoinRow entry)
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
