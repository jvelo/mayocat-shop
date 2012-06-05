<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
    <r:require modules="jquery"/>
    <r:require modules="twitterBootstrap"/>
    <r:require modules="styles"/>
    <nav:resources override="true"/>
		<g:layoutHead/>
    <r:layoutResources />
	</head>
	<body>
    <g:render template="/shared/navbar" />
    <div class="container-fluid" id="admin-container">
      <div class="row-fluid">
        <div class="span2">
          <g:render template="/shared/catalogueMenu" />
          <g:pageProperty name="page.menu"/>
        </div>
        <div class="span7">
          <g:layoutBody/>
        </div>
        <div class="span3">
          <div class="page-panel">
            <g:pageProperty name="page.panel"/>
          </div>
        </div>
      </div>
    </div>
		<div class="footer" role="contentinfo"></div>
    <r:layoutResources />
	</body>
</html>
