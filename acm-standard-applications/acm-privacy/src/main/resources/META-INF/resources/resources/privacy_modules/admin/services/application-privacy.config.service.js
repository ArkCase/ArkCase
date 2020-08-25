angular.module('admin').service('Admin.PrivacyConfigService', function ($http) {
    return ({
        savePrivacyConfig: savePrivacyConfig,
        getPrivacyConfig: getPrivacyConfig
    });

    function savePrivacyConfig(privacyConfig) {
        return $http({
            method: "POST",
            url: "api/latest/service/privacy/configuration",
            data: privacyConfig,
            headers: {
                "Content-Type": "application/json"
            }
        })
    }

    function getPrivacyConfig() {
        return $http({
            method: "GET",
            url: "api/latest/service/privacy/configuration"
        })
    }
});