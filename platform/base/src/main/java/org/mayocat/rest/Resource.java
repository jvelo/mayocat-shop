package org.mayocat.rest;

import org.xwiki.component.annotation.Role;

@Role
public interface Resource
{
    static final String SLASH = "/";

    static String ROOT_PATH = SLASH;

    static String API_ROOT_PATH = "/api/1.0/";
}
