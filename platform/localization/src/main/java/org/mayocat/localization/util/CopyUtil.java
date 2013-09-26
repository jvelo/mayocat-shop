package org.mayocat.localization.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mayocat.localization.util.support.FastByteArrayOutputStream;

/**
 * @version $Id$
 */
public class CopyUtil
{
    /**
     * Returns a copy of the object, or null if the object cannot  be serialized.
     *
     * See http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
     */
    public static Object deepCopy(Object orig)
    {
        Object obj = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos =
                    new FastByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in =
                    new ObjectInputStream(fbos.getInputStream());
            obj = in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
}
