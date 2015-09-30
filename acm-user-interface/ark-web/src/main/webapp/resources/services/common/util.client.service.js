'use strict';

angular.module('services').factory('UtilService', ['$q', '$window', 'LookupService',
    function ($q, $window, LookupService) {
        var Util = {
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

            ,forEachStripNg: function(data, callback) {
                _.forEach(data, function(v, k) {
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
                 _.cloneDeep(copy, function(v, k, o) {
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
            ,deepOmit: function(item, blackList) {
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
            ,_deepOmitObj: function(obj, blackList) {
                var copy = _.omit(obj, blackList);
                //var copy = _.omit(obj, function(v, k, o, d) {
                //    if (_.contains(blackList, k)) {
                //        return true;
                //    } else if (_.isString(k) && k.startsWith("$")) {
                //        return true;
                //    }
                //});
                var that = this;
                _.each(blackList, function(arg) {
                    if (_.contains(arg, '.')) {
                        var key  = _.first(arg.split('.'));
                        var rest = arg.split('.').slice(1).join(".");
                        copy[key] = that.deepOmit(copy[key], [rest]);
                    }
                });
                return copy;
            }
            ,_deepOmitArray: function(arr, blackList) {
                var that = this;
                return _.map(arr, function(item) {
                    return that.deepOmit(item, blackList);
                });
            }

            , uiGrid: {
                typicalOptions: function (config, scope) {
                    scope.gridOptions = scope.gridOptions || {};

                    scope.gridOptions.enableColumnResizing = true;
                    scope.gridOptions.enableRowSelection = false;
                    scope.gridOptions.enableRowHeaderSelection = false;
                    scope.gridOptions.multiSelect = false;
                    scope.gridOptions.noUnselect = false;

                    scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    scope.gridOptions.paginationPageSize = config.paginationPageSize;
                    scope.gridOptions.enableFiltering = config.enableFiltering;
                    scope.gridOptions.onRegisterApi = function (gridApi) {
                        scope.gridApi = gridApi;
                    }
                }
                , externalPaging: function (config, scope, updatePageData) {
                    scope.currentId = $stateParams.id;
                    scope.start = 0;
                    scope.pageSize = config.paginationPageSize || 10;
                    scope.sort = {by: "", dir: "asc"};
                    scope.filters = [];

                    scope.gridOptions.useExternalPagination = true;
                    scope.gridOptions.useExternalSorting = true;

                    //comment out filtering until service side supports it
                    //scope.gridOptions.enableFiltering = config.enableFiltering;
                    //scope.gridOptions.useExternalFiltering = true;

                    scope.gridOptions.onRegisterApi = function (gridApi) {
                        scope.gridApi = gridApi;
                        scope.gridApi.core.on.sortChanged(scope, function (grid, sortColumns) {
                            if (0 >= sortColumns.length) {
                                scope.sort.by = null;
                                scope.sort.dir = null;
                            } else {
                                scope.sort.by = sortColumns[0].field;
                                scope.sort.dir = sortColumns[0].sort.direction;
                            }
                            updatePageData();
                        });
                        scope.gridApi.core.on.filterChanged(scope, function () {
                            var grid = this.grid;
                            scope.filters = [];
                            for (var i = 0; i < grid.columns.length; i++) {
                                if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                    var filter = {};
                                    filter.by = grid.columns[i].field;
                                    filter.with = grid.columns[i].filters[0].term;
                                    scope.filters.push(filter);
                                }
                            }
                            updatePageData();
                        });
                        scope.gridApi.pagination.on.paginationChanged(scope, function (newPage, pageSize) {
                            scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                            scope.pageSize = pageSize;
                            updatePageData();
                        });
                    }
                }
                , showObject: function (objType, objId, scope) {
                    var promiseObjectTypes = Util.servicePromise({
                        service: LookupService.getObjectTypes
                        , callback: function (data) {
                            scope.objectTypes = [];
                            _.forEach(data, function (item) {
                                scope.objectTypes.push(item);
                            });
                            return scope.objectTypes;
                        }
                    });

                    $q.all([promiseObjectTypes]).then(function (data) {
                        var find = _.where(scope.objectTypes, {type: objType});
                        if (0 < find.length) {
                            var url = Util.goodValue(find[0].url);
                            url = url.replace(":id", objId);
                            $window.location.href = url;
                        }
                    });
                }
            }

        };
        return Util;
    }
]);