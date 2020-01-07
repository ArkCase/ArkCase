'use strict';

angular.module('services').factory('Cases.SuggestedCases', [ '$http', function($http) {

    return ({
        getSuggestedCases: getSuggestedCases
    });

    function getSuggestedCases(title, id) {
        return $http({
            url: 'api/latest/service/suggestion/' + title,
            method: 'GET',
            params:{
                objectId: id
            }
        });
    }

} ]);
