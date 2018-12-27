'use strict';

angular.module('request-info').controller('RequestInfo.SnowBoundViewer', [ '$rootScope', '$scope', '$sce', '$timeout', '$interval', 'UtilService', 'LookupService', 'ArkCaseCrossWindowMessagingService', function($rootScope, $scope, $sce, $timeout, $interval, Util, LookupService, ArkCaseCrossWindowMessagingService) {
    var SNOWBOUND_TIMEOUT = 100; // This delay prevents multiple snowboud requests

    $scope.$on('change-viewer-document', showDocument);

    var timeoutPromise = null;

    LookupService.getConfig("ecmFileService").then(function(data){
        $scope.ecmFileProperties = data;
    });

    /**
     * Show Snowbound document
     * @param e
     * @param url
     */
    function showDocument(e, url) {
        // If few requests to showDocument arrived, then it shows latest only
        if (timeoutPromise) {
            $timeout.cancel(timeoutPromise)
        }
        timeoutPromise = $timeout(function() {
            $scope.viewerDocumentUrl = $sce.trustAsResourceUrl(url);
            timeoutPromise = null;
        }, SNOWBOUND_TIMEOUT);
    }

    $scope.iframeLoaded = function() {
        ArkCaseCrossWindowMessagingService.addHandler('close-document', onCloseDocument);
        ArkCaseCrossWindowMessagingService.start();
    };

    function onCloseDocument(data){
        $scope.$bus.publish('remove-from-opened-documents-list', {id: data.id, version: data.version});
    }

} ]);
