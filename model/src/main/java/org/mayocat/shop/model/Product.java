package org.mayocat.shop.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

@PersistenceCapable(
    table = "product",
    detachable="true"
)
@Uniques({@Unique(name="UNIQUE_HANDLE_PER_TENANT", members={"handle", "tenant"})})
public class Product
{
    @Index
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Index
    private String handle;
    
    private String tenant;
    
    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }
    
    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }
}
