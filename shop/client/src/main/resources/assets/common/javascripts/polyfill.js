// Array forEach from MDN
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/forEach#Compatibility
if (!Array.prototype.forEach) {
    Array.prototype.forEach = function (fn, scope) {
        'use strict';
        var i, len;
        for (i = 0, len = this.length; i < len; ++i) {
            if (i in this) {
                fn.call(scope, this[i], i, this);
            }
        }
    };
}

// Array reduce for old browsers
if ('function' !== typeof Array.prototype.reduce) {
    Array.prototype.reduce = function(callback, opt_initialValue){
        'use strict';
        if (null === this || 'undefined' === typeof this) {
            // At the moment all modern browsers, that support strict mode, have
            // native implementation of Array.prototype.reduce. For instance, IE8
            // does not support strict mode, so this check is actually useless.
            throw new TypeError(
                'Array.prototype.reduce called on null or undefined');
        }
        if ('function' !== typeof callback) {
            throw new TypeError(callback + ' is not a function');
        }
        var index = 0, length = this.length >>> 0, value, isValueSet = false;
        if (1 < arguments.length) {
            value = opt_initialValue;
            isValueSet = true;
        }
        for ( ; length > index; ++index) {
            if (!this.hasOwnProperty(index)) continue;
            if (isValueSet) {
                value = callback(value, this[index], index, this);
            } else {
                value = this[index];
                isValueSet = true;
            }
        }
        if (!isValueSet) {
            throw new TypeError('Reduce of empty array with no initial value');
        }
        return value;
    };
}

// Array filter from MDN
if (!Array.prototype.filter) {
    Array.prototype.filter = function (fn, context) {
        var i,
            value,
            result = [],
            length;

        if (!this || typeof fn !== 'function' || (fn instanceof RegExp)) {
            throw new TypeError();
        }

        length = this.length;

        for (i = 0; i < length; i++) {
            if (this.hasOwnProperty(i)) {
                value = this[i];
                if (fn.call(context, value, i, this)) {
                    result.push(value);
                }
            }
        }
        return result;
    };
}
// Object.keys
if (!Object.keys) {
    Object.keys = (function () {
        'use strict';
        var hasOwnProperty = Object.prototype.hasOwnProperty,
        hasDontEnumBug = !({toString: null}).propertyIsEnumerable('toString'),
        dontEnums = [
            'toString',
            'toLocaleString',
            'valueOf',
            'hasOwnProperty',
            'isPrototypeOf',
            'propertyIsEnumerable',
            'constructor'
        ],
        dontEnumsLength = dontEnums.length;

        return function (obj) {
            if (typeof obj !== 'object' && typeof obj !== 'function' || obj === null) throw new TypeError('Object.keys called on non-object');

            var result = [];

            for (var prop in obj) {
                if (hasOwnProperty.call(obj, prop)) result.push(prop);
            }

            if (hasDontEnumBug) {
                for (var i=0; i < dontEnumsLength; i++) {
                    if (hasOwnProperty.call(obj, dontEnums[i])) result.push(dontEnums[i]);
                }
            }
            return result;
        };
    })();
}

// Array#map prototype for browser environments that don't support it.
// Taken from https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map
// Production steps of ECMA-262, Edition 5, 15.4.4.19
// Reference: http://es5.github.com/#x15.4.4.19
if (!Array.prototype.map) {
    Array.prototype.map = function(callback, thisArg) {

        var T, A, k;

        if (this == null) {
            throw new TypeError(" this is null or not defined");
        }

        // 1. Let O be the result of calling ToObject passing the |this| value as the argument.
        var O = Object(this);

        // 2. Let lenValue be the result of calling the Get internal method of O with the argument "length".
        // 3. Let len be ToUint32(lenValue).
        var len = O.length >>> 0;

        // 4. If IsCallable(callback) is false, throw a TypeError exception.
        // See: http://es5.github.com/#x9.11
        if (typeof callback !== "function") {
            throw new TypeError(callback + " is not a function");
        }

        // 5. If thisArg was supplied, let T be thisArg; else let T be undefined.
        if (thisArg) {
            T = thisArg;
        }

        // 6. Let A be a new array created as if by the expression new Array(len) where Array is
        // the standard built-in constructor with that name and len is the value of len.
        A = new Array(len);

        // 7. Let k be 0
        k = 0;

        // 8. Repeat, while k < len
        while(k < len) {

            var kValue, mappedValue;

            // a. Let Pk be ToString(k).
            //   This is implicit for LHS operands of the in operator
            // b. Let kPresent be the result of calling the HasProperty internal method of O with argument Pk.
            //   This step can be combined with c
            // c. If kPresent is true, then
            if (k in O) {

                // i. Let kValue be the result of calling the Get internal method of O with argument Pk.
                kValue = O[ k ];

                // ii. Let mappedValue be the result of calling the Call internal method of callback
                // with T as the this value and argument list containing kValue, k, and O.
                mappedValue = callback.call(T, kValue, k, O);

                // iii. Call the DefineOwnProperty internal method of A with arguments
                // Pk, Property Descriptor {Value: mappedValue, : true, Enumerable: true, Configurable: true},
                // and false.

                // In browsers that support Object.defineProperty, use the following:
                // Object.defineProperty(A, Pk, { value: mappedValue, writable: true, enumerable: true, configurable: true });

                // For best browser support, use the following:
                A[ k ] = mappedValue;
            }
            // d. Increase k by 1.
            k++;
        }

        // 9. return A
        return A;
    };
}

// Array#find and Array#findIndex polyfill (ECMAScript 6 draft spec)
// Credits: https://gist.github.com/dcherman/5167353
(function() {
    function polyfill( fnName ) {
        if ( !Array.prototype[fnName] ) {
            Array.prototype[fnName] = function( predicate /*, thisArg */ ) {
                var i, len, test, thisArg = arguments[ 1 ];
                if ( typeof predicate !== "function" ) {
                    throw new TypeError();
                }
                test = !thisArg ? predicate : function() {
                    return predicate.apply( thisArg, arguments );
                };
                for( i = 0, len = this.length; i < len; i++ ) {
                    if ( test(this[i], i, this) === true ) {
                        return fnName === "find" ? this[ i ] : i;
                    }
                }
                if ( fnName !== "find" ) {
                    return -1;
                }
            };
        }
    }
    for( var i in { find: 1, findIndex: 1 }) {
        polyfill( i );
    }
}());

/*
 * Copyright 2012 The Polymer Authors. All rights reserved.
 * Use of this source code is goverened by a BSD-style
 * license that can be found in the LICENSE file.
 */

// SideTable is a weak map where possible. If WeakMap is not available the
// association is stored as an expando property.
var SideTable;
// TODO(arv): WeakMap does not allow for Node etc to be keys in Firefox
if (typeof WeakMap !== 'undefined' && navigator.userAgent.indexOf('Firefox/') < 0) {
    SideTable = WeakMap;
} else {
    (function() {
        var defineProperty = Object.defineProperty;
        var hasOwnProperty = Object.hasOwnProperty;
        var counter = new Date().getTime() % 1e9;

        SideTable = function() {
            this.name = '__st' + (Math.random() * 1e9 >>> 0) + (counter++ + '__');
        };

        SideTable.prototype = {
            set: function(key, value) {
                defineProperty(key, this.name, {value: value, writable: true});
            },
            get: function(key) {
                return hasOwnProperty.call(key, this.name) ? key[this.name] : undefined;
            },
            delete: function(key) {
                this.set(key, undefined);
            }
        }
    })();
}
/**
 * Javascript MutationObserver for browsers that don't support it.
 *
 * See https://github.com/Polymer/MutationObservers
 */
/*
 * Copyright 2012 The Polymer Authors. All rights reserved.
 * Use of this source code is goverened by a BSD-style
 * license that can be found in the LICENSE file.
 */
(function(global) {

    var registrationsTable = new SideTable();

    // We use setImmediate or postMessage for our future callback.
    var setImmediate = window.msSetImmediate;

    // Use post message to emulate setImmediate.
    if (!setImmediate) {
        var setImmediateQueue = [];
        var sentinel = String(Math.random());
        window.addEventListener('message', function(e) {
            if (e.data === sentinel) {
                var queue = setImmediateQueue;
                setImmediateQueue = [];
                queue.forEach(function(func) {
                    func();
                });
            }
        });
        setImmediate = function(func) {
            setImmediateQueue.push(func);
            window.postMessage(sentinel, '*');
        };
    }

    // This is used to ensure that we never schedule 2 callas to setImmediate
    var isScheduled = false;

    // Keep track of observers that needs to be notified next time.
    var scheduledObservers = [];

    /**
     * Schedules |dispatchCallback| to be called in the future.
     * @param {MutationObserver} observer
     */
    function scheduleCallback(observer) {
        scheduledObservers.push(observer);
        if (!isScheduled) {
            isScheduled = true;
            setImmediate(dispatchCallbacks);
        }
    }

    function wrapIfNeeded(node) {
        return window.ShadowDOMPolyfill &&
            window.ShadowDOMPolyfill.wrapIfNeeded(node) ||
            node;
    }

    function dispatchCallbacks() {
        // http://dom.spec.whatwg.org/#mutation-observers

        isScheduled = false; // Used to allow a new setImmediate call above.

        var observers = scheduledObservers;
        scheduledObservers = [];
        // Sort observers based on their creation UID (incremental).
        observers.sort(function(o1, o2) {
            return o1.uid_ - o2.uid_;
        });

        var anyNonEmpty = false;
        observers.forEach(function(observer) {

            // 2.1, 2.2
            var queue = observer.takeRecords();
            // 2.3. Remove all transient registered observers whose observer is mo.
            removeTransientObserversFor(observer);

            // 2.4
            if (queue.length) {
                observer.callback_(queue, observer);
                anyNonEmpty = true;
            }
        });

        // 3.
        if (anyNonEmpty)
            dispatchCallbacks();
    }

    function removeTransientObserversFor(observer) {
        observer.nodes_.forEach(function(node) {
            var registrations = registrationsTable.get(node);
            if (!registrations)
                return;
            registrations.forEach(function(registration) {
                if (registration.observer === observer)
                    registration.removeTransientObservers();
            });
        });
    }

    /**
     * This function is used for the "For each registered observer observer (with
     * observer's options as options) in target's list of registered observers,
     * run these substeps:" and the "For each ancestor ancestor of target, and for
     * each registered observer observer (with options options) in ancestor's list
     * of registered observers, run these substeps:" part of the algorithms. The
     * |options.subtree| is checked to ensure that the callback is called
     * correctly.
     *
     * @param {Node} target
     * @param {function(MutationObserverInit):MutationRecord} callback
     */
    function forEachAncestorAndObserverEnqueueRecord(target, callback) {
        for (var node = target; node; node = node.parentNode) {
            var registrations = registrationsTable.get(node);

            if (registrations) {
                for (var j = 0; j < registrations.length; j++) {
                    var registration = registrations[j];
                    var options = registration.options;

                    // Only target ignores subtree.
                    if (node !== target && !options.subtree)
                        continue;

                    var record = callback(options);
                    if (record)
                        registration.enqueue(record);
                }
            }
        }
    }

    var uidCounter = 0;

    /**
     * The class that maps to the DOM MutationObserver interface.
     * @param {Function} callback.
     * @constructor
     */
    function JsMutationObserver(callback) {
        this.callback_ = callback;
        this.nodes_ = [];
        this.records_ = [];
        this.uid_ = ++uidCounter;
    }

    JsMutationObserver.prototype = {
        observe: function(target, options) {
            target = wrapIfNeeded(target);

            // 1.1
            if (!options.childList && !options.attributes && !options.characterData ||

                // 1.2
                options.attributeOldValue && !options.attributes ||

                // 1.3
                options.attributeFilter && options.attributeFilter.length &&
                    !options.attributes ||

                // 1.4
                options.characterDataOldValue && !options.characterData) {

                throw new SyntaxError();
            }

            var registrations = registrationsTable.get(target);
            if (!registrations)
                registrationsTable.set(target, registrations = []);

            // 2
            // If target's list of registered observers already includes a registered
            // observer associated with the context object, replace that registered
            // observer's options with options.
            var registration;
            for (var i = 0; i < registrations.length; i++) {
                if (registrations[i].observer === this) {
                    registration = registrations[i];
                    registration.removeListeners();
                    registration.options = options;
                    break;
                }
            }

            // 3.
            // Otherwise, add a new registered observer to target's list of registered
            // observers with the context object as the observer and options as the
            // options, and add target to context object's list of nodes on which it
            // is registered.
            if (!registration) {
                registration = new Registration(this, target, options);
                registrations.push(registration);
                this.nodes_.push(target);
            }

            registration.addListeners();
        },

        disconnect: function() {
            this.nodes_.forEach(function(node) {
                var registrations = registrationsTable.get(node);
                for (var i = 0; i < registrations.length; i++) {
                    var registration = registrations[i];
                    if (registration.observer === this) {
                        registration.removeListeners();
                        registrations.splice(i, 1);
                        // Each node can only have one registered observer associated with
                        // this observer.
                        break;
                    }
                }
            }, this);
            this.records_ = [];
        },

        takeRecords: function() {
            var copyOfRecords = this.records_;
            this.records_ = [];
            return copyOfRecords;
        }
    };

    /**
     * @param {string} type
     * @param {Node} target
     * @constructor
     */
    function MutationRecord(type, target) {
        this.type = type;
        this.target = target;
        this.addedNodes = [];
        this.removedNodes = [];
        this.previousSibling = null;
        this.nextSibling = null;
        this.attributeName = null;
        this.attributeNamespace = null;
        this.oldValue = null;
    }

    function copyMutationRecord(original) {
        var record = new MutationRecord(original.type, original.target);
        record.addedNodes = original.addedNodes.slice();
        record.removedNodes = original.removedNodes.slice();
        record.previousSibling = original.previousSibling;
        record.nextSibling = original.nextSibling;
        record.attributeName = original.attributeName;
        record.attributeNamespace = original.attributeNamespace;
        record.oldValue = original.oldValue;
        return record;
    };

    // We keep track of the two (possibly one) records used in a single mutation.
    var currentRecord, recordWithOldValue;

    /**
     * Creates a record without |oldValue| and caches it as |currentRecord| for
     * later use.
     * @param {string} oldValue
     * @return {MutationRecord}
     */
    function getRecord(type, target) {
        return currentRecord = new MutationRecord(type, target);
    }

    /**
     * Gets or creates a record with |oldValue| based in the |currentRecord|
     * @param {string} oldValue
     * @return {MutationRecord}
     */
    function getRecordWithOldValue(oldValue) {
        if (recordWithOldValue)
            return recordWithOldValue;
        recordWithOldValue = copyMutationRecord(currentRecord);
        recordWithOldValue.oldValue = oldValue;
        return recordWithOldValue;
    }

    function clearRecords() {
        currentRecord = recordWithOldValue = undefined;
    }

    /**
     * @param {MutationRecord} record
     * @return {boolean} Whether the record represents a record from the current
     * mutation event.
     */
    function recordRepresentsCurrentMutation(record) {
        return record === recordWithOldValue || record === currentRecord;
    }

    /**
     * Selects which record, if any, to replace the last record in the queue.
     * This returns |null| if no record should be replaced.
     *
     * @param {MutationRecord} lastRecord
     * @param {MutationRecord} newRecord
     * @param {MutationRecord}
     */
    function selectRecord(lastRecord, newRecord) {
        if (lastRecord === newRecord)
            return lastRecord;

        // Check if the the record we are adding represents the same record. If
        // so, we keep the one with the oldValue in it.
        if (recordWithOldValue && recordRepresentsCurrentMutation(lastRecord))
            return recordWithOldValue;

        return null;
    }

    /**
     * Class used to represent a registered observer.
     * @param {MutationObserver} observer
     * @param {Node} target
     * @param {MutationObserverInit} options
     * @constructor
     */
    function Registration(observer, target, options) {
        this.observer = observer;
        this.target = target;
        this.options = options;
        this.transientObservedNodes = [];
    }

    Registration.prototype = {
        enqueue: function(record) {
            var records = this.observer.records_;
            var length = records.length;

            // There are cases where we replace the last record with the new record.
            // For example if the record represents the same mutation we need to use
            // the one with the oldValue. If we get same record (this can happen as we
            // walk up the tree) we ignore the new record.
            if (records.length > 0) {
                var lastRecord = records[length - 1];
                var recordToReplaceLast = selectRecord(lastRecord, record);
                if (recordToReplaceLast) {
                    records[length - 1] = recordToReplaceLast;
                    return;
                }
            } else {
                scheduleCallback(this.observer);
            }

            records[length] = record;
        },

        addListeners: function() {
            this.addListeners_(this.target);
        },

        addListeners_: function(node) {
            var options = this.options;
            if (options.attributes)
                node.addEventListener('DOMAttrModified', this, true);

            if (options.characterData)
                node.addEventListener('DOMCharacterDataModified', this, true);

            if (options.childList)
                node.addEventListener('DOMNodeInserted', this, true);

            if (options.childList || options.subtree)
                node.addEventListener('DOMNodeRemoved', this, true);
        },

        removeListeners: function() {
            this.removeListeners_(this.target);
        },

        removeListeners_: function(node) {
            var options = this.options;
            if (options.attributes)
                node.removeEventListener('DOMAttrModified', this, true);

            if (options.characterData)
                node.removeEventListener('DOMCharacterDataModified', this, true);

            if (options.childList)
                node.removeEventListener('DOMNodeInserted', this, true);

            if (options.childList || options.subtree)
                node.removeEventListener('DOMNodeRemoved', this, true);
        },

        /**
         * Adds a transient observer on node. The transient observer gets removed
         * next time we deliver the change records.
         * @param {Node} node
         */
        addTransientObserver: function(node) {
            // Don't add transient observers on the target itself. We already have all
            // the required listeners set up on the target.
            if (node === this.target)
                return;

            this.addListeners_(node);
            this.transientObservedNodes.push(node);
            var registrations = registrationsTable.get(node);
            if (!registrations)
                registrationsTable.set(node, registrations = []);

            // We know that registrations does not contain this because we already
            // checked if node === this.target.
            registrations.push(this);
        },

        removeTransientObservers: function() {
            var transientObservedNodes = this.transientObservedNodes;
            this.transientObservedNodes = [];

            transientObservedNodes.forEach(function(node) {
                // Transient observers are never added to the target.
                this.removeListeners_(node);

                var registrations = registrationsTable.get(node);
                for (var i = 0; i < registrations.length; i++) {
                    if (registrations[i] === this) {
                        registrations.splice(i, 1);
                        // Each node can only have one registered observer associated with
                        // this observer.
                        break;
                    }
                }
            }, this);
        },

        handleEvent: function(e) {
            // Stop propagation since we are managing the propagation manually.
            // This means that other mutation events on the page will not work
            // correctly but that is by design.
            e.stopImmediatePropagation();

            switch (e.type) {
                case 'DOMAttrModified':
                    // http://dom.spec.whatwg.org/#concept-mo-queue-attributes

                    var name = e.attrName;
                    var namespace = e.relatedNode.namespaceURI;
                    var target = e.target;

                    // 1.
                    var record = new getRecord('attributes', target);
                    record.attributeName = name;
                    record.attributeNamespace = namespace;

                    // 2.
                    var oldValue =
                        e.attrChange === MutationEvent.ADDITION ? null : e.prevValue;

                    forEachAncestorAndObserverEnqueueRecord(target, function(options) {
                        // 3.1, 4.2
                        if (!options.attributes)
                            return;

                        // 3.2, 4.3
                        if (options.attributeFilter && options.attributeFilter.length &&
                            options.attributeFilter.indexOf(name) === -1 &&
                            options.attributeFilter.indexOf(namespace) === -1) {
                            return;
                        }
                        // 3.3, 4.4
                        if (options.attributeOldValue)
                            return getRecordWithOldValue(oldValue);

                        // 3.4, 4.5
                        return record;
                    });

                    break;

                case 'DOMCharacterDataModified':
                    // http://dom.spec.whatwg.org/#concept-mo-queue-characterdata
                    var target = e.target;

                    // 1.
                    var record = getRecord('characterData', target);

                    // 2.
                    var oldValue = e.prevValue;


                    forEachAncestorAndObserverEnqueueRecord(target, function(options) {
                        // 3.1, 4.2
                        if (!options.characterData)
                            return;

                        // 3.2, 4.3
                        if (options.characterDataOldValue)
                            return getRecordWithOldValue(oldValue);

                        // 3.3, 4.4
                        return record;
                    });

                    break;

                case 'DOMNodeRemoved':
                    this.addTransientObserver(e.target);
                // Fall through.
                case 'DOMNodeInserted':
                    // http://dom.spec.whatwg.org/#concept-mo-queue-childlist
                    var target = e.relatedNode;
                    var changedNode = e.target;
                    var addedNodes, removedNodes;
                    if (e.type === 'DOMNodeInserted') {
                        addedNodes = [changedNode];
                        removedNodes = [];
                    } else {

                        addedNodes = [];
                        removedNodes = [changedNode];
                    }
                    var previousSibling = changedNode.previousSibling;
                    var nextSibling = changedNode.nextSibling;

                    // 1.
                    var record = getRecord('childList', target);
                    record.addedNodes = addedNodes;
                    record.removedNodes = removedNodes;
                    record.previousSibling = previousSibling;
                    record.nextSibling = nextSibling;

                    forEachAncestorAndObserverEnqueueRecord(target, function(options) {
                        // 2.1, 3.2
                        if (!options.childList)
                            return;

                        // 2.2, 3.3
                        return record;
                    });

            }

            clearRecords();
        }
    };

    global.JsMutationObserver = JsMutationObserver;

})(this);

(function(global) {

    // Export mutation observer (un-prefixed in FF).

    global.MutationObserver = global.MutationObserver || global.WebKitMutationObserver || global.JsMutationObserver;

})(this);