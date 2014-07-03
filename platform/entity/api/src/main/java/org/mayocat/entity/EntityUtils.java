package org.mayocat.entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @version $Id$
 */
public class EntityUtils
{
    public static Set<LoadingOption> asSet(LoadingOption[] options)
    {
        return new HashSet(Arrays.asList(options));
    }
}
