<%@ page import="org.mayocat.shop.grails.ImageSet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'imageSet.label', default: 'ImageSet')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="create-imageSet" class="content scaffold-create" role="catalogue">
      <h2><g:message code="default.create.label" args="[entityName]" /></h2>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>
      <g:hasErrors bean="${imageSetInstance}">
      <ul class="errors" role="alert">
        <g:eachError bean="${imageSetInstance}" var="error">
        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
      </ul>
      </g:hasErrors>
        <form method="post"
              class="form-horizontal"
              enctype="multipart/form-data"
              action="${createLink(url:[controller:"imageSet", action:"save", params:[itemid: params.itemid, type:params.type]])}"
          >
        <fieldset>
          <div class="control-group">
            <label for="file" class="control-label">
              <g:message code="imageSet.file.label" default="Image file" />
              <span class="required-indicator">*</span>
            </label>
            <div class="controls">
              <input type="file" name="file" />
            </div>
          </div>
          <g:render template="form"/>
          <g:hiddenField name="itemid" value="${params.itemid}" />
          <g:hiddenField name="type" value="${params.type}" />
          <g:if test="${params.productid != null}">
            <g:hiddenField name="product.id" value="${params.productid}" />
          </g:if>
          <g:else>
            <g:hiddenField name="page.id" value="${params.pageid}" />
          </g:else>
          <div class="buttons form-actions">
            <g:submitButton name="create" class="save btn btn-primary" value="${message(code: 'default.button.create.label', default: 'Create')}" />
            <g:if test="${params.productid != null}">
              <g:link class="back btn" action="show" controller="product" params="[id:params.itemid]"><g:message code="default.cancel" /></g:link>
            </g:if>
            <g:else>
              <g:link class="back btn" action="show" controller="page" params="[id:params.itemid]"><g:message code="default.cancel" /></g:link>
            </g:else>
          </div>
        </fieldset>
      </form>
    </div>
	</body>
</html>
