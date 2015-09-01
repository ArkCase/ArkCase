'use strict';

angular.module('services').factory('Acm', ['$q',
    function ($q) {
        return {
            goodValue: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;
                return _.isEmpty(val) ? replacedWith : val;
            }
            ,goodObjValue: function (arr, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;
                if (angular.isArray(arr)) {
                    if (0 >= arr.length) {
                        return replacedWith;
                    }

                    var v = replacedWith;
                    for (var i = 0; i < arr.length; i++) {
                        if (0 == i) {
                            v = arr[0];
                        } else {
                            var k = arr[i];
                            v = v[k];
                        }

                        if (_.isEmpty(v)) {
                            return replacedWith;
                        }
                    }
                    return v;

                } else {
                    return replacedWith;
                }
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
        }
    }
]);