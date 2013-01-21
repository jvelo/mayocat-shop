package org.mayocat.shop.store;

/**
 * @version $Id$
 */
public class InvalidOperation extends Exception {
    /**
     * Generated serial UID. Change when the serialization of this class changes.
     */
    private static final long serialVersionUID = 1l;

    public InvalidOperation() {
        super();
    }

    public InvalidOperation(String message) {
        super(message);
    }
}
