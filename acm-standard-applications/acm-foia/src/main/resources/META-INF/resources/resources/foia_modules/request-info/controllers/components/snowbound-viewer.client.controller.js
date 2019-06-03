'use strict';

angular.module('request-info').controller('RequestInfo.SnowBoundViewer', [ '$rootScope', '$scope', '$sce', '$timeout', '$interval', '$modal', 'UtilService', 'LookupService', 'ArkCaseCrossWindowMessagingService', 'Object.LookupService', function($rootScope, $scope, $sce, $timeout, $interval, $modal, Util, LookupService, ArkCaseCrossWindowMessagingService, ObjectLookupService) {
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

    function onShowLoader() {
        var loaderModal = $modal.open({
            animation: true,
            templateUrl: 'modules/common/views/object.modal.loading-spinner.html',
            size: 'sm',
            backdrop: 'static'
        });
        $scope.loaderModal = loaderModal;
    }

    function onHideLoader() {
        $scope.loaderModal.close();
    }

    $scope.iframeLoaded = function() {
        ArkCaseCrossWindowMessagingService.addHandler('show-loader', onShowLoader);
        ArkCaseCrossWindowMessagingService.addHandler('hide-loader', onHideLoader);
        ArkCaseCrossWindowMessagingService.addHandler('close-document', onCloseDocument);
        ArkCaseCrossWindowMessagingService.start();

        ObjectLookupService.getLookupByLookupName("annotationTags").then(function (allAnnotationTags) {
            $scope.allAnnotationTags = allAnnotationTags;
            ArkCaseCrossWindowMessagingService.addHandler('select-annotation-tags', onSelectAnnotationTags);
            ArkCaseCrossWindowMessagingService.start('snowbound', $scope.ecmFileProperties['ecm.viewer.snowbound']);
        });
    };

    function onCloseDocument(data){
        $scope.$bus.publish('remove-from-opened-documents-list', {id: data.id, version: data.version});
    }
    

    function onSelectAnnotationTags(data) {
        var params = $scope.allAnnotationTags;
        var modalInstance = $modal.open({
            animation: true,
            moduleName: 'document-details',
            templateUrl: 'modules/document-details/views/components/annotation-tags-modal.client.view.html',
            controller: 'Document.AnnotationTagsModalController',
            backdrop: 'static',
            resolve: {
                params: function () {
                    return params;
                }
            }
        });

        modalInstance.result.then(function(result) {
            var message = {
                source: 'arkcase',
                action: 'add-annotation-tags',
                data: {
                    type: data.type,
                    annotationTags: result.annotationTags,
                    annotationNotes: result.annotationNotes
                }
            };
            ArkCaseCrossWindowMessagingService.send(message);
        }, function() {
            // Do nothing
        });
    }

} ]);
