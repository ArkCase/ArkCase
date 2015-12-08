'use strict';
/**
 * @ngdoc directive
 * @name global.directive:searchModal
 * @restrict E
 *
 * @description
 *
 *{@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/search-modal/search-modal.client.directive.js directives/search-modal/search-modal.client.directive.js}
 *
 * The "Search" modal with faceted search functionality
 *
 * @param {String} header - label for the header of the modal box
 * @param {String} search - label for the search button
 * @param {String} cancel - label for the cancel button
 * @param {String} ok - label for the add button
 * @param {String} searchPlaceholder - label for the input placeholder
 * @param {Object} filter - filter required to send to the faceted search by default (e.g. for client : "\"Object Sub Type\":CLIENT")
 * @param {Object} config - config of the parent scope used mostly for the UI-grid and to retrieve other params
 * @param {Object} modalInstance - current modalInstance in the parentScope, required to pass data when modal closes with "Add"
 **/


angular.module('directives').directive('searchModal', ['$q', 'SearchService', 'Search.QueryBuilderService',
    function ($q, SearchService, SearchQueryBuilder) {
        return {
            restrict: 'E',              //match only element name
            scope: {
                header: '@',            //@ : text binding (read-only and only strings)
                search: '@',
                cancel: '@',
                ok: '@',
                searchPlaceholder: '@',
                filter: '@',
                config: '&',            //& : one way binding (read-only, can return key, value pair via a getter function)
                modalInstance: '='      //= : two way binding (read-write both, parent scope and directive's isolated scope have two way binding)
            },

            link: function (scope) {    //dom operations
                scope.facets = [];
                scope.currentFacetSelection = [];
                scope.selectedItem = null;
                scope.queryExistingItems = function () {
                    var query = SearchQueryBuilder.buildFacetedSearchQuery(scope.searchQuery + '*', scope.filters, scope.pageSize, scope.start);
                    if (query) {
                        SearchService.queryFilteredSearch({
                                query: query
                            },
                            function (data) {
                                updateFacets(data.facet_counts.facet_fields);
                                scope.gridOptions.data = data.response.docs;
                                scope.gridOptions.totalItems = data.response.numFound;
                            });
                    }
                };

                scope.queryTypeahead = function (typeaheadQuery) {
                    typeaheadQuery = typeaheadQuery.replace('*', '');
                    var query = SearchQueryBuilder.buildFacetedSearchQuery(typeaheadQuery + '*', scope.filters, 10, 0);
                    var deferred = $q.defer();
                    if (query) {
                        SearchService.queryFilteredSearch({
                            query: query
                        }, function (res) {
                            var result = _.pluck(res.response.docs, 'name');
                            deferred.resolve(result);
                        });
                    }
                    return deferred.promise;
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
                    if (event.keyCode == 13 && scope.searchQuery) {
                        scope.queryExistingItems();
                    }
                };

                scope.addExistingItem = function () {
                    //when the modal is closed, the parent scope gets
                    //the selectedItem via the two-way binding
                    scope.modalInstance.close(scope.selectedItem);
                };

                scope.close = function () {
                    scope.modalInstance.dismiss('cancel')
                };

                //prepare the UI-grid
                if (scope.config()) {
                    scope.pageSize = scope.config().paginationPageSize;
                    scope.start = scope.config().start;
                    scope.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableRowHeaderSelection: false,
                        enableFiltering: scope.config().enableFiltering,
                        multiSelect: false,
                        noUnselect: false,
                        useExternalPagination: true,
                        paginationPageSizes: scope.config().paginationPageSizes,
                        paginationPageSize: scope.config().paginationPageSize,
                        columnDefs: scope.config().columnDefs,
                        onRegisterApi: function (gridApi) {
                            scope.gridApi = gridApi;

                            gridApi.selection.on.rowSelectionChanged(scope, function (row) {
                                scope.selectedItem = row.isSelected ? row.entity : null;
                            });


                            gridApi.pagination.on.paginationChanged(scope, function (newPage, pageSize) {
                                scope.start = (newPage - 1) * pageSize;   //newPage is 1-based index
                                scope.pageSize = pageSize;
                                scope.queryExistingItems();
                            });
                        }
                    };
                    if (scope.gridOptions) {
                        if (scope.filter) {
                            scope.filters = 'fq=' + scope.filter;
                        }
                    }
                }
            },

            templateUrl: 'directives/search-modal/search-modal.client.view.html'
        };
    }
]);