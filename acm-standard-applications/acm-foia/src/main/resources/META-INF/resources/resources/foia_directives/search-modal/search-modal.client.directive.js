'use strict';
/**
 * @ngdoc directive
 * @name global.directive:searchModal
 * @restrict E
 *
 * @description
 *
 *{@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/search-modal/search-modal.client.directive.js directives/search-modal/search-modal.client.directive.js}
 *
 * The "Search" modal with faceted search functionality
 *
 * @param {boolean} multiSelect - multiple rows selection enabled. This parameter is phased out. Specify multiSelect in config file
 * @param {String} header - (Optional)label for the header of the modal box. If not specified, default value is used
 * @param {String} search - (Optional)label for the search button. If not specified, default value is used
 * @param {String} cancel - (Optional)label for the cancel button. If not specified, default value is used
 * @param {String} ok - (Optional)label for the add button. If not specified, default value is used
 * @param {String} searchPlaceholder - (Optional)label for the input placeholder. If not specified, default value is used
 * @param {Object} filter - filter required to send to the faceted search by default (e.g. for client : "\"Object Sub Type\":CLIENT")
 * @param {Object} extraFilter - (Optional) extra filter to send to the faceted search if search against the "name" property
 * @param {Object} searchQuery - (Optional) Used in the scenario where the search string is pre-populated or determined before the Modal opens
 * @param {Object} findGroups - (Optional) Used in the scenario where the solr search for for the Owning Group(s) a user is a member of
 * @param {String} defaultFilter  - (Optional) Used to  retrieve data by default.
 * @param {Boolean} disableSearch  - (Optional) Used to disable search controls (input, filter and search button).
 * @param {Object} config - config of the parent scope used mostly for the UI-grid and to retrieve other params
 * @param {Object} modalInstance - (Optional)current modalInstance in the parentScope, required to pass data when modal closes with "Add".
 * If not specified, this directive only show the content part of the dialog. Header and footer are not shown. And user has to handle the
 * dialog closing logic.
 * @param {Object} search-control - (Optional)Search dialog API for caller:
 * @param {Function} search-control.getSelectedItems returns list of selected search items
 * @param {Function} on-items-selected Callback function in response to selected items in search result.
 * @param {boolean} draggable - whether or not modal dialog be draggable or not
 * If response contains only 1 item, then display it.
 **/

angular.module('directives').directive('searchModal',
        [ '$q', '$translate', '$filter', 'UtilService', 'SearchService', 'Search.QueryBuilderService', '$injector', 'Person.InfoService', 'Object.LookupService', 'Helper.UiGridService', 'ObjectService', function($q, $translate, $filter, Util, SearchService, SearchQueryBuilder, $injector, PersonInfoService, ObjectLookupService, HelperUiGridService, ObjectService) {
            return {
                restrict: 'E', //match only element name
                scope: {
                    //multiSelect: '@',
                    header: '@', //@ : text binding (read-only and only strings)
                    search: '@',
                    cancel: '@',
                    ok: '@',
                    searchPlaceholder: '@',
                    filter: '@',
                    extraFilter: '@',
                    searchQuery: '@',
                    findGroups: '@',
                    defaultFilter: '@',
                    disableSearch: '@',
                    externalSearchServiceParams: '=',
                    externalSearchServiceName: '@',
                    externalSearchServiceMethod: '@',
                    config: '&', //& : one way binding (read-only, can return key, value pair via a getter function)
                    modalInstance: '=', //= : two way binding (read-write both, parent scope and directive's isolated scope have two way binding)
                    searchControl: '=?', //=? : two way binding but property is optional
                    onItemsSelected: '=?', //=? : two way binding but property is optional
                    onNoDataMessage: '@',
                    draggable: '@',
                    onDblClickRow: '=?',
                    customization: '=?',
                    secondGrid: '@',
                    pickUserLabel: '@',
                    pickGroupLabel: '@',
                    showSelectedItemsGrid: '=?',
                    secondSelectionOptional: '@',
                    params: '&',
                    dataBindTmp: '@?'
                },

                link: function(scope, el, attrs) {

                    //dom operations
                    if (scope.draggable) {
                        el.parent().draggable();
                    }
                    scope.header = Util.goodValue(scope.header, $translate.instant("common.directive.searchModal.header"));
                    scope.onNoDataMessage = Util.goodValue(scope.onNoDataMessage, $translate.instant("common.directive.searchModal.noData.text"));
                    scope.search = Util.goodValue(scope.search, $translate.instant("common.directive.searchModal.btnSearch.text"));
                    scope.ok = Util.goodValue(scope.ok, $translate.instant("common.directive.searchModal.btnOk.text"));
                    scope.pickUserLabel = Util.goodValue(scope.pickUserLabel, $translate.instant("common.directive.searchModal.pickUserLabel"));
                    scope.pickGroupLabel = Util.goodValue(scope.pickGroupLabel, $translate.instant("common.directive.searchModal.pickGroupLabel"));
                    scope.cancel = Util.goodValue(scope.cancel, $translate.instant("common.directive.searchModal.btnCancel.text"));
                    scope.searchPlaceholder = Util.goodValue(scope.searchPlaceholder, $translate.instant("common.directive.searchModal.edtPlaceholder"));
                    scope.showHeaderFooter = !Util.isEmpty(scope.modalInstance);
                    scope.disableSearchControls = (scope.disableSearch === 'true') ? true : false;
                    scope.findGroups = scope.findGroups === 'true';
                    scope.dataBindTmp = Util.isEmpty(scope.dataBindTmp);
                    scope.secondGrid = scope.secondGrid === 'true';
                    scope.secondSelectionOptional = scope.secondSelectionOptional === 'true';
                    if (scope.searchQuery) {
                        scope.searchQuery = scope.searchQuery;
                    } else {
                        scope.searchQuery = '';
                    }
                    if (scope.secondSelectionOptional) { //check if the second grid selection is not mandatory, then if is true set some property
                        scope.firstGridSelected = false;
                        scope.disableSearchButton = true;
                        scope.selectedDetailItem = null;
                        scope.userNotValid = false;
                        scope.groupNotValid = false;
                        scope.participant = {
                            assignee: scope.params().assignee,
                            owningGroup: scope.params().owningGroup
                        };
                    }

                    scope.minSearchLength = 3;
                    if (typeof (scope.config().showFacets) === 'undefined') {
                        scope.config.showFacets = true;
                    } else {
                        scope.config.showFacets = scope.config().showFacets;
                    }
                    //if (scope.multiSelect == undefined || scope.multiSelect == '') {
                    //    scope.multiSelect = 'false';
                    //}
                    scope.multiSelect = Util.goodValue(scope.config().multiSelect, false);
                    scope.searchControl = {
                        getSelectedItems: function() {
                            if (scope.multiSelect) {
                                return scope.selectedItems;
                            } else {
                                return scope.selectedItem;
                            }
                        }
                    };

                    scope.facets = [];
                    scope.currentFacetSelection = [];
                    scope.selectedItem = null;
                    scope.selectedItems = [];

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: scope
                    });

                    var filterInitialValue = scope.filter;
                    scope.queryExistingItems = function() {
                        if (!Util.isEmpty(scope.searchQuery)) {
                            var query = '';
                            if (scope.extraFilter) {
                                scope.filters = filterInitialValue + scope.extraFilter + '*' + scope.searchQuery + '*';
                            }

                            if (scope.findGroups) {
                                query = SearchQueryBuilder.buildSafeFqFacetedSearchQuerySorted('*', scope.filters, scope.pageSize, scope.start, scope.sort);
                            } else {
                                query = SearchQueryBuilder.buildSafeFqFacetedSearchQuerySorted(scope.searchQuery + '*', scope.filters, scope.pageSize, scope.start, scope.sort, !Util.isEmpty(scope.config().parentDocument));
                            }

                            if (query) {
                                scope.showNoData = false;
                                if (!Util.isEmpty(scope.externalSearchServiceName) && !Util.isEmpty(scope.externalSearchServiceParams) && !Util.isEmpty(scope.externalSearchServiceParams.organizationId)) {
                                    scope.externalSearchService = $injector.get(scope.externalSearchServiceName);
                                    angular.extend(scope.externalSearchServiceParams, {
                                        query: query
                                    });
                                    scope.externalSearchService[scope.externalSearchServiceMethod](scope.externalSearchServiceParams, successSearchResult);
                                } else {
                                    SearchService.queryFilteredSearch({
                                        query: query
                                    }, successSearchResult);
                                }
                            }
                        }
                    };

                    function successSearchResult(data) {
                        updateFacets(data.facet_counts.facet_fields);

                        var searchResults = data.response.docs;

                        //person has a different structure than user, in it 'email_lcs' property does not exists
                        //because this property is needed on more locations,
                        //here is a check if person is searched to add the email of the person in the 'email_lcs' property
                        checkForPersonInTheSearchResult(searchResults);

                        changeReferenceType(searchResults);

                        scope.gridOptionsMaster.data = searchResults;

                        if (scope.secondGrid) {
                            scope.gridOptionsDetail.data = [];
                            scope.gridOptionsDetail.totalItems = 0;
                        }
                        if (scope.gridOptionsMaster.data.length < 1) {
                            scope.showNoDataResult = true;
                        } else {
                            scope.showNoDataResult = false;
                        }
                        scope.gridOptionsMaster.totalItems = data.response.numFound;
                    }

                    function checkForPersonInTheSearchResult(searchResults) {
                        var people = [];
                        var i;
                        for (i in searchResults) {
                            if (searchResults[i].object_type_s.toUpperCase() == "PERSON") {
                                people.push(searchResults[i]);
                            }
                        }

                        if (!Util.isArrayEmpty(people)) {
                            var promises = [];
                            _.forEach(people, function(person) {
                                promises.push(PersonInfoService.getPersonInfo(person.object_id_s).then(function(personInfo) {
                                    return personInfo;
                                }));
                            });

                            $q.all(promises).then(function(personInfo) {
                                for (var i = 0; i < people.length; i++) {
                                    var contactMethods = personInfo[i].contactMethods;
                                    if (!Util.isArrayEmpty(contactMethods)) {
                                        var j;
                                        for (j in contactMethods) {
                                            if (contactMethods[j].type == "email") {
                                                people[i].email_lcs = contactMethods[j].value;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                    //foia changes start here
                    function changeReferenceType(searchResult) {
                        searchResult = searchResult.map(function(value) {
                            ObjectLookupService.getObjectTypes().then(function(item) {
                                var referenceType = _.find(item, {
                                    key: "CASE_FILE"
                                });
                                if (value.object_type_s == "CASE_FILE") {
                                    value.object_type_s = referenceType.description.toUpperCase();
                                }
                                return value;
                            });
                        });
                        return searchResult;
                    }
                    //end of foia changes

                    scope.setSecondGridData = function() {
                        if (scope.selectedItem.object_type_s === 'USER') { // Selected a user
                            scope.querySubItems('fq="object_type_s":GROUP%26fq="member_id_ss":' + scope.selectedItem.object_id_s);
                        } else if (scope.selectedItem.object_type_s === 'GROUP') { // Select group
                            scope.querySubItems('fq="object_type_s":USER%26fq="groups_id_ss":' + scope.selectedItem.object_id_s);
                        }
                    };

                    scope.querySubItems = function(filters) {
                        if (!Util.isEmpty(filters)) {
                            var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuerySorted('*', filters, scope.pageSizeSecond, scope.startSecond, scope.sortSecond);

                            if (query) {
                                scope.showNoData = false;

                                SearchService.queryFilteredSearch({
                                    query: query
                                }, successSubSearchResult);
                            }
                        }
                    };

                    function successSubSearchResult(data) {
                        updateFacets(data.facet_counts.facet_fields);
                        scope.gridOptionsDetail.data = data.response.docs;
                        if (scope.gridOptionsDetail.data.length < 1) {
                            scope.showNoData = true;
                        } else {
                            scope.showNoData = false;
                        }
                        scope.gridOptionsDetail.totalItems = data.response.numFound;
                    }

                    function updateFacets(facets) {
                        if (facets) {
                            if (scope.facets.length) {
                                scope.facets.splice(0, scope.facets.length)
                            }
                            _.forEach(facets, function(value, key) {
                                if (value) {
                                    scope.facets.push({
                                        'name': key,
                                        'fields': value
                                    });
                                }
                            });
                        }
                    }

                    scope.selectFacet = function(checked, facet, field) {
                        if (checked) {
                            if (scope.filters) {
                                scope.filters += '&fq="' + facet + '":' + field;
                            } else {
                                scope.filters = '';
                                scope.filters += 'fq="' + facet + '":' + field;
                            }
                            scope.queryExistingItems();
                        } else {
                            if (scope.filters.indexOf('&fq="' + facet + '":' + field) > -1) {
                                scope.filters = scope.filters.split('&fq="' + facet + '":' + field).join('');
                            } else if (scope.filters.indexOf('fq="' + facet + '":' + field) > -1) {
                                scope.filters = '';
                            }
                            scope.queryExistingItems();
                        }
                    };

                    scope.keyUp = function(event) {
                        // Remove wildcard
                        scope.searchQuery = scope.searchQuery.replace('*', '');
                        if (event.keyCode == 13 && scope.searchQuery.length >= scope.minSearchLength) {
                            scope.queryExistingItems();
                        }
                    };

                    scope.onClickOk = function() {
                        //when the modal is closed, the parent scope gets
                        //the selectedItem via the two-way binding
                        if (scope.showSelectedItemsGrid) {
                            scope.modalInstance.close(scope.gridSelectedItems.data);
                        } else if (scope.secondGrid) {
                            var result = {
                                masterSelectedItem: scope.selectedItem,
                                detailSelectedItems: scope.selectedDetailItem
                            };
                            scope.modalInstance.close(result);
                        } else {
                            if (scope.multiSelect) {
                                scope.modalInstance.close(scope.selectedItems);
                            } else {

                                scope.modalInstance.close(scope.selectedItem);
                            }
                        }
                    };

                    scope.onClickCancel = function() {
                        scope.modalInstance.dismiss('cancel')
                    };

                    /**
                     * @ngdoc method
                     * @name onClickObjLink
                     * @methodOf global.directive:searchModal
                     *
                     * @param {String} event
                     * @param {String} rowEntity data
                     * @param {Boolean} keepModal Optional flag for keeping open modal active
                     * @param {Boolean} newTab Optional flag for opening the Object in a new tab
                     *
                     * @description
                     * Go to a page state that show the specified ArkCase Object (Case, Complaint, Document, etc.)
                     */
                    scope.onClickObjLink = function(event, rowEntity, keepModal, newTab) {
                        event.preventDefault();
                        var targetType = Util.goodMapValue(rowEntity, "object_type_s");
                        var targetId = '';
                        if(targetType == ObjectService.ObjectTypes.FILE) {
                            targetId= Util.goodMapValue(rowEntity, "object_id_s");
                        } else {
                            targetType = $filter('beautifyParentRefToParentTitle')(targetType);
                            targetId = Util.goodMapValue(rowEntity, "parentId");
                            targetId = parseInt(targetId.substring(0, targetId.indexOf('-')));
                        }
                        gridHelper.showObject(targetType, targetId, newTab);
                        if(!keepModal) {
                            scope.onClickCancel();
                        }
                    };

                    /**
                     * @ngdoc method
                     * @name onClickOpenFile
                     * @methodOf global.directive:searchModal
                     *
                     * @param {String} event
                     * @param {String} rowEntity data
                     * @param {Boolean} keepModal Optional flag for keeping open modal active
                     *
                     * @description
                     * Go to a page state that show the specified ArkCase File viewer of the selected item
                     */
                    scope.onClickOpenFile = function (event, rowEntity, keepModal){
                        event.preventDefault();

                        var targetId = Util.goodMapValue(rowEntity, "object_id_s");
                        var parentId = Util.goodMapValue(rowEntity.parent_document, "object_id_s");
                        var parentType = Util.goodMapValue(rowEntity.parent_document, "object_type_s");
                        var fileName = Util.goodMapValue(rowEntity, "title_parseable");

                        gridHelper.openObject(targetId, parentId, parentType, fileName);
                        if(!keepModal) {
                            scope.onClickCancel();
                        }
                    };

                    //prepare the UI-grid
                    if (scope.config()) {
                        scope.pageSize = scope.config().paginationPageSize;
                        scope.start = scope.config().start;
                        scope.sort = Util.goodValue(scope.config().sort, "");

                        if (scope.showSelectedItemsGrid) {
                            scope.gridSelectedItems = {
                                enableColumnResizing: true,
                                enableRowSelection: true,
                                enableRowHeaderSelection: false,
                                noUnselect: false,
                                paginationPageSizes: scope.config().paginationPageSizes,
                                paginationPageSize: scope.config().paginationPageSize,
                                columnDefs: scope.config().selectedItemsGrid.columnDefs,
                                deleteRow: function(rowEntity) {
                                    var index = scope.gridSelectedItems.data.findIndex(function(el) {
                                        return el.object_id_s === rowEntity.object_id_s;
                                    });
                                    scope.gridSelectedItems.data.splice(index, 1);
                                }
                            };
                            scope.gridSelectedItems.data = [];
                        }

                        scope.gridOptionsMaster = {
                            enableColumnResizing: true,
                            enableRowSelection: true,
                            enableRowHeaderSelection: false,
                            enableFiltering: scope.config().enableFiltering,
                            //multiSelect: scope.multiSelect === 'true' ? true : false,
                            multiSelect: scope.multiSelect,
                            noUnselect: false,
                            useExternalPagination: true,
                            paginationPageSizes: scope.config().paginationPageSizes,
                            paginationPageSize: scope.config().paginationPageSize,
                            columnDefs: scope.config().columnDefs,
                            onRegisterApi: function(gridApi) {
                                scope.gridApi = gridApi;
                                gridApi.selection.on.rowSelectionChanged(scope, function(row) {

                                    scope.selectedItems = gridApi.selection.getSelectedRows();
                                    scope.selectedItem = row.entity;
                                    if (scope.onItemsSelected) {
                                        scope.onItemsSelected(scope.selectedItems, [ scope.selectedItem ], row.isSelected);

                                    }
                                    if (scope.secondGrid) {
                                        scope.setSecondGridData();
                                    }

                                    if (scope.showSelectedItemsGrid && !_.isEmpty(scope.selectedItems)) {
                                        scope.gridSelectedItems.data = _.uniq(scope.gridSelectedItems.data.concat(scope.selectedItems));
                                    }
                                    if (row.isSelected && scope.secondSelectionOptional) {
                                        scope.disableSearchButton = true;
                                        scope.firstGridSelected = true;
                                        if (row.entity.object_type_s === 'USER') {
                                            _.find(row.entity.groups_id_ss, function(group) {
                                                if (scope.participant.owningGroup === group) { // Going through the collection of groups to see if there is a match with the current owning group
                                                    scope.disableSearchButton = false; // if there is a match that means the user is a member of the current owning group
                                                }
                                            });
                                            scope.userNotValid = scope.disableSearchButton;
                                        } else if (scope.participant.assignee != undefined) {
                                            _.find(row.entity.member_id_ss, function(member) {
                                                if (scope.participant.assignee.id === member) { //Going through the collection of members to see if there is a match with the current assignee
                                                    scope.disableSearchButton = false; // if there is a match that means the current assignee is within that owning group
                                                }
                                            });
                                            scope.groupNotValid = scope.disableSearchButton;
                                        } else {
                                            scope.groupNotValid = scope.disableSearchButton = false;
                                        }
                                    }
                                });

                                gridApi.selection.on.rowSelectionChangedBatch(scope, function(rows) {
                                    scope.selectedItems = gridApi.selection.getSelectedRows();
                                    if (scope.onItemsSelected) {
                                        scope.onItemsSelected(scope.selectedItems, scope.selectedItems, true);
                                    }
                                    if (scope.showSelectedItemsGrid && !_.isEmpty(scope.selectedItems)) {
                                        scope.gridSelectedItems.data = _.uniq(scope.gridSelectedItems.data.concat(scope.selectedItems));
                                    }
                                });

                                // Get the sorting info from UI grid
                                gridApi.core.on.sortChanged(scope, function(grid, sortColumns) {
                                    if (sortColumns.length > 0) {
                                        var sortColArr = [];
                                        _.each(sortColumns, function(col) {
                                            sortColArr.push((col.colDef.sortField || col.colDef.name) + " " + col.sort.direction);
                                        });
                                        scope.sort = sortColArr.join(',');
                                    } else {
                                        scope.sort = "";
                                    }
                                    scope.queryExistingItems();
                                });

                                gridApi.pagination.on.paginationChanged(scope, function(newPage, pageSize) {
                                    scope.start = (newPage - 1) * pageSize; //newPage is 1-based index
                                    scope.pageSize = pageSize;
                                    scope.queryExistingItems();
                                });
                            }
                        };

                        if (scope.secondGrid) {
                            scope.pageSizeSecond = scope.config().paginationPageSize;
                            scope.startSecond = scope.config().start;
                            scope.sortSecond = Util.goodValue(scope.config().sort, "");
                            scope.gridOptionsDetail = {
                                enableColumnResizing: true,
                                enableRowSelection: true,
                                enableRowHeaderSelection: false,
                                enableFiltering: scope.config().enableFiltering,
                                multiSelect: scope.multiSelect,
                                noUnselect: false,
                                useExternalPagination: true,
                                paginationPageSizes: scope.config().paginationPageSizes,
                                paginationPageSize: scope.config().paginationPageSize,
                                columnDefs: scope.config().columnDefs,
                                onRegisterApi: function(gridApi) {
                                    scope.gridApi = gridApi;

                                    gridApi.selection.on.rowSelectionChanged(scope, function(row) {
                                        if (row.isSelected) {
                                            scope.selectedDetailItem = row.entity;
                                        } else {
                                            scope.selectedDetailItem = null;
                                        }
                                    });

                                    gridApi.selection.on.rowSelectionChangedBatch(scope, function(rows) {
                                        scope.selectedDetailItem = gridApi.selection.getSelectedRows();

                                    });

                                    // Get the sorting info from UI grid
                                    gridApi.core.on.sortChanged(scope, function(grid, sortColumns) {
                                        if (sortColumns.length > 0) {
                                            var sortColArr = [];
                                            _.each(sortColumns, function(col) {
                                                sortColArr.push((col.colDef.sortField || col.colDef.name) + " " + col.sort.direction);
                                            });
                                            scope.sortSecond = sortColArr.join(',');
                                        } else {
                                            scope.sortSecond = "";
                                        }
                                        scope.querySubItems();
                                    });

                                    gridApi.pagination.on.paginationChanged(scope, function(newPage, pageSize) {
                                        scope.startSecond = (newPage - 1) * pageSize; //newPage is 1-based index
                                        scope.pageSizeSecond = pageSize;
                                        scope.setSecondGridData();

                                    });
                                }
                            };
                        }
                        /* Allows for overriding the default row template
                         * Used to add ng-dblClick etc properties to the template
                         * Default rowTemplate =
                         * "<div ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.uid\"
                         *       ui-grid-one-bind-id-grid=\"rowRenderIndex + '-' + col.uid + '-cell'\"
                         *       class=\"ui-grid-cell\"
                         *       ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\"
                         *       role=\"{{col.isRowHeader ? 'rowheader' : 'gridcell'}}\" ui-grid-cell>
                         *  </div>"
                         */
                        if (scope.config().rowTemplate) {
                            scope.gridOptionsMaster.rowTemplate = scope.config().rowTemplate;
                        }

                        if (scope.gridOptionsMaster) {
                            if (scope.filter) {
                                scope.filters = 'fq=' + scope.filter;
                            }
                            if (attrs.startWildcardSearch) {
                                scope.queryExistingItems();
                            }
                        }
                    }

                    // Perform initial request to get list of documents if defaultFilter is defined
                    if (scope.defaultFilter) {
                        var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuerySorted(scope.searchQuery + '*', scope.defaultFilter, scope.pageSize, 0, scope.sort);
                        if (query) {
                            scope.showNoData = true;
                            SearchService.queryFilteredSearch({
                                query: query
                            }, function(data) {
                                updateFacets(data.facet_counts.facet_fields);
                                scope.gridOptionsMaster.data = data.response.docs;
                                if (scope.gridOptionsMaster.data.length < 1) {
                                    scope.showNoData = true;
                                }
                                scope.gridOptionsMaster.totalItems = data.response.numFound;
                            });
                        }

                    }
                },

                templateUrl: 'directives/search-modal/search-modal.client.view.html'
            };
        } ]);
