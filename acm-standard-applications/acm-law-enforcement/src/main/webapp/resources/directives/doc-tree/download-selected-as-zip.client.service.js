'use strict';

angular.module('services').factory('DocTreeExt.DownloadSelectedAsZip', [ '$http', function($http) {

    var _downloadSelectedFoldersAndFiles = function(selectedNodes) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/compressor/download',
            data: selectedNodes,
            responseType: 'arraybuffer',
            headers: {
                'Accept': 'application/zip'
            },
        });
    };

    var _downloadSelectedFiles = function(selectedNodes) {
        return $http({
            method: 'POST',
            url: 'api/latest/service/compressor/download/files',
            data: JSON.stringify(selectedNodes),
            responseType: 'arraybuffer',
            headers: {
                'Content-Type': 'application/json'
            },
        });
    };

    return {
        downloadSelectedFoldersAndFiles: _downloadSelectedFoldersAndFiles,
        downloadSelectedFiles: _downloadSelectedFiles
    };

} ]);