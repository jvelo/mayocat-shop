
<%@ page import="org.mayocat.shop.grails.Order" %>
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
      <div class="message" role="status">${flash.message}</div>
      </g:if>
      <h3>
        <g:message code="order.orderDetails" default="Order details" />
      </h3>
      <table class="table">
        <thead>
          <tr>
            <th></th>
            <th>Item name</th>
            <th>Description</th>
            <th>Unit price</th>
            <th>Quantity</th>
            <th>Item total</th>
          </tr>
        </thead>
        <g:each var="item" in="${orderInstance.items}" status="count">
          <tr>
            <td>${count + 1}.</td>
            <td>${item.title}</td>
            <td>${item.description}</td>
            <td>${item.unitPrice} ${orderInstance.currency.getSymbol(Locale.UK)}</td> 
                %{-- The UK locale gives a euro symbol for EUR 
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

      <ol class="property-list order">
      
        <g:if test="${orderInstance?.customerEmail}">
        <li class="fieldcontain">
          <span id="customerEmail-label" class="property-label"><g:message code="order.customerEmail.label" default="Customer Email" /></span>
          
            <span class="property-value" aria-labelledby="customerEmail-label"><g:fieldValue bean="${orderInstance}" field="customerEmail"/></span>
          
        </li>
        </g:if>
      
        <g:if test="${orderInstance?.billingAddress}">
        <li class="fieldcontain">
          <span id="billingAddress-label" class="property-label"><g:message code="order.billingAddress.label" default="Billing Address" /></span>
          
            <span class="property-value" aria-labelledby="billingAddress-label"><g:link controller="address" action="show" id="${orderInstance?.billingAddress?.id}">${orderInstance?.billingAddress?.encodeAsHTML()}</g:link></span>
          
        </li>
        </g:if>
      
        <g:if test="${orderInstance?.deliveryAddress}">
        <li class="fieldcontain">
          <span id="deliveryAddress-label" class="property-label"><g:message code="order.deliveryAddress.label" default="Delivery Address" /></span>
          
            <span class="property-value" aria-labelledby="deliveryAddress-label"><g:link controller="address" action="show" id="${orderInstance?.deliveryAddress?.id}">${orderInstance?.deliveryAddress?.encodeAsHTML()}</g:link></span>
          
        </li>
        </g:if>
      
        <g:if test="${orderInstance?.status}">
        <li class="fieldcontain">
          <span id="status-label" class="property-label"><g:message code="order.status.label" default="Status" /></span>
          
            <span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${orderInstance}" field="status"/></span>
          
        </li>
        </g:if>
      
        <g:if test="${orderInstance?.dateCreated}">
        <li class="fieldcontain">
          <span id="dateCreated-label" class="property-label"><g:message code="order.dateCreated.label" default="Date Created" /></span>
          
            <span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${orderInstance?.dateCreated}" /></span>
          
        </li>
        </g:if>
      
        <g:if test="${orderInstance?.dateUpdated}">
        <li class="fieldcontain">
          <span id="dateUpdated-label" class="property-label"><g:message code="order.dateUpdated.label" default="Date Updated" /></span>
          
            <span class="property-value" aria-labelledby="dateUpdated-label"><g:formatDate date="${orderInstance?.dateUpdated}" /></span>
          
        </li>
        </g:if>
      
        <g:if test="${orderInstance?.items}">
        <li class="fieldcontain">
          <span id="items-label" class="property-label"><g:message code="order.items.label" default="Items" /></span>
          
            <g:each in="${orderInstance.items}" var="i">
            <span class="property-value" aria-labelledby="items-label"><g:link controller="orderItem" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></span>
            </g:each>
          
        </li>
        </g:if>
      
      </ol>
      <g:form>
        <fieldset class="buttons actions">
          <g:hiddenField name="id" value="${orderInstance?.id}" />
          <g:link class="list btn" action="list"><g:message code="default.back" /></g:link></li>
          <g:link class="edit btn primary" action="edit" id="${orderInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
	</body>
</html>
