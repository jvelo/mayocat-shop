<html>
<head>
	<meta name='layout' content='main'/>
	<title><g:message code="springSecurity.login.title"/></title>
</head>

<body>
<div class="span4">&nbsp;</div>
<div id='login' class="span4">

  <g:if test='${flash.message}'>
    <div class='alert'>${flash.message}</div>
  </g:if>

  <form action='${postUrl}' method='POST' id='loginForm' class='well form-horizontal' autocomplete='off'>
    <legend>
      <h3><g:message code="admin.login" default="Login" /></h3>
    </legend>

    <div class="control-group">
      <label for='username' class="control-label"><g:message code="springSecurity.login.username.label"/>:</label>
      <div class="controls">
        <input type='text' class='text_' name='j_username' id='username'/>
      </div>
    </div>

    <div class="control-group">
      <label for='password' class="control-label"><g:message code="springSecurity.login.password.label"/>:</label>
      <div class="controls">
        <input type='password' class='text_' name='j_password' id='password'/>
      </div>
    </div>

    <div class="control-group" id="remember_me_holder">
      <div class="controls">
        <label for='remember_me' class="checkbox">
        <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
        <g:message code="springSecurity.login.remember.me.label"/>
        </label>
      </div>
    </div>

    <div class="centered">
      <input type='submit' id="submit" class="btn btn-primary" value='${message(code: "springSecurity.login.button")}'/>
    </div>
  </form>

</div>
<div class="span4">&nbsp;</div>
<script type='text/javascript'>
	<!--
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
	// -->
</script>
</body>
</html>
