'use strict';

angular.module('dashboard.approvalRouting', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('organizations', {
                    title: 'Approval Routing',
                    description: 'Displays approval routing',
                    controller: 'Dashboard.ApprovalRoutingController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/approval-routing-widget.client.view.html',
                    commonName: 'approvalrouting'
                }
            );
    })
    .controller('Dashboard.ApprovalRoutingController', ['$scope', '$stateParams'
        , function ($scope, $stateParams) {

        }
    ]);