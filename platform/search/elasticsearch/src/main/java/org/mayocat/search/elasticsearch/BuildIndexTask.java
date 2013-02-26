package org.mayocat.search.elasticsearch;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.search.SearchEngine;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

@Component("bi")
public class BuildIndexTask extends Task implements org.mayocat.base.Task
{

    @Inject
    private Provider<SearchEngine> searchEngine;

    public BuildIndexTask()
    {
        this("bi");
    }

    protected BuildIndexTask(String name)
    {
        super(name);
    }

    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception
    {
        output.println("(Re)building index...");
        output.flush();

        // TODO
        // - does this task make sense ?
    }

}
