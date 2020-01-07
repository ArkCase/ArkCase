'use strict';

angular.module('admin').factory('Admin.SequenceRegistryService', ['$http', function($http){

    return({
        getSequenceRegistry: getSequenceRegistry
    });

    function getSequenceRegistry() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/sequence/registry'
        })
    }

}]);