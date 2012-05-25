<%@ page import="org.mayocat.shop.grails.Order" %>



<div class="control-group fieldcontain ${hasErrors(bean: orderInstance, field: 'customerEmail', 'error')} ">
	<label for="customerEmail">
		<g:message code="order.customerEmail.label" default="Customer Email" />
		
	</label>
  <div class="controls">
	<g:textField name="customerEmail" value="${orderInstance?.customerEmail}"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: orderInstance, field: 'billingAddress', 'error')} required">
	<label for="billingAddress">
		<g:message code="order.billingAddress.label" default="Billing Address" />
		<span class="required-indicator">*</span>
	</label>
  <div class="controls">
	<g:select id="billingAddress" name="billingAddress.id" from="${org.mayocat.shop.grails.Address.list()}" optionKey="id" required="" value="${orderInstance?.billingAddress?.id}" class="many-to-one"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: orderInstance, field: 'deliveryAddress', 'error')} ">
	<label for="deliveryAddress">
		<g:message code="order.deliveryAddress.label" default="Delivery Address" />
		
	</label>
  <div class="controls">
	<g:select id="deliveryAddress" name="deliveryAddress.id" from="${org.mayocat.shop.grails.Address.list()}" optionKey="id" value="${orderInstance?.deliveryAddress?.id}" class="many-to-one" noSelection="['null': '']"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: orderInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="order.status.label" default="Status" />
		
	</label>
  <div class="controls">
	<g:select name="status" from="${org.mayocat.shop.grails.OrderStatus?.values()}" keys="${org.mayocat.shop.grails.OrderStatus.values()*.name()}" value="${orderInstance?.status?.name()}" noSelection="['': '']"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: orderInstance, field: 'dateUpdated', 'error')} required">
	<label for="dateUpdated">
		<g:message code="order.dateUpdated.label" default="Date Updated" />
		<span class="required-indicator">*</span>
	</label>
  <div class="controls">
	<g:datePicker name="dateUpdated" precision="day"  value="${orderInstance?.dateUpdated}"  />
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: orderInstance, field: 'items', 'error')} ">
	<label for="items">
		<g:message code="order.items.label" default="Items" />
		
	</label>
  <div class="controls">
	<g:select name="items" from="${org.mayocat.shop.grails.OrderItem.list()}" multiple="multiple" optionKey="id" size="5" value="${orderInstance?.items*.id}" class="many-to-many"/>
  </div>
</div>

