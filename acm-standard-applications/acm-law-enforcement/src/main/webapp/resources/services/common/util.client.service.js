'use strict';

/**
 * @ngdoc service
 * @name services.service:UtilService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/common/util.client.service.js services/common/util.client.service.js}
 *
 * This service package contains various commonly used functions, support functions, or miscellaneous help functions.
 */

//angular.module('services').factory('UtilService', ['$q', '$window', 'ngBootbox', 'LookupService',
//    function ($q, $window, $ngBootbox, LookupService) {
angular.module('services').factory('UtilService', ['$q', '$log'
    , function ($q, $log) {
        var Util = {

            /**
             * @ngdoc method
             * @name goodValue
             * @methodOf services.service:UtilService
             *
             * @param {Object} val An object, including string, number and boolean
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * This function check if 'val' is empty. Returns it only it is not empty; else 'replacement' is used.
             */
            goodValue: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;
                return this.isEmpty(val) ? replacedWith : val;
            }

            /**
             * @ngdoc method
             * @name goodPositive
             * @methodOf services.service:UtilService
             *
             * @param {Number} val A number
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to 0
             *
             * @description
             * This function returns a valid positive number; else 'replacement' is used.
             */
            , goodPositive: function (val, replacement) {
                var rc = this.goodNumber(val, replacement);
                if (0 > rc) {
                    rc = (undefined === replacement) ? 0 : replacement;
                }
                return rc;
            }

            /**
             * @ngdoc method
             * @name goodNumber
             * @methodOf services.service:UtilService
             *
             * @param {Number} val A number
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to 0
             *
             * @description
             * This function returns a valid number value; else 'replacement' is used.
             */
            , goodNumber: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? 0 : replacement;
                var rc = replacedWith;
                var num = parseInt(val);
                if (!isNaN(num)) {
                    rc = num;
                }
                return rc;
            }

            /**
             * @ngdoc method
             * @name goodArray
             * @methodOf services.service:UtilService
             *
             * @param {Array} val An array
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to []
             *
             * @description
             * This function returns itself only it is a positive number; else 'replacement' is used.
             */
            , goodArray: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? [] : replacement;
                return this.isArray(val) ? val : replacedWith;
            }


            /**
             * @ngdoc method
             * @name goodMapValue
             * @methodOf services.service:UtilService
             *
             * @param {Object} map An object
             * @param {String} key A key. Use '.' to for parent-child linked key, and '[i]' for array item 'i'
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * This function check if value of given map with given key is empty. Returns the value only it is not empty; else 'replacement' is used.
             *
             * Example:
             *
             * var emp = {name: "John",
             *   id: {
             *       ssn: "123-45-6789",
             *       driveLicence: "ABCD1234"
             *  },
             *   phone: [
             *       {type: "home", number: "555-555-5555"},
             *       {type: "cell", number: "555-555-5556"}
             *       ]
             *   }
             *
             * var  name = UtilService.goodMapValue(emp, "name");               //returns "John"
             *
             * var  ssn = UtilService.goodMapValue(emp, "id.ssn");              //returns "123-45-6789"
             *
             * var  phoneType = UtilService.goodMapValue(emp, "phone[1].type"); //returns "cell"
             *
             * var  noName = UtilService.goodMapValue(null, "name");            //returns ""
             *
             * var  noName2 = UtilService.goodMapValue(emp, "noSuch", "NA");    //returns "NA"
             *
             * var  noSsn = UtilService.goodMapValue(emp, "id.ssnWrong");       //returns ""
             *
             * var  noSsn2 = UtilService.goodMapValue(emp, "idWrong.ssn");      //returns ""
             *
             * var  noType = UtilService.goodMapValue(emp, "phone[2].type", "Array out of bound"); //returns "Array out of bound"
             *
             */
            //
            //todo: consider using lodash impl _.map(obj, 'some.arr[0].name');
            //todo: also examine _.get()
            //
            , goodMapValue: function (map, key, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;

                key = key.replace(/(?!^)\[/g, '.['); //replace "[" with ".[" except when "[" is at the beginning
                var arr = key.split('.');
                if (this.isArrayEmpty(arr)) {
                    return replacedWith;
                }
                arr.unshift(map);
                return this._goodMapValueArr(arr, replacedWith);
            }
            , _goodMapValueArr: function (arr, replacedWith) {
                if (!this.isArray(arr)) {
                    return replacedWith;
                }

                if (0 >= arr.length) {
                    return replacedWith;
                }

                var v = replacedWith;
                for (var i = 0; i < arr.length; i++) {
                    if (0 == i) {
                        v = arr[0];
                    } else {
                        var k = arr[i];
                        if (k.match(/^\[[0-9]+\]$/)) {  // match to "[ numbers ]"
                            if (!this.isArray(v)) {
                                return replacedWith;
                            }
                            var idx = k.substring(1, k.length - 1);
                            if (v.length <= idx) {
                                return replacedWith;
                            }
                            v = v[idx];

                        } else {
                            v = v[k];
                        }
                    }

                    if (this.isEmpty(v)) {
                        return replacedWith;
                    }
                }
                return v;
            }


            /**
             * @ngdoc method
             * @name goodJsonObj
             * @methodOf services.service:UtilService
             *
             * @param {Object} str A JSON as string
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to {}
             *
             * @description
             * This function check if value of given string is a valid JSON. Returns JSON object only the string is a valid JSON; else 'replacement' is used.
             *
             * Example:
             *
             * var emp = '{name: "John",
             *   id: {
             *       ssn: "123-45-6789",
             *       driveLicence: "ABCD1234"
             *  },
             *   phone: [
             *       {type: "home", number: "555-555-5555"},
             *       {type: "cell", number: "555-555-5556"}
             *       ]
             *   }
             *
             * var empStr = '{"name":"John","id":{"ssn":"123-45-6589","driveLicence":"ABCD1234"},"phone":[{"type":"home","number":"555-55-5555"},{"type":"cell","number":"555-55-5556"}]}';
             * var good = UtilService.goodJsonObj(empStr);                       //good should be same as emp
             * var bad  =  UtilService.goodJsonObj("{some bad JSON: str]");      //bad should be {}
             */
            //todo: consider using lodash imppl
            //function parseLodash(str){
            //    return _.attempt(JSON.parse.bind(null, str));
            //}
            , goodJsonObj: function (str, replacement) {
                var replacedWith = (undefined === replacement) ? {} : replacement;
                var json = replacedWith;
                try {
                    json = JSON.parse(str);
                } catch (e) {
                    json = replacedWith;
                }
                return json;
            }

            /**
             * @ngdoc method
             * @name isEmpty
             * @methodOf services.service:UtilService
             *
             * @param {Object} val An object, including value
             *
             * @description
             * Return true if 'val' is undefined, null, "", or "null"; false otherwise
             */
            , isEmpty: function (val) {
                if (undefined == val) {
                    return true;
                } else if ("" === val) {
                    return true;
                } else if (null == val) {
                    return true;
                } else if ("null" == val) {
                    return true;
                }
                return false;
            }

            /**
             * @ngdoc method
             * @name isArray
             * @methodOf services.service:UtilService
             *
             * @param {Object} arr An object, including value
             *
             * @description
             * Return true if 'arr' is an array
             */
            , isArray: function (arr) {
                if (arr) {
                    if (arr instanceof Array) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * @ngdoc method
             * @name isArrayEmpty
             * @methodOf services.service:UtilService
             *
             * @param {Object} arr An object, including value
             *
             * @description
             * Return true for empty array or it is not an array at all
             */
            , isArrayEmpty: function (arr) {
                if (!this.isArray(arr)) {
                    return true;
                }
                return arr.length === 0;
            }

            /**
             * @ngdoc method
             * @name compare
             * @methodOf services.service:UtilService
             *
             * @param {Object} left An object, including value
             * @param {Object} right An object, including value
             *
             * @description
             * Return true if both values are equal. Returns true as well if both values are empty.
             */
            , compare: function (left, right) {  //equals() name is taken, so use compare()
                if (this.isEmpty(left)) {
                    return this.isEmpty(right);
                }
                return left == right;
            }


            ///**
            // * @ngdoc method
            // * @name compare
            // * @methodOf services.service:UtilService
            // *
            // * @param {Object} left An object, including value
            // * @param {Object} right An object, including value
            // *
            // * @description
            // * Append random parameter after a url to avoid undesired cached session variables
            // * The function handles input url in following sample cases:
            // * (1):  some.com/some/path
            // * (2):  some.com/some/path/
            // * (3):  some.com/some/path?var=abc
            // */
            //, noneCacheUrl: function(url) {
            //    var lastChar = url.slice(-1);
            //    var hasQmark = (-1 !== url.indexOf('?'));
            //
            //    if (hasQmark) {
            //        url += '&'
            //    } else {
            //        url += '?';
            //    }
            //    url += 'rand=' + Math.floor((Math.random()*10000000000));
            //    return url;
            //}

            /**
             * @ngdoc method
             * @name serviceCall
             * @methodOf services.service:UtilService
             *
             * @param {Object} arg Argument
             * @param {Function} arg.service Function to be called. It is usually defined in service using $resource,
             * @param {Object} arg.param (Optional)Parameters passed to service function. If not specified, it is default to {}
             * @param {Object} arg.data Data passed to service function. For GET call, 'data' should not be provided; For non-GET call, 'data' is mandatory, even with no data to pass (use {}, in this case)
             * @param {Object} arg.result (Optional)If result is truthy, it is used as result to resolve. No service call will be made; otherwise service call will be made. This is designed to be used with cached result
             * @param {Function} arg.onSuccess (Optional)Callback function when service call response is success. This function returns non-null object or value to indicate valid data. If no value is return, it is rejected as error. If not provided, the response from service function call is used to resolve
             * @param {Function} arg.onError (Optional)Callback function when service call response has error. If not provided, error is handle by default
             * @param {Function} arg.onInvalid (Optional)Callback function to create invalidate result to be resolved. If not provide, default result is used
             *
             * @description
             * This is a wrap function to call a service function, which is typically defined as a service using $resource to make REST call. It returns a promise
             */
            , serviceCall: function (arg) {
                var d = $q.defer();
                var callbacks = {};
                callbacks.onSuccess = function (successData) {
                    var rc = successData;
                    if (arg.onSuccess) {
                        rc = arg.onSuccess(successData);
                        if (undefined == rc) {
                            if (Util.goodMapValue(successData, "error", false)) {
                                rc = {
                                    status: Util.goodValue(Util.goodValue(successData.error.code), 0)
                                    , statusText: "Partial Success"
                                    , data: Util.goodValue(Util.goodValue(successData.error.msg), "Unknown error")
                                };

                            } else if (arg.onInvalid) {
                                var rcInvalid = arg.onInvalid(successData);
                                if (rcInvalid instanceof String) {
                                    rc = {
                                        status: 0
                                        , statusText: "Customized error"
                                        , data: rcInvalid
                                    };
                                }
                            } else {
                                rc = {
                                    status: 0
                                    , statusText: "Validation error"
                                    , data: "Validation error"
                                };
                            }
                            callbacks.onError(rc);
                        } else {
                            d.resolve(rc);
                        }
                    } else {
                        d.resolve(successData);
                    }
                    return rc;
                };
                callbacks.onError = function (errorData) {
                    var rc = errorData;
                    if (arg.onError) {
                        rc = arg.onError(errorData);
                    }
                    d.reject(rc);

                    if (rc) {
                        $log.error("service call error:[" + Util.goodMapValue(rc, "status") + ", " + Util.goodMapValue(rc, "statusText") + "]" + Util.goodMapValue(rc, "data"));
                    } else {
                        $log.error("service call error: (No error info)");
                    }
                    return rc;
                };

                if (arg.result) {
                    d.resolve(arg.result);

                } else {
                    var service = arg.service;
                    var param = this.goodValue(arg.param, {});
                    var data = arg.data;

                    if (data) {
                        service(param, data, callbacks.onSuccess, callbacks.onError);
                    } else {
                        service(param, callbacks.onSuccess, callbacks.onError);
                    }
                }
                return d.promise;
            }


            /**
             * @ngdoc method
             * @name errorPromise
             * @methodOf services.service:UtilService
             *
             * @param {Object} err Error message or object
             *
             * @description
             * It returns a promise that is reject right away
             */
            , errorPromise: function (err) {
                var d = $q.defer();
                d.reject(err);
                return d.promise;
            }

            , forEachStripNg: function (data, callback) {
                _.forEach(data, function (v, k) {
                    if (_.isString(k) && !k.startsWith("$")) {
                        callback(v, k);
                    }
                });
            }

            // "$" prefix is used by Angular to add properties or objects to data object.
            // "acm$_" prefix is used for additional properties or objects added to data object
            // omitNg() omits any properties or objects starts with "$" or "acm$_". Original data object is not modified
            //
            , omitNg: function (obj) {
                var copy = _.cloneDeep(obj);
                _.cloneDeep(copy, function (v, k, o) {
                    if (_.isString(k)) {
                        if (k.startsWith("$") || k.startsWith("acm$_")) {
                            delete o[k];
                            var z = 1;
                        }
                    }
                });
                return copy;
                //return this.deepOmit(copy, blackList);
            }
            , deepOmit: function (item, blackList) {
                if (!_.isArray(blackList)) {
                    blackList = [];
                }

                var res = item;
                if (this.isArray(item)) {
                    res = this._deepOmitArray(item, blackList);
                } else {
                    res = this._deepOmitObj(item, blackList);
                }
                return res;
            }
            , _deepOmitObj: function (obj, blackList) {
                var copy = _.omit(obj, blackList);
                //var copy = _.omit(obj, function(v, k, o, d) {
                //    if (_.contains(blackList, k)) {
                //        return true;
                //    } else if (_.isString(k) && k.startsWith("$")) {
                //        return true;
                //    }
                //});
                var that = this;
                _.each(blackList, function (arg) {
                    if (_.contains(arg, '.')) {
                        var key = _.first(arg.split('.'));
                        var rest = arg.split('.').slice(1).join(".");
                        copy[key] = that.deepOmit(copy[key], [rest]);
                    }
                });
                return copy;
            }
            , _deepOmitArray: function (arr, blackList) {
                var that = this;
                return _.map(arr, function (item) {
                    return that.deepOmit(item, blackList);
                });
            }



            //
            // Depth first tree builder for fancytree (more accurately a forest builder)
            //
            , FancyTreeBuilder: {
                _path: []
                , _depth: 0
                , _pushDepth: function (node) {
                    if (this._path.length > this._depth) {
                        this._path[this._depth] = node;
                    } else {
                        this._path.push(node);
                    }
                    this._depth++;
                }
                , _popDepth: function () {
                    if (0 >= this._depth) {
                        return null;
                    }
                    this._depth--;
                    return this._path[this._depth];
                }
                , _peekDepth: function () {
                    if (0 >= this._depth) {
                        return null;
                    }
                    return this._path[this._depth - 1];
                }

                , _nodes: []
                , reset: function () {
                    this._path = [];
                    this._depth = 0;
                    this._nodes = [];
                    return this;
                }
                , addBranch: function (node) {
                    return this._addNode(node, false);
                }
                , addBranchLast: function (node) {
                    return this._addNode(node, true);
                }
                , makeLast: function () {
                    //keep popping stack until a node that is not the last child is found
                    var nonLastChildFound = false;
                    do {
                        var item = this._peekDepth();
                        nonLastChildFound = false;
                        if (item) {
                            if (item.isLast) {
                                this._popDepth();
                                nonLastChildFound = true;
                            }
                        }
                    } while (nonLastChildFound);

                    this._popDepth();   //the node found is not last child, so next node to insert should be its sibling. Pop to parent to prepare for inserting its sibling

                    return this;
                }
                , addLeaf: function (node) {
                    this._addNode(node, false);
                    this._popDepth();
                    return this;
                }
                , addLeafLast: function (node) {
                    this._addNode(node, true);
                    this._popDepth();
                    this.makeLast();
                    return this;
                }
                , _addNode: function (node, isLast) {
                    if (0 == this._depth) {
                        this._nodes.push(node);
                    } else {
                        var parent = this._peekDepth();
                        if (!parent.node.children) {
                            parent.node.children = [node];
                        } else {
                            parent.node.children.push(node);
                        }
                    }
                    this._pushDepth({node: node, isLast: isLast});
                    return this;
                }
                , getTree: function () {
                    return this._nodes;
                }
            }

            , _padZero: function (i) {
                return (10 > i) ? "0" + i : "" + i;
            }

            /**
             * @ngdoc method
             * @name dateToISOString
             * @methodOf services.service:UtilService
             *
             * @description
             * Converts a date object into an ISO format string
             *
             * @param {Date} Date object
             * @Returns {String} ISO formatted date string YYYY-MM-DDTHH:mm:ss.SSSZZ
             */
            , dateToIsoString: function (d) {

                console.log("Compatibility warning: UtilService.dateToIsoString() phase out. Please use Util.DateService.dateToIso()");

                if (null == d) {
                    return "";
                }
                return moment(d).format("YYYY-MM-DDTHH:mm:ss.SSSZZ");
            }

            //get day string in "yyyy-mm-dd" format
            //parameter d is java Date() format; for some reason getDate() is 1 based while getMonth() is zero based
            , dateToString: function (d) {
                if (null == d) {
                    return "";
                }
                var month = d.getMonth() + 1;
                var day = d.getDate();
                var year = d.getFullYear();
                return this._padZero(month)
                    + "/" + this._padZero(day)
                    + "/" + year;
            }

            , getCurrentDay: function () {
                var d = new Date();
                return this.dateToString(d);
            }
            //Get date and time from format: "2014-04-30T16:51:33.914+0000"
            , getDateTimeFromDatetime: function (dt, format) {
                var d = "";
                if (!this.isEmpty(dt) && !this.isEmpty(format)) {
                    d = moment(dt).format(format);
                }
                return d;
            }

            , filterWidgets: function (model, allowedWidgets) {
                var filteredModel = model;
                //Assume that we aren't using more than 1 row
                var rowLength = filteredModel.rows.length - 1;
                _.forEach(filteredModel.rows[rowLength].columns, function (col, key) {
                    _.forEach(col, function (widgets, wKey) {
                        if (wKey == 'widgets') {
                            _.remove(widgets, function (widget) {
                                if (!(_.includes(allowedWidgets, widget.title))) {
                                    return true;
                                }
                            });
                        }
                    });
                });
                return filteredModel;
            }

            /**
             * @ngdoc method
             * @name encryptString
             * @methodOf services.service:UtilService
             *
             * @param {String} string to encrypt
             * @param {String} passphrase used for encryption
             *
             * @description
             * This method returns AES encrypted string
             * using the provided passphrase
             */
            , encryptString: function (string, passphrase) {
                if (passphrase) {
                    try {
                        var encrypted = CryptoJS.AES.encrypt(queryString, passphrase);
                        return encrypted.toString();
                    }
                    catch (e) {
                        $log.warn("Error on encryption, returning plain query string");
                    }
                }
                return string;
            }
        };


        //
        // Fix for incompatibility issues:
        // startsWith is a method proposed for the next version of JavaScript, ES6.
        // It's currently unsupported outside of Chrome 41+, and Firefox 17+.
        //
        if (!String.prototype.startsWith) {
            String.prototype.startsWith = function (searchString, position) {
                position = position || 0;
                return this.indexOf(searchString, position) === position;
            };
        }

        return Util;
    }
]);