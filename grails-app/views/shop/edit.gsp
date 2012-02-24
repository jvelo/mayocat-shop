<%@ page import="org.mayocat.shop.grails.Shop" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'shop.label', default: 'Shop')}" />
		<title><g:message code="admin.preferences.title" default="Shop Preferences" /></title>
	</head>
	<body>
    <div class="span16">
      <div id="edit-shop" role="main">
        <h2><g:message code="admin.preferences" default="Preferences" /></h2>
        <g:if test="${flash.message}">
        <div class="alert alert-success" role="status">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${shopInstance}">
        <ul class="errors" role="alert">
          <g:eachError bean="${shopInstance}" var="error">
          <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
          </g:eachError>
        </ul>
        </g:hasErrors>
        <g:form method="post" class="form-horizontal">
          <g:hiddenField name="id" value="${shopInstance?.id}" />
          <g:hiddenField name="version" value="${shopInstance?.version}" />
          <fieldset class="form">
            <legend><g:message code="admin.preferences.general.label" default="General" /></legend>
            <g:render template="form"/>
          </fieldset>
          <fieldset class="buttons form-actions">
            <g:actionSubmit class="save btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
            <g:link class="btn list" action="edit"><g:message code="default.reset" default="Reset" /></g:link>
          </fieldset>
        </g:form>
      </div>
    </div>
	</body>
</html>
