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

                Map<String, Serializable> globalValueAsMap = (Map<String, Serializable>) globalValue;

                if (!isConfigurableEntry(globalValueAsMap)) {
                    // We need to go deeper
                    try {
                        Map<String, Serializable> valueAsMap = (Map<String, Serializable>) value;
                        ValidationResult childResult = this.enforce(globalValueAsMap, valueAsMap);
                        validationResult.setHasErrors(validationResult.isHasErrors() || childResult.isHasErrors());
                        result.put(key, childResult.getResult());
                    } catch (ClassCastException e) {
                        // value should have been a map
                        validationResult.setHasErrors(true);
                    }
                } else {
                    if (!(Boolean) globalValueAsMap.get(CONFIGURABLE_KEY)) {
                        // Field is set as not configurable but just attempted to be configured
                        validationResult.setHasErrors(true);
                    } else {
                        // The global value is configurable
                        result.put(key, local.get(key));
                    }
                }
            }
        }
        validationResult.setResult(result);
        return validationResult;
    }
}
