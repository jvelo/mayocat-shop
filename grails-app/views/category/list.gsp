<%@ page import="org.mayocat.shop.grails.Category" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="catalogue">
		<g:set var="entityName" value="${message(code: 'category.label', default: 'Category')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="list-category" class="content" role="catalogue">
      <div class="page-header">
        <h2><g:message code="default.list.label" args="[entityName]" /></h2>
      </div>
      <g:if test="${flash.message}">
      <div class="message" role="status">${flash.message}</div>
      </g:if>
      <table class="table">
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
      <a class="add-entry btn btn-primary" href="${createLink(action:'create')}">
        <i class="icon-plus icon-white"></i>
        <g:message code="default.new.label" args="[entityName]" />
      </a>
    </div>
	</body>
</html>
