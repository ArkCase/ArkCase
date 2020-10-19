angular.module('services').service('Case.ExemptionService', function ($http) {
    return ({
        saveExemptionCode: saveExemptionCode,
        getExemptionCode: getExemptionCode,
        deleteExemptionCode: deleteExemptionCode,
        hasExemptionOnAnyDocumentsOnRequest: hasExemptionOnAnyDocumentsOnRequest,
        saveExemptionStatute: saveExemptionStatute,
        getExemptionStatute: getExemptionStatute,
        deleteExemptionStatute: deleteExemptionStatute
    });

    function saveExemptionCode(data) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/exemption/tags',
            data: data,
            headers: {
                'Content-Type': 'application/json'
            }
        })
    }

    function getExemptionCode(parentObjectId, parentObjectType) {
        return $http({
            method: 'GET',
            url: 'api/latest/service/exemption/' + parentObjectId + '/' + parentObjectType + '/tags',
            cache: false
        })
    }


    function deleteExemptionCode(tagId) {
        return $http({
            method: 'DELETE',
            url: 'api/latest/service/exemption/' + tagId
        })
    }

    function saveExemptionStatute(data) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/exemptionStatute/tags',
            data: data,
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    function hasExemptionOnAnyDocumentsOnRequest(parentObjectId, parentObjectType) {
        return $http({
            method: 'GET',
            url: 'api/latest/service/exemption/' + parentObjectId + '/' + parentObjectType + '/exemptions'
        });
    }

// exemption statute service
    function getExemptionStatute(parentObjectId, parentObjectType) {
        return $http({
            method: 'GET',
            url: 'api/latest/service/exemptionStatute/' + parentObjectId + '/' + parentObjectType + '/tags',
            cache: false
        })
    }

    function saveExemptionStatute(data) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/statute/tags',
            data: data,
            headers: {
                'Content-Type': 'application/json'
            }
        })
    }

    function deleteExemptionStatute(tagId) {
        return $http({
            method: 'DELETE',
            url: 'api/latest/service/statute/' + tagId
        })
    }

});
