<%@ page import="org.eschoppe.Product" %>



<div class="clearfix fieldcontain ${hasErrors(bean: productInstance, field: 'byname', 'error')} ">
	<label for="byname">
		<g:message code="product.byname.label" default="Byname" />
		
	</label>
  <div class="input">
	<g:textField name="byname" value="${productInstance?.byname}"/>
  </div>
</div>

<div class="clearfix fieldcontain ${hasErrors(bean: productInstance, field: 'categories', 'error')} ">
	<label for="categories">
		<g:message code="product.categories.label" default="Categories" />
		
	</label>
  <div class="input">
	
  </div>
</div>

<div class="clearfix fieldcontain ${hasErrors(bean: productInstance, field: 'price', 'error')} required">
	<label for="price">
		<g:message code="product.price.label" default="Price" />
		<span class="required-indicator">*</span>
	</label>
  <div class="input">
	<g:field type="number" name="price" required="" value="${fieldValue(bean: productInstance, field: 'price')}"/>
  </div>
</div>

<div class="clearfix fieldcontain ${hasErrors(bean: productInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="product.title.label" default="Title" />
		
	</label>
  <div class="input">
	<g:textField name="title" value="${productInstance?.title}"/>
  </div>
</div>

