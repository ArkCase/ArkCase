/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Model = Admin.Model || {
    create : function() {
        if (Admin.Model.AccessControl.create)           {Admin.Model.AccessControl.create();}
        if (Admin.Model.Correspondence.create)          {Admin.Model.Correspondence.create();}
        if (Admin.Model.Organization.create)            {Admin.Model.Organization.create();}
        if (Admin.Model.FunctionalAccessControl.create) {Admin.Model.FunctionalAccessControl.create();}

        if (Admin.Model.Tree.create)                    {Admin.Model.Tree.create();}
    }
    ,onInitialized: function() {
        if (Admin.Model.AccessControl.onInitialized)            {Admin.Model.AccessControl.onInitialized();}
        if (Admin.Model.Correspondence.onInitialized)           {Admin.Model.Correspondence.onInitialized();}
        if (Admin.Model.Organization.onInitialized)             {Admin.Model.Organization.onInitialized();}
        if (Admin.Model.FunctionalAccessControl.onInitialized) {Admin.Model.Organization.onInitialized();}

        if (Admin.Model.Tree.onInitialized)                     {Admin.Model.Tree.onInitialized();}
    }

    ,_totalCount: 0
    ,getTotalCount: function() {
        return this._totalCount;
    }
    ,setTotalCount: function(totalCount) {
        this._totalCount = totalCount;
    }

    ,AccessControl:{
        create : function() {
            this.cacheAccessControlList = new Acm.Model.CacheFifo(4);
        }
        ,onInitialized: function() {
        }

        ,validateAccessControlList: function(accessControlList) {
            if (Acm.isEmpty(accessControlList)) {
                return false;
            }
            if (Acm.isEmpty(accessControlList.totalCount)) {
                return false;
            }
            if (!Acm.isArray(accessControlList.resultPage)) {
                return false;
            }
            return true;
        }
    }

    ,Organization: {
        create : function() {
            this.cacheAllGroups = new Acm.Model.CacheFifo(4);
            this.cacheGroup = new Acm.Model.CacheFifo(4);
            this.cacheGroups = new Acm.Model.CacheFifo(4);
            this.cacheSubgroups = new Acm.Model.CacheFifo(4);
            this.cacheGroupMembers = new Acm.Model.CacheFifo(4);
            this.cacheAllUsers = new Acm.Model.CacheFifo(4);

            //Admin.Service.Organization.retrieveUsers();
            Admin.Service.Organization.retrieveGroups();

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REMOVED_GROUP_MEMBER, this.onModelModifiedGroupData);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_CREATED_ADHOC_GROUP, this.onModelModifiedGroupData);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ADDED_GROUP_MEMBER, this.onModelModifiedGroupData);
        }
        ,onInitialized: function() {
        }

        ,Tree:{
            _parentNode: null
            ,getParentNode : function() {
                return this._parentNode;
            }
            ,setParentNode : function(parentNode) {
                this._parentNode = parentNode;
            }
        }

        ,validateGroup: function(group) {
            if (Acm.isEmpty(group)) {
                return false;
            }
            return true;
        }

        ,onModelModifiedGroupData: function(){
            Admin.Service.Organization.retrieveGroups();
        }
    }

    ,Correspondence:{
        create : function() {
            this.cacheTemplatesList = new Acm.Model.CacheFifo(4);
            Admin.Service.Correspondence.retrieveTemplatesList();

        }
        ,onInitialized: function() {
        }

        ,validateTemplatesList: function(templatesList) {
            if (Acm.isEmpty(templatesList)) {
                return false;
            }
            return true;
        }
    }
    
    ,FunctionalAccessControl:{
        create : function() {
        	this.cacheApplicationRoles = new Acm.Model.CacheFifo(1);
        	this.cacheGroups = new Acm.Model.CacheFifo(1);
        	this.cacheApplicationRolesToGroups = new Acm.Model.CacheFifo(1);
        	this.cacheNotAuthorizedGroups = new Acm.Model.CacheFifo(1);
        	this.cacheAuthorizedGroups = new Acm.Model.CacheFifo(1);
        	
        	Admin.Service.FunctionalAccessControl.retrieveApplicationRoles();
        	Admin.Service.FunctionalAccessControl.retrieveGroups();
        	Admin.Service.FunctionalAccessControl.retrieveApplicationRolesToGroups();
        }
        ,onInitialized: function() {
        }
        
        ,validateApplicationRoles: function(roles) {
            if (Acm.isEmpty(roles) || !Acm.isArray(roles)) {
                return false;
            }
            return true;
        }
        
        ,validateGroups: function(groups) {
            if (Acm.isEmpty(groups)) {
                return false;
            }
            return true;
        }
        
        ,validateApplicationRolesToGroups: function(rolesToGroups) {
            if (Acm.isEmpty(rolesToGroups)) {
                return false;
            }
            return true;
        }
    }


    ,Tree: {
        create : function() {
            if (Admin.Model.Tree.Config.create)    {Admin.Model.Tree.Config.create();}
            if (Admin.Model.Tree.Key.create)       {Admin.Model.Tree.Key.create();}
        }
        ,onInitialized: function() {
            if (Admin.Model.Tree.Config.onInitialized)    {Admin.Model.Tree.Config.onInitialized();}
            if (Admin.Model.Tree.Key.onInitialized)       {Admin.Model.Tree.Key.onInitialized();}
        }

        ,Config: {
            create: function() {
            }
            ,onInitialized: function() {
            }
        }
        ,Key: {
            create: function() {
            }
            ,onInitialized: function() {
            }

            ,NODE_TYPE_PART_BRANCH_MAIN_PAGE:        "mp"
            ,NODE_TYPE_PART_LEAF_ACCESS_CONTROL:     "dac"
            ,NODE_TYPE_PART_LEAF_DASHBOARD:          "dc"
            ,NODE_TYPE_PART_LEAF_REPORTS:            "rc"
            ,NODE_TYPE_PART_BRANCH_ACCESS_CONTROL:   "acc"
            ,NODE_TYPE_PART_BRANCH_DASHBOARD:        "dsh"
            ,NODE_TYPE_PART_BRANCH_REPORTS:          "rpt"
            ,NODE_TYPE_PART_BRANCH_CORRESPONDENCE:   "cm"
            ,NODE_TYPE_PART_BRANCH_TEMPLATES:        "ct"
            ,NODE_TYPE_PART_BRANCH_ORGANIZATION:     "og"
            ,NODE_TYPE_PART_LEAF_FUNCTIONAL_ACCESS_CONTROL:"fac"


            ,_mapNodeType: [
                {nodeType: "mp"      ,icon: "",tabIds: ["tabMainPage"]}
                ,{nodeType: "acc"      ,icon: "",tabIds: ["tabACP"]}
                ,{nodeType: "dsh"      ,icon: "",tabIds: ["tabDashboard"]}
                ,{nodeType: "rpt"      ,icon: "",tabIds: ["tabReports"]}
                ,{nodeType: "dac"      ,icon: "",tabIds: ["tabACP"]}
                ,{nodeType: "dc"      ,icon: "",tabIds: ["tabDashboard"]}
                ,{nodeType: "rc"      ,icon: "",tabIds: ["tabReports"]}
                ,{nodeType: "cm"      ,icon: "",tabIds: ["tabCorrespondenceTemplates"]}
                ,{nodeType: "ct"      ,icon: "",tabIds: ["tabCorrespondenceTemplates"]}
                ,{nodeType: "og"      ,icon: "",tabIds: ["tOrganization"]}
                ,{nodeType: "fac"      ,icon: "",tabIds: ["tabFunctoinalAccessControl"]}
            ]

            ,getTabIdsByKey: function(key) {
                var nodeType = this.getNodeTypeByKey(key);
                //var tabIds = ["tabBlank"];
                var tabIds = [];
                for (var i = 0; i < this._mapNodeType.length; i++) {
                    if (nodeType == this._mapNodeType[i].nodeType) {
                        tabIds = this._mapNodeType[i].tabIds;
                        break;
                    }
                }
                return tabIds;
            }
            ,getTabIds: function() {
                var tabIds = [];
                for (var i = 0; i < this._mapNodeType.length; i++) {
                    var tabIdsThis = this._mapNodeType[i].tabIds;
                    for (var j = 0; j < tabIdsThis.length; j++) {
                        var tabId = tabIdsThis[j];
                        if (!Acm.isItemInArray(tabId, tabIds)) {
                            tabIds.push(tabId);
                        }
                    }
                }
                return tabIds;
            }

            ,getNodeTypeByKey: function(key) {
                if (Acm.isEmpty(key)) {
                    return null;
                }
                if (key == this.NODE_TYPE_PART_LEAF_ACCESS_CONTROL) {
                    return this.NODE_TYPE_PART_LEAF_ACCESS_CONTROL;
                } else if (key == this.NODE_TYPE_PART_LEAF_DASHBOARD) {
                    return this.NODE_TYPE_PART_LEAF_DASHBOARD;
                } else if (key == this.NODE_TYPE_PART_LEAF_REPORTS) {
                    return this.NODE_TYPE_PART_LEAF_REPORTS;
                } else if (key == this.NODE_TYPE_PART_BRANCH_ACCESS_CONTROL) {
                    return this.NODE_TYPE_PART_BRANCH_ACCESS_CONTROL;
                } else if (key == this.NODE_TYPE_PART_BRANCH_DASHBOARD) {
                    return this.NODE_TYPE_PART_BRANCH_DASHBOARD;
                } else if (key == this.NODE_TYPE_PART_BRANCH_REPORTS) {
                    return this.NODE_TYPE_PART_BRANCH_REPORTS;
                }else if (key == this.NODE_TYPE_PART_BRANCH_CORRESPONDENCE) {
                    return this.NODE_TYPE_PART_BRANCH_CORRESPONDENCE;
                }else if (key == this.NODE_TYPE_PART_BRANCH_TEMPLATES) {
                    return this.NODE_TYPE_PART_BRANCH_TEMPLATES;
                }else if (key == this.NODE_TYPE_PART_BRANCH_ORGANIZATION) {
                    return this.NODE_TYPE_PART_BRANCH_ORGANIZATION;
                }else if (key == this.NODE_TYPE_PART_LEAF_FUNCTIONAL_ACCESS_CONTROL) {
	                return this.NODE_TYPE_PART_LEAF_FUNCTIONAL_ACCESS_CONTROL;
	            }
                return null;
            }
        }
    }

}    