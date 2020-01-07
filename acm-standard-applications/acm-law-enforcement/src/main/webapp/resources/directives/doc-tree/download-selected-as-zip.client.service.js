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
            method: 'GET',
            url: 'api/latest/service/compressor/download/files',
            params: {
                "fileIds": selectedNodes
            },
            responseType: 'arraybuffer'
        });
    };

    var _downloadZipFile = function (selectedNodes) {
        return $http({
            method: 'GET',
            url: 'api/latest/service/compressor/download/files/zip',
            params: {
                "zipFilePath": selectedNodes
            },
            responseType: 'arraybuffer'
        });
    };

    return {
        downloadSelectedFoldersAndFiles: _downloadSelectedFoldersAndFiles,
        downloadSelectedFiles: _downloadSelectedFiles,
        downloadZipFile: _downloadZipFile
    };

} ]);