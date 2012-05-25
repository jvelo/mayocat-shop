
<%@ page import="org.mayocat.shop.grails.Order" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="noColumn">
		<g:set var="entityName" value="${message(code: 'order.label', default: 'Order')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
    <r:require modules="timeago"/>
	</head>
	<body>
    <div id="list-order" class="content" role="main">
      <div class="page-header">
        <h2><g:message code="default.list.label" args="[entityName]" /></h2>
      </div>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>
      <g:if test="${orderInstanceList.size() <= 0}">
        <div class="alert alert-block">
          <a class="close">×</a>
          <h4 class="alert-heading"><g:message code="order.tableEmpty.header" default="Mama mia! There are no order yet!."/></h4>
          <g:message code="order.tableEmpty.message" default="" />
        </div>
      </g:if>
      <g:else>
        <table class="table order">
          <thead>
            <tr>            
              <th></th>

              <g:sortableColumn property="grandTotal" title="${message(code: 'order.grandTotal.label', default: 'Total')}" />

              <g:sortableColumn property="customerEmail" title="${message(code: 'order.customerEmail.label', default: 'Customer Email')}" />
            
              <th><g:message code="order.billingAddress.label" default="Billing Address" /></th>
            
              <th><g:message code="order.deliveryAddress.label" default="Delivery Address" /></th>
            
              <g:sortableColumn property="status" title="${message(code: 'order.status.label', default: 'Status')}" />
            
              <g:sortableColumn property="dateCreated" title="${message(code: 'order.dateCreated.label', default: 'Date Created')}" />
            
              <g:sortableColumn property="dateUpdated" title="${message(code: 'order.dateUpdated.label', default: 'Date Updated')}" />
            
            </tr>
          </thead>
          <tbody>
          <g:each in="${orderInstanceList}" status="i" var="orderInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

              <td><g:link action="show" id="${orderInstance.id}"><span class="show"></span></g:link></td>
              <td>
                <span class="total">
                 <span class="value">${fieldValue(bean: orderInstance, field: "grandTotal")}</span>
                 <span class="currency"></span>
                </span>
              </td>
              <td><a href="mailto:${fieldValue(bean: orderInstance, field: "customerEmail")}">${fieldValue(bean: orderInstance, field: "customerEmail")}</a></td>
            
              <td><g:address address="${orderInstance.billingAddress}" /></td>

              <td>
                <g:if test="${orderInstance.deliveryAddress}">
                  <g:address address="${orderInstance.billingAddress}" />
                </g:if>
                <g:else>
                  -
                </g:else>
              </td>
            
              <td>${fieldValue(bean: orderInstance, field: "status")}</td>
            
              <td><g:time class="timeago" datetime="${orderInstance.dateCreated}"></g:time></td>

              <td><g:time class="timeago" datetime="${orderInstance.dateUpdated}"></g:time></td>
            
            </tr>
          </g:each>
          </tbody>
        </table>
        <div class="pagination">
          <g:paginate total="${orderInstanceTotal}" />
        </div>
        <script>
        // <![CDATA[
        jQuery(document).ready(function() {
          jQuery(".timeago").timeago();
        });
        // ]]>
        </script>
      </g:else>
		</div>
	</body>
</html>
