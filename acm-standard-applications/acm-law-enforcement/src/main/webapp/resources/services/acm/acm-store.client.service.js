'use strict';

/**
 * @ngdoc service
 * @name services:Acm.StoreService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/acm-store.client.service.js services/acm/acm-store.client.service.js}
 *
 * This service package contains objects and functions for data storage
 */
angular.module('services').factory('Acm.StoreService', ['$rootScope', '$window', 'UtilService', 'Util.TimerService'
    , function ($rootScope, $window, Util, UtilTimerService
    ) {
        var Store = {
            _owner: null
            /**
             * @ngdoc method
             * @name getOwner
             * @methodOf Acm.StoreService
             *
             * @description
             * Get owner associated with Store
             *
             * @returns {String} owner user ID
             */
            , getOwner: function() {
                return this._owner;
            }
            /**
             * @ngdoc method
             * @name setOwner
             * @methodOf Acm.StoreService
             *
             * @param {String} owner Current login ID.
             *
             * @description
             * Get owner associated with Store
             */
            , setOwner: function(owner) {
                this._owner = owner;
            }
            /**
             * @ngdoc method
             * @name prefixOwner
             * @methodOf Acm.StoreService
             *
             * @param {String} name Name of data store (cache)
             *
             * @description
             * Prefix owner before the data store name, with ":" as separator
             *
             * @returns {String} name with owner prefix
             */
            , prefixOwner: function(name) {
                var owner = this.getOwner();
                var prefixed = (owner)? owner + ":" : "";
                prefixed += name;
                return prefixed;
            }
            /**
             * @ngdoc method
             * @name fixOwner
             * @methodOf Acm.StoreService
             *
             * @param {String} owner Current login ID.
             *
             * @description
             * This function is called after user ID is available. It associates Store with current user as owner.
             * And it fixed previous cache names in registries which are created before user ID is available or
             * left over from previous login.
             *
             * @returns {String} owner owner user ID
             */
            , fixOwner: function (owner) {
                if (owner != Store.getOwner()) {
                    Store.setOwner(owner);
                    UtilTimerService.useTimer("fixStoreOwner"
                        , 500     //delay 500 milliseconds
                        , function () {
                            Store.Registry.removeLocalOrphan(owner);
                            Store.Registry.removeSessionOrphan(owner);
                            return false;
                        }
                    );
                }
            }

            /**
             * @ngdoc service
             * @name Acm.StoreService.Registry
             *
             * @description
             *
             * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/acm-store.client.service.js services/acm/acm-store.client.service.js}
             *
             * Store registries maintain list of caches which need to be cleared when logout. There are two registries,
             * one for sessionStorage caches, and one for localStorage caches.
             */
            , Registry: {
                LocalCacheNames: {
                    LOCAL_REGISTRY: "AcmLocalRegistry"
                }
                , SessionCacheNames: {
                    SESSION_REGISTRY: "AcmSessionRegistry"
                }
                /**
                 * @ngdoc method
                 * @name getLocalInstance
                 * @methodOf Acm.StoreService.Registry
                 *
                 * @description
                 * Get an instance to localStorage cache registry. The registry itself is implemented using LocalData
                 *
                 * @returns {Object} LocalData instance
                 */
                , getLocalInstance: function() {
                    var instance = new Store.LocalData({name: Store.Registry.LocalCacheNames.LOCAL_REGISTRY
                        , noOwner: true
                        , noRegistry: true
                    });
                    return instance;
                }
                /**
                 * @ngdoc method
                 * @name getSessionInstance
                 * @methodOf Acm.StoreService.Registry
                 *
                 * @description
                 * Get an instance to sessionStorage cache registry. The registry itself is implemented using SessionData
                 *
                 * @returns {Object} SessionData instance
                 */
                , getSessionInstance: function() {
                    var instance = new Store.SessionData({name: Store.Registry.SessionCacheNames.SESSION_REGISTRY
                        , noOwner: true
                        , noRegistry: true
                    });
                    return instance;
                }
                , _getOwnerFromKey: function(key) {
                    var ar = key.split(":");
                    if (2 <= ar.length) {
                        return ar[0];
                    }
                    return null;
                }
                /**
                 * @ngdoc method
                 * @name removeSessionOrphan
                 * @methodOf Acm.StoreService.Registry
                 *
                 * @param {String} (Optional)loginId Current login ID.
                 *
                 * @description
                 * This function performs two tasks:
                 * 1. Caches without owner are those created before login user info is available. They have current login user
                 * as owner after this call.
                 * 2. If loginId is given, caches with owners other than loginId, - presumably they are
                 * left over from previous login -, are removed.
                 */
                , removeSessionOrphan: function(loginId) {
                    var registry = this.getSessionInstance();
                    var data = registry.get();
                    _.forEach(data, function(item, key) {
                        var owner = Store.Registry._getOwnerFromKey(key);
                        if (Util.isEmpty(owner)) {
                            var orphanCopy = new Store.SessionData({name: key, noOwner: true, noRegistry: true});
                            var ownerCopy = new Store.SessionData({name: key, noOwner: false, noRegistry: true});
                            var orphanCopyData = orphanCopy.get();
                            var ownerCopyData = ownerCopy.get();
                            if (Util.isEmpty(ownerCopyData)) {
                                ownerCopy.set(orphanCopyData);
                            }
                            orphanCopy.remove();
                            delete data[key];
                            data[ownerCopy.getName()] = 1;

                        } else if (owner != loginId) {
                            var cache = new Store.SessionData({name: key, noOwner: true, noRegistry: true});
                            cache.remove();
                            delete data[key];
                        }
                    });
                    registry.set(data);
                }
                /**
                 * @ngdoc method
                 * @name removeLocalOrphan
                 * @methodOf Acm.StoreService.Registry
                 *
                 * @param {String} (Optional)loginId Current login ID.
                 *
                 * @description
                 * This function performs two tasks:
                 * 1. Caches without owner are those created before login user info is available. They have current login user
                 * as owner after this call.
                 * 2. If loginId is given, caches with owners other than loginId, - presumably they are
                 * left over from previous login -, are removed.
                 */
                , removeLocalOrphan: function(loginId) {
                    var registry = this.getLocalInstance();
                    var data = registry.get();
                    _.forEach(data, function(item, key) {
                        var owner = Store.Registry._getOwnerFromKey(key);
                        if (Util.isEmpty(owner)) {
                            var orphanCopy = new Store.LocalData({name: key, noOwner: true, noRegistry: true});
                            var ownerCopy = new Store.LocalData({name: key, noOwner: false, noRegistry: true});
                            var orphanCopyData = orphanCopy.get();
                            var ownerCopyData = ownerCopy.get();
                            if (Util.isEmpty(ownerCopyData)) {
                                ownerCopy.set(orphanCopyData);
                            }
                            orphanCopy.remove();
                            delete data[key];
                            data[ownerCopy.getName()] = 1;

                        } else if (owner != loginId) {
                            var cache = new Store.LocalData({name: key, noOwner: true, noRegistry: true});
                            cache.remove();
                            delete data[key];
                        }
                    });
                    registry.set(data);
                }
                /**
                 * @ngdoc method
                 * @name clearSessionCache
                 * @methodOf Acm.StoreService.Registry
                 *
                 * @description
                 * Clear caches that are registered with the sessionStorage registry
                 */
                , clearSessionCache: function() {
                    var registry = this.getSessionInstance();
                    var data = registry.get();
                    _.forEach(data, function(item, key) {
                        var cache = new Store.SessionData({name: key, noOwner: true, noRegistry: true});
                        cache.remove();
                    });
                    registry.remove();
                }
                /**
                 * @ngdoc method
                 * @name clearLocalCache
                 * @methodOf Acm.StoreService.Registry
                 *
                 * @description
                 * Clear caches that are registered with the localStorage registry
                 */
                , clearLocalCache: function() {
                    var registry = this.getLocalInstance();
                    var data = registry.get();
                    _.forEach(data, function(item, key) {
                        var cache = new Store.LocalData({name: key, noOwner: true, noRegistry: true});
                        cache.remove();
                    });
                    registry.remove();
                }
            }



            /**
             * @ngdoc service
             * @name Acm.StoreService.Variable
             *
             * @description
             *
             * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/acm-store.client.service.js services/acm/acm-store.client.service.js}
             *
             * Variable represents map like data structure. Data are saved in Angular $rootScope.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf Acm.StoreService.Variable
             *
             * @param {String} name Name
             * @param {Object} initValue Initial value
             *
             * @description
             * Create a reference object to a Variable.
             *
             * Example:
             *
             * var v = new Variable("MyData");
             *
             * var v2 = new Variable("MyData", "first");    //initialize value to "first"
             */
            , Variable: function (name, initValue) {
                this.name = name;
                $rootScope._storeVariableMap = $rootScope._storeVariableMap || {};
                if (undefined != initValue) {
                    $rootScope._storeVariableMap[name] = initValue;
                }
            }

            /**
             * @ngdoc service
             * @name Acm.StoreService.SessionData
             *
             * @description
             *
             * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/acm-store.client.service.js services/acm/acm-store.client.service.js}
             *
             * SessionData represent data saved in session. Each data are identified by a name. It persists through the entire login session.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf Acm.StoreService.SessionData
             *
             * @description
             * Create a reference object to a sessionStorage. Unless specified otherwise, current login ID is prefixed
             * to the name to represent as owner and it is added to the session registry by default.
             *
             * @param {Object} arg Arguments. It can be overloaded as a String. In this case, it represent a name to identify a sessionStorage data.
             * @param {String} arg.name A name to identify a sessionStorage data
             * @param {boolean} (Optional)arg.noOwner If true, do not prefix owner to the name. Default is false
             * @param {boolean} (Optional)arg.noRegistry If true, do not register this SessionData to registry. Default is false
             *
             * Example:
             *
             * var sd1 = new SessionData({name: "MyData"}); //actual storage name will be xxx:MyData, xxx is current login ID
             * var sd2 = new SessionData("MyData");         //same result as sd1
             * var sd3 = new SessionData({name: "MyData"}, noOwner: true); //do not prefix loginID as owner
             * var sd4 = new SessionData({name: "MyData"}, noRegister: true); //do not register, so it is not deleted automatically when logout
             *
             */
            , SessionData: function (arg) {
                if ("string" == typeof arg) {
                    arg = {name: arg};
                    arg.noOwner = false;
                    arg.noRegistry = false;
                }

                this.noOwner = Util.goodValue(arg.noOwner, false);
                this.name = (this.noOwner)? arg.name : Store.prefixOwner(arg.name);
                this.noRegistry = Util.goodValue(arg.noRegistry, false);
                if (!this.noRegistry) {
                    var registry = Store.Registry.getSessionInstance();
                    var data = Util.goodValue(registry.get(), {});
                    data[this.name] = 1;
                    registry.set(data);
                }
            }


            /**
             * @ngdoc service
             * @name Acm.StoreService.LocalData
             *
             * @description
             *
             * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/acm-store.client.service.js services/acm/acm-store.client.service.js}
             *
             * LocalData represent data saved in local storage. Each data are identified by a name. It persists on user computer until deleted.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf Acm.StoreService.LocalData
             *
             * @description
             * Create a reference object to a localStorage. Unless specified otherwise, current login ID is prefixed
             * to the name to represent as owner and it is added to the local registry by default.
             *
             * @param {Object} arg Arguments. It can be overloaded as a String. In this case, it represent a name to identify a localStorage data.
             * @param {String} arg.name A name to identify a localStorage data
             * @param {boolean} (Optional)arg.noOwner If true, do not prefix owner to the name. Default is false
             * @param {boolean} (Optional)arg.noRegistry If true, do not register this LocalData to registry. Default is false
             *
             * Example:
             *
             * var sd1 = new LocalData({name: "MyData"}); //actual storage name will be xxx:MyData, xxx is current login ID
             * var sd2 = new LocalData("MyData");         //same result as sd1
             * var sd3 = new LocalData({name: "MyData"}, noOwner: true); //do not prefix loginID as owner
             * var sd4 = new LocalData({name: "MyData"}, noRegister: true); //do not register, so it is not deleted automatically when logout
             *
             */
            , LocalData: function (arg) {
                if ("string" == typeof arg) {
                    arg = {name: arg};
                    arg.noOwner = false;
                    arg.noRegistry = false;
                }
                this.noOwner = Util.goodValue(arg.noOwner, false);
                this.name = (this.noOwner)? arg.name : Store.prefixOwner(arg.name);
                this.noRegistry = Util.goodValue(arg.noRegistry, false);
                if (!this.noRegistry) {
                    var registry = Store.Registry.getLocalInstance();
                    var data = Util.goodValue(registry.get(), {});
                    data[this.name] = 1;
                    registry.set(data);
                }
            }


            /**
             * @ngdoc service
             * @name Acm.StoreService.CacheFifo
             *
             * @description
             *
             * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/acm-store.client.service.js services/acm/acm-store.client.service.js}
             *
             * CacheFifo is cache using first in first out aging algorithm. Each cache is identified by a name.
             * Cache data persists in Angular $rootScope. Data is evicted after timeToLive limit.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} arg Argument. It can be an object or a name string. If a string, it is equivalent to {name: arg}
             * @param {String} arg.name (Optional)Name. If not provided, a random name is generated for use
             * @param {Number} arg.maxSize (Optional)Max size. If not provided, default size is 8
             * @param {Number} arg.timeToLive (Optional)Cache item time to live in milliseconds.
             *        If not provided, default value is 7200000 (2 hours); if -1, cache items live forever
             *
             * @description
             * Create a reference object to a CacheFifo.
             *
             * Example:
             *
             * var myCache = new CacheFifo("MyCache");
             * var myCache2 = new CacheFifo({name: "MyCache2", maxSize: 16});
             */
            , CacheFifo: function (arg) {
                if ("string" == typeof arg) {
                    arg = {name: arg};
                }
                this.name = Util.goodMapValue(arg, "name", "Cache" + Math.floor((Math.random() * 1000000000)));

                $rootScope._storeCacheMap = $rootScope._storeCacheMap || {};
                if (!$rootScope._storeCacheMap[this.name]) {
                    $rootScope._storeCacheMap[this.name] = {};

                    var thisCache = this._getThis();
                    thisCache.name = this.name;
                    thisCache.maxSize = Util.goodMapValue(arg, "maxSize", this.DEFAULT_MAX_CACHE_SIZE);
                    thisCache.timeToLive = Util.goodMapValue(arg, "timeToLive", this.DEFAULT_SHELF_LIFE);   //arg.timeToLive in milliseconds; -1 if live forever

                    this.reset();
                    this._evict(thisCache.name, thisCache.timeToLive);
                }
            }
        };
        Store.Variable.prototype = {
            /**
             * @ngdoc method
             * @name get
             * @methodOf Acm.StoreService.Variable
             *
             * @description
             * Get value of a Variable.
             *
             * Example:
             *
             * var dataVar = new Variable("MyData", "first");    //initialize value to "first"
             *
             * var data = dataVar.get();                         // returns "first"
             */
            get: function () {
                return $rootScope._storeVariableMap[this.name];
            }

            /**
             * @ngdoc method
             * @name set
             * @methodOf Acm.StoreService.Variable
             *
             * @param {Object} value Value to set
             *
             * @description
             * Set value of a Variable.
             *
             * Example:
             *
             * var dataVar = new Variable("MyData", "first");    //initialize value to "first"
             *
             * dataVar.set("last");                              // now it contains value "last"
             */
            , set: function (value) {
                $rootScope._storeVariableMap[this.name] = value;
            }
        };

        Store.SessionData.prototype = {
            /**
             * @ngdoc method
             * @name getName
             * @methodOf Acm.StoreService.SessionData
             *
             * @description
             * Get name of a SessionData reference object.
             */
            getName: function () {
                return this.name;
            }

            /**
             * @ngdoc method
             * @name get
             * @methodOf Acm.StoreService.SessionData
             *
             * @description
             * Get value of a SessionData reference object.
             *
             * Example:
             *
             * var dataCache = new SessionData("MyData");
             *
             * dataCache.set('{greeting: "Hello", who: "World"}');
             * var data = dataCache.get();                              // data contains value '{greeting: "Hello", who: "World"}'
             */
            , get: function () {
                var data = sessionStorage.getItem(this.name);
                var item = Util.goodJsonObj(data, null);
                //var item = ("null" === data) ? null : JSON.parse(data);
                return item;
            }

            /**
             * @ngdoc method
             * @name set
             * @methodOf Acm.StoreService.SessionData
             *
             * @param {Object} data Value to set
             *
             * @description
             * Set value of a SessionData.
             *
             * Example:
             *
             * var dataCache = new SessionData("MyData");
             *
             * dataCache.set('{greeting: "Hello", who: "World"}');
             *
             * var data = dataCache.get();                              // data contains value '{greeting: "Hello", who: "World"}'
             */
            , set: function (data) {
                var item = (Util.isEmpty(data)) ? null : JSON.stringify(data);
                sessionStorage.setItem(this.name, item);
            }

            /**
             * @ngdoc method
             * @name remove
             * @methodOf Acm.StoreService.SessionData
             *
             * @description
             * remove SessionData.
             */
            , remove: function () {
                sessionStorage.removeItem(this.name);
            }
        };


        Store.LocalData.prototype = {
            /**
             * @ngdoc method
             * @name getName
             * @methodOf Acm.StoreService.LocalData
             *
             * @description
             * Get name of a v reference object.
             */
            getName: function () {
                return this.name;
            }

            /**
             * @ngdoc method
             * @name get
             * @methodOf Acm.StoreService.LocalData
             *
             * @description
             * Get value of a LocalData reference object.
             *
             * Example:
             *
             * var dataCache = new LocalData("MyData");
             *
             * dataCache.set('{greeting: "Hello", who: "World"}');
             *
             * var data = dataCache.get();                              // data contains value '{greeting: "Hello", who: "World"}'
             */
            , get: function () {
                var data = localStorage.getItem(this.name);
                var item = Util.goodJsonObj(data, null);
                //var item = ("null" === data) ? null : JSON.parse(data);
                return item;
            }

            /**
             * @ngdoc method
             * @name set
             * @methodOf Acm.StoreService.LocalData
             *
             * @param {Object} data Value to set
             *
             * @description
             * Set value of a LocalData.
             *
             * Example:
             *
             * var dataCache = new LocalData("MyData");
             *
             * dataCache.set('{greeting: "Hello", who: "World"}');
             *
             * var data = dataCache.get();                              // data contains value '{greeting: "Hello", who: "World"}'
             */
            , set: function (data) {
                var item = (Util.isEmpty(data)) ? null : JSON.stringify(data);
                localStorage.setItem(this.name, item);
            }

            /**
             * @ngdoc method
             * @name remove
             * @methodOf Acm.StoreService.LocalData
             *
             * @description
             * remove LocalData.
             */
            , remove: function () {
                localStorage.removeItem(this.name);
            }
        };

        Store.CacheFifo.prototype = {
            DEFAULT_MAX_CACHE_SIZE: 8
            , DEFAULT_SHELF_LIFE: 7200000           //2 hours = 2 * 3600 * 1000 milliseconds

            , _getThis: function () {
                return $rootScope._storeCacheMap[this.name];
            }

            /**
             * @ngdoc method
             * @name get
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             *
             * @description
             * Get value of a CacheFifo
             *
             * @returns {Object} Object stored in cache
             *
             * Example:
             *
             * var dataCache = new CacheFifo({name: "MyData", maxSize: 3});
             *
             * dataCache.put("k1", "v1");
             *
             * dataCache.put("k2", "v2");
             *
             * dataCache.put("k3", "v3");
             *
             * dataCache.put("k4", "v4");
             *
             * dataCache.put("k3", "v31");
             *
             * var v1 = dataCache.get("k1");     // null, because it is pushed out by "k4"
             *
             * var v2 = dataCache.get("k2");     // "v2"
             *
             * var v3 = dataCache.get("k3");     // "v31", first "v3" is replaced by second "v31"
             *
             * var v4 = dataCache.get("k4");     // "v4"
             *
             */
            , get: function (key) {
                var thisCache = this._getThis();
                for (var i = 0; i < thisCache.size; i++) {
                    if (thisCache.keys[i] == key) {
                        return thisCache.cache[key];
                    }
                }
                return null;
            }

            /**
             * @ngdoc method
             * @name put
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             * @param {Object} item Value to set
             *
             * @description
             * Put value of into a CacheFifo. If a key already exists, put() updates the value, instead of creating a new one.
             *
             * Example:
             *
             * var dataCache = new CacheFifo({name: "MyData", maxSize: 3});
             *
             * dataCache.put("k1", "v1");
             *
             * dataCache.put("k2", "v2");
             *
             * dataCache.put("k3", "v3");
             *
             * dataCache.put("k4", "v4");
             *
             * dataCache.put("k3", "v31");
             *
             * var v1 = dataCache.get("k1");     // null, because it is pushed out by "k4"
             *
             * var v2 = dataCache.get("k2");     // "v2"
             *
             * var v3 = dataCache.get("k3");     // "v31", first "v3" is replaced by second "v31"
             *
             * var v4 = dataCache.get("k4");     // "v4"
             *
             */
            , put: function (key, item) {
                var thisCache = this._getThis();
                var putAt = -1;
                for (var i = 0; i < thisCache.size; i++) {
                    if (thisCache.keys[i] == key) {
                        putAt = i;
                        break;
                    }
                }

                if (0 > putAt) {
                    putAt = this._getNext();
                    this._advanceToNext();
                }

                thisCache.cache[key] = item;

                thisCache.timeStamp[key] = new Date().getTime();
                thisCache.keys[putAt] = key;
            }
            , _getNext: function () {
                return this._getNextN(0);
            }
            //Use n to keep track number of recursive call to _getNextN(), so that it will not exceed maxSize and into an infinite loop
            , _getNextN: function (n) {
                var thisCache = this._getThis();
                var next = thisCache.next;
                if (!this.isLock(thisCache.keys[next])) {
                    return next;
                }

                if (n > thisCache.maxSize) {     //when n == maxSize, _getNextN() is called maxSize times, pick the first one to avoid infinite loop
                    return next;
                }

                this._advanceToNext();
                return this._getNextN(n + 1);

            }
            , _advanceToNext: function () {
                var thisCache = this._getThis();
                thisCache.next = (thisCache.next + 1) % thisCache.maxSize;
                thisCache.size = (thisCache.maxSize > thisCache.size) ? (thisCache.size + 1) : thisCache.maxSize;
            }

            /**
             * @ngdoc method
             * @name remove
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             *
             * @description
             * Remove an item from CacheFifo.
             *
             */
            , remove: function (key) {
                var thisCache = this._getThis();
                var delAt = -1;
                for (var i = 0; i < thisCache.size; i++) {
                    if (thisCache.keys[i] == key) {
                        delAt = i;
                        break;
                    }
                }

                if (0 <= delAt) {
                    var newKeys = [];
                    for (var i = 0; i < thisCache.maxSize; i++) {
                        newKeys.push(null);
                    }

                    if (thisCache.size == thisCache.maxSize) {
                        var n = 0;
                        for (var i = 0; i < thisCache.size; i++) {
                            if (i != delAt) {
                                newKeys[n] = thisCache.keys[(thisCache.next + i + thisCache.maxSize) % thisCache.maxSize];
                                n++;
                            }
                        }
                    } else {
                        var n = 0;
                        for (var i = 0; i < thisCache.size; i++) {
                            if (i != delAt) {
                                newKeys[n] = thisCache.keys[i];
                                n++;
                            }
                        }
                    }
                    thisCache.size--;
                    thisCache.next = thisCache.size;

                    thisCache.keys = newKeys;

                    delete thisCache.cache[key];

                    delete thisCache.timeStamp[key];
                } //end if (0 <= delAt) {
            }

            /**
             * @ngdoc method
             * @name reset
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @description
             * Reset CacheFifo.
             *
             */
            , reset: function () {
                var thisCache = this._getThis();

                thisCache.next = 0;
                thisCache.size = 0;
                thisCache.cache = {};

                thisCache.timeStamp = {};
                thisCache.keys = [];
                for (var i = 0; i < thisCache.maxSize; i++) {
                    thisCache.keys.push(null);
                }
                thisCache.locks = [];
            }

            /**
             * @ngdoc method
             * @name keys
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @description
             * Return keys in array of CacheFifo.
             *
             * @returns {Array} Keys in array
             */
            , keys: function () {
                var thisCache = this._getThis();
                return thisCache.keys;
            }

            , _evict: function (name, timeToLive) {
                if (0 < timeToLive) {
                    var that = this;
                    var thisCache = this._getThis();
                    var now = new Date().getTime();
                    UtilTimerService.useTimer(name
                        , 300000     //every 5 minutes = 5 * 60 * 1000 milliseconds
                        , function () {
                            var keys = thisCache.keys;
                            var len = keys.length;
                            for (var i = 0; i < thisCache.size; i++) {
                                var key = keys[i];
                                var ts = thisCache.timeStamp[key];
                                if (timeToLive < now - ts) {
                                    that.remove(key);
                                }
                            }
                            return true;
                        }
                    );
                }
            }

            /**
             * @ngdoc method
             * @name lock
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             *
             * @description
             * Lock an item in CacheFifo, so that it has higher priority not to be aged first.
             *
             */
            , lock: function (key) {
                var thisCache = this._getThis();
                thisCache.locks.push(key);
            }

            /**
             * @ngdoc method
             * @name unlock
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             *
             * @description
             * Unlock an item in CacheFifo.
             *
             */
            , unlock: function (key) {
                var thisCache = this._getThis();
                for (var i = 0; i < thisCache.locks.length; i++) {
                    if (thisCache.locks[i] == key) {
                        thisCache.locks.splice(i, 1);
                        return;
                    }
                }
            }

            /**
             * @ngdoc method
             * @name isLock
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             *
             * @description
             * Return true if an item is locked in CacheFifo.
             *
             */
            , isLock: function (key) {
                var thisCache = this._getThis();
                for (var i = 0; i < thisCache.locks.length; i++) {
                    if (thisCache.locks[i] == key) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * @ngdoc method
             * @name getMaxSize
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @description
             * Get maxSize of CacheFifo setting.
             *
             */
            , getMaxSize: function () {
                var thisCache = this._getThis();
                return thisCache.maxSize;
            }

            /**
             * @ngdoc method
             * @name setMaxSize
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Number} maxSize Max size
             *
             * @description
             * Set maxSize of CacheFifo.
             *
             */
            , setMaxSize: function (maxSize) {
                var thisCache = this._getThis();
                thisCache.maxSize = maxSize;
            }

            /**
             * @ngdoc method
             * @name getTimeToLive
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @description
             * Get timeToLive of CacheFifo setting.
             *
             */
            , getTimeToLive: function () {
                var thisCache = this._getThis();
                return thisCache.timeToLive;
            }

            /**
             * @ngdoc method
             * @name setTimeToLive
             * @methodOf Acm.StoreService.CacheFifo
             *
             * @param {Number} timeToLive Expiration in milliseconds
             *
             * @description
             * Set timeToLive of CacheFifo.
             *
             */
            , setTimeToLive: function (timeToLive) {
                var thisCache = this._getThis();
                thisCache.timeToLive = timeToLive;
            }
        };


        //
        // IE11 has known issue with Local Storage. Following is a work-around until Microsoft provides a fix.
        // http://connect.microsoft.com/IE/feedbackdetail/view/812563/ie-11-local-storage-synchronization-issues#
        //
        var getIeVersion = function () {
            var sAgent = $window.navigator.userAgent;
            var Idx = sAgent.indexOf("MSIE");

            // If IE, return version number.
            if (Idx > 0)
                return parseInt(sAgent.substring(Idx+ 5, sAgent.indexOf(".", Idx)));

            // If IE 11 then look for Updated user agent string.
            else if (!!navigator.userAgent.match(/Trident\/7\./))
                return 11;

            else
                return 0; //It is not IE
        };
        if (11 == getIeVersion()) {
            $window.addEventListener("storage", function (e) {
                // Dummy
            }, false);
        }


        //
        // Initialize empty registries
        //
        Store.Registry.clearSessionCache();
        Store.Registry.clearLocalCache();

        return Store;
    }
]);