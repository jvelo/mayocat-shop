<%@ page import="org.mayocat.shop.grails.Shop" %>
%{-- Payment methods --}%

<fieldset>
  <legend><g:message code="admin.preferences.checkoutPages" default="Checkout pages" /></legend>
  
  <div class="control-group">
    <label class="control-label" for="logo">
      <g:message code="admin.preferences.checkoutPages.logo" default="Logo" />
    </label>
    <input type="file" name="logo" id="logo" />
  </div>
  
  <div class="control-group">
    <label class="control-label" for="extraCss">
      <g:message code="admin.preferences.checkoutPages.extraCss" default="Extra CSS code to insert" />
    </label>
    <textarea class="code" name="checkoutPages.extraCss" id="extraCss">${shopInstance.checkoutPages.extraCss}</textarea>
    <div id="extraCssAce"></div>  
  </div>
  
    <script type="text/javascript">
    $(document).ready(function(){
      var editor = ace.edit("extraCssAce")
         , ta = $('#extraCss').hide()
        , CssMode = require("ace/mode/css").Mode;
      editor.getSession().setValue(ta.val());
      editor.getSession().setMode(new CssMode());
      editor.getSession().on("change", function(){
          console.log("change", editor.getSession().getValue());
          console.log(ta);
        ta[0].innerHTML = editor.getSession().getValue();
      });
    });
    </script>
    <style type="text/css">
    #extraCssAce {
        position: relative;
        width: 700px;
        height: 400px;
    }
    </style>
    
</fieldset>