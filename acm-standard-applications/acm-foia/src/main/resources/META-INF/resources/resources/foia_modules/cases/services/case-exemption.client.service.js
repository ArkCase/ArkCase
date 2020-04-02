angular.module('services').service('Case.ExemptionService', function ($http) {
    return ({
        saveExemptionCode: saveExemptionCode,
        getExemptionCode: getExemptionCode,
        deleteExemptionCode: deleteExemptionCode,
        saveExemptionStatute: saveExemptionStatute
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
            url: 'api/latest/service/exemption/' + parentObjectId + '/' + parentObjectType + '/tags'
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
            method: 'PUT',
            url: 'api/latest/service/exemption/statute',
            data: data
        })
    }

});
