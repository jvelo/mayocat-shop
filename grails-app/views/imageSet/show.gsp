
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

        <h3><g:message code="imageSet.original" default="Original" /></h3>
        <ul class="property-list imageSet media-grid clearfix">
          <li>
            <a href="">
              <img class="thumbnail" src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: params.productid, id: imageSetInstance.id]])}" />
            </a>
          </li>
        </ul>

        <h3><g:message code="imageSet.thumbnails" default="Thumbnails" /></h3>
        <ul class="media-grid clearfix">
        <g:each in="${thumbnailSizes.keySet()}" status="i" var="size">
          <li>
            ${size} ${thumbnailSizes.get(size)}
            <a href="">
              <img class="thumbnail"
                   style="max-width:${thumbnailSizes.get(size)?.width}px; max-height:${thumbnailSizes.get(size)?.height}px" 
                   src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: params.productid, id: imageSetInstance.id]])}" />
            </a>
          </li>
        </g:each>
        </ul>

        <g:if test="${imageSetInstance?.description}">
        <li class="fieldcontain">
          <span id="description-label" class="property-label"><g:message code="imageSet.description.label" default="Description" /></span>
            <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${imageSetInstance}" field="description"/></span>            
        </li>
        </g:if>
      
        <g:if test="${imageSetInstance?.caption}">
        <li class="fieldcontain">
          <span id="caption-label" class="property-label"><g:message code="imageSet.caption.label" default="Caption" /></span>
            <span class="property-value" aria-labelledby="caption-label"><g:fieldValue bean="${imageSetInstance}" field="caption"/></span>
        </li>
        </g:if>
        
        <g:form>
          <fieldset class="buttons actions">
            <g:hiddenField name="id" value="${imageSetInstance?.id}" />
            <g:link class="list btn" action="list" params="[productid: imageSetInstance?.product?.id]"><g:message code="default.back" /></g:link></li>
            <g:link class="edit btn primary" action="edit" id="${imageSetInstance?.id}" params="[productid: imageSetInstance?.product?.id]"><g:message code="default.button.edit.label" default="Edit" /></g:link>
            <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
          </fieldset>
        </g:form>
      </div>
    </div>
	</body>
</html>
