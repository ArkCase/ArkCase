'use strict';

angular.module('services').factory('Cases.SuggestedCases', ['$http', function ($http) {

    return ({
        getSuggestedCases: getSuggestedCases
    });

    function getSuggestedCases(title, id) {
        return $http({
            url: 'api/latest/service/suggestion/' + encodeURIComponent(title.replace(/\//g, "%2F")),
            method: 'GET',
            params: {
                objectId: id
            }
        });
    }

} ]);
