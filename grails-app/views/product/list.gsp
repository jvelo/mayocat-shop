<%@ page import="org.mayocat.shop.grails.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="list-product" class="content" role="catalogue">
      <div class="page-header">
        <h2><g:message code="default.list.label" args="[entityName]" /></h2>
      </div>
      <g:if test="${flash.message}">
        <div class="alert" role="status">${flash.message}</div>
      </g:if>
      <g:if test="${productInstanceList.size() <= 0}">
        <div class="alert alert-block">
          <a class="close">×</a>
          <h4 class="alert-heading"><g:message code="product.catalogueEmpty.header" default="Mama mia! The catalogue is empty."/></h4>
          <g:message code="product.catalogueEmpty.message" default="The is no product in the catalogue yet. Why not start making it great by adding new products ?" />
        </div>
      </g:if>
      <g:else>
        <table class="products table">
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
            
              <td>
                <g:link action="show" id="${productInstance.id}">
                ${fieldValue(bean: productInstance, field: "title")}
                </g:link>
                <small>
                  /product/${fieldValue(bean:productInstance, field: "byname")}
                </small>
              </td>

              <td>${fieldValue(bean: productInstance, field: "price")}</td>
            
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
      </g:else>
      <div>
      <a href="${createLink(action:'create')}" class="add-entry btn btn-primary">
        <i class="icon-plus icon-white"></i>
        <g:message code="default.new.label" args="[entityName]" />
      </a>
      </div>
		</div>
	</body>
</html>
