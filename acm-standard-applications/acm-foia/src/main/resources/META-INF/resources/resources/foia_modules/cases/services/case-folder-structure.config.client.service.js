'use strict';

angular.module('services').service('Case.FolderStructureService', function($http) {
    return ({
        getFolderStructure: getFolderStructure
    });


    function getFolderStructure() {
        return $http({
            method: "GET",
            url: "api/latest/plugin/casefile/folderstructure/config"
        })
    }
});