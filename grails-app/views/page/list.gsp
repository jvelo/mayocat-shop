
<%@ page import="org.mayocat.shop.grails.Page" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'page.label', default: 'Page')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
    <div id="list-page" class="content" role="main">
      <div class="page-header">
      <h2><g:message code="default.list.label" args="[entityName]" /></h2>
      </div>
      <g:if test="${flash.message}">
      <div class="alert" role="status">${flash.message}</div>
      </g:if>
      <table class="page table">
        <thead>
          <tr>
          
            <g:sortableColumn property="byname" title="${message(code: 'page.byname.label', default: 'Byname')}" />

            <g:sortableColumn property="title" title="${message(code: 'page.title.label', default: 'Title')}" />
          
            <g:sortableColumn property="content" title="${message(code: 'page.content.label', default: 'Content')}" />
          
          </tr>
        </thead>
        <tbody>
        <g:each in="${pageInstanceList}" status="i" var="pageInstance">
          <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
          
            <td><g:link action="show" id="${pageInstance.id}">${fieldValue(bean: pageInstance, field: "byname")}</g:link></td>
          
            <td>${fieldValue(bean: pageInstance, field: "title")}</td>

            <td>${fieldValue(bean: pageInstance, field: "content")}</td>
          
          </tr>
        </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <g:paginate total="${pageInstanceTotal}" />
      </div>
      <a href="${createLink(action:'create')}" class="add-entry btn btn-primary">
        <i class="icon-plus icon-white"></i>
        <g:message code="default.new.label" args="[entityName]" />
      </a>
    </div>
	</body>
</html>
