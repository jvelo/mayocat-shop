var MayocatShop = angular.module('MayocatShop', [
    'mayocat',
    'search',
    'money',
    'product',
    'collection',
    'catalog',
    'pages',
    'page',
    'articles',
    'article',
    'orders',
    'order',
    'jqui',
    '$strap.directives'
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
        when('/configuration/', {templateUrl: 'partials/configuration.html', controller: 'ConfigurationController', title: 'Settings'}).
        otherwise({redirectTo: '/'});
}]);

/**
 * WYSIWYG-augmented textarea directive with CKEditor.
 *
 * See http://stackoverflow.com/questions/11997246/bind-ckeditor-value-to-model-text-in-angularjs-and-rails
 * and http://stackoverflow.com/questions/15483579/angularjs-ckeditor-directive-sometimes-fails-to-load-data-from-a-service
 */
MayocatShop.directive('ckEditor', function () {
    return {
        require: '?ngModel',
        link: function (scope, elm, attr, ngModel) {
            var ck = CKEDITOR.replace(elm[0],
                {
                    toolbarGroups: [
                        { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
                        { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align' ] },
                        { name: 'links' },
                        { name: 'styles' }
                    ],
                    removePlugins: 'elementspath',
                    height: '290px',
                    width: '99%'
                }
            );

            if (!ngModel) return;

            //loaded didn't seem to work, but instanceReady did
            //I added this because sometimes $render would call setData before the ckeditor was ready
            ck.on('instanceReady', function () {
                ck.setData(ngModel.$viewValue);
            });

            ck.on('pasteState', function () {
                scope.$apply(function () {
                    ngModel.$setViewValue(ck.getData());
                });
            });

            ngModel.$render = function (value) {
                ck.setData(ngModel.$viewValue);
            };

        }
    };
});

/**
 * A switch button similar to the ios toggle switch
 */
MayocatShop.directive('switchButton', function () {
    return {
        require: 'ngModel',
        restrict: 'E',
        template: '<div class="btn-group">' +
            '<button class="btn on" ng-click="on()"></button>' +
            '<button class="btn off" ng-click="off()"></button>' +
            '</div>',
        link: function ($scope, element, attrs, controller) {
            $scope.on = function () {
                $(element).find(".btn.on").addClass("btn-primary");
                $(element).find(".btn.off").removeClass("btn-primary");
                controller.$setViewValue(true);
            }
            $scope.off = function () {
                $(element).find(".btn.off").addClass("btn-primary");
                $(element).find(".btn.on").removeClass("btn-primary");
                controller.$setViewValue(false);
            }
            controller.$render = function () {
                if (typeof controller.$viewValue !== 'undefined') {
                    $scope[controller.$viewValue ? "on" : "off"]();
                }
            };
        }
    }
});

/**
 * 'active-class' directive for <a> elements or <li> elements with a children <a>.
 *
 * Sets an active class when the current location path matches the path of the href attribute of the target <a>
 * (which is either the link element on which the active-class attribute has been set, or the first link element
 * found when descending nodes down a list element.
 *
 * Inspired by http://stackoverflow.com/a/12631074/1281372
 */
MayocatShop.directive('activeClass', ['$location', function (location) {
    return {
        restrict: ['A', 'LI'],
        link: function (scope, element, attrs, controller) {
            var clazz = attrs.activeClass,
                path = attrs.href || $(element).find("a[href]").attr('href'),
                otherHrefs = attrs.otherActiveHref || $(element).find("a[other-active-href]").attr('other-active-href'),
                allHrefs = [ path ];

            if (typeof otherHrefs != "undefined") {
                otherHrefs = otherHrefs.split(",");
                for (var i = 0; i < otherHrefs.length; i++) {
                    allHrefs.push(otherHrefs[i].trim());
                }
            }

            if (allHrefs.length > 0) {
                scope.location = location;
                scope.$watch('location.path()', function (newPath) {
                    for (var i = 0; i < allHrefs.length; i++) {
                        var path = allHrefs[i].substring(1); //hack because path does bot return including hashbang
                        if (newPath.indexOf(path) === 0) {
                            element.addClass(clazz);
                            return;
                        }
                    }
                    element.removeClass(clazz);
                });
            }
        }
    };
}]);

mayocat.controller('MenuController', ['$rootScope', '$scope', '$location',
    function ($rootScope, scope, location) {

        scope.isCatalog = false;
        scope.isPages = false;
        scope.isNews = false;

        scope.$watch('location.path()', function (path) {

            scope.isCatalog = false;
            scope.isPages = false;
            scope.isNews = false;

            angular.forEach(["/catalog", "/products/", "/collections/"], function (catalogPath) {
                if (path.indexOf(catalogPath) == 0) {
                    scope.isCatalog = true;
                }
            });
            angular.forEach(["/contents", "/pages/"], function (pagePage) {
                if (path.indexOf([pagePage]) == 0) {
                    scope.isPages = true;
                }
            });
            angular.forEach(["/news", "/articles/"], function (newsPage) {
                if (path.indexOf([newsPage]) == 0) {
                    scope.isNews = true;
                }
            });
        });
    }]);

/**
 * TODO: move this in the AppController
 */
MayocatShop.run(['$rootScope', '$http', '$location', 'configurationService',
    function (scope, $http, location, configurationService) {

    /**
     * Set up a default page title and update it when changing route.
     * Update title when changing root
     */
    scope.page_title = Mayocat.applicationName + ' | Home';
    scope.$on('$routeChangeSuccess', function (event, current) {
        scope.page_title = Mayocat.applicationName + ' | ' + current.$route.title;
    });

}]);
