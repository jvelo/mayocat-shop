<sec:ifLoggedIn>
<li class="dropdown">
  <a href="#" class="dropdown-toggle" data-toggle="dropdown">
    <i class="icon-user icon-white"></i>
    <sec:loggedInUserInfo field="username"/>
  </a>
  <ul class="dropdown-menu">
    <li>
      <g:link controller="logout">Logout</g:link>
    </li>
  </ul>
</li>
</sec:ifLoggedIn>
