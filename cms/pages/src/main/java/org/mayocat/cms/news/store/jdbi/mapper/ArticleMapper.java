package org.mayocat.cms.news.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        Article article = new Article(resultSet.getLong("id"));
        article.setPublicationDate(resultSet.getDate("publication_date"));
        article.setTitle(resultSet.getString("title"));
        article.setContent(resultSet.getString("content"));
        article.setSlug(resultSet.getString("slug"));
        article.setPublished(resultSet.getBoolean("published"));
        return article;
    }
}
