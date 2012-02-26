<%@ page import="org.mayocat.shop.grails.Product" %>

<div class="control-group ${hasErrors(bean: productInstance, field: 'title', 'error')} required">
	<label for="title" class="control-label">
		<g:message code="product.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
  <div class="controls">
	<g:textField name="title" required="" value="${productInstance?.title}"/>
  </div>
</div>

<div class="control-group ${hasErrors(bean: productInstance, field: 'price', 'error')} ">
	<label for="price" class="control-label">
		<g:message code="product.price.label" default="Price" />
		
	</label>
  <div class="controls">
	<g:field type="number" name="price" min="0.0" value="${fieldValue(bean: productInstance, field: 'price')}"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: productInstance, field: 'exposed', 'error')} ">
	<label for="exposed" class="control-label">
		<g:message code="product.exposed.label" default="Exposed" />
		
	</label>
  <div class="controls">
	<g:checkBox name="exposed" value="${productInstance?.exposed}" />
  </div>
</div>
