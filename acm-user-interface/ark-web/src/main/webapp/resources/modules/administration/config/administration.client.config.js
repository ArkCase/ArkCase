'use strict';

// Pages module config
angular.module('administration').run(['Menus',
	function(Menus) {
		Menus.addMenuItem('leftnav', 'Administration', 'administration');
	}
]);