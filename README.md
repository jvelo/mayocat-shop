![Mayocat Logo](http://i.imgur.com/2TxYItk.png "Say Hello To The Mayo Cat")

Mayocat Shop is an attempt at building a modern e-commerce and marketplace management platform on the JVM.

Roadmap
-------

While young, Mayocat Shop is under active development. An initial end-to-end 1.0 version is planned for the end of April 2013.

The road to there comprise several milestones :

- M0 Basic infrastructure (Storage, multitenancy, authentication, configurability, etc.) (done)
- M1 Full catalog management with collections and variants (in progress)
- M2 View system (handlebars)
- M3 Payment gateway interface & orders
- M4 Pages (light CMS)

Architecture
------------

The high level philosophy behind Mayocat Shop architecture is described in [this article](http://velociter.fr/journal/my-idea-of-a-modern-web-app-on-the-jvm). Specifically, Mayocat Shop is built on a lightweight stack and spirit :

- Its foundation support library is the fantastic [Dropwizard framework](http://dropwizard.codahale.com/), so this mean Jetty + JAX-RS/Jersey + Jackson
- Dependency injection is realized using the [XWiki Component Manager](http://extensions.xwiki.org/xwiki/bin/view/Extension/Component+Module).
- The default persistency layer implementation targets RDBMS, using [JDBI](http://jdbi.org/). Right now only MySQL is tested, although their is a plan to switch to Postgres as the officialy supported DB.

One corollary of building on this stack is the fact the back-end service can be viewed as just a RESTful HTTP API. The user-facing back-office consumes this API and is built with [AngularJS](http://angularjs.org).

---

Copyright 2012-2013 Jérôme Velociter and contributors
