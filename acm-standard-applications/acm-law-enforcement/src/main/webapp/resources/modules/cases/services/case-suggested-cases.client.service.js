'use strict';

angular.module('services').factory('Cases.SuggestedCases', ['$http', 'base64', function ($http, base64) {

    return ({
        getSuggestedCases: getSuggestedCases
    });

    function getSuggestedCases(title, id) {
        return $http({
            url: 'api/latest/service/suggestion/' + base64.urlencode(title),
            method: 'GET',
            params: {
                objectId: id,
                objectType: "CASE_FILE"
            }
        });
    }

}]);
