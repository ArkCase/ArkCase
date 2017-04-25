'use strict';
angular.module('admin').controller('Admin.SecurityExchangeConfigurationController', ['$scope', 'Admin.ExchangeConfigurationService', 'MessageService', 'ConfigService',
    function($scope, ExchangeConfigurationService, MessageService, ConfigService) {
        $scope.exchangeConfigDataModel = {};
        $scope.serverVersionSelectOptions = [
        {
            value: 'Exchange2007_SP1',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeServerVersionOptions.Exchange2007_SP1'
        },
        {
            value: 'Exchange2010',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeServerVersionOptions.Exchange2010'
        },
        {
            value: 'Exchange2010_SP1',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeServerVersionOptions.Exchange2010_SP1'
        },
        {
            value: 'Exchange2010_SP2',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeServerVersionOptions.Exchange2010_SP2'
        }
        ];

        $scope.defaultAccessSelectOptions = [
        {
            value: 'None',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.None'
        },
        {
            value: 'Owner',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.Owner'
        },
        {
            value: 'PublishingEditor',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.PublishingEditor'
        },
        {
            value: 'Editor',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.Editor'
        },
        {
            value: 'PublishingAuthor',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.PublishingAuthor'
        },
        {
            value: 'Author',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.Author'
        },
        {
            value: 'NoneditingAuthor',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.NoneditingAuthor'
        },
        {
            value: 'Reviewer',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.Reviewer'
        },
        {
            value: 'Contributor',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.Contributor'
        },
        {
            value: 'FreeBusyTimeOnly',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.FreeBusyTimeOnly'
        },
        {
            value: 'FreeBusyTimeAndSubjectAndLocation',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.FreeBusyTimeAndSubjectAndLocation'
        },
        {
            value: 'Custom',
            label: 'admin.security.exchangeConfiguration.exchangeConfigForm.exchangeDefaultAccessOptions.Custom'
        }
        ];
        
        /*Get exchange configuration*/
        ExchangeConfigurationService.getExchangeConfiguration().then(function(res) {
            $scope.exchangeConfigDataModel = res.data;
        });

        $scope.save = function() {
            ExchangeConfigurationService.saveExchangeConfiguration($scope.exchangeConfigDataModel)
            .then(function(res) {
                MessageService.succsessAction();
            }, function(err) {
                if(err.status === 400) {
                        MessageService.errorAction();
                    } else {
                        MessageService.errorAction();
                    }
                });
        };
    }
    ]);