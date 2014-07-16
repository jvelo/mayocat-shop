/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
var MayocatShop = angular.module('MayocatShop', [
    'mayocat',
    'settings',
    'search',
    'money',
    'shipping',
    'product',
    'collection',
    'catalog',
    'pages',
    'page',
    'articles',
    'article',
    'homePage',
    'dashboard',
    'orders',
    'order',
    '$strap.directives', // Used for the date picker bs-datepicker directive
                         // TODO rationalize on ui-bootstrap
    'ui.bootstrap',
    'ui.sortable',
]);

MayocatShop.config(['$routeProvider', function ($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'partials/dashboard.html',
            controller: 'DashboardController',
            titleTranslation: 'home'
        })
        .when('/contents', {
            templateUrl: 'partials/contents.html',
            titleTranslation: 'pages'
        })
        .when('/orders', {
            templateUrl: 'partials/orders.html',
            controller: 'OrdersController',
            titleTranslation: 'orders'
        })
        .when('/orders/:order', {
            templateUrl: 'partials/order.html',
            controller: 'OrderController',
            titleTranslation: 'orders'
        })
        .when('/customers', {
            templateUrl: 'partials/customers.html',
            titleTranslation: 'customers'
        })
        .when('/home', {
            templateUrl: 'partials/homePage.html',
            controller: 'HomePageController',
            titleTranslation: 'homepage'
        })
        .when('/news', {
            templateUrl: 'partials/news.html',
            titleTranslation: 'news'
        })
        .when('/pages/:page', {
            templateUrl: 'partials/page.html',
            controller: 'PageController',
            titleTranslation: 'pages'
        })
        .when('/news/:article', {
            templateUrl: 'partials/article.html',
            controller: 'ArticleController',
            titleTranslation: 'news'
        })
        .when('/catalog', {
            templateUrl: 'partials/products.html',
            titleTranslation: 'catalog'
        })
        .when('/collections/', {
            templateUrl: 'partials/collections.html',
            controller: 'CatalogController',
            titleTranslation: 'catalog'
        })
        .when('/products/:product', {
            templateUrl: 'partials/product.html',
            controller: 'ProductController',
            titleTranslation: 'products'
        })
        .when('/collections/:collection', {
            templateUrl: 'partials/collection.html',
            controller: 'CollectionController',
            titleTranslation: 'catalog'
        })
        .when('/settings/', {
            templateUrl: 'partials/settingsGeneral.html',
            controller: 'SettingsController',
            titleTranslation: 'settings'
        })
        .when('/settings/tenant', {
            templateUrl: 'partials/settingsTenant.html',
            controller: 'SettingsTenantController',
            titleTranslation: 'settings'
        })
        .when('/settings/catalog', {
            templateUrl: 'partials/settingsCatalog.html',
            controller: 'SettingsController',
            titleTranslation: 'settings'
        })
        .when('/settings/shipping', {
            templateUrl: 'partials/settingsShipping.html',
            controller: 'SettingsShippingController',
            titleTranslation: 'settings'
        })
        .otherwise({
            redirectTo: '/'
        });
}]);

mayocat.controller('MenuController', ['$rootScope', '$scope', '$location',
    function ($rootScope, scope, location) {

        scope.isCatalog = false;
        scope.isPages = false;
        scope.isNews = false;
        scope.isSettings = false;
        scope.isHomePage = false;

        scope.$watch('location.path()', function (path) {

            scope.isCatalog = false;
            scope.isPages = false;
            scope.isNews = false;
            scope.isSettings = false;

            angular.forEach(["/catalog", "/products/", "/collections/"], function (catalogPath) {
                if (path.indexOf(catalogPath) == 0) {
                    scope.isCatalog = true;
                    return;
                }
            });
            angular.forEach(["/contents", "/pages/"], function (pagePage) {
                if (path.indexOf([pagePage]) == 0) {
                    scope.isPages = true;
                    return;
                }
            });
            angular.forEach(["/news", "/articles/"], function (newsPage) {
                if (path.indexOf([newsPage]) == 0) {
                    scope.isNews = true;
                    return;
                }
            });
            angular.forEach(["/settings/"], function (configurationPath) {
                if (path.indexOf(configurationPath) == 0) {
                    scope.isSettings = true;
                    return;
                }
            });
            if (path.indexOf(["/home"]) == 0) {
                scope.isHomePage = true;
                return;
            }
        });
    }]);

/**
 * TODO: move this in the AppController
 */
MayocatShop.run(['$rootScope', '$route', '$translate', '$modal',
    function ($rootScope, $route, $translate, $modal) {

        /**
         * Set up a default page title and update it when changing route.
         * Update title when changing root
         */

        function updatePageTitle() {
            $rootScope.page_title = Mayocat.applicationName + ' | ' + $translate('routes.title.' + $route.current.titleTranslation);
        }

        $rootScope.page_title = Mayocat.applicationName + ' | Home';

        $rootScope.$on('$routeChangeSuccess', updatePageTitle);
        $rootScope.$on('$translateChangeEnd', updatePageTitle);

        $rootScope.$on('event:serverError', function () {
            $modal.open({ templateUrl: 'serverError.html' });
        });

    }]);