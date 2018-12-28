'use strict';

angular.module('admin').factory('Admin.SequenceManagementResetService', [ '$http', '$httpParamSerializer', function($http, $httpParamSerializer) {

    return ({
        getSequenceReset: getSequenceReset,
        saveSequenceReset: saveSequenceReset,
        deleteSequenceReset: deleteSequenceReset
    });

    function getSequenceReset(sequenceName, sequencePartName) {
        var params = {
            sequenceName: sequenceName,
            sequencePartName: sequencePartName
        };

        var urlArgs = $httpParamSerializer(params);
        
        return $http({
            method: 'GET',
            url: 'api/latest/plugin/sequence/reset' + '?' + urlArgs,
            cache: false
        });
    }

    function saveSequenceReset(sequenceReset) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/sequence/reset',
            data: sequenceReset,
            headers: {
                "Content-Type": "application/json"
            },
            cache: false
        });
    }

    function deleteSequenceReset(sequenceReset) {
        return $http({
            method: 'DELETE',
            url: 'api/latest/plugin/sequence/reset',
            data: sequenceReset,
            headers: {
                "Content-Type": "application/json"
            },
            cache: false
        });
    }
} ]);