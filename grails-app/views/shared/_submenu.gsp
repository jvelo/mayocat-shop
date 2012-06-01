<ul class="nav-list nav navigation">
  <li class="nav-header"><g:message code="navigation.menu" default="Menu" /></li>
  <nav:eachItem group="submenu">
    <li class="${it.active ? 'active':''}"><a href=${it.link}>${it.title}</a></li>
  </nav:eachItem>
</ul>
