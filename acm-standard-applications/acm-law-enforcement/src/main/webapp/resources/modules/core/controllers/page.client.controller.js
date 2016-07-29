'use strict';

angular.module('core').controller('PageController', ['$scope', '$modal', 'Acm.LoginService', 'LoginWarningService',
    function ($scope, $modal, AcmLoginService, LoginWarningService)
    {
        $scope.isLeftMenuCollapsed = false;

        $scope.$on('isLeftMenuCollapsed', function (e, isLeftMenuCollapsed)
        {
            $scope.isLeftMenuCollapsed = isLeftMenuCollapsed;
        });

        LoginWarningService.queryLoginWarning().then(
            function (data)
            {
                if (data.enabled)
                {
                    var warningAccepted = sessionStorage.getItem('warningAccepted');
                    if (!warningAccepted)
                    {
                        showModalWarning(data);
                    }
                }
            });


        function showModalWarning(data)
        {
            var params = {
                message: data.message
            };

            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/core/views/warning-modal.client.view.html',
                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params)
                {
                    $scope.message = params.message;
                    $scope.onClickOk = function ()
                    {
                        $modalInstance.close({accepted: true});
                    };
                    $scope.onClickCancel = function ()
                    {
                        $modalInstance.close({accepted: false});
                    }
                }],
                size: 'lg',
                backdrop: "static",
                resolve: {
                    params: function ()
                    {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (data)
            {
                if (data.accepted)
                {
                    //put in local/session storage that user has accepted warning
                    sessionStorage.setItem('warningAccepted', true);
                }
                else
                {
                    //redirect to logout
                    AcmLoginService.logout();
                }
            });
        }
    }
]);
