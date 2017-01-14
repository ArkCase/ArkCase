'use strict';

angular.module('complaints').controller('Complaints.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Complaint.LookupService'
    , 'Object.SubscriptionService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, ComplaintLookupService
        , ObjectSubscriptionService, ComplaintInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "actions"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.restricted = objectInfo.restricted;
            $scope.showCreateAndClose = ($scope.objectInfo.status !== "CLOSED");
            var group = ObjectModelService.getGroup(objectInfo);
            $scope.owningGroup = group;
            var assignee = ObjectModelService.getAssignee(objectInfo);
            $scope.assignee = assignee;

            Authentication.queryUserInfo().then(function (userInfo) {
                $scope.userId = userInfo.userId;
                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.COMPLAINT
                    , $scope.objectInfo.complaintId).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.COMPLAINT,
                        objectId: $scope.objectInfo.complaintId
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });
            });

            $scope.closeParams = {
                complaintId: objectInfo.complaintId
                , complaintNumber: objectInfo.complaintNumber
            };
        };

        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.objectInfo.restricted) {
                $scope.objectInfo.restricted = $scope.restricted;

                var complaintInfo = Util.omitNg($scope.objectInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo).then(function () {

                }, function () {
                    $scope.restricted = !$scope.restricted;
                });
            }
        };

        $scope.subscribe = function (complaintInfo) {
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.COMPLAINT, complaintInfo.complaintId).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (complaintInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.COMPLAINT, complaintInfo.complaintId).then(function (data) {
                $scope.showBtnSubscribe = true;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

        UserInfoService.getUserInfo().then(function (infoData) {
            $scope.currentUserProfile = infoData;
        });

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        $scope.claim = function (objectInfo) {
            ObjectModelService.setAssignee(objectInfo, $scope.currentUserProfile.userId);
            objectInfo.modified = null;//this is because we need to trigger update on case file
            ComplaintInfoService.saveComplaintInfo(objectInfo).then(function (response) {
                //success
                $scope.refresh();
            });
        };

        $scope.unclaim = function (objectInfo) {
            ObjectModelService.setAssignee(objectInfo, "");
            objectInfo.modified = null;//this is because we need to trigger update on case file
            ComplaintInfoService.saveComplaintInfo(objectInfo).then(function (response) {
                //success
                $scope.refresh();
            });
        };
    }
]);