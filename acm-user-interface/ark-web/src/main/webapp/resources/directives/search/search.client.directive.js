'use strict';

/**
 * @ngdoc directive
 * @name global.directive:search
 * @restrict E
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/search\search.client.directive.js directives/search\search.client.directive.js}
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
angular.module('directives').directive('search', ['SearchService', 'Search.QueryBuilderService', '$q', 'UtilService','HelperService', 'LookupService', 'StoreService', '$window',
    function (SearchService, SearchQueryBuilder, $q, Util, Helper, LookupService, Store, $window) {
        return {
            restrict: 'E',              //match only element name
            scope: {
                header: '@',            //@ : text binding (read-only and only strings)
                searchBtn: '@',
                searchQuery: '@',
                searchPlaceholder: '@',
                filter: '@',
                config: '='            //= : two way binding so that the data can be monitored for changes
            },

            link: function (scope) {    //dom operations
                scope.facets=[];
                scope.currentFacetSelection = [];
                scope.selectedItem = null;
                scope.queryExistingItems = function (){
                    if(scope.pageSize >=0 && scope.start >=0){
                        var query = SearchQueryBuilder.buildFacetedSearchQuery(scope.searchQuery + "*",scope.filters,scope.pageSize,scope.start);
                        if(query){
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


                function updateFacets(facets){
                    if(facets){
                        if(scope.facets.length){
                            scope.facets.splice(0,scope.facets.length)
                        }
                        _.forEach(facets, function(value, key) {
                            if(value){
                                scope.facets.push({"name": key, "fields":value});
                            }
                        });
                    }
                }

                scope.selectFacet = function (checked, facet, field){
                    if(checked){
                        if(scope.filters){
                            scope.filters += '&fq="' + facet + '":' + field;
                        }
                        else{
                            scope.filters += 'fq="' + facet + '":' + field;
                        }
                        scope.queryExistingItems();
                    }else{
                        if(scope.filters.indexOf('&fq="' + facet + '":' + field) > -1){
                            scope.filters = scope.filters.split('&fq="' + facet + '":' + field).join('');
                        }
                        else if(scope.filters.indexOf('fq="' + facet + '":' + field) > -1){
                            scope.filters='';
                        }
                        scope.queryExistingItems();
                    }
                }

                var cacheObjectTypes = new Store.SessionData(Helper.SessionCacheNames.OBJECT_TYPES);
                var objectTypes = cacheObjectTypes.get();
                var promiseObjectTypes = Util.serviceCall({
                    service: LookupService.getObjectTypes
                    , result: objectTypes
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

                scope.onClickObjLink = function (event, rowEntity) {
                    event.preventDefault();
                    promiseObjectTypes.then(function (data) {
                        var found = _.find(scope.objectTypes, {type: rowEntity.object_sub_type_s ? rowEntity.object_sub_type_s : rowEntity.object_type_s});
                        if (found && found.url) {
                            var url = Util.goodValue(found.url);
                            var id = Util.goodMapValue(rowEntity, "object_id_s");
                            url = url.replace(":id", id);
                            $window.location.href = url;
                        }
                    });
                };

                scope.keyDown = function (event) {
                    if (event.keyCode == 13 && scope.searchQuery) {
                        scope.queryExistingItems();
                    }
                };

                //prepare the UI-grid
                scope.gridOptions = {};
                scope.$watchCollection('config', function(newValue, oldValue) {
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
                        }
                        if(scope.gridOptions){
                            if(scope.filter){
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