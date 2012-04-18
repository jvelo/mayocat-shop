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

    <content tag="panel">
      <div class="alert alert-error">
        <strong>
          <g:message code="common.dangerZone" default="Danger Zone!" />
        </strong>
        <g:message code="product.delete" default="There is no undo." />
        <div>
          <g:form method="post"> 
            <g:hiddenField name="id" value="${productInstance?.id}" />
            <g:hiddenField name="version" value="${productInstance?.version}" />
            <g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'product.delete.label', default: 'Delete this product')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
          </g:form>
        </div>
      </div>
    </content>

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
        <div class="alert alert-info" role="status">${flash.message}</div>
      </g:if>

      <h3>
        <g:message code="product.details.title" default="Product's details" />
      </h3>

      <g:hasErrors bean="${productInstance}">
      <ul class="errors" role="alert">
        <g:eachError bean="${productInstance}" var="error">
        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
      </ul>
      </g:hasErrors>
      <g:form method="post" class="form-horizontal">
        <g:hiddenField name="id" value="${productInstance?.id}" />
        <g:hiddenField name="version" value="${productInstance?.version}" />
        <g:render template="form"/>
        <fieldset class="buttons actions">
          <g:actionSubmit class="save btn primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
        </fieldset>
      </g:form>
      
      <h3>
        <g:message code="product.categories.title" default="Categories" />
      </h3>

      <g:include controller="product" action="editCategories" params="[productid:productInstance.id]" />

      <g:include controller="imageSet" action="list" params="[productid:productInstance.id]" />
 
    </div>
	</body>
</html>
