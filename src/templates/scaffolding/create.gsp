<%=packageName%>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label', default: '${className}')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span16">
      <div id="create-${domainClass.propertyName}" class="content scaffold-create" role="main">
        <h2><g:message code="default.create.label" args="[entityName]" /></h2>
        <g:if test="\${flash.message}">
        <div class="message" role="status">\${flash.message}</div>
        </g:if>
        <g:hasErrors bean="\${${propertyName}}">
        <ul class="errors" role="alert">
          <g:eachError bean="\${${propertyName}}" var="error">
          <li <g:if test="\${error in org.springframework.validation.FieldError}">data-field-id="\${error.field}"</g:if>><g:message error="\${error}"/></li>
          </g:eachError>
        </ul>
        </g:hasErrors>
        <g:form action="save" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
          <fieldset class="form">
            <g:render template="form"/>
          </fieldset>
          <fieldset class="buttons actions">
            <g:submitButton name="create" class="save btn primary" value="\${message(code: 'default.button.create.label', default: 'Create')}" />
            <g:link class="list btn" action="list"><g:message code="default.cancel" /></g:link>
          </fieldset>
        </g:form>
      </div>
    </div>
	</body>
</html>
