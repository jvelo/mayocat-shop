<sec:ifLoggedIn>
  <span class="user">
    <i class="icon-user icon-white"></i>
    <sec:loggedInUserInfo field="username"/>
    (<g:link controller="logout">logout</g:link>)
  </span>
</sec:ifLoggedIn>
