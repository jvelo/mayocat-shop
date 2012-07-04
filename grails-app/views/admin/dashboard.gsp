<%@page import="org.mayocat.shop.grails.Order"%>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require modules="timeago"/>
	</head>
	<body>
    <div class="hero-unit">
      <h2>
        <g:message code="dashboard.welcome" default="Welcome to your shop administration" />
      </h2>
    </div>
    <div class="widgets">
      <div class="widget span4">
        <h3>
          <g:message code="dashboard.ordersToProcess" default="Orders to process" />
        </h3>
        <g:set var="orders" value="${Order.findAllByStatus("PAID", [max: 15, sort: "dateCreated", order: "desc"])}" />
        <ul class="unstyled orders">
        <g:each in="${orders}" var="order">
          <li class="order">
            <g:link action="show" controller="order" id="${order.id}">
              <span class="show"></span>
            </g:link>
            <span class="total">
              <span class="value">${fieldValue(bean: order, field: "grandTotal")}</span>
              <span class="currency"></span>
            </span>
            <span class="customerEmail">${order.customerEmail}</span>
            <div class="date">
              <g:formatDate format="yyyy/MM/dd" date="${order.dateCreated}" />
              <g:time class="timeago" datetime="${order.dateCreated}"></g:time>
            </div>
          </li>
        </g:each>
        </ul>
      </div>

      <div class="widget span4">
        <h3>
          <g:message code="dashboard.ordersToProcess" default="Orders waiting for payment" />
        </h3>
        <g:set var="orders" value="${Order.findAllByStatus("WAITING_FOR_PAYMENT", [max: 15, sort: "dateCreated", order: "desc"])}" />
        <g:each in="${orders}" var="order">
        <ul class="orders unstyled">
          <li class="order">
            <g:link action="show" controller="order" id="${order.id}">
              <span class="show"></span>
            </g:link>
            <span class="total">
              <span class="value">${fieldValue(bean: order, field: "grandTotal")}</span>
              <span class="currency"></span>
            </span>
            <span class="customerEmail">${order.customerEmail}</span>
            <div class="date">
              <g:formatDate format="yyyy/MM/dd" date="${order.dateCreated}" />
              <g:time class="timeago" datetime="${order.dateCreated}"></g:time>
            </div>
          </li>
        </ul>
        </g:each>
      </div>
    </div>
    <script>
    // <![CDATA[
    jQuery(document).ready(function() {
      jQuery(".timeago").timeago();
    });
    // ]]>
    </script>
	</body>
</html>
