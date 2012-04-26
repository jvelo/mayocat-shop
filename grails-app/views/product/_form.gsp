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
    <g:if test="${(!productInstance.stock || productInstance.stock == 1) && shopInstance?.singleUnitProducts}">
         disabled
    </g:if>
  />
  </div>
</div>
</fieldset>

<g:if test="$shopInstance?.sentBySnailMail">
  <fieldset class="form">
    <legend>Package</legend>
    <g:set var="counter" value="${0}" />
    <g:set var="consumed" value="[]"/>
    <g:each in="${productInstance.packageDimensions}" var="dimension">
      <g:if test="${shopInstance?.packageManagement?.getProperty(dimension.type)}">
      <div class="control-group fieldcontain ${hasErrors(bean: productInstance, field: 'price', 'error')} ">
        <label for="price" class="control-label">
          <g:message code="product.package.${dimension.type}"
             default="${dimension.type.substring(0, 1).toUpperCase()}${dimension.type.substring(1, dimension.type.size())}" />
        </label>
        <div class="controls">
          <input type="number" name="packageDimensions[${counter}].value"
               value="${dimension.value}" step="10"/>
          <span class="unit">
            <g:if test="${dimension.type == 'weight'}">grams</g:if>
            <g:else>cm</g:else>
          </span>
        </div>
      </div>
      <input type="hidden" name="packageDimensions[${counter}].type"
               value="${dimension.type}" />
      </g:if>
      <g:else>
        <input type="hidden" name="packageDimensions[${counter}].value"
               value="${dimension.value}"/>
        <input type="hidden" name="packageDimensions[${counter}].type"
               value="${dimension.type}" />
      </g:else>
      <g:set var="counter" value="${counter + 1}" />
      <g:set var="void" value="${consumed.add(dimension.type)}" />
    </g:each>
    <g:each in="['weight','width','length','height']" status="i" var="it">
      <g:if test="${shopInstance?.packageManagement?.getProperty(it) && !consumed.contains(it)}">
        <label for="price" class="control-label">
          <g:message code="product.package.${it}"
             default="${it.substring(0, 1).toUpperCase()}${it.substring(1, it.size())}" />
        </label>
        <div class="controls">
          <input type="number" name="packageDimensions[${counter}].value"
               value="" step="10"/>
          <span class="unit">
            <g:if test="${it == 'weight'}">grams</g:if>
            <g:else>cm</g:else>
          </span>
        </div>
        <input type="hidden" name="packageDimensions[${counter}].type"
               value="${it}" />
        <g:set var="counter" value="${counter + 1}" />
      </g:if>
    </g:each>
  </fieldset>
</g:if>
