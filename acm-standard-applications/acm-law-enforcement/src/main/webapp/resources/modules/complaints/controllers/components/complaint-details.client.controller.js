'use strict';

angular.module('complaints').controller(
        'Complaints.DetailsController',
        [
                '$scope',
                '$stateParams',
                '$translate',
                'UtilService',
                'ConfigService',
                'Complaint.InfoService',
                'MessageService',
                'Helper.ObjectBrowserService',
                function($scope, $stateParams, $translate, Util, ConfigService, ComplaintInfoService, MessageService,
                        HelperObjectBrowserService) {

                    new HelperObjectBrowserService.Component({
                        scope : $scope,
                        stateParams : $stateParams,
                        moduleId : "complaints",
                        componentId : "details",
                        retrieveObjectInfo : ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo : ComplaintInfoService.validateComplaintInfo
                    });

                    $scope.options = {
                        focus : true,
                        dialogsInBody : true
                    //,height: 120
                    };

                    $scope.saveDetails = function() {
                        var complaintInfo = Util.omitNg($scope.objectInfo);
                        ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function(complaintInfo) {
                            MessageService.info($translate.instant("complaints.comp.details.informSaved"));
                            return complaintInfo;
                        });
                    };
                } ]);