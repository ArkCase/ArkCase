'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope', 'ConfigService'
    , function ($scope, ConfigService) {

        ConfigService.getComponentConfig("complaints", "calendar").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });
    }
]);