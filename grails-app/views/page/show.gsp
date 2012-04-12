
<%@ page import="org.mayocat.shop.grails.Page" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'page.label', default: 'Page')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
   <div id="show-page" class="content" role="catalogue">
     <div class="page-header">
     <h1>
       ${pageInstance.title}
       <small>
       (<g:message code="commons.at" default="at" />
         <g:link target="_blank" controller="page" action="expose" params="[byname:pageInstance.byname]">/page/${pageInstance.byname}</g:link>)
       </small>
     </h1>
     </div>

      <g:if test="${flash.message}">
        <div class="alert alert-info" role="status">${flash.message}</div>
      </g:if>
      <ol class="property-list page">
      
     <g:hasErrors bean="${pageInstance}">
     <ul class="errors" role="alert">
       <g:eachError bean="${pageInstance}" var="error">
       <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
       </g:eachError>
     </ul>
     </g:hasErrors>
     <g:form method="post" class="form-horizontal">
       <g:hiddenField name="id" value="${pageInstance?.id}" />
       <g:hiddenField name="version" value="${pageInstance?.version}" />
       <g:render template="form"/>
       <fieldset class="buttons actions">
         <g:actionSubmit class="save btn primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
       </fieldset>
     </g:form>

   </div>
	</body>
</html>
