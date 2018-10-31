'use strict';

angular.module('request-info').controller('RequestInfo.SnowBoundViewer', [ '$rootScope', '$scope', '$sce', '$timeout', '$interval', 'UtilService', 'LookupService', function($rootScope, $scope, $sce, $timeout, $interval, Util, LookupService) {
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
        startInitSnowboundMessaging();
    };

    var initSnowboundMessagingPromise;

    function startInitSnowboundMessaging() {
        stopInitSnowboundMessaging();
        initSnowboundMessagingPromise = $interval(initSnowboundMessaging, 250);
    }

    function stopInitSnowboundMessaging() {
        $interval.cancel(initSnowboundMessagingPromise);
    }

    function initSnowboundMessaging() {
        var snowboundIframe = getSnowboundIframe();
        if (!Util.isEmpty(snowboundIframe)) {
            stopInitSnowboundMessaging();
            if (Util.isEmpty($rootScope.snowboundMessaging)) {
                $rootScope.snowboundMessaging = {};
                $rootScope.snowboundMessaging.receiver = snowboundIframe;
                $rootScope.snowboundMessaging.send = function send(message) {
                    if (!Util.isEmpty($rootScope.snowboundMessaging.receiver)) {
                        var targetOrigin = getTargetOrigin();
                        $rootScope.snowboundMessaging.receiver.postMessage(message, targetOrigin);
                    }
                };
                $rootScope.snowboundMessaging.receive = function receive(e) {
                    if (!Util.isEmpty(e) && !Util.isEmpty(e.data) && !Util.isEmpty(e.data.source) && e.data.source == "snowbound" && !Util.isEmpty(e.data.action)) {
                        // Do actions sent from Frevvo
                        if (e.data.action == "close-document") {
                            onCloseDocument(e.data.data.id, e.data.data.version);
                        }
                    }
                };

                window.addEventListener("message", $rootScope.snowboundMessaging.receive);
            } else {
                $rootScope.snowboundMessaging.receiver = snowboundIframe;
            }
        }
    }

    function getTargetOrigin() {
        var host = $scope.ecmFileProperties['ecm.viewer.snowbound'];
        var targetOrigin = '*';
        if (host) {
            targetOrigin = host;
        }
        return targetOrigin;
    }

    function getSnowboundIframe() {
        if (!Util.isEmpty(document) && !Util.isEmpty(document.getElementById('snowboundIframe')) && !Util.isEmpty(document.getElementById('snowboundIframe').contentWindow) && !Util.isEmpty(document.getElementById('snowboundIframe').contentWindow.document)
            && !Util.isEmpty(document.getElementById('snowboundIframe').contentWindow.document.getElementsByTagName('iframe')) && document.getElementById('snowboundIframe').contentWindow.document.getElementsByTagName('iframe').length > 0
            && !Util.isEmpty(document.getElementById('snowboundIframe').contentWindow.document.getElementsByTagName('iframe')[0]) && !Util.isEmpty(document.getElementById('snowboundIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow)) {
            return document.getElementById('snowboundIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow;
        }

        return null;
    }

    function onCloseDocument(id, version){
        $scope.$bus.publish('remove-from-opened-documents-list', {id: id, version: version});
    }

} ]);
