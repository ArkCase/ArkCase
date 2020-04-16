angular.module('services').factory('SimilarOrganizationService', ['$http', function ($http) {

    return {
        getSimilarOrganizationsByName: getSimilarOrganizationsByName
    };

    function getSimilarOrganizationsByName(organizationName){
            return $http({
                method: 'GET',
                url: 'api/latest/plugin/organizations/findExistingOrganization/' + organizationName
            });
    }

}]);