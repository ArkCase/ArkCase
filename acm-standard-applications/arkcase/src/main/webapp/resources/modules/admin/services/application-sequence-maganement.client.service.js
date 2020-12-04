'use strict';

angular.module('admin').factory('Admin.SequenceManagementService', [ '$http', '$httpParamSerializer', function($http, $httpParamSerializer) {

    return ({
        getSequences: getSequences,
        saveSequences: saveSequences,
        updateSequences: updateSequences,
        updateSequenceNumber: updateSequenceNumber,
        getSequenceEntity: getSequenceEntity
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

    //Save and update sequence number
    function updateSequenceNumber(sequenceEntityObj) {
        return $http({
            method: 'PUT',
            url: 'api/latest/plugin/sequence/configuration/updateSequenceNumber',
            cache: false,
            data: sequenceEntityObj
        });
    }

    //Get sequence from acm_sequence table
    function getSequenceEntity(sequenceName, sequencePartName) {
        var params = {
            sequenceName: sequenceName,
            sequencePartName: sequencePartName
        };

        var urlArgs = $httpParamSerializer(params);

        return $http({
            method: 'GET',
            url: 'api/latest/plugin/sequence/configuration/getSequence' + '?' + urlArgs,
            cache: false
        });
    }

} ]);