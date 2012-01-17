<%@ page import="org.eschoppe.Product" %>



<div class="clearfix fieldcontain ${hasErrors(bean: productInstance, field: 'title', 'error')} required">
	<label for="title">
		<g:message code="product.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
  <div class="input">
	<g:textField name="title" required="" value="${productInstance?.title}"/>
  </div>
</div>

<div class="clearfix fieldcontain ${hasErrors(bean: productInstance, field: 'price', 'error')} required">
	<label for="price">
		<g:message code="product.price.label" default="Price" />
		<span class="required-indicator">*</span>
	</label>
  <div class="input">
	<g:field type="number" name="price" min="0.0" required="" value="${fieldValue(bean: productInstance, field: 'price')}"/>
  </div>
</div>

