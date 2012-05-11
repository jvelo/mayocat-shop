<%@ page import="org.mayocat.shop.grails.ImageSet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'imageSet.label', default: 'ImageSet')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="list-imageSet" class="content" role="catalogue">
      <h3><g:message code="imageSet.list" default="Images" /></h3>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>
      <ul class="thumbnails">
        <g:each in="${imageSetInstanceList}" status="i" var="imageSetInstance">
          <g:if test="${i % 6 == 0 && i > 0}">
            </ul>
            <ul class="thumbnails">
          </g:if>
          <li class="span2" >
            <div class="thumbnail">
              <g:link params="[itemid: params.itemid, id:imageSetInstance.id, type:params.type]" action="show">
                <img class="thumb"
                     src="${createLink(url: [controller:'imageSet', action:'view', params: [itemid: params.itemid, id: imageSetInstance.id, type:params.type]])}"
                     title="${fieldValue(bean: imageSetInstance, field: 'caption')}" />
              </g:link>
              <p class="description">${fieldValue(bean: imageSetInstance, field: "description")}</p>
            </div>
          </li>
        </g:each>
      </ul>

        <g:if test="${imageSetInstanceTotal > imageSetInstanceList.size()}">
          <div class="pagination">
          <g:paginate controller="imageSet" action="list" params="[itemid:params.itemid, type:params.type]" total="${imageSetInstanceTotal}" />
          </div>
        </g:if>

        <g:link controller="imageSet" action="create" params="[itemid: params.itemid, type:params.type]" class="create btn" action="create">
          <i class="icon-plus"></i>
          <g:message code="imageSet.add" default="Add an image" />
        </g:link>
		</div>
	</body>
</html>
