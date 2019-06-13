'use strict';

angular.module('cases').factory('Cases.SuggestedCases', [ '$http', function($http) {

    return ({
        getSuggestedCases: getSuggestedCases,
    });

    function getSuggestedCases(title) {
        return $http({
            url: '/api/v1/service/suggestion',
            method: 'GET',
            isArray: true,
            params: {
                title:title
            }
        });
    }

} ]);
