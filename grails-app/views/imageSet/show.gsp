<%@ page import="org.mayocat.shop.grails.ImageSet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'imageSet.label', default: 'ImageSet')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
    <r:require module="thumbnailEditor" />
	</head>
	<body>
    <div id="show-imageSet" class="content scaffold-show" role="catalogue">
      <h2><g:message code="default.show.label" args="[entityName]" /></h2>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>

      <div id="preview-modal" class="modal hide">
        <div class="modal-header">
          <a href="#" class="close">&times;</a>
          <h3><g:message code="imageSet.editThumbnail" default="Edit thumbnail" /></h3>
        </div>
        <div class="modal-body loading"></div>
        <div class="modal-footer">
          <a href="#" class="btn primary"><g:message code="imageSet.editThumbnail.save" default="Save" /></a>
          <a href="#" class="btn secondary"><g:message code="imageSet.editThumbnail.cancel" default="Cancel" /></a>
        </div>
      </div>

      <h3><g:message code="imageSet.original" default="Original" /></h3>
      <ul class="property-list imageSet media-grid clearfix">
        <li>
          <a href="">
            <img class="thumbnail" src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: params.productid, id: imageSetInstance.id]])}" />
          </a>
        </li>
      </ul>

      <h3><g:message code="imageSet.thumbnails" default="Thumbnails" /></h3>
      <noscript>
        <div class="alert-message warning">
          <p>
            <g:message code="imageSet.editThumbnail.javascript.required"
                       default="JavaScript is required in order to edit thumbnails. Activate it in your browser and reload the page in order to edit this image thumbnails." />
          </p>
        </div>
      </noscript>
      <ul class="media-grid clearfix">
      <g:each in="${thumbnailSizes.keySet()}" status="i" var="size">
        <li class="thumbnail-list">
          <div class="thumbnail-wrapper">
            <img class="thumbnail"
                 style="max-width:${thumbnailSizes.get(size)?.width}px; max-height:${thumbnailSizes.get(size)?.height}px" 
                 src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: params.productid, id: imageSetInstance.id]])}" />
          </div>
          <div>
            <span class="label notice">${size} <small>(${thumbnailSizes.get(size).width}:${thumbnailSizes.get(size).height})</small></span>
            <div>
              <span rel="modal" 
                    data-edit-uri="${createLink(url: [controller:'imageSet', action:'editThumbnail', params: [productid: params.productid, id: imageSetInstance.id, size:size]])}"
                    data-save-uri="${createLink(url: [controller:'imageSet', action:'saveThumbnail', params: [productid: params.productid, id: imageSetInstance.id, size:size]])}"
                    data-size-hint="${size}" 
                    data-size-width="${thumbnailSizes.get(size).width}"
                    data-size-height="${thumbnailSizes.get(size).height}">
                    <g:message code="imageSet.thumbnail.edit" default="edit" />
              </span>
            </div>
          </div>
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
          <g:link class="back btn" action="show" controller="product" params="[id: imageSetInstance?.product?.id]"><g:message code="default.back" /></g:link></li>
          <g:link class="edit btn primary" action="edit" id="${imageSetInstance?.id}" params="[productid: imageSetInstance?.product?.id]"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete btn danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
	</body>
</html>
