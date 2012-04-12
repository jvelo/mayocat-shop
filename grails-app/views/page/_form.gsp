<%@ page import="org.mayocat.shop.grails.Page" %>

<div class="control-group fieldcontain ${hasErrors(bean: pageInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="page.title.label" default="Title" />
		
	</label>
  <div class="controls">
	<g:textField name="title" value="${pageInstance?.title}"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: pageInstance, field: 'content', 'error')} ">
	<label for="content">
		<g:message code="page.content.label" default="Content" />
		
	</label>
  <div class="controls">
	<g:textArea name="content" value="${pageInstance?.content}"/>
  </div>
</div>
