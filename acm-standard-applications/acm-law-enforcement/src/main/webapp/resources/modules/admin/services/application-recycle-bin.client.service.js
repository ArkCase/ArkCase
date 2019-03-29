'use strict';

angular.module('admin').factory('Admin.RecycleBinService', [ '$http', function($http) {

    return ({
        findRecycleBinItems: findRecycleBinItems,
        restoreItemsFromRecycleBin: restoreItemsFromRecycleBin,
        removeItemsFromRecycleBin: removeItemsFromRecycleBin
    });

        function findRecycleBinItems(params) {
        return $http({
            method: 'GET',
            url: 'api/latest/service/recycleBin',
            params: {
                start: params.start,
                n: params.maxRows,
                sortBy: params.sortBy,
                sortDir: params.sortDir
            }
        });
    }

    function restoreItemsFromRecycleBin(filesToBeRestored) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/recycleBin',
            data: filesToBeRestored,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    function removeItemsFromRecycleBin (filesToBeDeleted) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/ecm/permanent',
            data: filesToBeDeleted,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }
} ]);