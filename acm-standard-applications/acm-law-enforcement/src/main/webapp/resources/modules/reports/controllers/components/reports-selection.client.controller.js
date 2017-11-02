'use strict';

angular.module('reports').controller('Reports.SelectionController', ['$scope',
    function ($scope) {

        $scope.$watchCollection('data.reports', updateAvailableReports);
        function updateAvailableReports() {
            $scope.availableReports = [];
            _.forEach($scope.data.reports, function (value, key) {
                $scope.availableReports.push({"name": key.split('_').join(' '), "id": key});
            })
        }

    }
]);