angular.module('services').factory('SimilarOrganizationService', ['$http', 'UtilService', function ($http, Util) {

    return {
        getSimilarOrganizationsByName: getSimilarOrganizationsByName
    };

    function getSimilarOrganizationsByName(organizationName){
            return $http({
                method: 'GET',
                url: 'api/latest/plugin/organizations/searchExisting/' + organizationName,
                cache: false,
                isArray: false,
                transformResponse: Util.transformSearchResponse
            })
    }

}]);