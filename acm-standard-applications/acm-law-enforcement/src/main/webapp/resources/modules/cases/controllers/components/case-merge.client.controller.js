'use strict';

angular.module('cases').controller('Cases.MergeController',
        [ '$scope', '$modalInstance', 'config', function($scope, $modalInstance, config) {

            $scope.modalInstance = $modalInstance;
            $scope.config = config;
            //$scope.filter = filter;
        } ]);