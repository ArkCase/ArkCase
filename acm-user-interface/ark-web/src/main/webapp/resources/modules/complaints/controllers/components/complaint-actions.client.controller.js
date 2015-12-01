'use strict';

angular.module('complaints').controller('Complaints.ActionsController', ['$scope', '$state', '$stateParams', '$q', 'UtilService'
    , 'ConstantService', 'Authentication', 'Object.LookupService', 'Complaint.LookupService', 'Object.SubscriptionService', 'Object.ModelService', 'Complaint.InfoService'
    , function ($scope, $state, $stateParams, $q, Util, Constant, Authentication, ObjectLookupService, ComplaintLookupService, ObjectSubscriptionService, ObjectModelService, ComplaintInfoService) {

        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        var promiseQueryUser = Authentication.queryUserInfoNew();

        var promiseGetGroups = ObjectLookupService.getGroups();

        var previousId = null;
        $scope.$on('complaint-updated', function (e, data) {
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
                    ObjectSubscriptionService.getSubscriptions(userInfo.userId, Constant.ObjectTypes.COMPLAINT, $scope.complaintInfo.id).then(function (subscriptions) {
                        var found = _.find(subscriptions, {
                            userId: userInfo.userId,
                            subscriptionObjectType: Constant.ObjectTypes.COMPLAINT,
                            objectId: $scope.complaintInfo.id
                        });
                        $scope.showBtnSubscribe = Util.isEmpty(found);
                        $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    });
                });

                previousId = $stateParams.id;
            }
        });

        $scope.restricted = false;
        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.complaintInfo.restricted) {
                $scope.complaintInfo.restricted = $scope.restricted;

                var complaintInfo = Util.omitNg($scope.complaintInfo);
                ComplaintInfoService.saveComplaintInfo(complaintInfo);
            }
        };

        $scope.createNew = function () {
            $state.go('newComplaint');

        };

        $scope.close = function () {
            $state.go('closeComplaint');
        };

        $scope.subscribe = function (complaintInfo) {
            ObjectSubscriptionService.subscribe($scope.userId, Constant.ObjectTypes.COMPLAINT, $scope.complaintInfo.id).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (complaintInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, Constant.ObjectTypes.COMPLAINT, $scope.complaintInfo.id).then(function (data) {
                $scope.showBtnSubscribe = true;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

    }
]);