'use strict';

angular.module('subscriptions').controller('Subscriptions.InputController', ['$scope', 'SubscriptionService', 'ResultService',
    function ($scope, SubscriptionService, ResultService) {
        $scope.$emit('req-component-config', 'input');
        $scope.config=null;
        $scope.start='';
        $scope.count='';
        //$scope.start = 0;
        //$scope.count = 10;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'input') {
                $scope.config = config;
                $scope.start=config.searchParams.start;
                $scope.count=config.searchParams.n;
            }
        }
        $scope.keyDown = function (event) {
            if (event.keyCode == 13) {
                queryData();
            }
        };
        $scope.search = queryData;
        function queryData() {
            if (!$scope.searchQuery) {
                $scope.searchQuery = '';
            }
            SearchService.queryFacetedSearch({
                input: $scope.searchQuery + '*',
                start: $scope.start,
                n: $scope.count},
            function (data) {
                ResultService.passData(data, $scope.searchQuery + '*','');
            });
        };
    }
]);