'use strict';

angular.module('complaints').controller('Complaints.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Complaint.LookupService'
    , 'Object.SubscriptionService', 'Object.ModelService', 'Complaint.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, ComplaintLookupService
        , ObjectSubscriptionService, ObjectModelService, ComplaintInfoService, HelperObjectBrowserService) {

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

        var promiseQueryUser = Authentication.queryUserInfo();
        var promiseGetGroups = ObjectLookupService.getGroups();

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;

            var group = ObjectModelService.getGroup(objectInfo);
            var assignee = ObjectModelService.getAssignee(objectInfo);
            var promiseGetApprovers = ComplaintLookupService.getApprovers(group, assignee);
            $q.all([promiseQueryUser, promiseGetGroups, promiseGetApprovers]).then(function (data) {
                var userInfo = data[0];
                var groups = data[1];
                var assignees = data[2];
                $scope.restricted = ObjectModelService.checkRestriction(userInfo.userId, assignee, group, assignees, groups);
            });


            promiseQueryUser.then(function (userInfo) {
                $scope.userId = userInfo.userId;
                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.COMPLAINT, $scope.objectInfo.complaintId).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.COMPLAINT,
                        objectId: $scope.objectInfo.complaintId
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });
            });
        };

        $scope.restricted = false;
        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.objectInfo.restricted) {
                $scope.objectInfo.restricted = $scope.restricted;

                var complaintInfo = Util.omitNg($scope.objectInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
            }
        };

        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-complaint"
            });


            var targetType = ObjectService.ObjectTypes.COMPLAINT;
            var targetId = Util.goodMapValue(rowEntity, "object_id_s");
            gridHelper.showObject(targetType, targetId);
        };

        $scope.close = function (complaintInfo) {
            $state.go("frevvo", {
                name: "close-complaint"
                , arg: {
                    complaintId: complaintInfo.complaintId
                    , complaintNumber: complaintInfo.complaintNumber
                    , mode: "create"
                }
            });
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

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

    }
]);