
<%@ page import="org.eschoppe.ImageSet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'imageSet.label', default: 'ImageSet')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span14 offset1">
      <div id="show-imageSet" class="content scaffold-show" role="main">
        <h2><g:message code="default.show.label" args="[entityName]" /></h2>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <ol class="property-list imageSet">
          <li>
            <img class="Photo" src="${createLink(controller:'imageSet', action:'view', params:'[id: imageSetInstance.id]')}" />
          </li> 
        </ol>
        <g:form>
          <fieldset class="buttons actions">
            <g:hiddenField name="id" value="${imageSetInstance?.id}" />
            <g:link class="list btn" action="list"><g:message code="default.back" /></g:link></li>
            <g:link class="edit btn primary" action="edit" id="${imageSetInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
            <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
          </fieldset>
        </g:form>
      </div>
    </div>
	</body>
</html>
