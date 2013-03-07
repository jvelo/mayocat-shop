package org.mayocat.jackson;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

public class TestBean
{
    @JsonDeserialize(using = OptionalStringListDeserializer.class)
    private Optional<List<String>> foo = Optional.absent();

    private String bar;

    public Optional<List<String>> getFoo()
    {
        return foo;
    }

    public String getBar()
    {
        return bar;
    }
}