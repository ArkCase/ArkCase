'use strict';

angular.module('dashboard.approvalRouting', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('approvalRouting', {
                    title: 'Approval Routing',
                    description: 'Displays Approval Routing',
                    controller: 'Dashboard.ApprovalRoutingController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/approval-routing-widget.client.view.html',
                    commonName: 'approvalRouting'
                }
            );
    })
    .controller('Dashboard.ApprovalRoutingController', ['$scope', '$stateParams'
        , function ($scope, $stateParams) {

        }
    ]);