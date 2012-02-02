<%@ page import="org.mayocat.shop.grails.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
    <r:require module="productEditor" />
	</head>
	<body>
    <div id="show-product" class="content" role="catalogue">
      <div class="page-header">
      <h1>
        ${productInstance.title}
        <small>
        (<g:message code="commons.at" default="at" />
          <g:link target="_blank" controller="product" action="expose" params="[byname:productInstance.byname]">/product/${productInstance.byname}</g:link>)
        </small>
      </h1>
      </div>
      <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
      </g:if>

      <div class="exposition">
        <input type="checkbox" ${productInstance.exposed ? 'checked' : ''} 
               name="exposed"
               value="true"
               data-update-uri="${createLink(url: [controller:'product', action:'update', params:[id:productInstance.id]])}"/> Exposed
      </div>

      <ul class="property-list product">

        <g:if test="${productInstance?.price}">
        <li class="fieldcontain">
          <span id="price-label" class="property-label"><g:message code="product.price.label" default="Price" /></span>
          
            <span class="property-value" aria-labelledby="price-label"><g:fieldValue bean="${productInstance}" field="price"/></span>
          
        </li>
        </g:if>
      
        <g:if test="${productInstance?.categories}">
        <li class="fieldcontain">
          <span id="categories-label" class="property-label"><g:message code="product.categories.label" default="Categories" /></span>
          
            <g:each in="${productInstance.categories}" var="c">
            <span class="property-value" aria-labelledby="categories-label"><g:link controller="category" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
            </g:each>
          
        </li>
        </g:if>
      
      </ul>

      <g:include controller="imageSet" action="list" params="[productid:productInstance.id]" />

      <g:form>
        <fieldset class="buttons actions">
          <g:hiddenField name="id" value="${productInstance?.id}" />
          <g:link class="list btn" action="list"><g:message code="default.back" /></g:link></li>
          <g:link class="edit btn primary" action="edit" id="${productInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
	</body>
</html>
