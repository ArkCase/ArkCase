'use strict';

angular.module('admin').factory('Admin.SequenceManagementService', [ '$http', function($http) {

    return ({
        getSequences: getSequences,
        saveSequences: saveSequences,
        updateSequences: updateSequences
    });

    function getSequences() {
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/sequence/configuration'
        });
    }

    //Save and update sequence
    function saveSequences(sequenceConfig) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/sequence/configuration',
            data: sequenceConfig,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    //Save and update sequence
    function updateSequences(sequenceConfig) {
        return $http({
            method: 'PUT',
            url: 'api/latest/plugin/sequence/configuration',
            data: sequenceConfig
        });
    }
} ]);