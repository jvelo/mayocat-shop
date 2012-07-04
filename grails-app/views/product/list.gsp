<%@ page import="org.mayocat.shop.grails.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="hasSubMenu">
    <parameter name="submenu" value="submenu:catalogue" />
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
        <div class="filters-toggle" class="toggle"  data-toggle="collapse" data-target="#filters">
          <i class="icon-cog"></i> filters...
        </div>
        <div id="filters"  class="collapse">
          <g:form>
            <fieldset class="well">
              <label class="checkbox">
                <input type="checkbox" name="exposed" value="1" <g:if test="${params.exposed}">checked</g:if>> <g:message code="product.filters.exposed" default="Also include products marked as not exposed" />
              </label>
              <button type="submit" class="btn"><g:message code="product.filters.update" default="Update" /></button>              
            </fieldset>
          </g:form>
        </div>
        <table class="products table">
          <thead>
            <tr>
              <g:set var="exposed" value="${request.exposed ? 1 : 0}" />

              <g:sortableColumn property="title" title="${message(code: 'product.title.label', default: 'Title')}" params="[exposed: exposed]" />
            
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

              <td><g:price price="${productInstance.price}" currency="EUR" /></td>
            
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
          <g:if test="${params.exposed}">
            <g:paginate total="${productInstanceTotal}" params="[exposed:1]" />
          </g:if>
          <g:else>
            <g:paginate total="${productInstanceTotal}" />
          </g:else>
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
