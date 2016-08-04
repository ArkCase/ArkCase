'use strict';

/**
 * @ngdoc directive
 * @name global.directive:search
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/search/search.client.directive.js directives/search/search.client.directive.js}
 *
 * The "Search" directive triggers the faceted search functionality
 *
 * @param {String} header Label for the header of the module (for e.g. it can come from a resource file)
 * @param {String} searchBtn Label for the search button (for e.g. it can come from a resource file)
 * @param {String} searchPlaceholder Label for the input placeholder (for e.g. it can come from a resource file)
 * @param {String} searchQuery Item to be searched for
 * @param {String} filter Filter required to send to the faceted search by default (e.g. for client : "\"Object Type\":CASE_FILE")
 * @param {expression} config Configuration for search provided by the parent scope (required to render the UI-grid)
 *
 * @example
 <example>
 <file name="index.html">
 <search header="{{'module.title' | translate}}"
 search-btn="{{'module.search.btn' | translate}}"
 search-query="{{searchQuery}}"
 search-placeholder="{{'module.search.placeholder' | translate}}"
 filter="{{filter}}"
 config="config">
 </search>
 </file>
 <file name="app.js">
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
            $scope.config = {
                        "id": "searchModule",
                        "title": "Search Module Search",
                        "enabled": "true",
                        "type": "grid",
                        "enableFiltering": false,
                        "columnDefs": [
                            {
                                "name": "id",
                                "displayName": "module.id",
                                "headerCellFilter": "translate"
                            }
                        ],
                        "paginationPageSizes": [
                            5,
                            10,
                            20,
                            50
                        ],
                        "paginationPageSize": 10,
                        "start": 0,
                        "filter": "\"Object Type\": FILTER"
                    };
                    $scope.filter = config.filter;
                });
 </file>
 </example>
 */
angular.module('directives').directive('search', ['SearchService', 'Search.QueryBuilderService', '$q', 'UtilService', 'Object.LookupService', '$window', 'uiGridExporterConstants', '$translate', 'Tags.TagsService',
    function (SearchService, SearchQueryBuilder, $q, Util, ObjectLookupService, $window, uiGridExporterConstants, $translate, TagsService) {
        return {
            restrict: 'E',              //match only element name
            scope: {
                header: '@',            //@ : text binding (read-only and only strings)
                searchBtn: '@',
                exportBtn: '@',
                searchQuery: '@',
                searchPlaceholder: '@',
                filter: '@',
                multiFilter: '@',
                config: '='            //= : two way binding so that the data can be monitored for changes
            },

            link: function (scope) {    //dom operations
                scope.facets = [];
                scope.currentFacetSelection = [];
                scope.selectedItem = null;
                scope.emptySearch = true;
                scope.exportUrl = "";
                if (typeof scope.config.emptySearch !== 'undefined') {
                    scope.emptySearch = scope.config.emptySearch;
                }
                scope.queryExistingItems = function () {
                    if (!scope.searchQuery || scope.searchQuery.length === 0) {
                        if (!scope.emptySearch) {
                            scope.searchQuery = "";
                            return;
                        }
                        else {
                            scope.searchQuery = "";
                        }
                    }
                    if (scope.pageSize >= 0 && scope.start >= 0) {
                        if (scope.multiFilter) {
                            if (scope.searchQuery) {
                                if (scope.filters.indexOf("Tag Token") <= 0) {
                                    scope.filters += "&fq" + scope.multiFilter;
                                }
                                _.map(scope.searchQuery, function (tag) {
                                    scope.filters += tag.tag_token_lcs + "|";
                                });
                            }
                        }
                        var query = SearchQueryBuilder.buildFacetedSearchQuery((scope.multiFilter ? "*" : scope.searchQuery + "*"), scope.filters, scope.pageSize, scope.start);
                        if (query) {
                            setExportUrl(query);
                            SearchService.queryFilteredSearch({
                                    query: query
                                },
                                function (data) {
                                    updateFacets(data.facet_counts.facet_fields);
                                    scope.gridOptions.data = data.response.docs;
                                    scope.gridOptions.totalItems = data.response.numFound;
                                }
                            );
                        }
                    }
                };

                scope.onTagRemoved = function (tagRemoved) {
                    scope.filters = 'fq=' + scope.filter;
                };

                scope.checkTag = function (tagSelected) {
                    if (!tagSelected.tag_token_lcs) {
                        return false;
                    }
                    return true;
                };

                scope.loadTags = function loadTags(query) {
                    var deferred = $q.defer();
                    TagsService.searchTags({
                        query: query,
                        filter: 'fq=' + scope.filter
                    }).then(function (tags) {
                        deferred.resolve(tags);
                    });
                    return deferred.promise;
                }

                scope.queryTypeahead = function (typeaheadQuery) {
                    typeaheadQuery = typeaheadQuery.replace('*', '');
                    typeaheadQuery = '/' + typeaheadQuery + '.*/';
                    console.log(scope.filters);
                    if (!scope.hideTypeahead) {
                        if (scope.filters && scope.filters.indexOf("USER") >= 0) {
                            return scope.queryTypeaheadForUser(typeaheadQuery);
                        } else {
                            var query = SearchQueryBuilder.buildFacetedSearchQuery(typeaheadQuery, scope.filters, 10, 0);
                            var deferred = $q.defer();
                            if (query) {
                                SearchService.queryFilteredSearch({
                                    query: query
                                }, function (res) {
                                    var result = _.pluck(res.response.docs, scope.typeAheadColumn);
                                    deferred.resolve(result);
                                });
                            }
                            return deferred.promise;
                        }
                    }
                };

                scope.queryTypeaheadForUser = function (typeaheadQuery) {
                    typeaheadQuery = 'first_name_lcs:' + typeaheadQuery + ' OR last_name_lcs:' + typeaheadQuery;
                    var deferred = $q.defer();
                    if (typeaheadQuery) {
                        SearchService.queryFilteredSearchForUser({
                            query: typeaheadQuery,
                            start: 0,
                            maxRows: 10
                        }, function (res) {
                            var result = _.pluck(res.response.docs, 'name');
                            deferred.resolve(result);
                        });
                    }

                    var query = SearchQueryBuilder.buildFacetedSearchQuery(typeaheadQuery + '*', scope.filters, 10, 0);
                    var deferred = $q.defer();
                    if (query) {
                        setExportUrl(query);
                        SearchService.queryFilteredSearch({
                            query: query
                        }, function (res) {
                            var result = _.pluck(res.response.docs, scope.typeAheadColumn);
                            deferred.resolve(result);
                        });
                    }
                    else {
                        deferred.reject();
                    }

                    return deferred.promise;
                };

                function setExportUrl(query) {
                    var fields = [];
                    var columns = scope.config.columnDefs;
                    _.forEach(columns, function (value) {
                        if ('visible' in value) {
                            if (value.visible) {
                                fields.push(value.name);
                            }
                        } else {
                            fields.push(value.name);
                        }
                    });

                    scope.exportUrl = SearchService.exportUrl(query, fields, 'csv', scope.config.reportFileName);
                }

                function updateFacets(facets) {
                    if (facets) {
                        if (scope.facets.length) {
                            scope.facets.splice(0, scope.facets.length)
                        }
                        _.forEach(facets, function (value, key) {
                            if (value) {
                                scope.facets.push({"name": key, "fields": value});
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
                            scope.filters = "";
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

                var promiseObjectTypes = ObjectLookupService.getObjectTypes().then(
                    function (objectTypes) {
                        scope.objectTypes = objectTypes;
                        return objectTypes;
                    }
                );

                scope.onClickObjLink = function (event, objectType, objectId) {
                    event.preventDefault();
                    promiseObjectTypes.then(function (data) {
                        var found = _.find(scope.objectTypes, {type: objectType});
                        if (found && found.url) {
                            var url = Util.goodValue(found.url);
                            var id = objectId;
                            url = url.replace(":id", id);

                            // Target property is used to control open mode: _parent, _blank
                            if (found.target) {
                                $window.open(url, found.target);
                            } else {
                                $window.location.href = url;
                            }
                        }
                    });
                };

                scope.onClickParentObjLink = function (event, parentReference) {
                    var objectId = parentReference.substring(0, parentReference.indexOf('-'));
                    var objectType = parentReference.substring(parentReference.indexOf('-') + 1);
                    event.preventDefault();
                    promiseObjectTypes.then(function (data) {
                        var found = _.find(scope.objectTypes, {type: objectType});
                        if (found && found.url) {
                            var url = Util.goodValue(found.url);
                            var id = objectId;
                            url = url.replace(":id", id);

                            // Target property is used to control open mode: _parent, _blank
                            if (found.target) {
                                $window.open(url, found.target);
                            } else {
                                $window.location.href = url;
                            }
                        }
                    });
                };

                scope.keyUp = function (event) {
                    scope.searchQuery = scope.searchQuery.replace('*', '');
                    if (event.keyCode == 13 && scope.searchQuery) {
                        scope.queryExistingItems();
                    }
                };

                scope.downloadCSV = function () {
                    if (scope.gridApi && scope.gridApi.exporter) {
                        scope.gridApi.exporter.csvExport(uiGridExporterConstants.VISIBLE);
                    }
                };

                //prepare the UI-grid
                scope.gridOptions = {};

                scope.$watchCollection('config', function (newValue, oldValue) {
                    $q.when(newValue).then(function (config) {
                        scope.filterName = config.filterName;
                        scope.pageSize = config.paginationPageSize;
                        scope.start = config.start;
                        scope.gridOptions = {
                            enableColumnResizing: true,
                            enableRowSelection: true,
                            enableRowHeaderSelection: false,
                            enableFiltering: config.enableFiltering,
                            multiSelect: false,
                            noUnselect: false,
                            useExternalPagination: true,
                            paginationPageSizes: config.paginationPageSizes,
                            paginationPageSize: config.paginationPageSize,
                            enableSelectAll: true,
                            exporterCsvFilename: config.csvFileName,
                            exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
                            exporterHeaderFilter: $translate.instant,
                            columnDefs: config.columnDefs,
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

                        scope.isMultiFilter = false;
                        if (config.multiFilter) {
                            scope.isMultiFilter = true;
                        }
                        //hideTypeahead is false by default, it will be changed in true if it is added in config
                        scope.hideTypeahead = false;
                        if (config.hideTypeahead)
                            scope.hideTypeahead = true;
                        //default for typeAheadColumn is name
                        scope.typeAheadColumn = "name";
                        if (config.typeAheadColumn)
                            scope.typeAheadColumn = config.typeAheadColumn;

                        if (scope.gridOptions) {
                            if (scope.filter) {
                                scope.filters = 'fq=' + scope.filter;
                            }
                            scope.queryExistingItems();
                        }
                    }, true);
                });

            },

            templateUrl: 'directives/search/search.client.view.html'
        };
    }
]);