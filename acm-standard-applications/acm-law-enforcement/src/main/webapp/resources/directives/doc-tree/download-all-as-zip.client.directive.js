'use strict';

/**
 * @ngdoc directive
 * @name global.directive:downloadAllAsZip
 * @restrict E
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/download-all-as-zip.directive.js directives/doc-tree/download-all-as-zip.directive.js}
 *
 * The "Download All As Zip" directive contains the method to download all documents of an object into a single compressed folder.
 *
 * @example
 <example>
 <file name="index.html">
 <download-all-as-zip></download-all-as-zip>
 </file>
 </example>
 */

angular.module('directives').directive('downloadAllAsZip', ['MessageService', 'UtilService', '$translate'
    , function (MessageService, Util, $translate) {
        return {
            restrict: 'E',
            templateUrl: 'directives/doc-tree/download-all-as-zip.html',
            link: function (scope) {
                scope.downloadInProgress = false;

                scope.downloadAllAsZip = function () {
                    scope.downloadInProgress = true;

                    var folderId = Util.goodMapValue(scope.objectInfo, 'container.folder.id', false);

                    if (folderId) {
                        var url = "api/latest/service/compressor/download/" + folderId;

                        //Triggers a download popup, to allow the user to select where to download the file
                        $('#downloadForm').attr("action", url);
                        this.$input = $('<input>').attr({
                            folderId: folderId
                        });
                        this.$input.val(folderId).appendTo($('#downloadForm'));
                        $('#downloadForm').submit();
                        $('#downloadForm').empty();

                        scope.downloadInProgress = false;

                    } else {
                        scope.downloadInProgress = false;
                        MessageService.error($translate.instant('common.directive.downloadAllAsZip.message.containerError'));
                    }
                };
            }
        };
    }
]);