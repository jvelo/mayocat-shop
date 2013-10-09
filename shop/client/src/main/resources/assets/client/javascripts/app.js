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
    'orders',
    'order',
    '$strap.directives', // Used for the date picker bs-datepicker directive
                         // TODO rationalize on ui-bootstrap ?
    'ui.sortable',
    'ui.bootstrap.tabs',
    'pascalprecht.translate'
]);

MayocatShop.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: 'partials/home.html', controller: 'HomeCtrl', title: 'Home'}).
        when('/contents', {templateUrl: 'partials/contents.html', title: 'Contents'}).
        when('/orders', {templateUrl: 'partials/orders.html', controller: 'OrdersController', title: 'Orders'}).
        when('/orders/:order', {templateUrl: 'partials/order.html', controller: 'OrderController', title: 'Orders'}).
        when('/customers', {templateUrl: 'partials/customers.html', title: 'Customers'}).
        when('/news', {templateUrl: 'partials/news.html', title: 'News'}).
        when('/pages/:page', {templateUrl: 'partials/page.html', controller: 'PageController', title: 'Pages'}).
        when('/news/:article', {templateUrl: 'partials/article.html', controller: 'ArticleController', title: 'News'}).
        when('/catalog', {templateUrl: 'partials/products.html', title: 'Catalog'}).
        when('/collections/', {templateUrl: 'partials/collections.html', controller: 'CatalogController', title: 'Catalog'}).
        when('/products/:product', {templateUrl: 'partials/product.html', controller: 'ProductController', title: 'Products'}).
        when('/collections/:collection', {templateUrl: 'partials/collection.html', controller: 'CollectionController', title: 'Catalog'}).
        when('/settings/', {templateUrl: 'partials/settingsGeneral.html', controller: 'SettingsController', title: 'Settings'}).
        when('/settings/tenant', {templateUrl: 'partials/settingsTenant.html', controller: 'SettingsTenantController', title: 'Settings'}).
        when('/settings/catalog', {templateUrl: 'partials/settingsCatalog.html', controller: 'SettingsController', title: 'Settings'}).
        when('/settings/shipping', {templateUrl: 'partials/settingsShipping.html', controller: 'SettingsShippingController', title: 'Settings'}).
        otherwise({redirectTo: '/'});
}]);

mayocat.controller('MenuController', ['$rootScope', '$scope', '$location',
    function ($rootScope, scope, location) {

        scope.isCatalog = false;
        scope.isPages = false;
        scope.isNews = false;
        scope.isSettings = false;

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
        });
    }]);

/**
 * TODO: move this in the AppController
 */
MayocatShop.run(['$rootScope',
    function (scope) {

        /**
         * Set up a default page title and update it when changing route.
         * Update title when changing root
         */
        scope.page_title = Mayocat.applicationName + ' | Home';
        scope.$on('$routeChangeSuccess', function (event, current) {
            scope.page_title = Mayocat.applicationName + ' | ' + current.$$route.title;
        });

    }]);
