
<%@ page import="org.eschoppe.Category" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'category.label', default: 'Category')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div class="span14 offset1">
      <div id="list-category" class="content scaffold-list" role="main">
        <h2><g:message code="default.list.label" args="[entityName]" /></h2>
        <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        </g:if>
        <table>
          <thead>
            <tr>
            
              <g:sortableColumn property="byname" title="${message(code: 'category.byname.label', default: 'Byname')}" />
            
              <g:sortableColumn property="title" title="${message(code: 'category.title.label', default: 'Title')}" />
            
            </tr>
          </thead>
          <tbody>
          <g:each in="${categoryInstanceList}" status="i" var="categoryInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
            
              <td><g:link action="show" id="${categoryInstance.id}">${fieldValue(bean: categoryInstance, field: "byname")}</g:link></td>
            
              <td>${fieldValue(bean: categoryInstance, field: "title")}</td>
            
            </tr>
          </g:each>
          </tbody>
        </table>
        <div class="pagination">
          <g:paginate total="${categoryInstanceTotal}" />
        </div>
        <div class="add-entry">
          <g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </div>
      </div>
		</div>
	</body>
</html>
