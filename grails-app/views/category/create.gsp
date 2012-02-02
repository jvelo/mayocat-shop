<%@ page import="org.mayocat.shop.grails.Category" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'category.label', default: 'Category')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="create-category" class="content scaffold-create" role="catalogue">
      <div class="page-header">
        <h2><g:message code="default.create.label" args="[entityName]" /></h2>
      </div>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>
      <g:hasErrors bean="${categoryInstance}">
      <ul class="errors" role="alert">
        <g:eachError bean="${categoryInstance}" var="error">
        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
      </ul>
      </g:hasErrors>
      <g:form action="save" >
        <fieldset class="form">
          <g:render template="form"/>
        </fieldset>
        <fieldset class="buttons actions">
          <g:submitButton name="create" class="save btn primary" value="${message(code: 'default.button.create.label', default: 'Create')}" />
          <g:link class="list btn" action="list"><g:message code="default.cancel" /></g:link>
        </fieldset>
      </g:form>
    </div>
	</body>
</html>
