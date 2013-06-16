/**
 * @license angular.tree v0.3
 * (c) 2012 Cory Thomas http://github.com/dump247/angular.tree
 * License: MIT
 */

(function (angular, navigator) {
    'use strict';

    var multiSelectKey = 'ctrlKey';

    if (navigator.appVersion.indexOf("Mac") >= 0) {
        multiSelectKey = 'metaKey';
    }

    function getItemTemplate (document, treeElem) {
        var itemTemplate;

        while (treeElem.childNodes.length > 0) {
            var childNode = treeElem.childNodes[0];

            treeElem.removeChild(childNode);

            if (childNode.nodeName === 'LI') {
                if (itemTemplate) {
                    throw new Error('Tree ul must contain only a single li template.');
                }

                var createWrapper = childNode.childNodes.length === 0;
                var innerNodes = 0;

                for (var i = 0; i < childNode.childNodes.length; i += 1) {
                    var innerNode = childNode.childNodes[i];

                    if (innerNode.nodeName === '#text') {
                        if (! /^\s*$/.test(innerNode.nodeValue)) {
                            createWrapper = true;
                            break;
                        }
                    } else if (innerNodes > 0) {
                        createWrapper = true;
                        break;
                    } else {
                        innerNodes += 1;
                    }
                }

                if (createWrapper) {
                    var wrapperEl = document.createElement('DIV');

                    while (childNode.childNodes.length > 0) {
                        wrapperEl.appendChild(childNode.childNodes[0]);
                    }

                    childNode.appendChild(wrapperEl);
                }

                var ulEl = document.createElement('UL');
                ulEl.className = treeElem.className;
                childNode.appendChild(ulEl);

                itemTemplate = angular.element(childNode);
                itemTemplate.addClass('ng-tree-node');
            }
        }

        return itemTemplate || angular.element('<li class="ng-tree-node"><div>{{item}}</div><ul></ul></li>');
    }

    function findParentListItem (target) {
        var parentListItem = target;

        if (target && (target.nodeName !== 'LI' || ! target.className.match(/\bng-tree-node\b/))) {
            parentListItem = findParentListItem(target.parentNode);
        }

        return parentListItem ? angular.element(parentListItem) : null;
    }

    function descendNodes (listElem, callback) {
        angular.forEach(listElem.children('li'), function (itemElem) {
            var $itemElem = angular.element(itemElem);

            if (callback(listElem, $itemElem) !== false) {
                descendNodes($itemElem.children().eq(1), callback);
            }
        });
    }

    function initTree (treeElem, attributes, $compile, $document, $parse) {
        var itemTemplate = getItemTemplate($document[0], treeElem[0]);
        var treeModelExpr = attributes.src || attributes.ngTree;
        var eachIter = itemTemplate[0].getAttribute('each');
        var contextName = 'item';
        var collectionExpr = 'item.children';
        var selectExpr = itemTemplate.attr('select');
        var selectedExpr = itemTemplate[0].getAttribute('selected')||'$selected';

        if (eachIter) {
            var match = /^\s*(\w+)\s+in\s+(.*?)\s*$/.exec(eachIter);

            if (! match) {
                throw new Error('Invalid item iteration expression: "' + eachIter + '". The expression must follow the pattern "name in collection".');
            }

            contextName = match[1];
            collectionExpr = contextName + '.' + match[2];
        }

        treeElem.addClass('ng-tree');

        var tree = {
            multiple: 'multiple' in attributes,
            direct: 'direct' in attributes,
            rootElem: treeElem,
            treeModelExpr: treeModelExpr,
            itemTemplate: $compile(itemTemplate),
            contextName: contextName,
            collectionExpr: collectionExpr,

            collectionWatch: function (scope) { return scope.$eval(collectionExpr); },
            treeModelWatch: function (scope) { return scope.$eval(treeModelExpr); },

            getItem: function (scope) {
                return scope.$eval(this.contextName);
            },

            setItem: function (scope, value) {
                scope[this.contextName] = value;
            },

            trackSelection: !!selectExpr,

            selected: function (scope, value, evt) {
                if (this.trackSelection) {
                    this.selectedProperty(scope,value);

                    if (selectExpr) {
                        var f = $parse(selectExpr);
                        f(scope, {
                            '$event': evt
                        });
                    }
                }
            },

            selectedProperty: function(scope,value) {
                if (arguments.length > 1) {
                    $parse(selectedExpr).assign(scope,value);
                } else {
                    return $parse(selectedExpr)(scope);
                }
            }
        };

        if (tree.trackSelection) {
            treeElem.bind('click', function (evt) {
                var selectedItemElem = findParentListItem(evt.target);
                var selectedItemScope = selectedItemElem ? selectedItemElem.scope() : null;

                if ((evt[multiSelectKey] || tree.direct) && tree.multiple) {
                    if (selectedItemScope) {
                        selectedItemScope.$apply(function () {
                            tree.selected(selectedItemScope, ! tree.selectedProperty(selectedItemScope), evt);
                        });
                    }
                } else {
                    descendNodes(treeElem, function (listElem, itemElem) {
                        if (! selectedItemElem || itemElem[0] !== selectedItemElem[0]) {
                            var itemScope = itemElem.scope();

                            if (tree.selectedProperty(itemScope)) {
                                itemScope.$apply(function () {
                                    tree.selected(itemScope, false, evt);
                                });
                            }
                        }
                    });

                    if (selectedItemScope && ! tree.selectedProperty(selectedItemScope)) {
                        selectedItemScope.$apply(function () {
                            tree.selected(selectedItemScope, true, evt);
                        });
                    }
                }
            });
        }

        return tree;
    }

    function addListItem (scope, tree, listElem, item, index) {
        var itemScope = scope.$new();
        tree.setItem(itemScope, item);

        var itemElem = tree.itemTemplate(itemScope, angular.noop);

        if (tree.trackSelection) {
            tree.selectedProperty(itemScope,false);
        }

        insertListItem(listElem, itemElem, index);

        var childrenListElem = itemElem.children().eq(1);

        loadTree(itemScope, tree, childrenListElem, tree.collectionWatch);
    }

    function insertListItem (listElem, itemElem, index) {
        if (index < 0) {
            listElem.append(itemElem);
        } else if (index === 0) {
            listElem.prepend(itemElem);
        } else {
            listElem.children().eq(index - 1).after(itemElem);
        }
    }

    function loadTree (scope, tree, listElem, listWatch) {
        scope.$watch(listWatch, function (newList, oldList) {
            if (typeof newList === 'undefined' || newList === null || newList.length === 0) {
                listElem.children().remove();
                return;
            }

            angular.forEach(newList, function (item, itemIndex) {
                var listChildElems = listElem.children();

                if (itemIndex >= listChildElems.length) {
                    addListItem(scope, tree, listElem, item, -1);
                    return;
                }

                for (var childElemIndex = itemIndex; childElemIndex < listChildElems.length; childElemIndex += 1) {
                    var childElem = angular.element(listChildElems[childElemIndex]);
                    var childItem = tree.getItem(childElem.scope());

                    if (childItem === item) {
                        break;
                    }
                }

                if (childElemIndex >= listChildElems.length) {
                    addListItem(scope, tree, listElem, item, itemIndex);
                } else if (childElemIndex !== itemIndex) {
                    insertListItem(listElem, listChildElems[childElemIndex], itemIndex);
                }
            });

            while (listElem.children().length > newList.length) {
                var removeElem = listElem.children().eq(newList.length);

                if (tree.selectedProperty(removeElem.scope())) {
                    tree.selected(removeElem.scope(), false);
                }

                removeElem.remove();
            }
        }, true);
    }

    angular.module('angularTree', []).
        directive('ngTree', ['$compile', '$document', '$parse', function ($compile, $document, $parse) {
            return {
                compile: function (elem, attrs) {
                    var tree = initTree(elem, attrs, $compile, $document, $parse);

                    return function (scope, elem, attrs) {
                        loadTree(scope, tree, elem, tree.treeModelWatch);
                    };
                }
            };
        }]);
})(angular, navigator);
