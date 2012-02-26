<%@ page import="org.mayocat.shop.grails.Product" %>

<div class="control-group fieldcontain ${hasErrors(bean: productInstance, field: 'title', 'error')} required">
	<label for="title" class="control-label">
		<g:message code="product.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
  <div class="controls">
	<g:textField name="title" required="" value="${productInstance?.title}"/>
  </div>
</div>
