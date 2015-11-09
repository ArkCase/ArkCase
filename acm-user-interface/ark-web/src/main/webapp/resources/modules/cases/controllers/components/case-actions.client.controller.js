'use strict';

/**
 * @ngdoc controller
 * @name cases:Cases.ActionsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/controllers/components/case-actions.client.controller.js modules/cases/controllers/components/case-actions.client.controller.js}
 *
 * The Cases module actions controller
 */
angular.module('cases').controller('Cases.ActionsController', ['$scope', '$state', '$q', 'ConfigService', 'CasesService', 'UtilService', 'ValidationService', 'Authentication', 'SubscriptionService',
    function ($scope, $state, $q, ConfigService, CasesService, Util, Validator, Authentication, SubscriptionService) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.userId = '';
        $scope.isSubscribed = false;

        // Obtains the currently logged in user
        var userInfo = Authentication.queryUserInfo({});

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;

                userInfo.$promise.then(function (data) {
                    $scope.userId = data.userId;

                    // Obtains the existing subscriptions from ArkCase
                    SubscriptionService.getSubscribers({userId: $scope.userId, objectId: $scope.caseInfo.id})
                        .then(function (data) {
                            var subscriptionArray = data.data;

                            // Is the currently logged in user subscribed to this case file already?
                            $scope.isSubscribed = SubscriptionService.isSubscribed($scope.userId, $scope.caseInfo, subscriptionArray);
                        });
                })
            }
        });

        /**
         * @ngdoc method
         * @name loadNewCaseFrevvoForm
         * @methodOf cases:Cases.ActionsController
         *
         * @description
         * Displays the create new case Frevvo form for the user
         */
        $scope.loadNewCaseFrevvoForm = function () {
            $state.go('wizard');
        };

        /**
         * @ngdoc method
         * @name loadChangeCaseStatusFrevvoForm
         * @methodOf cases:Cases.ActionsController
         *
         * @description
         * Displays the change case status Frevvo form for the user
         *
         * @param {Object} caseInfo contains the metadata for the existing case which will be edited
         */
        $scope.loadChangeCaseStatusFrevvoForm = function (caseInfo) {
            if (caseInfo && caseInfo.id && caseInfo.caseNumber && caseInfo.status) {
                $state.go('status', {id: caseInfo.id, caseNumber: caseInfo.caseNumber, status: caseInfo.status});
            }
        };

        /**
         * @ngdoc method
         * @name subscribeCase
         * @methodOf cases:Cases.ActionsController
         *
         * @description
         * Subscribes the currently logged in user to the given case
         *
         * @param {Object} caseInfo contains the metadata for the existing case which will be subscribed
         */
        $scope.subscribeCase = function (caseInfo) {
            SubscriptionService.subscribe({userId: $scope.userId, objectId: caseInfo.id})
                .then(function (data) {
                    if (data && data.data && SubscriptionService.isSubscribed($scope.userId, caseInfo, [data.data])) {
                        $scope.isSubscribed = true;
                    }
                });
        };

        /**
         * @ngdoc method
         * @name unsubscribeCase
         * @methodOf cases:Cases.ActionsController
         *
         * @description
         * Unsubscribes the currently logged in user from the given case
         *
         * @param {Object} caseInfo contains the metadata for the existing case which will be unsubscribed
         */
        $scope.unsubscribeCase = function (caseInfo) {
            SubscriptionService.unsubscribe({userId: $scope.userId, objectId: caseInfo.id})
                .then(function (data) {
                    if (data && data.data && data.data.deletedSubscriptionId &&
                        data.data.deletedSubscriptionId == caseInfo.id) {
                        $scope.isSubscribed = false;
                    }
                });
        };
    }
]);