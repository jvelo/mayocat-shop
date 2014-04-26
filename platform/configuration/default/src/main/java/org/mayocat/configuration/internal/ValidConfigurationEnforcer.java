/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class ValidConfigurationEnforcer extends AbstractJsonConfigurationHandler
{
    public ValidConfigurationEnforcer(Map<String, Serializable> platform, Map<String, Serializable> tenant)
    {
        super(platform, tenant);
    }

    public class ValidationResult
    {
        private HashMap<String, Serializable> result;

        private boolean hasErrors = false;

        public HashMap<String, Serializable> getResult()
        {
            return result;
        }

        public void setResult(HashMap<String, Serializable> result)
        {
            this.result = result;
        }

        public boolean isHasErrors()
        {
            return hasErrors;
        }

        public void setHasErrors(boolean hasErrors)
        {
            this.hasErrors = hasErrors;
        }
    }

    public ValidationResult enforce()
    {
        return enforce(platform, tenant);
    }

    private ValidationResult enforce(Map<String, Serializable> global, Map<String, Serializable> local)
    {
        ValidationResult validationResult = new ValidationResult();
        HashMap<String, Serializable> result = Maps.newHashMap();
        for (String key : local.keySet()) {
            if (!global.containsKey(key)) {
                // Ignore configuration key : it does not exist
                validationResult.setHasErrors(true);
            } else {
                Serializable value = local.get(key);
                Serializable globalValue = global.get(key);
                try {
                    Map<String, Serializable> valueAsMap = (Map<String, Serializable>) value;
                    try {
                        Map<String, Serializable> globalValueAsMap = (Map<String, Serializable>) globalValue;
                        ValidationResult childResult = this.enforce(globalValueAsMap, valueAsMap);
                        validationResult.setHasErrors(validationResult.isHasErrors() || childResult.isHasErrors());
                        result.put(key, childResult.getResult());
                    } catch (ClassCastException ex) {
                        // Incompatible types between the local and global conf : ignore
                        validationResult.setHasErrors(true);
                    }
                } catch (ClassCastException e) {
                    // Not a map
                    try {
                        Map<String, Serializable> globalValueAsMap = (Map<String, Serializable>) globalValue;
                        if (isConfigurableEntry(globalValueAsMap)) {
                            if (!(Boolean) globalValueAsMap.get(CONFIGURABLE_KEY)) {
                                validationResult.setHasErrors(true);
                            } else {
                                // The global value is configurable
                                result.put(key, local.get(key));
                            }
                        }
                    } catch (ClassCastException ex) {
                        // The global value is not a map, we assume it is a configurable field
                        result.put(key, local.get(key));
                    }
                }
                // TODO add support for list of values
                // i.e. try to cast to List<Object> etc.
            }
        }
        validationResult.setResult(result);
        return validationResult;
    }
}
