'use strict';

// Authentication service for user variables
angular.module('services').factory('Authentication', [
	function() {
		var _this = this;

		_this._data = {
			user: 'ann-bactes'
		};

		return _this._data;
	}
]);