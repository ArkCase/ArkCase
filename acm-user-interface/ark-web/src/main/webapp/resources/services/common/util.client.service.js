'use strict';

angular.module('services').factory('UtilService', ['$q',
    function ($q) {
        return {
            goodValue: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;
                return this.isEmpty(val) ? replacedWith : val;
            }
            ,goodArray: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? [] : replacement;
                return this.isArray(val) ? val : replacedWith;
            }

            //
            //todo: use lodash impl _.map(obj, 'some.arr[0].name');
            //
            ,goodMapValue: function (arr, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;
                if (this.isArray(arr)) {
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

                } else {
                    return replacedWith;
                }
            }

            //todo: use lodash imppl
            //function parseLodash(str){
            //    return _.attempt(JSON.parse.bind(null, str));
            //}
            ,goodJsonObj: function (str, replacement)  {
                var replacedWith = (undefined === replacement) ? {} : replacement;
                var json = replacedWith;
                try {
                    json = JSON.parse(str);
                } catch (e) {
                    json = replacedWith;
                }
                return json;
            }
            ,isEmpty: function (val) {
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
            ,isArray: function(arr) {
                if (arr) {
                    if (arr instanceof Array) {
                        return true;
                    }
                }
                return false;
            }
            ,isArrayEmpty: function (arr) {
                if(!this.isArray(arr)) {
                    return true;
                }
                return arr.length === 0;
            }

            ,servicePromise: function(arg) {
                var d = $q.defer();
                var service = arg.service;
                var param = this.goodValue(arg.param, {});
                if (arg.result) {
                    d.resolve(arg.result);

                } else {
                    service(param, function(data) {
                        if (arg.callback) {
                            d.resolve(arg.callback(data));
                        } else {
                            d.resolve(data);
                        }
                    });
                }
                return d.promise;
            }
            ,forEachTypical: function(data, callback) {
                _.forEach(data, function(v, k) {
                    if (("string" == (typeof k)) && !k.startsWith("$")) {
                        callback(v, k);
                    }
                });
            }
        }
    }
]);