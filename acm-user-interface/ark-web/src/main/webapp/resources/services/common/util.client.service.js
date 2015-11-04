'use strict';

/**
 * @ngdoc service
 * @name UtilService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/util.client.service.js services/common/util.client.service.js}
 *
 * This service package contains various commonly used functions, support functions, or miscellaneous help functions.
 */

//angular.module('services').factory('UtilService', ['$q', '$window', 'ngBootbox', 'LookupService',
//    function ($q, $window, $ngBootbox, LookupService) {
angular.module('services').factory('UtilService', ['$q', '$window', 'LookupService',
    function ($q, $window, LookupService) {
        var Util = {
            Constant: {
                OBJTYPE_CASE_FILE: "CASE_FILE"
                , OBJTYPE_COMPLAINT: "COMPLAINT"
                , OBJTYPE_TASK: "TASK"
                , OBJTYPE_TIMESHEET: "TIMESHEET"
                , OBJTYPE_COSTSHEET: "COSTSHEET"
                , OBJTYPE_FILE: "FILE"

                , LOOKUP_USER_FULL_NAMES: "userFullNames"
                , LOOKUP_PERSON_TYPES: "personTypes"
                , LOOKUP_PARTICIPANT_TYPES: "participantTypes"
                , LOOKUP_PARTICIPANT_NAMES: "participantNames"
                , LOOKUP_TASK_OUTCOMES: "taskOutcomes"
                , LOOKUP_CONTACT_METHODS_TYPES: "contactMethodTypes"
                , LOOKUP_ORGANIZATION_TYPES: "organizationTypes"
                , LOOKUP_ADDRESS_TYPES: "addressTypes"
                , LOOKUP_ALIAS_TYPES: "aliasTypes"
                , LOOKUP_SECURITY_TAG_TYPES: "securityTagTypes"
            }

            /**
             * @ngdoc method
             * @name goodValue
             * @methodOf UtilService
             *
             * @param {Object} val An object, including value
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * This function check if 'val' is empty. Returns it only it is not empty. Else 'replacement' is used.
             */
            , goodValue: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;
                return this.isEmpty(val) ? replacedWith : val;
            }
            ,goodArray: function (val, replacement) {
                var replacedWith = (undefined === replacement) ? [] : replacement;
                return this.isArray(val) ? val : replacedWith;
            }


            /**
             * @ngdoc method
             * @name goodMapValue
             * @methodOf UtilService
             *
             * @param {Object} map An object
             * @param {String} key A key. Use '.' to for parent-child linked key, and '[i]' for array item 'i'
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * This function check if value of given map with given key is empty. Returns the value only it is not empty. Else 'replacement' is used.
             *
             * Example:
             *
             * var emp = {name: "John",
             *   id: {
             *       ssn: "123-45-6789"
             *       driveLicence: "ABCD1234"
             *  }
             *   phone: [
             *       {type: "home", number: "555-555-5555"}
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

            //todo: consider using lodash imppl
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

            /**
             * @ngdoc method
             * @name isEmpty
             * @methodOf UtilService
             *
             * @param {Object} val An object, including value
             *
             * @description
             * Return true if 'val' is undefined, null, "", or "null"; false otherwise
             */
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

            /**
             * @ngdoc method
             * @name isArray
             * @methodOf UtilService
             *
             * @param {Object} arr An object, including value
             *
             * @description
             * Return true if 'arr' is an array
             */
            ,isArray: function(arr) {
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
             * @methodOf UtilService
             *
             * @param {Object} arr An object, including value
             *
             * @description
             * Return true for empty array or it is not an array at all
             */
            ,isArrayEmpty: function (arr) {
                if(!this.isArray(arr)) {
                    return true;
                }
                return arr.length === 0;
            }

            /**
             * @ngdoc method
             * @name compare
             * @methodOf UtilService
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


            /**
             * @ngdoc method
             * @name serviceCall
             * @methodOf UtilService
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
                var onSuccess = function (successData) {
                    var rc = successData;
                    if (arg.onSuccess) {
                        rc = arg.onSuccess(successData);
                        if (undefined == rc) {
                            if (arg.onInvalid) {
                                d.reject(arg.onInvalid(successData));
                            } else {
                                d.reject("Validation failure");
                            }
                        } else {
                            d.resolve(rc);
                        }
                    } else {
                        d.resolve(successData);
                    }
                    return rc;
                };
                var onError = function (errorData) {
                    var rc = errorData;
                    if (arg.onError) {
                        rc = arg.onError(errorData);
                    }
                    d.reject(rc);
                    return rc;
                };

                if (arg.result) {
                    d.resolve(arg.result);

                } else {
                    var service = arg.service;
                    var param = this.goodValue(arg.param, {});
                    var data = arg.data;

                    if (data) {
                        service(param, data, onSuccess, onError);
                    } else {
                        service(param, onSuccess, onError);
                    }
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

            , _padZero: function(i) {
                return (10 > i) ? "0" + i : "" + i;
            }

            //get day string in "yyyy-mm-dd" format
            //parameter d is java Date() format; for some reason getDate() is 1 based while getMonth() is zero based
            , dateToString: function(d) {
                if (null == d) {
                    return "";
                }
                var month = d.getMonth()+1;
                var day = d.getDate();
                var year = d.getFullYear();
                return this._padZero(month)
                    + "/" + this._padZero(day)
                    + "/" + year;
            }

            , getCurrentDay: function() {
                var d = new Date();
                return this.dateToString(d);
            }

            , AcmGrid: {
                hidePagingControlsIfAllDataShown: function (scope, totalCount) {
                    if (scope && scope.gridOptions && scope.gridOptions.paginationPageSize) {
                        if (totalCount <= scope.gridOptions.paginationPageSize) {
                            // Hides pagination controls since there is only 1 page of data
                            scope.gridOptions.enablePaginationControls = false;
                        } else {
                            // need to re-enable pagination if a record is added to the next page
                            scope.gridOptions.enablePaginationControls = true;
                        }
                    }
                },
                setBasicOptions: function (scope, config) {
                    scope.gridOptions = scope.gridOptions || {};
                    scope.config = config;

                    scope.gridOptions.enableColumnResizing = true;
                    scope.gridOptions.enableRowSelection = false;
                    scope.gridOptions.enableRowHeaderSelection = false;
                    scope.gridOptions.multiSelect = false;
                    scope.gridOptions.noUnselect = false;

                    scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                    scope.gridOptions.paginationPageSize = config.paginationPageSize;
                    scope.gridOptions.enableFiltering = config.enableFiltering;

                    var d = $q.defer();
                    scope.gridOptions.promiseRegisterApi = d.promise;
                    scope.gridOptions.onRegisterApi = function (gridApi) {
                        scope.gridApi = gridApi;
                        d.resolve(gridApi);
                    }
                }
                , setExternalPaging: function (scope, config, retrieveGridData) {
                    //scope.currentId = $stateParams.id;
                    scope.start = 0;
                    scope.pageSize = config.paginationPageSize || 10;
                    scope.sort = {by: "", dir: "asc"};
                    scope.filters = [];

                    scope.gridOptions.useExternalPagination = true;
                    scope.gridOptions.useExternalSorting = true;

                    //comment out filtering until service side supports it
                    //scope.gridOptions.enableFiltering = config.enableFiltering;
                    scope.gridOptions.enableFiltering = false;
                    //scope.gridOptions.useExternalFiltering = true;


                    scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                        scope.gridApi.core.on.sortChanged(scope, function (grid, sortColumns) {
                            if (0 >= sortColumns.length) {
                                scope.sort.by = null;
                                scope.sort.dir = null;
                            } else {
                                scope.sort.by = sortColumns[0].field;
                                scope.sort.dir = sortColumns[0].sort.direction;
                            }
                            retrieveGridData();
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
                            retrieveGridData();
                        });
                        scope.gridApi.pagination.on.paginationChanged(scope, function (newPage, pageSize) {
                            scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                            scope.pageSize = pageSize;
                            retrieveGridData();
                        });
                    });
                }
                , setInPlaceEditing: function (scope, config, updateRow, canUpdate) {
                    scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                        gridApi.edit.on.afterCellEdit(scope, function (rowEntity, colDef, newValue, oldValue) {
                            if (newValue == oldValue) {
                                return;
                            }

                            if (Util.isEmpty(canUpdate) || canUpdate(rowEntity)) {
                                updateRow(rowEntity);
                            }
                        });
                    });
                }
                , addGridApiHandler: function (scope, handler) {
                    scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                        handler(gridApi);
                    });
                }
                , setColumnDefs: function (scope, config) {
                    scope.gridOptions = scope.gridOptions || {};
                    scope.gridOptions.columnDefs = config.columnDefs;
                }
                , showObject: function (scope, objType, objId) {
                    var promiseObjectTypes = Util.serviceCall({
                        service: LookupService.getObjectTypes
                        , onSuccess: function (data) {
                            scope.objectTypes = [];
                            _.forEach(data, function (item) {
                                scope.objectTypes.push(item);
                            });
                            return scope.objectTypes;
                        }
                    });

                    promiseObjectTypes.then(function (data) {
                        var found = _.find(scope.objectTypes, {type: objType});
                        if (found) {
                            var url = Util.goodValue(found.url);
                            url = url.replace(":id", objId);
                            $window.location.href = url;
                        }
                    });
                }
                , getUsers: function (scope) {
                    return Util.serviceCall({
                        service: LookupService.getUsers
                        , onSuccess: function (data) {
                            scope.userFullNames = [];
                            var arr = Util.goodArray(data);
                            for (var i = 0; i < arr.length; i++) {
                                var obj = Util.goodJsonObj(arr[i]);
                                if (obj) {
                                    var user = {};
                                    user.id = Util.goodValue(obj.object_id_s);
                                    user.name = Util.goodValue(obj.name);
                                    scope.userFullNames.push(user);
                                }
                            }
                            return scope.userFullNames;
                        }
                    });
                }
                , setUserNameFilter: function (scope, promiseUsers) {
                    $q.all([promiseUsers]).then(function (data) {
                        for (var i = 0; i < scope.config.columnDefs.length; i++) {
                            if (Util.Constant.LOOKUP_USER_FULL_NAMES == scope.config.columnDefs[i].lookup) {
                                scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";
                            }
                        }
                    });
                }
                , withPagingParams: function (scope, arg) {
                    var sort = "";
                    if (scope.sort) {
                        if (!Util.isEmpty(scope.sort.by) && !Util.isEmpty(scope.sort.dir)) {
                            sort = scope.sort.by + " " + scope.sort.dir;
                        }
                    }
                    //implement filtering here when service side supports it
                    //var filter = "";
                    ////$scope.filters = [{by: "eventDate", with: "term"}];

                    arg.startWith = scope.start;
                    arg.count = scope.pageSize;
                    arg.sort = sort;

                    return arg;
                }
                , addDeleteButton: function (columnDefs, onClickDelete) {
                    var columnDef = {
                        name: "act"
                        ,
                        cellEditableCondition: false
                        //,enableFiltering: false
                        //,enableHiding: false
                        //,enableSorting: false
                        //,enableColumnResizing: false
                        ,
                        width: 40
                        ,
                        headerCellTemplate: "<span></span>"
                        ,
                        cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='" + onClickDelete + "'></i></span>"
                    };
                    columnDefs.push(columnDef);
                }
                , deleteRow: function (scope, rowEntity) {
                    var idx = _.findIndex(scope.gridOptions.data, function (obj) {
                        return (obj == rowEntity);
                    });
                    if (0 <= idx) {
                        scope.gridOptions.data.splice(idx, 1);
                    }
                }
            } //AcmGrid

            , Ui: {}
        };
        return Util;
    }
]);