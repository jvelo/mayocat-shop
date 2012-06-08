<%@ page import="org.mayocat.shop.grails.Shop"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="hasSubMenu">
<parameter name="submenu" value="submenu:preferences" />
<g:set var="entityName"
	value="${message(code: 'shop.label', default: 'Shop')}" />
<title><g:message code="admin.preferences.title"
		default="Shop Preferences" /></title>
<r:require modules="handlebars"/>
</head>
<body>
	<content tag="menu">
	<div class="alert alert-warning">
		<g:message code="admin.preferences.help"
			default="Manage your shop preferences" />
	</div>
	</content>
	<div id="edit-shop" role="main">
		<h2>
			<g:message code="admin.preferences"
				default="Configure payment method: {0}" args="[method.displayName]" />
		</h2>
		<g:link action="editPaymentMethods">
			<g:message
				code="admin.preferences.paymentMethods.configureMethod.back"
				default="<< Back" />
		</g:link>
		<g:if test="${flash.message}">
			<div class="alert alert-success" role="status">
				${flash.message}
			</div>
		</g:if>

		<form class="well"
			action="${createLink(action:'configurePaymentMethod', id: method.technicalName)}"
			method="POST">
            <fieldset>
			${configurationForm}
            </fieldset>
			<div>
				<input type="submit" class="btn"
					value="<g:message code="admin.preferences.paymentMethods.updateConfiguration" default="Update" />" />
			</div>
		</form>

        <script id="configuration-template" type="text/x-handlebars-template">
        ${template}
		</script>
		
		<script type="text/javascript">
        // <![CDATA[
        
        // Utility to serialize a form to JSON
        // From : http://stackoverflow.com/questions/1184624/convert-form-data-to-js-object-with-jquery
        // FIXME: support for nested objects with DOT notation in input name.
        //        example : <input name="foo.bar"> -> {'foo' : { 'bar' : value }}
        $.fn.serializeObject = function() {
          var o = {},
              a = this.serializeArray();
          $.each(a, function() {
            if (o[this.name] !== undefined) {
              if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
              }
              o[this.name].push(this.value || '');
            } else {
              o[this.name] = this.value || '';
            }
          });
          return o;
        };

        $(document).ready(function(){
           var source   = $("#configuration-template").html()
             , template = Handlebars.compile(source);
            
          $("form input[type='submit']").click(function(event){            
            event.preventDefault();
            $.ajax({
              type: 'POST'
            , url: $("form").attr("action")
              // Send the serialized JSON representation of the configuration
            , data: JSON.stringify($("form").serializeObject())
            , contentType: "application/json; charset=utf-8"
              // Callback handle
            , success: function(json) {
                console.log(json);
                console.log(typeof json);
            	$("form fieldset").html(template(json));
            }});
          });
        });
        // ]]>
        </script>
	</div>
</body>
</html>