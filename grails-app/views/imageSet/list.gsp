
<%@ page import="org.eschoppe.ImageSet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'imageSet.label', default: 'ImageSet')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span14 offset1">
      <div id="list-imageSet" class="content scaffold-list" role="main">
        <h2><g:message code="default.list.label" args="[entityName]" /></h2>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <table>
          <thead>
            <tr>
              <th><g:message code="imageSet.product.thumbnail" default="Thumbnail" /></th>
              <g:sortableColumn property="description" title="${message(code: 'imageSet.description.label', default: 'Description')}" />
              <g:sortableColumn property="caption" title="${message(code: 'imageSet.caption.label', default: 'Caption')}" />
            </tr>
          </thead>
          <tbody>
          <g:each in="${imageSetInstanceList}" status="i" var="imageSetInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td class="media-grid">
                <g:link params="[productid: params.productid, id:imageSetInstance.id]" action="show">
                  <img class="thumbnail" src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: params.productid, id: imageSetInstance.id]])}" />
                </g:link>
              </td>
              <td>${fieldValue(bean: imageSetInstance, field: "description")}</td>
              <td>${fieldValue(bean: imageSetInstance, field: "caption")}</td>
            </tr>
          </g:each>
          </tbody>
        </table>
        <div class="pagination">
          <g:paginate total="${imageSetInstanceTotal}" />
        </div>
        <div class="add-entry">
          <g:link params="[productid: params.productid]" class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </div>
      </div>
		</div>
	</body>
</html>
