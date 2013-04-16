package org.mayocat.shop.payment.model;

import java.util.Map;

import org.mayocat.model.Identifiable;

/**
 * @version $Id$
 */
public class PaymentOperation implements Identifiable
{
    public enum Result
    {
        INITIALIZED,
        AUTHORIZED,
        CAPTURED,
        CANCELLED,
        FAILED;
    }

    private Long id;

    private Long orderId;

    private String gatewayId;

    private String externalId;

    private Result result;

    private Map<String, Object> memo;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public String getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public Map<String, Object> getMemo()
    {
        return memo;
    }

    public void setMemo(Map<String, Object> memo)
    {
        this.memo = memo;
    }
}
