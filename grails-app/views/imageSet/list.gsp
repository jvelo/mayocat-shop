<%@ page import="org.mayocat.shop.grails.ImageSet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'imageSet.label', default: 'ImageSet')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span16">
      <div id="list-imageSet" class="content scaffold-list" role="main">
        <h3><g:message code="imageSet.list" default="Images" /></h3>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <ul class="media-grid">
          <g:each in="${imageSetInstanceList}" status="i" var="imageSetInstance">
            <li class="${(imageSetInstance.id == imageSetInstance.product.featuredImage.id) ? 'featured' : ''}">
              <g:link params="[productid: params.productid, id:imageSetInstance.id]" action="show">
                <img class="thumbnail"
                     src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: params.productid, id: imageSetInstance.id]])}"
                     title="${fieldValue(bean: imageSetInstance, field: 'caption')}" />
              </g:link>
              <div class="hidden description">${fieldValue(bean: imageSetInstance, field: "description")}</div>
            </li>
          </g:each>
        </ul>
        <g:if test="${imageSetInstanceTotal > imageSetInstanceList.size()}">
        <div class="pagination">
          <g:paginate controller="imageSet" action="list" params="[productid:params.productid]" total="${imageSetInstanceTotal}" />
        </div>
        </g:if>
        <div class="add-entry">
          <g:link params="[productid: params.productid]" class="create" action="create"><g:message code="imageSet.add" default="Add an image" /></g:link></li>
        </div>
      </div>
		</div>
	</body>
</html>
