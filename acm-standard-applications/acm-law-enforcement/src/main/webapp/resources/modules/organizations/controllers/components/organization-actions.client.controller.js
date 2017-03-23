'use strict';

angular.module('organizations').controller('Organizations.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Organization.LookupService'
    , 'Object.SubscriptionService', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, OrganizationLookupService
        , ObjectSubscriptionService, OrganizationInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "actions"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        $scope.showBtnChildOutcomes = false;
        $scope.availableChildOutcomes = [];

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.showBtnChildOutcomes = false;

            Authentication.queryUserInfo().then(function (userInfo) {
                $scope.userId = userInfo.userId;
                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.ORGANIZATION
                    , $scope.objectInfo.complaintId).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.ORGANIZATION,
                        objectId: $scope.objectInfo.id
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });
            });
        };


        $scope.subscribe = function (objectInfo) {
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.ORGANIZATION, objectInfo.id).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (objectInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.ORGANIZATION, objectInfo.id).then(function (data) {
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

        $scope.onClickChildOutcome = function (name) {
            $scope.$bus.publish('CHILD_OBJECT_OUTCOME_CLICKED', name);
        };

        $scope.$bus.subscribe('CHILD_OBJECT_OUTCOMES_FOUND', function (outcomes) {
            $scope.availableChildOutcomes = outcomes;
            $scope.showBtnChildOutcomes = true;
        });
    }
]);