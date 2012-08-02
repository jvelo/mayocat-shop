package org.mayocat.shop.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

@PersistenceCapable(table = "user", detachable = "true")
@Uniques({@Unique(name = "UNIQUE_EMAIL", members = {"email"})})
public class User extends Entity
{
    @Index
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Index
    private String email;
    
    /**
     * The password hash.
     */
    private String password;

}
