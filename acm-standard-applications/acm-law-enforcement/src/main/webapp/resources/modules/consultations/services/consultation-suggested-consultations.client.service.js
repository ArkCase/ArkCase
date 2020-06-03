'use strict';

angular.module('services').factory('Consultations.SuggestedConsultations', ['$http', function ($http) {

    return ({
        getSuggestedConsultations: getSuggestedConsultations
    });

    function getSuggestedConsultations(title, id) {
        return $http({
            url: 'api/latest/service/suggestion/' + encodeURIComponent(title.replace(/\//g, "%2F")),
            method: 'GET',
            params: {
                objectId: id
            }
        });
    }

} ]);
