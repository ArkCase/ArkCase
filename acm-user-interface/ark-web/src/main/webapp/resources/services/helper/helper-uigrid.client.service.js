'use strict';

/**
 * @ngdoc service
 * @name services:Helper.UiGridService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/helper/helper-uigrid.client.service.js services/helper/helper-uigrid.client.service.js}

 * Helper.UiGridService has functions for typical usage in ArCase of 'ui-grid' directive
 */
angular.module('services').factory('Helper.UiGridService', ['$resource', '$q', '$translate'
    , 'UtilService', 'LookupService', 'Object.LookupService'
    , function ($resource, $q, $translate, Util, LookupService, ObjectLookupService) {
        var Service = {
            Lookups: {
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

            /**
             * @ngdoc method
             * @name Grid Constructor
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} arg Map arguments
             * @param {Object} arg.scope Angular $scope
             * @param {Function} arg.makeGridNode Function to make tree node from object data
             *
             * @description
             * Helper.UiGridService.Grid is to help a typical usage of 'ui-grid' directive.
             * Assumption: 'ui-grid' directive must set 'gridOptions' with data model $scope.gridOptions and
             * following data may also be used:
             *   $scope.gridApi
             *   $scope.start
             *   $scope.pageSize
             *   $scope.sort
             *   $scope.filters
             *   $scope.userFullNames
             */
            , Grid: function (arg) {
                this.scope = arg.scope;
            }
        };

        Service.Grid.prototype = {

            /**
             * @ngdoc method
             * @name setBasicOptions
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} config Component configuration data with grid options
             *
             * @description
             * Set some typical ui-grid options.
             */
            setBasicOptions: function (config) {
                var that = this;
                that.scope.gridOptions = that.scope.gridOptions || {};
                that.scope.config = config;

                that.scope.gridOptions.enableColumnResizing = true;
                that.scope.gridOptions.enableRowSelection = false;
                that.scope.gridOptions.enableRowHeaderSelection = false;
                that.scope.gridOptions.multiSelect = false;
                that.scope.gridOptions.noUnselect = false;

                that.scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                that.scope.gridOptions.paginationPageSize = config.paginationPageSize;
                that.scope.gridOptions.enableFiltering = config.enableFiltering;
                that.scope.gridOptions.enableSorting = config.enableSorting;

                var dfd = $q.defer();
                that.scope.gridOptions.promiseRegisterApi = dfd.promise;
                that.scope.gridOptions.onRegisterApi = function (gridApi) {
                    that.scope.gridApi = gridApi;
                    dfd.resolve(gridApi);
                };
                return dfd.promise;
            }

            /**
             * @ngdoc method
             * @name setExternalPaging
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} Component configuration data with grid options
             * @param {Function} retrieveGridData Callback function to call when need to retrieve data for ui-grid
             *
             * @description
             * Set ui-grid options to support external paging.
             */
            , setExternalPaging: function (config, retrieveGridData) {
                var that = this;
                //scope.currentId = $stateParams.id;
                that.scope.start = 0;
                that.scope.pageSize = config.paginationPageSize || 10;
                that.scope.sort = {by: "", dir: "asc"};
                that.scope.filters = [];

                that.scope.gridOptions.useExternalPagination = true;
                that.scope.gridOptions.useExternalSorting = true;

                //comment out filtering until service side supports it
                //that.scope.gridOptions.enableFiltering = config.enableFiltering;
                that.scope.gridOptions.enableFiltering = false;
                //that.scope.gridOptions.useExternalFiltering = true;


                that.scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                    that.scope.gridApi.core.on.sortChanged(that.scope, function (grid, sortColumns) {
                        if (0 >= sortColumns.length) {
                            that.scope.sort.by = null;
                            that.scope.sort.dir = null;
                        } else {
                            that.scope.sort.by = sortColumns[0].field;
                            that.scope.sort.dir = sortColumns[0].sort.direction;
                        }
                        retrieveGridData();
                    });
                    that.scope.gridApi.core.on.filterChanged(that.scope, function () {
                        var grid = this.grid;
                        that.scope.filters = [];
                        for (var i = 0; i < grid.columns.length; i++) {
                            if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                var filter = {};
                                filter.by = grid.columns[i].field;
                                filter.with = grid.columns[i].filters[0].term;
                                that.scope.filters.push(filter);
                            }
                        }
                        retrieveGridData();
                    });
                    that.scope.gridApi.pagination.on.paginationChanged(that.scope, function (newPage, pageSize) {
                        that.scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                        that.scope.pageSize = pageSize;
                        retrieveGridData();
                    });
                });
            }

            /**
             * @ngdoc method
             * @name setInPlaceEditing
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} Component configuration data with grid options
             * @param {Function} updateRow Callback function to update a row of a ui-grid
             * @param {Function} canUpdate Callback function to to check condition if a row can be updated
             *
             * @description
             * Set ui-grid options to support in place editing.
             */
            , setInPlaceEditing: function (config, updateRow, canUpdate) {
                var that = this;
                that.scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                    gridApi.edit.on.afterCellEdit(that.scope, function (rowEntity, colDef, newValue, oldValue) {
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
             * @methodOf services:Helper.UiGridService
             *
             * @param {Function} handler Callback function to to check condition if a row can be updated
             *
             * @description
             * Register an handler function when grid API is ready.
             */
            , addGridApiHandler: function (handler) {
                this.scope.gridOptions.promiseRegisterApi.then(function (gridApi) {
                    handler(gridApi);
                });
            }

            /**
             * @ngdoc method
             * @name setColumnDefs
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} Component configuration data with grid options
             *
             * @description
             * Define ui-grid columns
             */
            , setColumnDefs: function (config) {
                this.scope.gridOptions = this.scope.gridOptions || {};
                this.scope.gridOptions.columnDefs = config.columnDefs;
            }

            /**
             * @ngdoc method
             * @name getUsers
             * @methodOf services:Helper.UiGridService
             *
             * @description
             * Get list of user full names
             */
            , getUsers: function () {
                var that = this;
                return LookupService.getUserFullNames().then(function (userFullNames) {
                    that.scope.userFullNames = userFullNames;
                    return userFullNames;
                });
            }

            /**
             * @ngdoc method
             * @name setUserNameFilter
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} promiseUsers Promise of acquiring list of user full names
             *
             * @description
             * Set 'mapKeyValue' filter of user full name column when user full names are ready
             */
            , setUserNameFilter: function (promiseUsers) {
                var that = this;
                $q.all([promiseUsers]).then(function (data) {
                    for (var i = 0; i < that.scope.config.columnDefs.length; i++) {
                        if (Service.Lookups.USER_FULL_NAMES == that.scope.config.columnDefs[i].lookup) {
                            that.scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name withPagingParams
             * @methodOf services:Helper.UiGridService
             *
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
            , withPagingParams: function (arg) {
                var that = this;
                var sort = "";
                if (that.scope.sort) {
                    if (!Util.isEmpty(that.scope.sort.by) && !Util.isEmpty(that.scope.sort.dir)) {
                        sort = that.scope.sort.by + " " + that.scope.sort.dir;
                    }
                }
                //implement filtering here when service side supports it
                //var filter = "";
                ////$that.scope.filters = [{by: "eventDate", with: "term"}];

                arg.startWith = that.scope.start;
                arg.count = that.scope.pageSize;
                arg.sort = sort;

                return arg;
            }

            /**
             * @ngdoc method
             * @name showObject
             * @methodOf services:Helper.UiGridService
             *
             * @param {String} objType ArkCase Object type
             * @param {String} objId ArkCase Object ID
             *
             * @description
             * Go to a page that show the specified ArkCase Object (Case, Complaint, Document, etc.)
             */
            , showObject: function (objType, objId) {
                var promiseObjectTypes = ObjectLookupService.getObjectTypes().then(
                    function (objectTypes) {
                        var found = _.find(objectTypes, {type: objType});
                        if (found) {
                            var url = Util.goodValue(found.url);
                            url = url.replace(":id", objId);
                            $window.location.href = url;
                        }
                        return objectTypes;
                    }
                );
            }

            /**
             * @ngdoc method
             * @name addDeleteButton
             * @methodOf services:Helper.UiGridService
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
                    , cellEditableCondition: false
                    //, enableFiltering: false
                    //, enableHiding: false
                    //, enableSorting: false
                    //, enableColumnResizing: false
                    , width: 40
                    , headerCellTemplate: "<span></span>"
                    , cellTemplate: "<span><i class='fa fa-trash-o fa-lg' ng-click='" + onClickDelete + "'></i></span>"
                };
                columnDefs.push(columnDef);
            }

            /**
             * @ngdoc method
             * @name deleteRow
             * @methodOf services:Helper.UiGridService
             *
             * @param {Object} rowEntity Row entity object of ui-grid representing a row
             *
             * @description
             * Remove a row
             */
            , deleteRow: function (rowEntity) {
                var that = this;
                var idx = _.findIndex(that.scope.gridOptions.data, function (obj) {
                    return (obj == rowEntity);
                });
                if (0 <= idx) {
                    that.scope.gridOptions.data.splice(idx, 1);
                }
            }

            /**
             * @ngdoc method
             * @name hidePagingControlsIfAllDataShown
             * @methodOf services:Helper.UiGridService
             *
             * @param {Number} totalCount Total number of grid rows
             *
             * @description
             * Hide paging controls of Angular ui-grid if all data has already shown
             */
            , hidePagingControlsIfAllDataShown: function (totalCount) {
                var that = this;
                if (that.scope && that.scope.gridOptions && that.scope.gridOptions.paginationPageSize) {
                    if (totalCount <= that.scope.gridOptions.paginationPageSize) {
                        // Hides pagination controls since there is only 1 page of data
                        that.scope.gridOptions.enablePaginationControls = false;
                    } else {
                        // need to re-enable pagination if a record is added to the next page
                        that.scope.gridOptions.enablePaginationControls = true;
                    }
                }
            }

        };

        return Service;
    }
]);
