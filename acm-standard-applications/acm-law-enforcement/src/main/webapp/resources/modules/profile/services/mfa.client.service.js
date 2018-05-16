'use strict';

angular.module('profile').factory('Profile.MfaService', [ '$resource', '$translate', 'UtilService', function($resource, $translate, Util) {
    var Service = $resource('api/latest/plugin', {}, {

        _getAvailableFactors: {
            method: 'GET',
            url: 'api/latest/plugin/okta/factor/enrollment/available',
            cache: false,
            isArray: true
        }

        ,
        _getEnrolledFactors: {
            method: 'GET',
            url: 'api/latest/plugin/okta/factor/enrollment',
            cache: false,
            isArray: true
        }

        ,
        _enrollFactor: {
            method: 'POST',
            url: 'api/latest/plugin/okta/factor/enrollment',
            cache: false
        }

        ,
        _activateFactor: {
            method: 'POST',
            url: 'api/latest/plugin/okta/factor/enrollment/activate',
            cache: false
        }

        ,
        _deleteFactor: {
            method: 'DELETE',
            url: 'api/latest/plugin/okta/factor/enrollment?factorId=:factorId',
            cache: false
        }

        ,
        _getAuthProfile: {
            method: 'GET',
            url: 'api/latest/plugin/okta/authprofile',
            cache: false
        }

    });

    Service.getAvailableFactors = function() {
        return Util.serviceCall({
            service: Service._getAvailableFactors,
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                return error;
            }
        });
    };

    Service.getEnrolledFactors = function() {
        return Util.serviceCall({
            service: Service._getEnrolledFactors,
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                return error;
            }
        });
    };

    Service.enrollFactor = function(factor) {
        return Util.serviceCall({
            service: Service._enrollFactor,
            data: factor,
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                return error;
            }
        });
    };

    Service.activateFactor = function(activateInfo) {
        return Util.serviceCall({
            service: Service._activateFactor,
            data: activateInfo,
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                return error;
            }
        });
    };

    Service.deleteFactor = function(factorId) {
        return Util.serviceCall({
            service: Service._deleteFactor,
            param: {
                factorId: factorId
            },
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                return error;
            }
        });
    };

    Service.getAuthProfile = function() {
        return Util.serviceCall({
            service: Service._getAuthProfile,
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                return error;
            }
        });
    };

    Service.getFactorType = function(factorInfo) {
        if (!Util.isEmpty(factorInfo) && !Util.isEmpty(factorInfo.factorType)) {
            if (Util.compare('email', factorInfo.factorType)) {
                return $translate.instant('profile.mfa.email');
            } else if (Util.compare('sms', factorInfo.factorType)) {
                return $translate.instant('profile.mfa.sms');
            } else if (Util.compare('token:software:totp', factorInfo.factorType)) {
                return $translate.instant('profile.mfa.authy');
            }
            return factorInfo.factorType;
        }
        return '';
    };

    Service.getFactorDetails = function(factorInfo) {
        if (!Util.isEmpty(factorInfo) && !Util.isEmpty(factorInfo.profile)) {
            if (Util.compare('email', factorInfo.factorType)) {
                return factorInfo.profile.email;
            } else if (Util.compare('sms', factorInfo.factorType)) {
                return factorInfo.profile.phoneNumber;
            }
        }
        return '';
    };

    return Service;
} ]);