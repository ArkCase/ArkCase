'use strict';

/**
 * @ngdoc service
 * @name services.service:HelperService
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
            ObjectTypes: {
                CASE_FILE: "CASE_FILE"
                , COMPLAINT: "COMPLAINT"
                , TASK: "TASK"
                , ADHOC_TASK: "ADHOC"
                , TIMESHEET: "TIMESHEET"
                , COSTSHEET: "COSTSHEET"
                , FILE: "FILE"
            }
            , Lookups: {
                USER_FULL_NAMES: "userFullNames"
                , PERSON_TYPES: "personTypes"
                , PARTICIPANT_TYPES: "participantTypes"
                , PARTICIPANT_NAMES: "participantNames"
                , TASK_OUTCOMES: "taskOutcomes"
                , CONTACT_METHODS_TYPES: "contactMethodTypes"
                , ORGANIZATION_TYPES: "organizationTypes"
                , ADDRESS_TYPES: "addressTypes"
                , ALIAS_TYPES: "aliasTypes"
                , SECURITY_TAG_TYPES: "securityTagTypes"
            }
            , SessionCacheNames: {
                USER_INFO: "AcmUserInfo"
                , USER_FULL_NAMES: "AcmUserFullNames"
                , USERS: "AcmUsers"
                , GROUPS: "AcmGroups"
                , PRIORITIES: "AcmPriorities"
                , OBJECT_TYPES: "AcmObjectTypes"
                , FILE_TYPES: "AcmFileTypes"
                , FORM_TYPES: "AcmFormTypes"
                , PARTICIPANT_TYPES: "AcmParticipantTypes"
                , PARTICIPANT_USERS: "AcmParticipantUsers"
                , PARTICIPANT_GROUPS: "AcmParticipantGroups"
                , PERSON_TYPES: "AcmPersonTypes"
                , CONTACT_METHOD_TYPES: "AcmContactMethodTypes"
                , ORGANIZATION_TYPES: "AcmOrganizationTypes"
                , ADDRESS_TYPES: "AcmAddressTypes"
                , ALIAS_TYPES: "AcmAliasTypes"
                , SECURITY_TAG_TYPES: "AcmSecurityTagTypes"

                , CASE_CONFIG: "AcmCaseConfig"
                , CASE_TYPES: "AcmCaseTypes"
                , CASE_CORRESPONDENCE_FORMS: "AcmCaseCorrespondenceForms"

                , COMPLAINT_CONFIG: "AcmComplaintConfig"
                , COMPLAINT_TYPES: "AcmComplaintTypes"
                , COMPLAINT_CORRESPONDENCE_FORMS: "AcmComplaintCorrespondenceForms"

                , TASK_CONFIG: "AcmTaskConfig"
                , TASK_TYPES: "AcmTaskTypes"
                , TASK_CORRESPONDENCE_FORMS: "AcmTaskCorrespondenceForms"

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

                , COMPLAINT_LIST: "ComplaintList"
                , COMPLAINT_INFO: "ComplaintInfo"
                , COMPLAINT_HISTORY_DATA: "ComplaintHistoryData"
                , COMPLAINT_CORRESPONDENCE_DATA: "ComplaintCorrespondenceData"
                , COMPLAINT_NOTES: "ComplaintNotes"
                , COMPLAINT_COST_SHEETS: "ComplaintCostSheets"
                , COMPLAINT_TIME_SHEETS: "ComplaintTimeSheets"

                , TASK_LIST: "TaskList"
                , TASK_INFO: "TaskInfo"
                , TASK_HISTORY_DATA: "TaskHistoryData"
                , TASK_NOTES: "TaskNotes"
            }

            /**
             * @ngdoc method
             * @name requestComponentConfig
             * @methodOf services.service:HelperService
             *
             * @param {Object} scope Angular scope
             * @param {String} theComponentId Component ID
             * @param {Function} onConfigAcquired Callback function when configuration is acquired
             *
             * @description
             * This method asks for config data for a specified component
             */
            , requestComponentConfig: function (scope, theComponentId, onConfigAcquired) {
                var dfd = $q.defer();
                scope.$emit('req-component-config', theComponentId);
                scope.$on('component-config', function (e, componentId, config) {
                    if (theComponentId == componentId) {
                        onConfigAcquired(config);
                        dfd.resolve(config);
                    }
                });
                return dfd.promise;
            }

            /**
             * @ngdoc method
             * @name getUserInfo
             * @methodOf services.service:HelperService
             *
             * @description
             * Retrieves current login user info
             */
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

            /**
             * @ngdoc service
             * @name HelperService.Grid
             *
             * @description
             *
             * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/helper.client.service.js services/common/helper.client.service.js}
             *
             * Grid contains frequently used functions to help creating ui-grid.
             */
            , Grid: {
                /**
                 * @ngdoc method
                 * @name hidePagingControlsIfAllDataShown
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Number} totalCount Total number of grid rows
                 *
                 * @description
                 * Hide paging controls of Angular ui-grid if all data has already shown
                 */
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
                }


                /**
                 * @ngdoc method
                 * @name setBasicOptions
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} config Configuration data
                 *
                 * @description
                 * Set some typical ui-grid options.
                 */
                , setBasicOptions: function (scope, config) {
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

                /**
                 * @ngdoc method
                 * @name setExternalPaging
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} config Configuration data
                 * @param {Function} retrieveGridData Callback function to call when need to retrieve data for ui-grid
                 *
                 * @description
                 * Set ui-grid options to support external paging.
                 */
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

                /**
                 * @ngdoc method
                 * @name setInPlaceEditing
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} config Configuration data
                 * @param {Function} updateRow Callback function to update a row of a ui-grid
                 * @param {Function} canUpdate Callback function to to check condition if a row can be updated
                 *
                 * @description
                 * Set ui-grid options to support in place editing.
                 */
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

                /**
                 * @ngdoc method
                 * @name setInPlaceEditing
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Function} handler Callback function to to check condition if a row can be updated
                 *
                 * @description
                 * Register an handler function when grid API is ready.
                 */
                , addGridApiHandler: function (scope, handler) {
                    scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                        handler(gridApi);
                    });
                }

                /**
                 * @ngdoc method
                 * @name setColumnDefs
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} config Configuration data
                 *
                 * @description
                 * Define ui-grid columns
                 */
                , setColumnDefs: function (scope, config) {
                    scope.gridOptions = scope.gridOptions || {};
                    scope.gridOptions.columnDefs = config.columnDefs;
                }

                /**
                 * @ngdoc method
                 * @name showObject
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {String} objType ArkCase Object type
                 * @param {String} objId ArkCase Object ID
                 *
                 * @description
                 * Go to a page that show the specified ArkCase Object (Case, Complaint, Document, etc.)
                 */
                , showObject: function (scope, objType, objId) {
                    var cacheObjectTypes = new Store.SessionData(Helper.SessionCacheNames.OBJECT_TYPES);
                    var objectTypes = cacheObjectTypes.get();
                    var promiseObjectTypes = Util.serviceCall({
                        service: LookupService.getObjectTypes
                        , result: cacheObjectTypes
                        , onSuccess: function (data) {
                            if (Validator.validateObjectTypes(data)) {
                                objectTypes = [];
                                _.forEach(data, function (item) {
                                    objectTypes.push(item);
                                });
                                cacheObjectTypes.set(objectTypes);
                                return objectTypes;
                            }
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

                /**
                 * @ngdoc method
                 * @name getUsers
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 *
                 * @description
                 * Get list of user full names
                 */
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

                /**
                 * @ngdoc method
                 * @name setUserNameFilter
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} promiseUsers Promise of acquiring list of user full names
                 *
                 * @description
                 * Set 'mapKeyValue' filter of user full name column when user full names are ready
                 */
                , setUserNameFilter: function (scope, promiseUsers) {
                    $q.all([promiseUsers]).then(function (data) {
                        for (var i = 0; i < scope.config.columnDefs.length; i++) {
                            if (Helper.Lookups.USER_FULL_NAMES == scope.config.columnDefs[i].lookup) {
                                scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";
                            }
                        }
                    });
                }

                /**
                 * @ngdoc method
                 * @name withPagingParams
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} arg Map with paging related arguments
                 * @param {Object} arg.sort Sort argument
                 * @param {Object} arg.sort.by Sort field
                 * @param {Object} arg.sort.dir Sort direction, either 'asc' or 'desc'
                 * @param {Object} arg.startWith Records where page starts
                 * @param {Object} arg.count Number of records in a page (page size)
                 *
                 * @description
                 * Create paging arguments
                 */
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

                /**
                 * @ngdoc method
                 * @name withPagingParams
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} columnDefs ui-grid column definition
                 * @param {Function} onClickDelete Callback function to response to button click event
                 *
                 * @description
                 * Create a new column with delete button
                 */
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

                /**
                 * @ngdoc method
                 * @name withPagingParams
                 * @methodOf HelperService.Grid
                 *
                 * @param {Object} scope Angular scope
                 * @param {Object} rowEntity Row entity object of ui-grid representing a row
                 *
                 * @description
                 * Remove a row
                 */
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