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


angular.module('directives').directive('searchModal', ['$q', '$translate', 'UtilService', 'SearchService', 'Search.QueryBuilderService',
    function ($q, $translate, Util, SearchService, SearchQueryBuilder) {
        return {
            restrict: 'E',              //match only element name
            scope: {
                //multiSelect: '@',
                header: '@',            //@ : text binding (read-only and only strings)
                search: '@',
                cancel: '@',
                ok: '@',
                searchPlaceholder: '@',
                filter: '@',
                extraFilter: '@',
                defaultFilter: '@',
                disableSearch: '@',
                config: '&',            //& : one way binding (read-only, can return key, value pair via a getter function)
                modalInstance: '=',     //= : two way binding (read-write both, parent scope and directive's isolated scope have two way binding)
                searchControl: '=?',    //=? : two way binding but property is optional
                onItemsSelected: '=?',   //=? : two way binding but property is optional
                onNoDataMessage: '@',
                draggable: '@',
                onDblClickRow: '=?',
                customization: '=?'
            },

            link: function (scope, el, attrs) {
                //dom operations
                if (scope.draggable) {
                    el.parent().draggable();
                }
                scope.header = Util.goodValue(scope.header, $translate.instant("common.directive.searchModal.header"));
                scope.onNoDataMessage = Util.goodValue(scope.onNoDataMessage, $translate.instant("common.directive.searchModal.noData.text"));
                scope.search = Util.goodValue(scope.search, $translate.instant("common.directive.searchModal.btnSearch.text"));
                scope.ok = Util.goodValue(scope.ok, $translate.instant("common.directive.searchModal.btnOk.text"));
                scope.cancel = Util.goodValue(scope.cancel, $translate.instant("common.directive.searchModal.btnCancel.text"));
                scope.searchPlaceholder = Util.goodValue(scope.searchPlaceholder, $translate.instant("common.directive.searchModal.edtPlaceholder"));
                scope.showHeaderFooter = !Util.isEmpty(scope.modalInstance);
                scope.disableSearchControls = (scope.disableSearch === 'true') ? true : false;
                scope.searchQuery = '';
                scope.minSearchLength = 3;
                if (typeof(scope.config().showFacets) === 'undefined') {
                    scope.config.showFacets = true;
                }
                else {
                    scope.config.showFacets = scope.config().showFacets;
                }
                //if (scope.multiSelect == undefined || scope.multiSelect == '') {
                //    scope.multiSelect = 'false';
                //}
                scope.multiSelect = Util.goodValue(scope.config().multiSelect, false);
                scope.searchControl = {
                    getSelectedItems: function () {
                        //return scope.selectedItems;
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
                scope.queryExistingItems = function () {

                    if (scope.extraFilter) {
                        scope.filters = scope.filters + scope.extraFilter + '*' + scope.searchQuery + '*';
                    }
                    var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuerySorted(scope.searchQuery + '*', scope.filters, scope.pageSize, scope.start, scope.sort);

                    if (query) {
                        scope.showNoData = false;
                        SearchService.queryFilteredSearch({
                                query: query
                            },
                            function (data) {
                                updateFacets(data.facet_counts.facet_fields);
                                scope.gridOptions.data = data.response.docs;
                                if (scope.gridOptions.data.length < 1) {
                                    scope.showNoData = true;
                                }
                                scope.gridOptions.totalItems = data.response.numFound;
                            });
                    }
                };

                function updateFacets(facets) {
                    if (facets) {
                        if (scope.facets.length) {
                            scope.facets.splice(0, scope.facets.length)
                        }
                        _.forEach(facets, function (value, key) {
                            if (value) {
                                scope.facets.push({'name': key, 'fields': value});
                            }
                        });
                    }
                }

                scope.selectFacet = function (checked, facet, field) {
                    if (checked) {
                        if (scope.filters) {
                            scope.filters += '&fq="' + facet + '":' + field;
                        }
                        else {
                            scope.filters = '';
                            scope.filters += 'fq="' + facet + '":' + field;
                        }
                        scope.queryExistingItems();
                    } else {
                        if (scope.filters.indexOf('&fq="' + facet + '":' + field) > -1) {
                            scope.filters = scope.filters.split('&fq="' + facet + '":' + field).join('');
                        }
                        else if (scope.filters.indexOf('fq="' + facet + '":' + field) > -1) {
                            scope.filters = '';
                        }
                        scope.queryExistingItems();
                    }
                };

                scope.keyUp = function (event) {
                    // Remove wildcard
                    scope.searchQuery = scope.searchQuery.replace('*', '');
                    if (event.keyCode == 13 && scope.searchQuery.length >= scope.minSearchLength) {
                        scope.queryExistingItems();
                    }
                };

                scope.onClickOk = function () {
                    //when the modal is closed, the parent scope gets
                    //the selectedItem via the two-way binding
                    if (scope.multiSelect) {
                        scope.modalInstance.close(scope.selectedItems);
                    } else {
                        scope.modalInstance.close(scope.selectedItem);
                    }
                };

                scope.onClickCancel = function () {
                    scope.modalInstance.dismiss('cancel')
                };

                //prepare the UI-grid
                if (scope.config()) {
                    scope.pageSize = scope.config().paginationPageSize;
                    scope.start = scope.config().start;
                    scope.sort = Util.goodValue(scope.config().sort, "");
                    scope.gridOptions = {
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
                        onRegisterApi: function (gridApi) {
                            scope.gridApi = gridApi;

                            gridApi.selection.on.rowSelectionChanged(scope, function (row) {
                                scope.selectedItems = gridApi.selection.getSelectedRows();
                                //scope.selectedItem = row.isSelected ? row.entity : null;
                                scope.selectedItem = row.entity;
                                if (scope.onItemsSelected) {
                                    //scope.onItemsSelected(scope.selectedItems);
                                    scope.onItemsSelected(scope.selectedItems, [scope.selectedItem], row.isSelected);
                                }
                            });

                            gridApi.selection.on.rowSelectionChangedBatch(scope, function (rows) {
                                scope.selectedItems = gridApi.selection.getSelectedRows();
                                if (scope.onItemsSelected) {
                                    //scope.onItemsSelected(scope.selectedItems);
                                    scope.onItemsSelected(scope.selectedItems, scope.selectedItems, true);
                                }
                            });

                            // Get the sorting info from UI grid
                            gridApi.core.on.sortChanged(scope, function (grid, sortColumns) {
                                if (sortColumns.length > 0) {
                                    var sortColArr = [];
                                    _.each(sortColumns, function (col) {
                                        sortColArr.push((col.colDef.sortField || col.colDef.name) + " " + col.sort.direction);
                                    });
                                    scope.sort = sortColArr.join(',');
                                }
                                else {
                                    scope.sort = "";
                                }
                                scope.queryExistingItems();
                            });

                            gridApi.pagination.on.paginationChanged(scope, function (newPage, pageSize) {
                                scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                                scope.pageSize = pageSize;
                                scope.queryExistingItems();
                            });
                        }
                    };

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
                        scope.gridOptions.rowTemplate = scope.config().rowTemplate;
                    }

                    if (scope.gridOptions) {
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
                            },
                            function (data) {
                                updateFacets(data.facet_counts.facet_fields);
                                scope.gridOptions.data = data.response.docs;
                                if (scope.gridOptions.data.length < 1) {
                                    scope.showNoData = true;
                                }
                                scope.gridOptions.totalItems = data.response.numFound;
                            });
                    }

                }
            },

            templateUrl: 'directives/search-modal/search-modal.client.view.html'
        };
    }
]);