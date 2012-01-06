
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
            
            </tr>
          </thead>
          <tbody>
          <g:each in="${imageSetInstanceList}" status="i" var="imageSetInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            
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
