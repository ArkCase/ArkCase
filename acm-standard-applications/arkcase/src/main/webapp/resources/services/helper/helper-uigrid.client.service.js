'use strict';

/**
 * @ngdoc service
 * @name services:Helper.UiGridService
 *
 * @description
 *
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/helper/helper-uigrid.client.service.js services/helper/helper-uigrid.client.service.js}

 * Helper.UiGridService has functions for typical usage in ArCase of 'ui-grid' directive
 */
angular.module('services').factory(
        'Helper.UiGridService',
        [ '$resource', '$q', '$translate', 'UtilService', 'LookupService', 'ApplicationConfigService', 'Object.LookupService', 'ObjectService', 'uiGridConstants', 'Object.AuditService',
                function($resource, $q, $translate, Util, LookupService, ApplicationConfigService, ObjectLookupService, ObjectService, uiGridConstants, ObjectAuditService) {
                    var Service = {
                        Lookups: {
                            USER_FULL_NAMES: "userFullNames",
                            PERSON_TYPES: "personTypes",
                            PARTICIPANT_TYPES: "participantTypes",
                            PARTICIPANT_NAMES: "participantNames",
                            TASK_OUTCOMES: "taskOutcomes",
                            CONTACT_METHODS_TYPES: "contactMethodTypes",
                            ORGANIZATION_TYPES: "organizationTypes",
                            ADDRESS_TYPES: "addressTypes",
                            ALIAS_TYPES: "aliasTypes",
                            SECURITY_TAG_TYPES: "securityTagTypes"
                        },
                        CommonButtons: [ {
                            "name": "edit",
                            "clickFn": "editRow",
                            "icon": "fa fa-pencil",
                            "readOnlyFn": "isReadOnly"
                        }, {
                            "name": "delete",
                            "clickFn": "deleteRow",
                            "icon": "fa fa-trash-o",
                            "readOnlyFn": "isReadOnly"
                        }, {
                            "name": "copy",
                            "clickFn": "copyRow",
                            "icon": "fa fa-copy",
                            "readOnlyFn": "isReadOnly"
                        } ]

                        /**
                         * @ngdoc method
                         * @name Grid Constructor
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} arg Map arguments
                         * @param {Object} arg.scope Angular $scope
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
                        ,
                        Grid: function(arg) {
                            this.scope = arg.scope;
                            this.scope.utils = this.scope.$root.utils;
                            this.scope.gridOptions = this.scope.gridOptions || {};

                            if (!this.scope.isReadOnly) {
                                this.scope.isReadOnly = function(objectInfo) {
                                    return false;
                                }
                            }

                            // The onRegisterApi handler must be defined immediately,
                            // otherwise angular will never call it and gridApi will never be defined
                            var that = this;
                            var dfd = $q.defer();
                            this.scope.gridOptions.promiseRegisterApi = dfd.promise;
                            this.scope.gridOptions.onRegisterApi = function(gridApi) {
                                that.scope.gridApi = gridApi;
                                dfd.resolve(gridApi);
                            };
                        }
                    };

                    /**
                     * @ngdoc method
                     * @name filterRestricted
                     * @methodOf services:Helper.UiGridService
                     *
                     * @param {Object} solrObjects Fetched objects from SOLR
                     * @param {Object} objectAssociations Persons, Organizations from the associated object (CASE_FILE, CONSULTATION ect.)
                     *
                     * @description
                     * Filter the restricted persons and organizations
                     */
                    Service.filterRestricted = function (solrObjects, objectAssociations) {
                        var gridObjects = [];
                        _.filter(objectAssociations, function(objectAssoc) {
                            _.forEach(solrObjects, function(solrObject) {
                                if (objectAssoc.person && objectAssoc.person.id == solrObject.object_id_s) {
                                    gridObjects.push(objectAssoc);
                                }
                                if (objectAssoc.organization && objectAssoc.organization.organizationId == solrObject.object_id_s) {
                                    gridObjects.push(objectAssoc);
                                }
                            });
                        });
                        return gridObjects;
                    };

                    Service.Grid.prototype = {

                        /**
                         * @ngdoc method
                         * @name getGridOptions
                         * @methodOf services:Helper.UiGridService
                         *
                         *
                         * @description
                         * Get ui-grid gridOptions that were set previously.
                         */
                        getGridOptions: function() {
                            return this.scope.gridOptions;
                        },

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
                        setBasicOptions: function(config) {
                            var that = this;
                            //that.scope.gridOptions = that.scope.gridOptions || {};
                            that.scope.config = config;

                            that.scope.gridOptions.enableColumnResizing = true;
                            that.scope.gridOptions.enableRowSelection = config.enableRowSelection;

                            that.scope.gridOptions.showColumnFooter = config.showColumnFooter || false;
                            that.scope.gridOptions.showGridFooter = config.showGridFooter || false;

                            that.scope.gridOptions.enableRowHeaderSelection = config.enableRowHeaderSelection;
                            that.scope.gridOptions.enableFullRowSelection = config.enableFullRowSelection;
                            that.scope.gridOptions.multiSelect = config.multiSelect;
                            that.scope.gridOptions.noUnselect = config.noUnselect;

                            that.scope.gridOptions.paginationPageSizes = config.paginationPageSizes || [ 250, 500, 1000 ];
                            that.scope.gridOptions.paginationPageSize = config.paginationPageSize || that.scope.gridOptions.paginationPageSizes[0];
                            that.scope.gridOptions.enableFiltering = config.enableFiltering;
                            that.scope.gridOptions.enableSorting = config.enableSorting;
                        }

                        /**
                         * @ngdoc method
                         * @name disableGridScrolling
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} config Component configuration data with grid options
                         *
                         * @description
                         * Disabling vertical and horizontal scrolling to ui-grid options.
                         */
                        ,
                        disableGridScrolling: function(config) {
                            var that = this;
                            that.scope.config = config;
                            that.scope.gridOptions.enableHorizontalScrollbar = uiGridConstants.scrollbars.NEVER;
                            that.scope.gridOptions.enableVerticalScrollbar = uiGridConstants.scrollbars.NEVER;
                        }

                        /**
                         * @ngdoc method
                         * @name setExternalPaging
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} config Component configuration data with grid options
                         * @param {Function} retrieveGridData Callback function to call when need to retrieve data for ui-grid
                         *
                         * @description
                         * Set ui-grid options to support external paging.
                         */
                        ,
                        setExternalPaging: function(config, retrieveGridData) {
                            var that = this;
                            //scope.currentId = $stateParams.id;
                            that.scope.start = 0;
                            that.scope.pageSize = config.paginationPageSize || 10;
                            that.scope.sort = {
                                by: "",
                                dir: "asc"
                            };
                            that.scope.filters = [];

                            that.scope.gridOptions.useExternalPagination = true;
                            that.scope.gridOptions.useExternalSorting = true;

                            //comment out filtering until service side supports it
                            //that.scope.gridOptions.enableFiltering = config.enableFiltering;
                            that.scope.gridOptions.enableFiltering = false;
                            that.scope.gridOptions.useExternalFiltering = true;

                            that.scope.gridOptions.promiseRegisterApi.then(function(gridApi) {
                                that.scope.gridApi.core.on.sortChanged(that.scope, function(grid, sortColumns) {
                                    if (0 >= sortColumns.length) {
                                        that.scope.sort.by = null;
                                        that.scope.sort.dir = null;
                                    } else {
                                        if (sortColumns[0].colDef.sortField) {
                                            that.scope.sort.by = sortColumns[0].colDef.sortField;
                                        } else
                                            that.scope.sort.by = sortColumns[0].field;

                                        that.scope.sort.dir = sortColumns[0].sort.direction;
                                    }
                                    retrieveGridData();
                                });
                                that.scope.gridApi.core.on.filterChanged(that.scope, function() {
                                    var grid = this.grid;
                                    that.scope.filters = [];
                                    for (var i = 0; i < grid.columns.length; i++) {
                                        if (!_.isEmpty(grid.columns[i].filters[0].term)) {
                                            var filter = {};
                                            filter.by = grid.columns[i].field;
                                            filter['with'] = grid.columns[i].filters[0].term;
                                            that.scope.filters.push(filter);
                                        }
                                    }
                                    retrieveGridData();
                                });
                                that.scope.gridApi.pagination.on.paginationChanged(that.scope, function(newPage, pageSize) {
                                    that.scope.start = (newPage - 1) * pageSize; //newPage is 1-based index
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
                         * @param {Object} config Component configuration data with grid options
                         * @param {Function} updateRow Callback function to update a row of a ui-grid
                         * @param {Function} canUpdate Callback function to to check condition if a row can be updated
                         *
                         * @description
                         * Set ui-grid options to support in place editing.
                         */
                        ,
                        setInPlaceEditing: function(config, updateRow, canUpdate) {
                            var that = this;
                            that.scope.gridOptions.promiseRegisterApi.then(function(gridApi) {
                                gridApi.edit.on.afterCellEdit(that.scope, function(rowEntity, colDef, newValue, oldValue) {
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
                        ,
                        addGridApiHandler: function(handler) {
                            this.scope.gridOptions.promiseRegisterApi.then(function(gridApi) {
                                handler(gridApi);
                            });
                        }

                        /**
                         * @ngdoc method
                         * @name gotoLastPage
                         * @methodOf services:Helper.UiGridService
                         *
                         * @description
                         * Loads the last page of data into the ui-grid
                         */
                        ,
                        gotoLastPage: function() {
                            if (this.scope && this.scope.gridApi) {
                                var lastPage = this.scope.gridApi.pagination.getTotalPages();
                                this.scope.gridApi.pagination.seek(lastPage);
                            }
                        }

                        /**
                         * @ngdoc method
                         * @name setColumnDefs
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} config Component configuration data with grid options
                         *
                         * @description
                         * Define ui-grid columns
                         */
                        ,
                        setColumnDefs: function(config) {
                            this.scope.gridOptions = this.scope.gridOptions || {};
                            this.scope.gridOptions.columnDefs = config.columnDefs;
                        }

                        /**
                         * @ngdoc method
                         * @name setWidgetsGridData
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} widgetData Widgets data object with grid options
                         *
                         * @description
                         * Define ui-grid data
                         */
                        ,
                        setWidgetsGridData: function(widgetData) {
                            if (!Util.isArrayEmpty(widgetData)) {
                                this.scope.gridOptions.data = widgetData;
                                this.scope.gridOptions.noData = false;
                                this.scope.gridOptions.totalItems = widgetData.length;
                            } else {
                                this.scope.gridOptions.data = [];
                                this.scope.gridOptions.noData = true;
                                this.scope.gridOptions.totalItems = 0;
                            }
                        }

                        /**
                         * @ngdoc method
                         * @name getUsers
                         * @methodOf services:Helper.UiGridService
                         *
                         * @description
                         * Get list of user full names
                         */
                        ,
                        getUsers: function() {
                            var that = this;
                            return LookupService.getUserFullNames().then(function(userFullNames) {
                                that.scope.userFullNames = userFullNames;
                                return userFullNames;
                            });
                        }

                        /**
                         * @ngdoc method
                         * @name setUserNameFilterToConfig
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} promiseUsers Promise of acquiring list of user full names
                         * @param {Object} config Configuration holding column definitions
                         *
                         * @description
                         * Set 'mapKeyValue' filter of user full name column when user full names are ready for a specific grid configuration
                         */
                        ,
                        setUserNameFilterToConfig: function(promiseUsers, config) {
                            var that = this;
                            var config = (config === undefined) ? that.scope.config : config;

                            var deferred = $q.defer();

                            $q.all([ ApplicationConfigService.getProperty(ApplicationConfigService.PROPERTIES.DISPLAY_USERNAME), promiseUsers ]).then(function(data) {
                                var userNamePop = data[0];

                                if (userNamePop == "userName" && _.get(config, 'columnDefs')) {
                                    for (var i = 0; i < config.columnDefs.length; i++) {
                                        if (Service.Lookups.USER_FULL_NAMES == config.columnDefs[i].lookup || Service.Lookups.PARTICIPANT_NAMES == config.columnDefs[i].lookup) {
                                            var tempColumn = angular.copy(config.columnDefs[i]);
                                            tempColumn.cellFilter = "mapKeyValue: grid.appScope.userFullNames:'id':'name'";
                                            config.columnDefs.splice(i, 1, tempColumn);
                                        }
                                    }
                                }

                                deferred.resolve(config);
                            });
                            return deferred.promise;
                        }

                        /**
                         * @ngdoc method
                         * @name showUserFullNames
                         * @methodOf services:Helper.UiGridService
                         *
                         * @description
                         * Replace user id with user full name.
                         */
                        ,
                        showUserFullNames: function() {
                            var that = this;
                            $q.all([ ApplicationConfigService.getProperty(ApplicationConfigService.PROPERTIES.DISPLAY_USERNAME) ]).then(function(result) {
                                var userNamePop = result[0];

                                if (userNamePop == "userName" && _.get(that, 'scope.config.columnDefs')) {
                                    for (var i = 0; i < that.scope.config.columnDefs.length; i++) {
                                        if (that.scope.config.columnDefs[i].hasOwnProperty('fullNameField')) {
                                            var tempColumn = angular.copy(that.scope.config.columnDefs[i]);
                                            tempColumn.field = tempColumn.fullNameField;
                                            that.scope.config.columnDefs.splice(i, 1, tempColumn);
                                        }
                                    }
                                }
                            });
                        }

                        /**
                         * @ngdoc method
                         * @name setLookupDropDown
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {String} lookupName Lookup name, used in ui-grid column def to identify a lookup a column is use
                         * @param {String} lookupKeyName Lookup key name
                         * @param {String} lookupValueName Lookup value name
                         * @param {Array} lookupArray Lookup array of pairs of (key, value)
                         *
                         * @description
                         * Set up dropdown options for a column identified by a 'lookupName'. A 'lookupName' is defined in columndDefs
                         * of a ui-grid. It has to be unique to the lookup. 'lookupArray' is an array contains key-value pairs.
                         * The names of key and value are part of parameters of this function. The lookup data must be same for
                         * all rows.
                         */
                        ,
                        setLookupDropDown: function(lookupName, lookupKeyName, lookupValueName, lookupArray) {
                            var that = this;
                            that.scope.gridOptions.enableRowSelection = false; //need to turn off for inline edit
                            for (var i = 0; i < that.scope.config.columnDefs.length; i++) {
                                if (lookupName == that.scope.config.columnDefs[i].lookup) {
                                    that.scope.gridOptions.columnDefs[i].enableCellEdit = true;
                                    that.scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                    that.scope.gridOptions.columnDefs[i].editDropdownIdLabel = lookupKeyName;
                                    that.scope.gridOptions.columnDefs[i].editDropdownValueLabel = lookupValueName;
                                    that.scope.gridOptions.columnDefs[i].editDropdownOptionsArray = lookupArray;
                                    that.scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: col.colDef.editDropdownOptionsArray:'" + lookupKeyName + "':'" + lookupValueName + "'";

                                }
                            }
                        }

                        /**
                         * @ngdoc method
                         * @name setRowLookupDropDown
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {String} lookupName Lookup name, used in ui-grid column def to identify a lookup a column is use
                         * @param {String} lookupKeyName Lookup key name
                         * @param {String} lookupValueName Lookup value name
                         * @param {Array} lookupArrayPath Lookup array of pairs of (key, value)
                         *
                         * @description
                         * Set up dropdown options for a column identified by a 'lookupName'. A 'lookupName' is defined in columndDefs
                         * of a ui-grid. It has to be unique to the lookup. Each row may have different lookup data set.
                         * A property of the row points to the lookup the row is using. The property name is passed as a parameter
                         * 'lookupArraypath'.
                         *
                         */
                        ,
                        setRowLookupDropDown: function(lookupName, lookupKeyName, lookupValueName, lookupArrayPath) {
                            var that = this;
                            that.scope.gridOptions.enableRowSelection = false; //need to turn off for inline edit
                            for (var i = 0; i < that.scope.config.columnDefs.length; i++) {
                                if (lookupName == that.scope.config.columnDefs[i].lookup) {
                                    that.scope.gridOptions.columnDefs[i].enableCellEdit = true;
                                    that.scope.gridOptions.columnDefs[i].editableCellTemplate = "ui-grid/dropdownEditor";
                                    that.scope.gridOptions.columnDefs[i].editDropdownIdLabel = lookupKeyName;
                                    that.scope.gridOptions.columnDefs[i].editDropdownValueLabel = lookupValueName;
                                    that.scope.gridOptions.columnDefs[i].editDropdownRowEntityOptionsArrayPath = lookupArrayPath;
                                    that.scope.gridOptions.columnDefs[i].cellFilter = "mapKeyValue: row.entity." + lookupArrayPath + ":'" + lookupKeyName + "':'" + lookupValueName + "'";
                                }
                            }
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
                        ,
                        withPagingParams: function(arg) {
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
                         * @param {String} objTypeKey Lookup key for an ArkCase Object type. Typically, lookup key is of format
                         * "OBJECT_TYPE" or "OBJECT_TYPE.SUB_TYPE"
                         * @param {String} objId ArkCase Object ID
                         * @param {Boolean} newTab Optional flag for opening the Object in a new tab
                         *
                         * @description
                         * Go to a page state that show the specified ArkCase Object (Case, Complaint, Document, etc.)
                         */
                        ,
                        showObject: function (objTypeKey, objId, newTab) {
                            return ObjectService.showObject(objTypeKey, objId, newTab);

                        }

                        /**
                         * @ngdoc method
                         * @name transitionToState
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {String} objTypeKey Lookup key for an ArkCase Object type. Typically, lookup key is of format
                         * "OBJECT_TYPE" or "OBJECT_TYPE.SUB_TYPE"
                         * @param {String} objId ArkCase Object ID
                         * @param {String} state Angular route state
                         *
                         * @description
                         * Go to a page state that show the specified ArkCase Object (Case, Complaint, Document, etc.)
                         */
                        ,
                        transitionToState: function(objType, objId, state) {
                            return ObjectService.transitionToState(objType, objId, state);
                        }

                        /**
                         * @ngdoc method
                         * @name openObject
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {String} parentType, Lookup parent Type of the file.
                         * @param {String} fileName Lookup name.
                         * @param {Number} targetId, target id of the file.
                         * @param {Number} parentId,  parent id of the file.
                         *
                         * @description
                         * Go to a page state that show the specified ArkCase File viewer.
                         */
                        ,
                        openObject: function(targetId, parentId, parentType, fileName) {
                            return ObjectService.openObject(targetId, parentId, parentType, fileName);
                        }

                        /**
                         * @ngdoc method
                         * @name addEditButton
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {Object} columnDefs ui-grid column definition
                         * @param {Function} onClickEdit Callback function to response to button click event
                         *
                         * @description
                         * Create a new column with edit button
                         */
                        ,
                        addEditButton: function(columnDefs, onClickEdit) {
                            console.log("Warning: HelperUiGridService.Grid.addEditButton() is outdated. Please use addButton() instead");

                            var columnDef = {
                                name: "edit",
                                cellEditableCondition: false,
                                width: 40,
                                headerCellTemplate: "<span></span>",
                                cellTemplate: "<span><i class='fa fa-pencil fa-lg' style='cursor :pointer' ng-hide='grid.appScope.isReadOnly(row.entity)' ng-click='" + onClickEdit + "'></i></span>"
                            };
                            columnDefs.push(columnDef);
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
                        ,
                        addDeleteButton: function(columnDefs, onClickDelete) {
                            console.log("Warning: HelperUiGridService.Grid.addDeleteButton() is outdated. Please use addButton() instead");

                            var columnDef = {
                                name: "act",
                                cellEditableCondition: false
                                //, enableFiltering: false
                                //, enableHiding: false
                                //, enableSorting: false
                                //, enableColumnResizing: false
                                ,
                                width: 40,
                                headerCellTemplate: "<span></span>",
                                cellTemplate: "<span title=\"{{'admin.security.ldapConfig.table.deleteRecord' | translate}}\">" + "<i class='fa fa-trash-o fa-lg' style='cursor :pointer' ng-hide='grid.appScope.isReadOnly(row.entity)' ng-click='" + onClickDelete + "'></i></span>"
                            };
                            columnDefs.push(columnDef);
                        }

                        /**
                         * @ngdoc method
                         * @name addButton
                         * @methodOf services:Helper.UiGridService
                         *
                         * @description
                         * Add a button to a column.
                         *
                         * @param {Object} config Component configuration data with grid options.
                         * @param {String} name Button name, serve as unique key in the button group.
                         * @param {String} icon (Optional) Font awesome icon.
                         *     If not provided, attempt is made to find in CommonButtons.
                         * @param {String} clickFn (Optional) Callback function name to button click event. The function must be define within app $scope.
                         *     If not provided, attempt is made to find in CommonButtons.
                         * @param {String} readOnlyFn (Optional) Read only function name to check if grid is read only.
                         *     If not provided, attempt is made to find in CommonButtons. If still not found, it is assume the button is read only and is thus
                         *     not subject to any read only function control (i.e., it is always regardless if grid is read only or not)
                         */
                        ,
                        addButton: function(config, name, icon, clickFn, readOnlyFn, title) {
                            if (Util.isEmpty(icon) || Util.isEmpty(clickFn) || Util.isEmpty(readOnlyFn)) {
                                var found = _.find(Service.CommonButtons, {
                                    name: name
                                });
                                if (found) {
                                    if (Util.isEmpty(icon)) {
                                        icon = found.icon;
                                    }
                                    if (Util.isEmpty(clickFn)) {
                                        clickFn = found.clickFn;
                                    }
                                    if (Util.isEmpty(readOnlyFn)) {
                                        readOnlyFn = found.readOnlyFn;
                                    }
                                }
                            }
                            //var cellTemplate = "<span><i class='" + icon //"fa fa-trash-o fa-lg"
                            //        + "' style='cursor :pointer'"
                            //        + " ng-click='grid.appScope." + clickFn + "(row.entity)'"
                            //    ;
                            //if (readOnlyFn) {
                            //    cellTemplate += " ng-hide='grid.appScope." + readOnlyFn + "(row.entity)'";
                            //}
                            //cellTemplate += "></i></span>";

                            var cellTemplate = title ? "<a title=" + title + " class='inline animated btn btn-default btn-xs'" + " ng-click='grid.appScope." + clickFn + "(row.entity)'" : "<a title='' class='inline animated btn btn-default btn-xs'" + " ng-click='grid.appScope." + clickFn + "(row.entity)'";
                            if (readOnlyFn) {
                                cellTemplate += " ng-hide='grid.appScope." + readOnlyFn + "(row.entity)'";
                            }
                            cellTemplate += "><i class='" + icon + "'></i></a>";

                            var columnDefs = Util.goodArray(config.columnDefs);
                            var columnDef = _.find(columnDefs, {
                                name: "act"
                            });
                            if (columnDef) {
                                columnDef.cellTemplate += cellTemplate;

                            } else {
                                columnDef = {
                                    name: "act",
                                    cellEditableCondition: false
                                    //, enableFiltering: false
                                    //, enableHiding: false
                                    //, enableSorting: false
                                    //, enableColumnResizing: false
                                    ,
                                    width: 65,
                                    headerCellTemplate: "<span></span>",
                                    cellTemplate: cellTemplate
                                };
                                columnDefs.push(columnDef);
                            }

                            return this;
                        }

                        /**
                         * @ngdoc method
                         * @name addConfigurableButton
                         * @methodOf services:Helper.UiGridService
                         *
                         * @description
                         * Add a button to a column using parameters from config.json
                         *
                         * @param {Object} config Component configuration data with grid options.
                         * @param {Object} button Specific button configuration. Note that the information in button is actually
                         *                  contained within config. Calls were structured this way for code readability
                         */

                        ,
                        addConfigurableButton: function(config, button) {
                            var icon, clickFn, readOnlyFn;
                            // if the config file was missing a button parameter, search the predefined buttons for the missing params
                            if (Util.isEmpty(button.icon) || Util.isEmpty(button.clickFn) || Util.isEmpty(button.readOnlyFn)) {
                                var found = _.find(Service.CommonButtons, {
                                    name: name
                                });
                                if (found) {
                                    if (Util.isEmpty(icon)) {
                                        icon = found.icon;
                                    }
                                    if (Util.isEmpty(clickFn)) {
                                        clickFn = found.clickFn;
                                    }
                                    if (Util.isEmpty(readOnlyFn)) {
                                        readOnlyFn = found.readOnlyFn;
                                    }
                                }
                            } else {
                                // all params were found in config
                                icon = button.icon;
                                clickFn = button.clickFn;
                                readOnlyFn = button.readOnlyFn;
                            }

                            var cellTemplate = "<a title='' class='inline animated btn btn-default btn-xs' ng-if='" + readOnlyFn + " == true'" + " ng-click='grid.appScope." + clickFn + "(row.entity)'";

                            cellTemplate += "><i class='" + icon + "'></i></a>";

                            var columnDefs = Util.goodArray(config.columnDefs);
                            var columnDef = _.find(columnDefs, {
                                name: "act"
                            });
                            if (columnDef) {
                                columnDef.cellTemplate += cellTemplate;

                            } else {
                                columnDef = {
                                    name: "act",
                                    cellEditableCondition: false
                                    //, enableFiltering: false
                                    //, enableHiding: false
                                    //, enableSorting: false
                                    //, enableColumnResizing: false
                                    ,
                                    width: 50,
                                    headerCellTemplate: "<span></span>",
                                    cellTemplate: cellTemplate
                                };
                                columnDefs.push(columnDef);
                            }

                            return this;
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
                        ,
                        deleteRow: function(rowEntity) {
                            var that = this;
                            var idx = _.findIndex(that.scope.gridOptions.data, function(obj) {
                                return (obj == rowEntity);
                            });
                            if (0 <= idx) {
                                that.scope.gridOptions.data.splice(idx, 1);
                            }
                        }

                        /**
                         * @ngdoc method
                         * @name retrieveAuditData
                         * @methodOf services:Helper.UiGridService
                         *
                         * @param {String} objectType query audit for given object type
                         *
                         * @description
                         * Retrieves audit data for current grid context (objectType, objectId)
                         */
                        ,
                        retrieveAuditData: function(objectType, objectId) {
                            var that = this;
                            if (Util.goodPositive(objectId, false)) {
                                var promiseQueryAudit = ObjectAuditService.queryAudit(objectType, objectId, Util.goodValue(this.scope.start, 0), Util.goodValue(this.scope.pageSize, 10), Util.goodMapValue(this.scope.sort, "by"), Util.goodMapValue(this.scope.sort, "dir"));

                                $q.all([ promiseQueryAudit ]).then(function(data) {
                                    var auditData = data[0];

                                    that.scope.gridOptions = that.scope.gridOptions || {};
                                    that.scope.gridOptions.data = auditData.resultPage;
                                    that.scope.gridOptions.totalItems = auditData.totalCount;
                                });
                            }
                            // subscribe for update, reload data
                            var eventName = "object.changed/" + objectType + "/" + objectId;
                            var subscription = this.scope.subscription;
                            if (subscription) {
                                this.scope.$bus.unsubscribe(subscription);
                            }
                            this.scope.subscription = this.scope.$bus.subscribe(eventName, function(data) {
                                that.retrieveAuditData(objectType, objectId);
                            });
                        }

                    };

                    return Service;
                } ]);