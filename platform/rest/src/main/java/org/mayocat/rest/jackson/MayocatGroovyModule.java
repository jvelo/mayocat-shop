package org.mayocat.rest.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import groovy.lang.GString;

/**
 * Jackson module for the groovy language.
 *
 * @version $Id$
 */
public class MayocatGroovyModule extends SimpleModule
{
    public MayocatGroovyModule()
    {
        super(Version.unknownVersion());
        addSerializer(GString.class, new MayocatGroovyGStringSerializer());
    }
}
