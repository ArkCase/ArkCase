'use strict';

angular.module('people').controller('People.ActionsController', ['$scope', '$state', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Person.LookupService'
    , 'Object.SubscriptionService', 'Person.InfoService', 'Helper.ObjectBrowserService', 'Object.ModelService', 'Profile.UserInfoService'
    , function ($scope, $state, $stateParams, $q
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, PersonLookupService
        , ObjectSubscriptionService, PersonInfoService, HelperObjectBrowserService, ObjectModelService, UserInfoService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "people"
            , componentId: "actions"
            , retrieveObjectInfo: PersonInfoService.getPersonInfo
            , validateObjectInfo: PersonInfoService.validatePersonInfo
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
                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.PERSON
                    , $scope.objectInfo.personId).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.PERSON,
                        objectId: $scope.objectInfo.id
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });
            });
        };


        $scope.subscribe = function (objectInfo) {
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.PERSON, objectInfo.id).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (objectInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.PERSON, objectInfo.id).then(function (data) {
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