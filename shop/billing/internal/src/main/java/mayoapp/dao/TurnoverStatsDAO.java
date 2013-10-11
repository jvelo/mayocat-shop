package mayoapp.dao;

import org.mayocat.shop.billing.model.stats.TurnoverStatEntry;
import org.mayocat.shop.billing.store.jdbi.mapper.TurnoverStatEntryMapper;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(TurnoverStatEntryMapper.class)
@UseStringTemplate3StatementLocator
public interface TurnoverStatsDAO
{
    @SqlQuery
    TurnoverStatEntry daily();

    @SqlQuery
    TurnoverStatEntry weekly();

    @SqlQuery
    TurnoverStatEntry monthly();

    @SqlQuery
    TurnoverStatEntry forever();
}
