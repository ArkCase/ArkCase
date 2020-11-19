'use strict';

angular.module('admin').factory('Admin.ObjectTitleConfigurationService', [ '$http',
    function ($http) {

    return {
        getObjectTitleConfiguration: getObjectTitleConfiguration,
        saveObjectTitleConfiguration: saveObjectTitleConfiguration
    };

    function getObjectTitleConfiguration() {
        return $http({
            method: "GET",
            url: "api/latest/service/object-title-config"
        });
    }
    function saveObjectTitleConfiguration(objectTitleConfiguration) {
        return $http({
            method: "POST",
            url: "api/latest/service/object-title-config",
            data: objectTitleConfiguration,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    }]);