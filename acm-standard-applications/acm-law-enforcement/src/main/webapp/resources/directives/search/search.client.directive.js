'use strict';

/**
 * @ngdoc directive
 * @name global.directive:search
 * @restrict E
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/search/search.client.directive.js directives/search/search.client.directive.js}
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
 angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log, ) {
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
angular.module('directives').directive(
        'search',
    ['SearchService', '$rootScope', '$window', '$q', '$location', '$browser', '$translate', '$interval', 'UtilService', 'Object.LookupService', 'uiGridExporterConstants', 'Tags.TagsService', 'Search.QueryBuilderService', 'ObjectService', 'Search.AutoSuggestService', '$state', 'MessageService',
        'DocTreeExt.DownloadSelectedAsZip', 'Websockets.MessageHandler',
        function (SearchService, $rootScope, $window, $q, $location, $browser, $translate, $interval, Util, ObjectLookupService, uiGridExporterConstants, TagsService, SearchQueryBuilder, ObjectService, AutoSuggestService, $state, MessageService
            , DownloadSelectedAsZip, messageHandler) {
                    return {
                        restrict: 'E', //match only element name
                        scope: {
                            header: '@', //@ : text binding (read-only and only strings)
                            searchBtn: '@',
                            exportBtn: '@',
                            compressBtn: '@',
                            searchQuery: '@',
                            searchPlaceholder: '@',
                            filter: '@',
                            multiFilter: '@',
                            config: '=', //= : two way binding so that the data can be monitored for changes
                            customization: '=?'
                        },

                        link: function(scope) { //dom operations
                            scope.facets = [];
                            scope.currentFacetSelection = {};
                            scope.selectedItem = null;
                            scope.selectedRows = [];
                            scope.emptySearch = true;
                            scope.exportUrl = "";
                            scope.disableCompressBtn = false;

                            scope.facetLimit = 10; //default value for facetLimit
                            if (typeof scope.config.facetLimit !== 'undefined') {
                                scope.facetLimit = scope.config.facetLimit;
                            }

                            var searchObject = new Object();
                            try {
                                searchObject = JSON.parse(scope.searchQuery);
                            } catch (e) {
                                searchObject.searchQuery = scope.searchQuery;
                                searchObject.isSelected = false;
                            }

                            var isSelected = searchObject.isSelected;
                            scope.searchQuery = searchObject.searchQuery;
                            scope.onSelect = function($item, $model, $label) {
                                isSelected = true;
                                searchObject.searchQuery = $model;
                            };

                            scope.search = function() {
                                scope.filters = '';
                                scope.selectedRows = [];

                                //reapply default filter if it exists
                                if (scope.filter) {
                                    scope.filters += 'fq=' + scope.filter;
                                }

                                scope.clearAllFacets();
                                scope.queryExistingItems();
                            };

                            scope.queryExistingItems = function(start) {
                                scope.start = Util.goodNumber(start, 0);
                                scope.searchQuery = searchObject.searchQuery;
                                if (!scope.searchQuery || scope.searchQuery.length === 0) {
                                    if (!scope.emptySearch) {
                                        scope.searchQuery = "";
                                        return;
                                    } else {
                                        scope.searchQuery = "";
                                    }
                                }
                                if (scope.pageSize >= 0 && scope.start >= 0) {
                                    if (scope.isMultiFilter) {
                                        if (scope.searchQuery) {
                                            // AFDP-3698: use Solr join query in tags module
                                            var joinQueryStr = scope.multiFilter.replace("${tagName}", "\"" + scope.searchQuery + "\"");
                                            scope.join = joinQueryStr;
                                        }
                                    }

                                    if (scope.isAutoSuggestActive && scope.searchQuery !== "" && isSelected) {
                                        var query = SearchQueryBuilder.buildFacetedSearchQuerySorted((scope.multiFilter ? "*" : "\"" + scope.searchQuery + "\""), scope.filters, scope.join, scope.pageSize, scope.start, scope.sort);
                                        isSelected = false;
                                    } else {
                                        scope.searchQuery = searchObject.searchQuery;
                                        var query = SearchQueryBuilder.buildFacetedSearchQuerySorted((scope.multiFilter ? "*" : "\"" + scope.searchQuery + "\"" + "*"), scope.filters, scope.join, scope.pageSize, scope.start, scope.sort);
                                    }

                                    setExportQuery(query);
                                    if (query) {
                                        SearchService.queryFilteredSearch({
                                            query: query
                                        }, function(data) {
                                            updateFacets(data.facet_counts.facet_fields);
                                            scope.gridOptions.data = data.response.docs;
                                            scope.gridOptions.totalItems = data.response.numFound;
                                        });
                                    }
                                }
                            };

                            scope.onTagRemoved = function(tagRemoved) {
                                scope.filters = 'fq=' + scope.filter;
                            };

                            scope.checkTag = function(tagSelected) {
                                scope.searchQuery = tagSelected.title_parseable;
                                scope.queryExistingItems();
                                if (!tagSelected.title_parseable) {
                                    return false;
                                }
                                return true;
                            };

                            scope.loadTags = function loadTags(query) {
                                var deferred = $q.defer();
                                var autoSuggestObjectType = scope.objectType;
                                AutoSuggestService.autoSuggest(query, "QUICK", autoSuggestObjectType).then(function(tags) {
                                    deferred.resolve(tags);
                                });
                                return deferred.promise;
                            };

                            scope.queryTypeahead = function(typeaheadQuery) {
                                if (!scope.hideTypeahead) {
                                    if (scope.filters && scope.filters.indexOf("USER") >= 0) {
                                        return scope.queryTypeaheadForUser(typeaheadQuery);
                                    } else {
                                        var deferred = $q.defer();
                                        if (typeaheadQuery.length >= 2) {
                                            var deferred = $q.defer();
                                            if (scope.objectType !== 'undefined') {
                                                AutoSuggestService.autoSuggest(typeaheadQuery, "QUICK", scope.objectType).then(function(res) {
                                                    var results = _.pluck(res, scope.typeAheadColumn);
                                                    deferred.resolve(results);
                                                });
                                                return deferred.promise;
                                            } else {
                                                AutoSuggestService.autoSuggest(typeaheadQuery, "QUICK", null).then(function(res) {
                                                    var results = _.pluck(res, scope.typeAheadColumn);
                                                    deferred.resolve(results);
                                                });
                                                return deferred.promise;
                                            }
                                        }
                                    }
                                }
                            };

                            scope.queryTypeaheadForUser = function(typeaheadQuery) {
                                typeaheadQuery = 'first_name_lcs:' + typeaheadQuery + ' OR last_name_lcs:' + typeaheadQuery;
                                var deferred = $q.defer();
                                if (typeaheadQuery) {
                                    SearchService.queryFilteredSearchForUser({
                                        query: typeaheadQuery,
                                        start: 0,
                                        maxRows: 10
                                    }, function(res) {
                                        var result = _.pluck(res.response.docs, 'name');
                                        deferred.resolve(result);
                                    });
                                }

                                var query = SearchQueryBuilder.buildFacetedSearchQuery(typeaheadQuery + '*', scope.filters, 10, 0);
                                var deferred = $q.defer();
                                setExportQuery(query);
                                if (query) {
                                    SearchService.queryFilteredSearch({
                                        query: query
                                    }, function(res) {
                                        var result = _.pluck(res.response.docs, scope.typeAheadColumn);
                                        deferred.resolve(result);
                                    });
                                } else {
                                    deferred.reject();
                                }

                                return deferred.promise;
                            };

                            var setExportQuery = function(query) {
                                scope.query = query;
                            };

                            scope.exportSearch = function() {
                                if (Util.isEmpty(scope.query)) {
                                    return;
                                }

                                var fields = [];
                                var titles = [];
                                var columns = scope.config.columnDefs;
                                _.forEach(columns, function(value) {
                                    if ('visible' in value) {
                                        if (value.visible) {
                                            fields.push(value.name);
                                            titles.push($translate.instant(value.displayName));
                                        }
                                    } else {
                                        fields.push(value.name);
                                        titles.push($translate.instant(value.displayName));
                                    }
                                });

                                var absUrl = $location.absUrl();
                                var baseHref = $browser.baseHref();
                                var appUrl = absUrl.substring(0, absUrl.indexOf(baseHref) + baseHref.length);
                                $window.location.href = appUrl + SearchService.exportUrl(scope.query, 'csv', scope.config.reportFileName, fields, titles);
                            };

                            scope.$bus.subscribe("zip_completed", function (data) {
                                messageHandler.handleZipGenerationMessage(data.filePath);
                            });

                            scope.downloadSelectedFiles = function() {
                                var fileIds = [];
                                var fileCounter = 0;
                                scope.disableCompressBtn = true;
                                _.forEach(scope.selectedRows, function(selectedFile) {
                                    if (selectedFile.object_type_s === "FILE" && fileCounter <= 50) {
                                    fileIds.push(parseInt(selectedFile.object_id_s));
                                        fileCounter++;
                                    }
                                });
                                if (!Util.isArrayEmpty(scope.selectedRows)) {
                                    DownloadSelectedAsZip.downloadSelectedFiles(fileIds).then(function () {
                                        scope.disableCompressBtn = false;
                                        MessageService.info($translate.instant("common.directive.downloadAllAsZip.message.start"));
                                    });
                                } else {
                                    //if there is no selected files for download, download all files in the search result
                                    var allRecords = [];
                                    scope.searchQuery = searchObject.searchQuery;
                                    var query = SearchQueryBuilder.buildFacetedSearchQuerySorted((scope.multiFilter ? "*" : scope.searchQuery + "*"), scope.filters, scope.join, scope.gridOptions.totalItems, scope.start, scope.sort);
                                    if (query) {
                                        SearchService.queryFilteredSearch({
                                            query: query
                                        }, function(data) {
                                            allRecords = data.response.docs;
                                            _.forEach(allRecords, function(item) {
                                                if (fileCounter === 50) {
                                                    return false;
                                                } else {
                                                    if (item.object_type_s === "FILE") {
                                                        fileIds.push(parseInt(item.object_id_s));
                                                        fileCounter++;
                                                    }
                                                }
                                            });
                                            DownloadSelectedAsZip.downloadSelectedFiles(fileIds).then(function() {
                                                scope.disableCompressBtn = false;
                                                MessageService.info($translate.instant("common.directive.downloadAllAsZip.message.start"));
                                            });
                                        });
                                    }
                                }
                            };


                            function updateFacets(facets) {
                                if (facets) {
                                    if (scope.facets.length) {
                                        scope.facets.splice(0, scope.facets.length)
                                    }
                                    _.forEach(facets, function(value, key) {
                                        //check if facet key is in hidden facets
                                        //and if it is we will ignore it
                                        var hidden = false;
                                        if (typeof scope.config.hiddenFacets !== 'undefined' && Util.isArray(scope.config.hiddenFacets)) {
                                            hidden = _.includes(scope.config.hiddenFacets, key);
                                        }
                                        if (!hidden && value) {
                                            var fieldCategory = Util.goodValue($translate.getKey(key, "common.directive.search.facet.names"));
                                            var tokens = fieldCategory.split(".");
                                            fieldCategory = tokens.pop();
                                            fieldCategory = fieldCategory.replace(/%$/, "");
                                            //var nameTranslated = $translate.data(key, "common.directive.search.facet.names");
                                            var facet = {
                                                name: key,
                                                fields: value,
                                                limit: scope.facetLimit,
                                                //nameTranslated: nameTranslated,
                                                nameTranslated: key,
                                                fieldCategory: fieldCategory
                                            };

                                            // _.each(facet.fields, function(field) {
                                            //     field.name.nameTranslated = $translate.data(field.name.nameFiltered,
                                            //         "common.directive.search.facet.fields." + fieldCategory);
                                            // });

                                            scope.facets.push(facet);
                                        }
                                    });

                                    _translateFacets(scope.facets);

                                    //allow predetermined order of facets, defined in config
                                    if (Util.goodMapValue(scope.config, 'preferredFacetOrder', false) && Util.isArray(scope.config.preferredFacetOrder)) {
                                        sortFacets(scope.facets, scope.config.preferredFacetOrder);
                                    }
                                }
                            }

                            scope.$bus.subscribe('$translateChangeSuccess', function(data) {
                                _translateFacets(scope.facets);
                                // _.each(scope.facets, function(facet){
                                //     facet.nameTranslated = $translate.data(facet.name, "common.directive.search.facet.names");
                                //     _.each(facet.fields, function(field) {
                                //         field.name.nameTranslated = $translate.data(field.name.nameFiltered,
                                //             "common.directive.search.facet.fields." + facet.fieldCategory);
                                //     });
                                // });
                            });

                            var _translateFacets = function(facets) {
                                _.each(facets, function(facet) {
                                    facet.nameTranslated = $translate.data(facet.name, "common.directive.search.facet.names");
                                    _.each(facet.fields, function(field) {
                                        if (field.name.nameFiltered == "") {
                                            field.name.nameTranslated = $translate.instant("common.directive.search.facet.blankValue");
                                        } else {
                                            field.name.nameTranslated = $translate.data(field.name.nameFiltered, "common.directive.search.facet.fields." + facet.fieldCategory);
                                        }
                                    });
                                });
                            };

                            function sortFacets(facets, facetOrder) {
                                facets.sort(function(a, b) {
                                    var aPos = _.indexOf(facetOrder, a.name);
                                    var bPos = _.indexOf(facetOrder, b.name);

                                    //Handle possibility of facet not being on ordered list
                                    if (aPos == -1 && bPos != -1) {
                                        return 1;
                                    } else if (aPos != -1 && bPos == -1) {
                                        return -1;
                                    }

                                    return aPos - bPos;
                                })
                            }

                            scope.selectFacet = function(checked, facet, field) {
                                if (checked) {
                                    if (scope.filters) {
                                        scope.filters += '&fq="' + facet + '":' + field;
                                    } else {
                                        scope.filters = "";
                                        scope.filters += 'fq="' + facet + '":' + field;
                                    }
                                } else {
                                    if (scope.filters.indexOf('&fq="' + facet + '":' + field) > -1) {
                                        scope.filters = scope.filters.split('&fq="' + facet + '":' + field).join('');
                                    } else if (scope.filters.indexOf('fq="' + facet + '":' + field) > -1) {
                                        scope.filters = '';
                                        scope.clearAllFacets();
                                    }
                                }
                                scope.queryExistingItems();
                            };

                            scope.onClickObjLink = function(event, objectData) {
                                event.preventDefault();

                                if (Util.goodMapValue(scope, "customization.showObject", false)) {
                                    scope.customization.showObject(objectData);

                                } else {
                                    var objectTypeKey = Util.goodMapValue(objectData, "object_type_s");
                                    var objectId = Util.goodMapValue(objectData, "object_id_s");
                                    ObjectService.showObject(objectTypeKey, objectId);
                                }
                            };

                            scope.onClickParentObjLink = function(event, objectData) {
                                event.preventDefault();

                                if (Util.goodMapValue(scope, "customization.showParentObject", false)) {
                                    scope.customization.showParentObject(objectData);

                                } else {
                                    var parentReference = Util.goodMapValue(objectData, "parent_ref_s", "-");
                                    var objectId = parentReference.substring(0, parentReference.indexOf('-'));
                                    var objectTypeKey = parentReference.substring(parentReference.indexOf('-') + 1);

                                    ObjectService.showObject(objectTypeKey, objectId);
                                }
                            };

                            scope.keyUp = function(event) {
                                scope.searchQuery = scope.searchQuery.replace('*', '');
                                searchObject.searchQuery = scope.searchQuery;

                                if (event.keyCode == 13 && scope.searchQuery) {
                                    scope.search();
                                }
                            };

                            scope.downloadCSV = function() {
                                if (scope.gridApi && scope.gridApi.exporter) {
                                    scope.gridApi.exporter.csvExport(uiGridExporterConstants.VISIBLE);
                                }
                            };

                            scope.clearAllFacets = function() {
                                var selections = scope.currentFacetSelection;
                                for ( var selection in selections) {
                                    selections[selection] = false;
                                }
                            };

                            scope.increaseFacetLimit = function(facet) {
                                facet.limit = facet.fields.length;
                            };

                            scope.decreaseFacetLimit = function(facet) {
                                facet.limit = scope.facetLimit;
                            };

                            scope.onClick = function(objectType, title) {

                                if (objectType == ObjectService.ObjectTypes.ASSOCIATED_TAG) {
                                    $state.go('tags', {
                                        query: title
                                    });
                                }
                            };

                            //prepare the UI-grid
                            scope.gridOptions = {};

                            scope.$watchCollection('config', function(newValue, oldValue) {
                                $q.when(newValue).then(function(config) {
                                    scope.filterName = config.filterName;
                                    scope.pageSize = config.paginationPageSize;
                                    scope.start = config.start;
                                    scope.sort = Util.goodValue(config.sort, "");
                                    scope.objectType = config.autoSuggestObjectType;
                                    scope.gridOptions = {
                                        enableColumnResizing: true,
                                        enableRowSelection: true,
                                        enableRowHeaderSelection: false,
                                        enableFiltering: config.enableFiltering,
                                        multiSelect: true,
                                        noUnselect: false,
                                        useExternalPagination: true,
                                        paginationPageSizes: config.paginationPageSizes,
                                        paginationPageSize: config.paginationPageSize,
                                        enableSelectAll: true,
                                        exporterCsvFilename: config.csvFileName,
                                        exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
                                        exporterHeaderFilter: $translate.instant,
                                        columnDefs: config.columnDefs,
                                        onRegisterApi: function(gridApi) {
                                            scope.gridApi = gridApi;

                                            scope.gridApi.selection.on.rowSelectionChanged(scope, function(selectedRow) {
                                                scope.selectedItem = selectedRow.isSelected ? selectedRow.entity : null;
                                                if (selectedRow.isSelected) {
                                                    if (selectedRow.entity.object_type_s === "FILE") {
                                                        scope.selectedRows.push(scope.selectedItem);
                                                    }
                                                } else {
                                                    var id = selectedRow.entity.id;
                                                    var index = _.findIndex(scope.selectedRows, function(foundObject) {
                                                        return foundObject.id == id;
                                                    });
                                                    scope.selectedRows.splice(index, 1);
                                                }
                                            });

                                            //mark rendered rows as selected after pagination
                                            scope.gridApi.core.on.rowsRendered(scope, function() {
                                                var grid = scope.gridOptions.data;
                                                if (!Util.isArrayEmpty(scope.selectedRows)) {
                                                    grid.forEach(function(part, index) {
                                                        var id = grid[index].id;
                                                        var found = _.findIndex(scope.selectedRows, function(foundObject) {
                                                            return foundObject.id == id;
                                                        });
                                                        //mark row as selected in the grid list
                                                        if (found != -1) {
                                                            scope.gridApi.grid.rows[index].setSelected(true);
                                                        }
                                                    });
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
                                                scope.queryExistingItems(scope.start);
                                            });
                                        }
                                    };

                                    scope.join = "";
                                    scope.isMultiFilter = false;
                                    if (config.multiFilter) {
                                        scope.multiFilter = scope.config.multiFilter;
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

                                        scope.isAutoSuggestActive = false;
                                        if (config.isAutoSuggestActive) {
                                            scope.isAutoSuggestActive = config.isAutoSuggestActive;

                                        }
                                        if (typeof scope.config.emptySearch !== 'undefined') {
                                            scope.emptySearch = scope.config.emptySearch;
                                        }

                                        if (scope.emptySearch) {
                                            scope.queryExistingItems(scope.start);
                                        }

                                    }
                                }, true);
                            });

                            $interval(function() {
                                scope.search();
                            }, 0, 1);

                        },

                        templateUrl: 'directives/search/search.client.view.html'
                    };
                } ]);