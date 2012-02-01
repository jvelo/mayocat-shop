<%@ page import="org.mayocat.shop.grails.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span16">
      <div id="edit-product" class="content scaffold-edit" role="main">
        <h2><g:message code="default.edit.label" args="[entityName]" /></h2>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${productInstance}">
        <ul class="errors" role="alert">
          <g:eachError bean="${productInstance}" var="error">
          <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
          </g:eachError>
        </ul>
        </g:hasErrors>
        <g:form method="post" >
          <g:hiddenField name="id" value="${productInstance?.id}" />
          <g:hiddenField name="version" value="${productInstance?.version}" />
          <fieldset class="form">
            <g:render template="form"/>
          </fieldset>
          <fieldset class="buttons actions">
            <g:link class="btn list" action="list"><g:message code="default.cancel" /></g:link>
            <g:actionSubmit class="save btn primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
            <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" formnovalidate="" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
          </fieldset>
        </g:form>
      </div>
    </div>
	</body>
</html>
