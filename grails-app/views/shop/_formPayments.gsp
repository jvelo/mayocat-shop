<%@ page import="org.mayocat.shop.grails.Shop" %>
%{-- Payment methods --}%

<fieldset>
  <legend><g:message code="admin.preferneces.paymentMethods" default="Payment methods" /></legend>

    <g:set var="counter" value="${0}" />
    %{-- existing methods --}%
    <g:each in="${shopInstance.paymentMethod}" var="paymentMethod">
      <div class="control-group">
        %{-- technical name --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.technicalName" default="Technical name" />
        </label>
        <div class="controls">
          <input type="text" name="paymentMethod[${counter}].technicalName"
                             value="${paymentMethod.technicalName}" placeholder="Payment method name" class="" />
        </div>
      </div>
      <div class="control-group">
        %{-- display name --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.displayName" default="Display name" />
        </label>
        <div class="controls">
          <input type="text" name="paymentMethod[${counter}].displayName"
                             value="${paymentMethod.displayName}" placeholder="Display name" class="" />
        </div>
      </div>
      <div class="control-group">
        %{-- description --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.description" default="Description" />
        </label>
        <div class="controls">
          <textarea name="paymentMethod[${counter}].description">${paymentMethod.description}</textarea>
        </div>
      </div>
      <div class="control-group">
        %{-- display name --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.className" default="Class name" />
        </label>
        <div class="controls">
          <input type="text" name="paymentMethod[${counter}].className"
                             value="${paymentMethod.className}" placeholder="Ex: com.example.MyPaymentMethod" />
        </div>
      </div>
      <div class="control-group">
        %{-- enabled ? --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.enabled" default="Enabled" />
        </label>
        <div class="controls">
          <g:checkBox name="paymentMethod[${counter}].enabled" value="${paymentMethod.enabled}" />
        </div>
      </div>
      <div class="control-group">
        <g:link action="configurePaymentMethod" id="${paymentMethod.technicalName}" rel="modal">
          <g:message code="admin.preferences.paymentMethods.configure" default="Configure" />
        </g:link>
      </div>
      <hr />
      <g:set var="counter" value="${counter + 1}" />
    </g:each>

    %{-- new method --}%
    <div class="control-group">
      <a class="create btn" data-toggle="collapse" data-target="#addpaymentmethod">
                <i class="icon-plus"></i>
                <g:message code="admin.preferences.paymentMethods.addPaymentMethod" default="New payment method" />
      </a>
    </div>
    <div id="addpaymentmethod" class="collapse">
      %{-- default to not enabled --}%
      <input type="hidden" name="paymentMethod[${counter}].enabled" value="false"  />
      <div class="control-group">
        %{-- technical name --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.technicalName" default="Technical name" />
        </label>
        <div class="controls">
          <input type="text" name="paymentMethod[${counter}].technicalName"
                             value="" placeholder="Payment method name" class="" />
        </div>
      </div>
      <div class="control-group">
        %{-- display name --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.displayName" default="Display name" />
        </label>
        <div class="controls">
          <input type="text" name="paymentMethod[${counter}].displayName"
                             value="" placeholder="Display name" class="" />
        </div>
      </div>
      <div class="control-group">
        %{-- description --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.description" default="Description" />
        </label>
        <div class="controls">
          <textarea name="paymentMethod[${counter}].description"></textarea>
        </div>
      </div>
      <div class="control-group">
        %{-- display name --}%
        <label for="" class="control-label">
          <g:message code="admin.preferences.paymentMethods.className" default="Class name" />
        </label>
        <div class="controls">
          <input type="text" name="paymentMethod[${counter}].className"
                             value="" placeholder="Ex: com.example.MyPaymentMethod" />
        </div>
      </div>
    </div>

</fieldset>

