'use strict';

/**
 * @ngdoc service
 * @name services.service:StoreService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/store.client.service.js services/common/store.client.service.js}
 *
 * This service package contains objects and functions for data storage
 */
angular.module('services').factory('StoreService', ['$rootScope', 'UtilService',
    function ($rootScope, Util) {
        var Store = {
            /**
             * @ngdoc service
             * @name StoreService.Variable
             *
             * @description
             *
             * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/store.client.service.js services/common/store.client.service.js}
             *
             * Variable represents map like data structure. Data are saved in Angular $rootScope.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf StoreService.Variable
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
            Variable: function (name, initValue) {
                this.name = name;
                $rootScope._storeVariableMap = $rootScope._storeVariableMap || {};
                if (undefined != initValue) {
                    $rootScope._storeVariableMap[name] = initValue;
                }
            }

            /**
             * @ngdoc service
             * @name StoreService.SessionData
             *
             * @description
             *
             * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/store.client.service.js services/common/store.client.service.js}
             *
             * SessionData represent data saved in session. Each data are identified by a name. It persists through the entire login session.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf StoreService.SessionData
             *
             * @param {String} name (Optional)Name. If not provided, a random name is generated for use
             *
             * @description
             * Create a reference object to a SessionData.
             *
             * Example:
             *
             * var sd = new SessionData("MyData");
             */
            , SessionData: function (name) {
                this.name = name;
            }


            /**
             * @ngdoc service
             * @name StoreService.LocalData
             *
             * @description
             *
             * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/store.client.service.js services/common/store.client.service.js}
             *
             * LocalData represent data saved in local storage. Each data are identified by a name. It persists on user computer until deleted.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf StoreService.LocalData
             *
             * @param {String} name (Optional)Name. If not provided, a random name is generated for use
             *
             * @description
             * Create a reference object to a LocalData.
             *
             * Example:
             *
             * var ld = new LocalData("MyData");
             */
            , LocalData: function (name) {
                this.name = name;
            }


            /**
             * @ngdoc service
             * @name StoreService.CacheFifo
             *
             * @description
             *
             * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/store.client.service.js services/common/store.client.service.js}
             *
             * CacheFifo is cache using first in first out aging algorithm. Each cache is identified by a name.
             * Cache data persists in Angular $rootScope. Data is evicted after expiration limit.
             */
            /**
             * @ngdoc method
             * @name Constructor
             * @methodOf StoreService.CacheFifo
             *
             * @param {Object} arg Argument. It can be an object or a name string. If a string, it is equivalent to {name: arg}
             * @param {String} arg.name (Optional)Name. If not provided, a random name is generated for use
             * @param {Number} arg.maxSize (Optional)Max size. If not provided, default size is 8
             * @param {Number} arg.expiration (Optional)Expiration in milliseconds. If not provided, default value is 7200000 (2 hours)
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
                    thisCache.expiration = Util.goodMapValue(arg, "expiration", this.DEFAULT_EXPIRATION);   //arg.expiration in milliseconds; -1 if never expired

                    this.reset();
                    this._evict(thisCache.name, thisCache.expiration);
                }
            }
        };
        Store.Variable.prototype = {
            /**
             * @ngdoc method
             * @name get
             * @methodOf StoreService.Variable
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
             * @methodOf StoreService.Variable
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
             * @methodOf StoreService.SessionData
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
             * @methodOf StoreService.SessionData
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
             * @methodOf StoreService.SessionData
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
        };


        Store.LocalData.prototype = {
            /**
             * @ngdoc method
             * @name getName
             * @methodOf StoreService.LocalData
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
             * @methodOf StoreService.LocalData
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
             * @methodOf StoreService.LocalData
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
        };

        Store.CacheFifo.prototype = {
            DEFAULT_MAX_CACHE_SIZE: 8
            , DEFAULT_EXPIRATION: 7200000           //2 hours = 2 * 3600 * 1000 milliseconds

            , _getThis: function () {
                return $rootScope._storeCacheMap[this.name];
            }

            /**
             * @ngdoc method
             * @name get
             * @methodOf StoreService.CacheFifo
             *
             * @param {Object} key Key to cache
             *
             * @description
             * Get value of a CacheFifo
             *
             * Example:
             *
             * var dataCache = new CacheFifo({name: "MyData", maxSize: 3});
             *
             * dataCache.set("k1", "v1");
             *
             * dataCache.set("k2", "v2");
             *
             * dataCache.set("k3", "v3");
             *
             * dataCache.set("k4", "v4");
             *
             * dataCache.set("k3", "v31");
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
             * @methodOf StoreService.CacheFifo
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
             * dataCache.set("k1", "v1");
             *
             * dataCache.set("k2", "v2");
             *
             * dataCache.set("k3", "v3");
             *
             * dataCache.set("k4", "v4");
             *
             * dataCache.set("k3", "v31");
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
             * @methodOf StoreService.CacheFifo
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
             * @methodOf StoreService.CacheFifo
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
            , _evict: function (name, expiration) {
                //fixme: need a timer

                //if (0 < expiration) {
                //    var that = this;
                //    Util.Timer.useTimer(name
                //        , 300000     //every 5 minutes = 5 * 60 * 1000 milliseconds
                //        , function () {
                //            var keys = that.keys;
                //            var len = keys.length;
                //            for (var i = 0; i < that.size; i++) {
                //                var key = keys[i];
                //                var ts = that.timeStamp[key];
                //                var now = new Date().getTime();
                //                if (expiration < now - ts) {
                //                    that.remove(key);
                //                }
                //            }
                //            return true;
                //        }
                //    );
                //}
            }

            /**
             * @ngdoc method
             * @name lock
             * @methodOf StoreService.CacheFifo
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
             * @methodOf StoreService.CacheFifo
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
             * @methodOf StoreService.CacheFifo
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
             * @methodOf StoreService.CacheFifo
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
             * @methodOf StoreService.CacheFifo
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
             * @name getExpiration
             * @methodOf StoreService.CacheFifo
             *
             * @description
             * Get expiration of CacheFifo setting.
             *
             */
            , getExpiration: function () {
                var thisCache = this._getThis();
                return thisCache.expiration;
            }

            /**
             * @ngdoc method
             * @name setExpiration
             * @methodOf StoreService.CacheFifo
             *
             * @param {Number} expiration Expiration in milliseconds
             *
             * @description
             * Set expiration of CacheFifo.
             *
             */
            , setExpiration: function (expiration) {
                var thisCache = this._getThis();
                thisCache.expiration = expiration;
            }
        };


        return Store;
    }
]);