'use strict';

angular.module('services').factory('DocTreeExt.DownloadSelectedAsZip', ['$http'
    ,function ($http) {

    var _downloadSelectedFoldersAndFiles = function(selectedNodes){
        return $http({
            method: 'POST',
            url: 'api/latest/service/compressor/download',
            data: selectedNodes,
            headers: {
                'Accept': 'application/zip'
            },
        });
    };

    return {
        downloadSelectedFoldersAndFiles: _downloadSelectedFoldersAndFiles
    };

    }
]);