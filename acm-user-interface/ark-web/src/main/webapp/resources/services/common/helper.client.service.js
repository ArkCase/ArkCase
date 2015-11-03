'use strict';

/**
 * @ngdoc service
 * @name services:HelperService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/helper.client.service.js services/common/helper.client.service.js}
 *
 * This service package contains various commonly used miscellaneous help functions.
 */

angular.module('services').factory('HelperService', ['$q', '$window', 'StoreService', 'UtilService', 'ValidationService', 'LookupService', 'Authentication',
    function ($q, $window, Store, Util, Validator, LookupService, Authentication) {
        var Helper = {
            SessionCacheNames: {
                USER_INFO: "AcmUserInfo"
                , USER_FULL_NAMES: "AcmUserFullNames"
                , USERS: "AcmUsers"
                , GROUPS: "AcmGroups"
                , PRIORITIES: "AcmPriorities"
                , CASE_TYPES: "AcmCaseTypes"
                , CASES_CONFIG: "AcmCasesConfig"
                , OBJECT_TYPES: "AcmObjectTypes"
                , FILE_TYPES: "AcmFileTypes"
                , FORM_TYPES: "AcmFormTypes"
                , CASE_CORRESPONDENCE_FORMS: "AcmCaseCorrespondenceForms"
                , PARTICIPANT_TYPES: "AcmParticipantTypes"
                , PARTICIPANT_USERS: "AcmParticipantUsers"
                , PARTICIPANT_GROUPS: "AcmParticipantGroups"
                , PERSON_TYPES: "AcmPersonTypes"
                , CONTACT_METHOD_TYPES: "AcmContactMethodTypes"
                , ORGANIZATION_TYPES: "AcmOrganizationTypes"
                , ADDRESS_TYPES: "AcmAddressTypes"
                , ALIAS_TYPES: "AcmAliasTypes"
                , SECURITY_TAG_TYPES: "AcmSecurityTagTypes"

            }
            , CacheNames: {
                MY_TASKS: "MyTasks"
                , CASE_LIST: "CaseList"
                , CASE_INFO: "CaseInfo"
                , CASE_HISTORY_DATA: "CaseHistoryData"
                , CASE_CORRESPONDENCE_DATA: "CaseCorrespondenceData"
                , CASE_NOTES: "CaseNotes"
                , CASE_COST_SHEETS: "CaseCostSheets"
                , CASE_TIME_SHEETS: "CaseTimeSheets"
            }

            , requestComponentConfig: function (scope, theComponentId, onConfigAcquired) {
                var dfd = $q.defer();
                //if (scope.config) {
                //    dfd.resolve(config);
                //} else {
                scope.$emit('req-component-config', theComponentId);
                scope.$on('component-config', function (e, componentId, config) {
                    if (theComponentId == componentId) {
                        scope.config = config;
                        onConfigAcquired(config);
                        dfd.resolve(config);
                    }
                });
                //}
                return dfd.promise;
            }
            , getUserInfo: function () {
                var cacheUserInfo = new Store.SessionData(Helper.SessionCacheNames.USER_INFO);
                var userInfo = cacheUserInfo.get();
                return Util.serviceCall({
                    service: Authentication.queryUserInfo
                    , result: userInfo
                    , onSuccess: function (data) {
                        if (Validator.validateUserInfo(data)) {
                            userInfo = data;
                            cacheUserInfo.set(userInfo);
                            return userInfo;
                        }
                    }
                });
            }

            , Grid: {
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
                    var cacheObjectTypes = new Store.SessionData(Helper.SessionCacheNames.OBJECT_TYPES);
                    var objectTypes = cacheObjectTypes.get();
                    var promiseObjectTypes = Util.serviceCall({
                        service: LookupService.getObjectTypes
                        , result: objectTypesStore
                        , onSuccess: function (data) {
                            objectTypes = [];
                            _.forEach(data, function (item) {
                                objectTypes.push(item);
                            });
                            cacheObjectTypes.set(objectTypes);
                            return objectTypes;
                        }
                    }).then(
                        function (objectTypes) {
                            scope.objectTypes = objectTypes;
                            return objectTypes;
                        }
                    );

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
                    var cacheUserFullNames = new Store.SessionData(Helper.SessionCacheNames.USER_FULL_NAMES);
                    var userFullNames = cacheUserFullNames.get();
                    return Util.serviceCall({
                        service: LookupService.getUsers
                        , result: userFullNames
                        , onSuccess: function (data) {
                            if (Util.isArray(data)) {
                                userFullNames = [];
                                var arr = data;
                                for (var i = 0; i < arr.length; i++) {
                                    var obj = Util.goodJsonObj(arr[i]);
                                    if (obj) {
                                        var user = {};
                                        user.id = Util.goodValue(obj.object_id_s);
                                        user.name = Util.goodValue(obj.name);
                                        userFullNames.push(user);
                                    }
                                }
                                cacheUserFullNames.set(userFullNames);
                                return userFullNames;
                            }
                        }
                    }).then(
                        function (userFullNames) {
                            scope.userFullNames = userFullNames;
                            return userFullNames;
                        }
                    );
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
                        //, enableFiltering: false
                        //, enableHiding: false
                        //, enableSorting: false
                        //, enableColumnResizing: false
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
            } //Grid

        };
        return Helper;
    }
]);