
<%@ page import="org.eschoppe.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span14 offset1">
      <div id="show-product" class="content scaffold-show" role="main">
        <h2><g:message code="default.show.label" args="[entityName]" /></h2>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <ol class="property-list product">
        
          <g:if test="${productInstance?.byname}">
          <li class="fieldcontain">
            <span id="byname-label" class="property-label"><g:message code="product.byname.label" default="Byname" /></span>
            
              <span class="property-value" aria-labelledby="byname-label"><g:fieldValue bean="${productInstance}" field="byname"/></span>
            
          </li>
          </g:if>
        
          <g:if test="${productInstance?.title}">
          <li class="fieldcontain">
            <span id="title-label" class="property-label"><g:message code="product.title.label" default="Title" /></span>
            
              <span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${productInstance}" field="title"/></span>
            
          </li>
          </g:if>
        
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
        
        </ol>
        <g:form>
          <fieldset class="buttons actions">
            <g:hiddenField name="id" value="${productInstance?.id}" />
            <g:link class="list btn" action="list"><g:message code="default.back" /></g:link></li>
            <g:link class="edit btn primary" action="edit" id="${productInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
            <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
          </fieldset>
        </g:form>
      </div>
    </div>
	</body>
</html>
