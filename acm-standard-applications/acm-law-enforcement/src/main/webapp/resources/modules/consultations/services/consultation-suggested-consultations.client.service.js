'use strict';

angular.module('services').factory('Consultation.SuggestedConsultations', ['$http', 'base64', function ($http, base64) {

    return ({
        getSuggestedConsultations: getSuggestedConsultations
    });

    function getSuggestedConsultations(title, id) {
        return $http({
            url: 'api/latest/service/suggestion/' + base64.urlencode(title),
            method: 'GET',
            params: {
                objectId: id,
                objectType: "CONSULTATION"
            }
        });
    }

} ]);
