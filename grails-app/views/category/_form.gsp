<%@ page import="org.mayocat.shop.grails.Category" %>

<div class="clearfix fieldcontain ${hasErrors(bean: categoryInstance, field: 'byname', 'error')} ">
	<label for="byname">
		<g:message code="category.byname.label" default="Byname" />
		
	</label>
  <div class="input">
	<g:textField name="byname" value="${categoryInstance?.byname}"/>
  </div>
</div>

<div class="clearfix fieldcontain ${hasErrors(bean: categoryInstance, field: 'products', 'error')} ">
	<label for="products">
		<g:message code="category.products.label" default="Products" />
		
	</label>
  <div class="input">
	<g:select name="products" from="${org.mayocat.shop.grails.Product.list()}" multiple="multiple" optionKey="id" size="5" value="${categoryInstance?.products*.id}" class="many-to-many"/>
  </div>
</div>

<div class="clearfix fieldcontain ${hasErrors(bean: categoryInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="category.title.label" default="Title" />
		
	</label>
  <div class="input">
	<g:textField name="title" value="${categoryInstance?.title}"/>
  </div>
</div>

