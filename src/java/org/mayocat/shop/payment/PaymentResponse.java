package org.mayocat.shop.payment;

import java.util.Map;

import org.mayocat.shop.grails.OrderStatus;

public class PaymentResponse
{
    private OrderStatus newStatus;

    private Map<String, Object> entityToSave;

    private Long orderId;

    private String contentType;

    private String response;

    public PaymentResponse(Long orderId, OrderStatus newStatus, Map<String, Object> entityToSave,
        String responseContentType, String response)
    {
        this.orderId = orderId;
        this.newStatus = newStatus;
        this.entityToSave = entityToSave;
        this.contentType = responseContentType;
        this.response = response;
    }

    public OrderStatus getNewStatus()
    {
        return this.newStatus;
    }

    public Map<String, Object> getEntityToSave()
    {
        return this.entityToSave;
    }

    public Long getOrderId()
    {
        return this.orderId;
    }

    public String getResponseContentType()
    {
        return this.contentType;
    }

    public String getResponseContent()
    {
        return this.response;
    }
}
