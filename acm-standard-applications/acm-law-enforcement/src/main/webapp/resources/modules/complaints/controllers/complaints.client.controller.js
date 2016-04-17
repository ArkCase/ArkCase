'use strict';

angular.module('complaints').controller('ComplaintsController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService, ComplaintInfoService, ObjectService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "complaints"
            , resetObjectInfo: ComplaintInfoService.resetComplaintInfo
            , getObjectInfo: ComplaintInfoService.getComplaintInfo
            , updateObjectInfo: ComplaintInfoService.updateComplaintInfo
            , getObjectIdFromInfo: function (complaintInfo) {
                return Util.goodMapValue(complaintInfo, "complaintId");
            }
            , getObjectTypeFromInfo: function (complaintInfo) {
                return ObjectService.ObjectTypes.COMPLAINT;
            }
        });

    }
]);