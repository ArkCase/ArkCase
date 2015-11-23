'use strict';

angular.module('cases').controller('Cases.ActionsController', ['$scope', '$state', 'UtilService', 'ConstantService', 'Authentication', 'Object.SubscriptionService'
    , function ($scope, $state, Util, Constant, Authentication, ObjectSubscriptionService) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        var promiseQueryUser = Authentication.queryUserInfoNew();

        $scope.$on('case-updated', function (e, data) {
            $scope.caseInfo = data;

            promiseQueryUser.then(function (userInfo) {
                $scope.userId = userInfo.userId;

                ObjectSubscriptionService.getSubscriptions(userInfo.userId, Constant.ObjectTypes.CASE_FILE, $scope.caseInfo.id).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: Constant.ObjectTypes.CASE_FILE,
                        objectId: $scope.caseInfo.id
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });

                //// Obtains the existing subscriptions from ArkCase
                //SubscriptionService.getSubscribers({userId: $scope.userId, objectId: $scope.caseInfo.id})
                //    .then(function (data) {
                //        var subscriptionArray = data.data;
                //
                //        // Is the currently logged in user subscribed to this case file already?
                //        $scope.isSubscribed = SubscriptionService.isSubscribed($scope.userId, $scope.caseInfo, subscriptionArray);
                //    });
            })
        });

        $scope.isRestrict = function () {
            console.log("isRestrict");
            return true;
        };

        $scope.onClickRestrict = function ($event) {
            console.log("onClickRestrict");
        };

        $scope.createNew = function () {
            $state.go('cases.wizard');
        };

        $scope.edit = function () {
            console.log('edit');
        };

        $scope.changeStatus = function (caseInfo) {
            if (caseInfo && caseInfo.id && caseInfo.caseNumber && caseInfo.status) {
                $state.go('cases.status', {id: caseInfo.id, caseNumber: caseInfo.caseNumber, status: caseInfo.status});
            }
        };
        $scope.reinvestigate = function () {
            console.log('reinvestigate');
        };
        $scope.subscribe = function (caseInfo) {
            ObjectSubscriptionService.subscribe($scope.userId, Constant.ObjectTypes.CASE_FILE, $scope.caseInfo.id).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (caseInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, Constant.ObjectTypes.CASE_FILE, $scope.caseInfo.id).then(function (data) {
                $scope.showBtnSubscribe = true;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

        $scope.merge = function () {
            console.log('merge');
        };

        $scope.split = function () {
            console.log('split');
        };

        ///**
        // * ngdoc method
        // * name loadNewCaseFrevvoForm
        // * methodOf cases:Cases.ActionsController
        // *
        // * @description
        // * Displays the create new case Frevvo form for the user
        // */
        //$scope.loadNewCaseFrevvoForm = function () {
        //    $state.go('newcase');
        //};
        //
        ///**
        // * ngdoc method
        // * name loadChangeCaseStatusFrevvoForm
        // * methodOf cases:Cases.ActionsController
        // *
        // * @description
        // * Displays the change case status Frevvo form for the user
        // *
        // * @param {Object} caseInfo contains the metadata for the existing case which will be edited
        // */
        //$scope.loadChangeCaseStatusFrevvoForm = function (caseInfo) {
        //    if (caseInfo && caseInfo.id && caseInfo.caseNumber && caseInfo.status) {
        //        $state.go('status', {id: caseInfo.id, caseNumber: caseInfo.caseNumber, status: caseInfo.status});
        //    }
        //};


        ///**
        // * ngdoc method
        // * name subscribeCase
        // * methodOf cases:Cases.ActionsController
        // *
        // * @description
        // * Subscribes the currently logged in user to the given case
        // *
        // * @param {Object} caseInfo contains the metadata for the existing case which will be subscribed
        // */
        //$scope.subscribeCase = function (caseInfo) {
        //    SubscriptionService.subscribe({userId: $scope.userId, objectId: caseInfo.id})
        //        .then(function (data) {
        //            if (data && data.data && SubscriptionService.isSubscribed($scope.userId, caseInfo, [data.data])) {
        //                $scope.isSubscribed = true;
        //            }
        //        });
        //};
        //
        ///**
        // * ngdoc method
        // * name unsubscribeCase
        // * methodOf cases:Cases.ActionsController
        // *
        // * @description
        // * Unsubscribes the currently logged in user from the given case
        // *
        // * @param {Object} caseInfo contains the metadata for the existing case which will be unsubscribed
        // */
        //$scope.unsubscribeCase = function (caseInfo) {
        //    SubscriptionService.unsubscribe({userId: $scope.userId, objectId: caseInfo.id})
        //        .then(function (data) {
        //            if (data && data.data && data.data.deletedSubscriptionId &&
        //                data.data.deletedSubscriptionId == caseInfo.id) {
        //                $scope.isSubscribed = false;
        //            }
        //        });
        //};
    }
]);