<%@ page import="org.mayocat.shop.grails.Product" %>

<fieldset class="form">
<div class="control-group ${hasErrors(bean: productInstance, field: 'title', 'error')} required">
	<label for="title" class="control-label">
		<g:message code="product.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
  <div class="controls">
	<g:textField name="title" required="" value="${productInstance?.title}"/>
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
</fieldset>

<div class="control-group fieldcontain ${hasErrors(bean: productInstance, field: 'description', 'error')} ">
	<label for="exposed" class="control-label">
		<g:message code="product.description.label" default="Description" />
		
	</label>
  <div class="controls">
	<g:textArea name="description" value="${productInstance?.description}" rows="8" />
  </div>
</div>
</fieldset>

<fieldset class="form">
<legend>Price, stock, variants</legend>
<div class="control-group fieldcontain ${hasErrors(bean: productInstance, field: 'price', 'error')} ">
	<label for="price" class="control-label">
		<g:message code="product.price.label" default="Price" />
		
	</label>
  <div class="controls">
	<input type="number" name="price" value="${productInstance.price}" min="0.0" step="0.01" />
  </div>
</div>
<div class="control-group fieldcontain ${hasErrors(bean: productInstance, field: 'stock', 'error')} ">
	<label for="stock" class="control-label">
		<g:message code="product.price.label" default="In stock" />
		
	</label>
  <div class="controls">
	<input type="number"
         name="stock"
         value="${productInstance.stock}"
         min="0"
         step="1"
    <g:if test="${(!productInstance.stock || productInstance.stock == 1) && shopInstance.singleUnitProducts}">
         disabled
    </g:if>
  />
  </div>
</div>
</fieldset>

