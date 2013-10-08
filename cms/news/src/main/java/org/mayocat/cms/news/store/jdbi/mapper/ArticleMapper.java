package org.mayocat.cms.news.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.cms.news.model.Article;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class ArticleMapper implements ResultSetMapper<Article>
{
    @Override
    public Article map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Article article = new Article((UUID) resultSet.getObject("id"));
        article.setPublicationDate(resultSet.getTimestamp("publication_date"));
        article.setTitle(resultSet.getString("title"));
        article.setContent(resultSet.getString("content"));
        article.setSlug(resultSet.getString("slug"));
        if (resultSet.getObject("published") != null) {
            article.setPublished(resultSet.getBoolean("published"));
        }
        article.setFeaturedImageId((UUID) resultSet.getObject("featured_image_id"));

        return article;
    }
}
