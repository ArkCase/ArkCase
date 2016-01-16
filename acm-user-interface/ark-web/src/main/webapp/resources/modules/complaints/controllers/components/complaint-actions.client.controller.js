'use strict';

angular.module('complaints').controller('Complaints.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Complaint.LookupService'
    , 'Object.SubscriptionService', 'Object.ModelService', 'Complaint.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, ComplaintLookupService
        , ObjectSubscriptionService, ObjectModelService, ComplaintInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("complaints", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        var promiseQueryUser = Authentication.queryUserInfo();
        var promiseGetGroups = ObjectLookupService.getGroups();


        var previousId = null;
        $scope.$on('object-updated', function (e, data) {
            updateData(data);
        });

        $scope.$on('object-refreshed', function (e, data) {
            var previousId = null;
            updateData(data);
        });

        var updateData = function (data) {
            if (!ComplaintInfoService.validateComplaintInfo(data)) {
                return;
            }
            $scope.complaintInfo = data;

            var group = ObjectModelService.getGroup(data);
            var assignee = ObjectModelService.getAssignee(data);
            if (previousId != $stateParams.id) {
                var promiseGetApprovers = ComplaintLookupService.getApprovers(group, assignee);
                $q.all([promiseQueryUser, promiseGetGroups, promiseGetApprovers]).then(function (data) {
                    var userInfo = data[0];
                    var groups = data[1];
                    var assignees = data[2];
                    $scope.restricted = ObjectModelService.checkRestriction(userInfo.userId, assignee, group, assignees, groups);
                });


                promiseQueryUser.then(function (userInfo) {
                    $scope.userId = userInfo.userId;
                    ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.COMPLAINT, $scope.complaintInfo.complaintId).then(function (subscriptions) {
                        var found = _.find(subscriptions, {
                            userId: userInfo.userId,
                            subscriptionObjectType: ObjectService.ObjectTypes.COMPLAINT,
                            objectId: $scope.complaintInfo.complaintId
                        });
                        $scope.showBtnSubscribe = Util.isEmpty(found);
                        $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    });
                });

                previousId = $stateParams.id;
            }
        };

        $scope.restricted = false;
        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.complaintInfo.restricted) {
                $scope.complaintInfo.restricted = $scope.restricted;

                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
            }
        };

        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-complaint"
            });
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
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.COMPLAINT, $scope.complaintInfo.complaintId).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (complaintInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.COMPLAINT, $scope.complaintInfo.complaintId).then(function (data) {
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