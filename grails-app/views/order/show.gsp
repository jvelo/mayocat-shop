
<%@ page import="org.mayocat.shop.grails.Order" %>
<%@ page import="org.mayocat.shop.grails.OrderStatus" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'order.label', default: 'Order')}" />
    <title><g:message code="order.header" default="Order #" />${orderInstance.id}</title>
  </head>
  <body>
    <div id="show-order" class="content" role="main">
      <div class="page-header">
        <h1><g:message code="order.header" default="Order #" />${orderInstance.id}</h1>
      </div>
      <g:if test="${flash.message}">
        <div class="alert alert-info" role="status">${flash.message}</div>
      </g:if>

      <h3>
        <g:message code="order.orderStatus" default="Status" />
      </h3>


      <g:form method="post" class="form-horizontal">
        <div>
          <g:hiddenField name="id" value="${orderInstance.id}" />
          <g:hiddenField name="version" value="${orderInstance.version}" />
          <g:set var="statusChangeable" value="${![OrderStatus.PAID, OrderStatus.WAITING_FOR_PAYMENT].contains(orderInstance.status)}" />
          <select name="status" <g:if test="${!statusChangeable}">disabled="disabled"</g:if>>
            <g:each var="option" in="${OrderStatus.values()}">
              <option value="${option}" <g:if test="${orderInstance.status.equals(option)}">selected</g:if> ><g:orderStatus status="${option}"/></option>
            </g:each>
          </select>
          <g:if test="${statusChangeable}">
            <g:actionSubmit class="save btn primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
          </g:if>
          <g:else>
            <g:if test="${orderInstance.status == OrderStatus.PAID}">
              <g:actionSubmit class="save btn btn-primary" action="makeShipped" value="${message(code: 'orders.shipOrder', default: 'Ship order')}" />
            </g:if>
            <g:if test="${orderInstance.status == OrderStatus.WAITING_FOR_PAYMENT}">
              <g:actionSubmit class="save btn btn-primary" action="acceptPayment" value="${message(code: 'orders.acceptPayment', default: 'Accept payment')}" />
              <g:actionSubmit class="save btn btn-danger" action="cancelOrder" value="${message(code: 'orders.cancelOrder', default: 'Cancel order')}" />
            </g:if>
          </g:else>
        </div>
      </g:form>

      <h3>
        <g:message code="order.orderDetails" default="Order details" />
      </h3>

      <table class="table">
        <thead>
          <tr>
            <th></th>
            <th><g:message code="order.table.item" default="Item name" /></th>
            <th><g:message code="order.table.description" default="Description" /></th>
            <th><g:message code="order.table.unitPrice" default="Unit price" /></th>
            <th><g:message code="order.table.quantity" default="Quantity" /></th>
            <th><g:message code="order.table.itemTotal" default="Item total" /></th>
          </tr>
        </thead>

        <g:each var="item" in="${orderInstance.items}" status="count">
          <tr>
            <td>${count + 1}.</td>
            <td>${item.title}</td>
            <td>${item.description}</td>
            <td>${item.unitPrice} ${orderInstance.currency.getSymbol(Locale.UK)}</td> 
                %{-- Using the UK locale gives a euro symbol for EUR 
                     TODO : Maybe adapt the code from the second post at http://www.java.net/node/691596 in a grails taglib
                     OR, checkout the enhancements in JDK7 at https://blogs.oracle.com/naotoj/entry/currency_enhancements_in_jdk71
                  --}%
            <td>${item.quantity}</td>
            <td>${item.quantity * item.unitPrice} ${orderInstance.currency.getSymbol(Locale.UK)}</td>
          </tr>
        </g:each>

        <tfoot>
          <tr class="productsTotal">
            <td></td>
            <td colspan="4"><g:message code="order.productsTotal" default="Products total" /></td>
            <td class="price">${orderInstance.totalProducts} ${orderInstance.currency.getSymbol(Locale.UK)}</td>
          </tr>
          <g:if test="${orderInstance.shipping}">
            <tr class="shipping">
              <td></td>
              <td colspan="4"><g:message code="order.productsTotal" default="Shipping" /></td>
              <td class="price">${orderInstance.shipping} ${orderInstance.currency.getSymbol(Locale.UK)}</td>
            </tr>
          </g:if>
          <tr class="grandTotal">
            <td></td>
            <td colspan="4"><g:message code="order.productsTotal" default="Grand total" /></td>
            <td class="price">${orderInstance.grandTotal} ${orderInstance.currency.getSymbol(Locale.UK)}</td>
          </tr>
        </tfoot>
      </table>

      <h3>
        <g:message code="order.customerDetails" default="Customer details" />
      </h3>

      <div class="well">
        <g:message code="order.customerEmail" default="Contact email" />
        <h4>
          ${fieldValue(bean: orderInstance, field: "customerEmail")}
          <a class="mail" href="mailto:${fieldValue(bean: orderInstance, field: 'customerEmail')}"></a>
        </h4>
      </div>

      <div class="addresses">
        %{-- 1. First: if no delivery address is present, this is both a billing and delivery address.
                If not, it's the billing address only.
          --}%
        <div class="address">
          <g:if test="${orderInstance.deliveryAddress}">
            <div class="header">
              <g:message code="order.deliveryAddress" default="Delivery Address" />
            </div>
          </g:if>
          <g:else>
            <div class="header">
              <g:message code="order.address" default="Address (billing & delivery)" />
            </div>
          </g:else>
          <g:address address="${orderInstance.billingAddress}" full="full" />
        </div>
        <g:if test="${orderInstance.deliveryAddress != null}">
        %{-- 2. Delivery address, if different.
          --}%
          <div class="address">
            <div class="header">
              <g:message code="order.deliveryAddress" default="Delivery Address" />
            </div>
            <g:address address="${orderInstance.deliveryAddress}" full="full" />
          </div>
        </g:if>
      </div>

    </div>
  </body>
</html>
