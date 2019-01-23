angular.module('admin').service('Admin.FoiaConfigService', function($http){
    return ({
        saveFoiaConfig: saveFoiaConfig,
        getFoiaConfig: getFoiaConfig
    });

    function saveFoiaConfig(foiaConfig){
        return $http({
            method: "POST",
            url: "api/latest/service/foia/configuration",
            data: foiaConfig,
            headers: {
                "Content-Type": "application/json"
            }
        })
    }

    function getFoiaConfig(){
        return $http({
            method: "GET",
            url: "api/latest/service/foia/configuration"
        })
    }
});