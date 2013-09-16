package mayoapp.dao;

import java.util.List;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Localized;
import org.mayocat.store.rdbms.dbi.extraction.EntityExtractor;
import org.mayocat.store.rdbms.dbi.jointype.EntityTranslationJoinRow;
import org.mayocat.store.rdbms.dbi.mapper.EntityTranslationsJoinRowMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.yammer.dropwizard.util.Generics;

public abstract class AbstractLocalizedEntityDAO<E extends Localized> implements TranslationDAO,
    EntityDAO<E>
{

    /*
    public void insertTranslations(UUID entityId, Translations translations)
    {
        if (translations == null) {
            return;
        }
        List<UUID> ids = new ArrayList<UUID>();
        List<String> languages = new ArrayList<String>();
        List<String> texts = new ArrayList<String>();

        for (String field : translations.keySet()) {
            Map<Locale, String> fieldTranslations = translations.get(field);
            UUID translationId = UUID.randomUUID();
            createTranslation(translationId, entityId, field);
            for (Locale locale : fieldTranslations.keySet()) {
                ids.add(translationId);
                languages.add(locale.toString());
                texts.add(fieldTranslations.get(locale));
            }
        }

        if (ids.size() > 0) {
            // FIXME check in the entity class the size of the translation
            insertTranslations("small", ids, languages, texts);
        }
    }
    */

    public E findBySlugWithTranslations(String type, String slug, Tenant tenant) {
        List<EntityTranslationJoinRow> rows = this.findBySlugWithTranslationsRows(type, slug, tenant);

        E entity = null;
        Class< E > thisEntityType = Generics.getTypeParameter(getClass(), Localized.class);

        EntityExtractor<E> extractor = new EntityExtractor<E>();
        for (EntityTranslationJoinRow row : rows) {
            if (entity == null) {
                entity = extractor.extract(row.getEntityData(), thisEntityType);
            }
            String field = row.getField();
            if (field != null) {
                //if (!translations.containsKey(field)) {
                //    translations.put(field, new HashMap<Locale, String>());
                //}
                //Map<Locale, String> fieldTranslations = translations.get(field);
                //fieldTranslations.put(row.getLocale(), row.getText());
            }
        }
        if (entity != null) {
            //entity.setTranslations(translations);
        }
        return entity;
    }


    
    @RegisterMapper(EntityTranslationsJoinRowMapper.class)
    @SqlQuery
    (
        "SELECT entity.id, "
      + "       entity.slug, "
      + "       entity.type, "
      + "       entity.tenant_id, "
      + "       entity.parent_id, "
      + "       <type>.*, "
      + "       translation.field as _translation_field, "
      + "       COALESCE(translation_small.locale, translation_medium.locale) as _translation_locale, "
      + "       COALESCE(translation_small.text, translation_medium.text) as _translation_text "
      + "FROM   entity " 
      + "       INNER JOIN <type> " 
      + "               ON entity.id = <type>.entity_id " 
      + "       LEFT JOIN translation "
      + "              ON translation.entity_id = entity.id " 
      + "       LEFT JOIN translation_small "
      + "              ON translation_small.translation_id = translation.id " 
      + "       LEFT JOIN translation_medium "
      + "              ON translation_medium.translation_id = translation.id " 
      + "WHERE  entity.slug = :slug " 
      + "       AND entity.type = '<type>' " 
      + "       AND entity.tenant_id = :tenant.id "
    )
    abstract List<EntityTranslationJoinRow> findBySlugWithTranslationsRows(@Define("type") String type, @Bind("slug") String slug, @BindBean("tenant") Tenant tenant);
}
