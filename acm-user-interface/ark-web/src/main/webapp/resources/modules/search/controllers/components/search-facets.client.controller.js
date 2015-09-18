'use strict';

angular.module('search').controller('Search.FacetsController', ['$scope', 'SearchService', 'resultService',
    function ($scope, SearchService, resultService) {
        $scope.$emit('req-component-config', 'facets');
        $scope.facets=[];
        $scope.facetsDetails=[];
        $scope.checkEmpty=function(value){
            if(value.length>0){
                return true;
            }
            else{
                return false;
            }
        }
        $scope.$on('queryComplete', function () {
            console.log(resultService.data.facet_counts.facet_fields);
            $scope.facets=resultService.data.facet_counts.facet_fields;
        });
    }
]);