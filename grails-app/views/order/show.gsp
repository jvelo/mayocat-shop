
<%@ page import="org.mayocat.shop.grails.Order" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'order.label', default: 'Order')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="show-order" class="content" role="main">
      <div class="page-header">
        <h2><g:message code="default.show.label" args="[entityName]" /></h2>
      </div>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>
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
