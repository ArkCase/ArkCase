'use strict';

angular.module('subscription').controller('Subscriptions.FacetsController', ['$scope', 'SubscriptionService', 'ResultService',
    function ($scope, SubscriptionService, ResultService) {
        $scope.$emit('req-component-config', 'facets');
        $scope.facets = [];
        $scope.filter = '';  //filter parameter
        $scope.start = '';
        $scope.count = '';
        $scope.selected = {};  //object used to hold record for checkbox checked/unchecked
        $scope.config=null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'facets') {
                $scope.config = config;
                $scope.start=config.searchParams.start;
                $scope.count=config.searchParams.n;
            }
        }
        //method for adding the filter parameters when checked
        function addFilterParams(value, key) {
            if (!$scope.filter) {
                $scope.filter = '"' + key + '":' + value;
            }
            else {
                $scope.filter += '%26fq="' + key + '":' + value;
            }
            return $scope.filter;
        }
        ;
        //method for removing filter parameter when unchecked
        function removeFilterParams(value, key) {
            var indexOfKey = $scope.filter.indexOf(key);
            if (indexOfKey == 1) {
                //since the different key can have same value, use key position as index
                // 2 is length of  ' ": ' and 7 is the length of ' %26fq=" '
                $scope.filter = $scope.filter.substring(indexOfKey + key.length + 2 + value.length + 7);
            }
            else {
                //since the different key can have same value, use key position as index
                $scope.filter = $scope.filter.replace($scope.filter.substring(indexOfKey - 7, indexOfKey + key.length + 2 + value.length), '');
            }
            return $scope.filter;
        }
        $scope.checkEmpty = function (value) {
            if (value.length > 0) {
                return true;
            }
            else {
                return false;
            }
        };
        //method to add a properties "selected" into data return, for checkbox used
        function addDataField() {
            //$scope.facets is facet_counts.facet_fields
            _.forEach($scope.facets, function (value, key) {
                _.forEach(value, function (value, key) {
                    value.checked = false;
                });
            });
        }
        ;
        //apply the checked value for checkbox
        function applySelected() {
            _.forEach($scope.selected, function (value, key) {
                for (var i = 0; i < $scope.facets[key].length; i++) {
                    if ($scope.selected[key].indexOf($scope.facets[key][i].name) > -1) {
                        $scope.facets[key][i].checked = true;
                    }
                }
            });
        }
        ;
        $scope.facetSearch = function (name, facetKey, facetSelected) {
            //checkbox is checked
            if (facetSelected) {
                $scope.selected[facetKey] ? $scope.selected[facetKey].push(name) : $scope.selected[facetKey] = [name];
                SubscriptionService.queryFilteredSearch({
                    input: ResultService.queryString,
                    start: $scope.start,
                    n: $scope.count,
                    filters: addFilterParams(name, facetKey)
                },
                function (data) {
                    ResultService.passData(data, ResultService.queryString, $scope.filter);
                });
            }
            //checkbox is unchecked
            else {
                //remove from selected, and delete it if empty after removing
                $scope.selected[facetKey].splice($scope.selected[facetKey].indexOf(name), 1);
                $scope.selected[facetKey].length > 0 ? '' : delete $scope.selected[facetKey];
                //if $scope.selected is empty, no filter apply
                if (angular.equals({}, $scope.selected)) {
                    removeFilterParams(name, facetKey);
                    SubscriptionService.queryFacetedSearch({
                        input: ResultService.queryString,
                        start: $scope.start,
                        n: $scope.count
                    },
                    function (data) {
                        ResultService.passData(data, ResultService.queryString, '');
                    });
                }
                //else remove filter and call services
                else {
                    SubscriptionService.queryFilteredSearch({
                        input: ResultService.queryString,
                        start: $scope.start,
                        n: $scope.count,
                        filters: removeFilterParams(name, facetKey)
                    },
                    function (data) {
                        ResultService.passData(data, ResultService.queryString, $scope.filter);
                    });
                }
                ;
            }
        };
        //listen on event
        $scope.$watch(
                function () {
                    return ResultService.data;
                },
                function () {
                    if (ResultService.data) {
                        $scope.facets = ResultService.data.facet_counts.facet_fields;
                        addDataField();
                        if(ResultService.filterParams){
                            applySelected();
                        }else{
                            $scope.selected={};
                            $scope.filter='';
                        }
                    };                     
                }
        );
    }
]);