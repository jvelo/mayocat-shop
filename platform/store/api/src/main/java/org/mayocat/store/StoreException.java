/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store;

public class StoreException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -153504107891229925L;

    public StoreException()  {
        super();
    }
    
    public StoreException(String message){
        super(message);
    }
    
    public StoreException(Throwable cause){
        super(cause);
    }
    
    public StoreException(String message, Throwable cause){
        super(message, cause);
    }
}
