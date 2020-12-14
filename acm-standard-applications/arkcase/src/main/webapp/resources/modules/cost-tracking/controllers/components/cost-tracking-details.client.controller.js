'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.DetailsController',
        [ '$scope', '$translate', '$stateParams', 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'ObjectService', 'Mentions.Service',
                function($scope, $translate, $stateParams, Util, ConfigService, CostTrackingInfoService, MessageService, HelperObjectBrowserService, ObjectService, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cost-tracking",
                        componentId: "details",
                        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo: CostTrackingInfoService.validateCostsheet
                    });

                    
                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.saveDetails = function() {
                        var costsheetInfo = Util.omitNg($scope.objectInfo);
                        CostTrackingInfoService.saveCostsheetInfo(costsheetInfo, "Save").then(function(costsheetInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.COSTSHEET, "DETAILS", costsheetInfo.id, costsheetInfo.details);
                            MessageService.info($translate.instant("costTracking.comp.details.informSaved"));
                            return costsheetInfo;
                        })
                    };

                } ]);