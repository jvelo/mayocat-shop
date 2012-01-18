
<%@ page import="org.eschoppe.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span16">
      <div id="list-product" class="content scaffold-list" role="main">
        <h2><g:message code="default.list.label" args="[entityName]" /></h2>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <table class="products">
          <thead>
            <tr>
            
              <g:sortableColumn property="title" title="${message(code: 'product.title.label', default: 'Title')}" />
            
              <g:sortableColumn property="price" title="${message(code: 'product.price.label', default: 'Price')}" />
              
              <g:sortableColumn property="exposed" title="${message(code: 'product.exposed.label', default: 'Exposed')}" />
            
            </tr>
          </thead>
          <tbody>
          <g:each in="${productInstanceList}" status="i" var="productInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'} ${productInstance.exposed ? 'exposed' : 'notExposed'}">
            
              <td><g:link action="show" id="${productInstance.id}">${fieldValue(bean: productInstance, field: "title")}</g:link></td>
            
              <td>${fieldValue(bean: productInstance, field: "title")}</td>
            
              <td class="exposition <g:if test='${!productInstance.exposed}'>notExposed</g:if>">
                <g:if test='${productInstance.exposed}'>
                  <g:message code="produt.exposed" default="Exposed" />
                </g:if>
                <g:else>
                  <g:message code="product.notExposed" default="Not exposed" />
                </g:else>
              </td>
            
            </tr>
          </g:each>
          </tbody>
        </table>
        <div class="pagination">
          <g:paginate total="${productInstanceTotal}" />
        </div>
        <div class="add-entry">
          <g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </div>
      </div>
		</div>
	</body>
</html>
