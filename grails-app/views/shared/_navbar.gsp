<sec:ifLoggedIn>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">

				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="${createLink('controller':'admin')}">Administration</a>

				<div class="nav-collapse">
					<ul class="nav">
						<nav:eachItem group="main">
							<li class="${it.active ? 'active':''}"><a href=${it.link}>
									${it.title}
							</a></li>
						</nav:eachItem>
					</ul>
					<!-- Here will be the search form -->
					<ul class="nav pull-right">
						<g:render template="/shared/navbarUserStatus" />
					</ul>
				</div>
			</div>
		</div>
	</div>
</sec:ifLoggedIn>