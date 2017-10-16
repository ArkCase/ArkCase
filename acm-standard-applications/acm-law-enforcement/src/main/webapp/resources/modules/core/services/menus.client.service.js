'use strict';

//Menu service used for managing  menus
angular.module('core').service('Menus', ['$q', 'PermissionsService', 'Admin.ModulesService', 'Authentication',
    function ($q, PermissionsService, ModuleService, Authentication) {
        // Define a set of default roles
        this.defaultRoles = ['*'];

        // Define the menus object
        this.menus = {};
        this.allMenuObjects = [];

        var appModulesPromise = ModuleService.getAppModules();
        var userRolesPromise = Authentication.queryUserInfo();

        // A private function for rendering decision
        var shouldRender = function (user) {
            if (user) {
                if (!!~this.roles.indexOf('*')) {
                    return true;
                } else {
                    for (var userRoleIndex in user.roles) {
                        for (var roleIndex in this.roles) {
                            if (this.roles[roleIndex] === user.roles[userRoleIndex]) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                return this.isPublic;
            }

            return false;
        };

        // Validate menu existance
        this.validateMenuExistance = function (menuId) {
            if (menuId && menuId.length) {
                if (this.menus[menuId]) {
                    return true;
                } else {
                    throw new Error('Menu does not exists');
                }
            } else {
                throw new Error('MenuId was not provided');
            }

            return false;
        };

        // Get the menu object by menu id
        this.getMenu = function (menuId) {
            // Validate that the menu exists
            this.validateMenuExistance(menuId);

            // Return the menu object
            return this.menus[menuId];
        };

        // Add new menu object by menu id
        this.addMenu = function (menuId, isPublic, roles) {
            // Create the new menu
            this.menus[menuId] = {
                isPublic: isPublic || false,
                roles: roles || this.defaultRoles,
                items: [],
                shouldRender: shouldRender
            };

            // Return the menu object
            return this.menus[menuId];
        };

        // Remove existing menu object by menu id
        this.removeMenu = function (menuId) {
            // Validate that the menu exists
            this.validateMenuExistance(menuId);

            // Return the menu object
            delete this.menus[menuId];
        };

        // Add menu item object
        this.addMenuItem = function (menuId, menuItemTitle, menuItemURL, menuItemType, menuItemUIRoute, isPublic, roles, position) {
            // Validate that the menu exists
            this.validateMenuExistance(menuId);
            var context = this;

            // Push new menu item
            context.menus[menuId].items.push({
                title: menuItemTitle,
                link: menuItemURL,
                menuItemType: menuItemType || 'item',
                menuItemClass: menuItemType,
                uiRoute: menuItemUIRoute || ('/' + menuItemURL),
                isPublic: ((isPublic === null || typeof isPublic === 'undefined') ? context.menus[menuId].isPublic : isPublic),
                roles: ((roles === null || typeof roles === 'undefined') ? context.menus[menuId].roles : roles),
                position: position || 0,
                items: [],
                shouldRender: shouldRender
            });


            // Return the menu object
//            return this.menus[menuId];
        };

        // Add menu item object
        this.addMenuItems = function (menuObjects) {
            var context = this;

            $q.all([appModulesPromise, userRolesPromise]).then(function (data) {
                var appModules = data[0].data;
                var userRoles = data[1].authorities;

                for (var i = 0; i < menuObjects.length; i++) {
                    var menuObj = menuObjects[i];
                    context.allMenuObjects.push(menuObj);
                    // Validate that the menu exists
                    context.validateMenuExistance(menuObj.menuId);
                    // Check if we have defined permission rule with name of menu
                    (function processMenuPermission(menuObj) {
                        var action = menuObj.permissionAction;
                        if (!action) {
                            action = menuObj.menuItemURL;
                        }
                        PermissionsService.getActionPermission(action, null).then(function (moduleAllowedByActionPermission) {
                            var moduleObject = null;
                            var moduleAllowedByRoles = false;

                            //iterate trough all application modules to isolate it and check later if module is allowed to be used
                            //based on the user roles
                            angular.forEach(appModules, function (module) {
                                if (menuObj.moduleId != null && menuObj.moduleId != "none") {
                                    if (menuObj.moduleId === module.id) {
                                        moduleObject = module;
                                    }
                                }
                            });

                            // No need to check for role base permissions for items that does not belongs to ArkCase module.
                            // All menu items allowed by ActionPermissions based on the rules in
                            // accessControlRules.json will be visible in this case.
                            if (menuObj.moduleId != null && menuObj.moduleId === "none") {

                                if (moduleAllowedByActionPermission) {
                                    // Push new menu item
                                    pushMenuItem(menuObj, context);
                                }
                            }
                            if (moduleObject != null) {
                                ModuleService.getRolesForModulePrivilege(moduleObject.privilege).then(function (rolesForModule) {
                                    angular.forEach(rolesForModule.data, function (role) {
                                        angular.forEach(userRoles, function (userRole) {
                                            if (role === userRole) {
                                                moduleAllowedByRoles = true;
                                            }
                                        })
                                    });

                                    if (moduleAllowedByActionPermission && moduleAllowedByRoles) {
                                        // Push new menu item
                                        pushMenuItem(menuObj, context);
                                    }
                                })
                            }
                        })
                    })(menuObj);
                }
            });

        };


// Add submenu item object
        this.addSubMenuItem = function (menuId, rootMenuItemURL, menuItemTitle, menuItemURL, menuItemUIRoute, isPublic, roles, position) {
            // Validate that the menu exists
            this.validateMenuExistance(menuId);

            // Search for menu item
            for (var itemIndex in this.menus[menuId].items) {
                if (this.menus[menuId].items[itemIndex].link === rootMenuItemURL) {
                    // Push new submenu item
                    this.menus[menuId].items[itemIndex].items.push({
                        title: 'core.menus.' + menuId + '.' + menuItemURL,
                        link: menuItemURL,
                        uiRoute: menuItemUIRoute || ('/' + menuItemURL),
                        isPublic: ((isPublic === null || typeof isPublic === 'undefined') ? this.menus[menuId].items[itemIndex].isPublic : isPublic),
                        roles: ((roles === null || typeof roles === 'undefined') ? this.menus[menuId].items[itemIndex].roles : roles),
                        position: position || 0,
                        shouldRender: shouldRender
                    });
                }
            }

            // Return the menu object
            return this.menus[menuId];
        };

// Remove existing menu object by menu id
        this.removeMenuItem = function (menuId, menuItemURL) {
            // Validate that the menu exists
            this.validateMenuExistance(menuId);

            // Search for menu item to remove
            for (var itemIndex in this.menus[menuId].items) {
                if (this.menus[menuId].items[itemIndex].link === menuItemURL) {
                    this.menus[menuId].items.splice(itemIndex, 1);
                }
            }

            // Return the menu object
            return this.menus[menuId];
        };

// Remove existing menu object by menu id
        this.removeSubMenuItem = function (menuId, submenuItemURL) {
            // Validate that the menu exists
            this.validateMenuExistance(menuId);

            // Search for menu item to remove
            for (var itemIndex in this.menus[menuId].items) {
                for (var subitemIndex in this.menus[menuId].items[itemIndex].items) {
                    if (this.menus[menuId].items[itemIndex].items[subitemIndex].link === submenuItemURL) {
                        this.menus[menuId].items[itemIndex].items.splice(subitemIndex, 1);
                    }
                }
            }

            // Return the menu object
            return this.menus[menuId];
        };

//Adding the topbar menu
        this.addMenu('topbar');

//Adding the leftnav menu
        this.addMenu('leftnav');

//Adding the user menu
        this.addMenu('usermenu');

        function pushMenuItem(menuObj, context) {
            // Push new menu item
            context.menus[menuObj.menuId].items.push({
                title: 'core.menus.' + menuObj.menuId + '.' + menuObj.menuItemURL,
                link: menuObj.menuItemURL,
                menuItemType: 'item',
                uiRoute: '/' + menuObj.menuItemURL,
                isPublic: true,
                position: menuObj.position || 0,
                iconClass: menuObj.iconClass,
                permissionAction: menuObj.permissionAction || 'noAction'
            });
        }

    }
]);