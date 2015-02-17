/**
 * Created by manoj.dhungana on 12/4/2014.
 */

Admin.Model = Admin.Model || {
    create : function() {
        if (Admin.Model.Correspondence.create)          {Admin.Model.Correspondence.create();}
        if (Admin.Model.Organization.create)            {Admin.Model.Organization.create();}
        if (Admin.Model.FunctionalAccessControl.create) {Admin.Model.FunctionalAccessControl.create();}
        if (Admin.Model.ReportsConfiguration.create)    {Admin.Model.ReportsConfiguration.create();}
        if (Admin.Model.WorkflowConfiguration.create)   {Admin.Model.WorkflowConfiguration.create();}


        if (Admin.Model.Tree.create)                    {Admin.Model.Tree.create();}
    }
    ,onInitialized: function() {
        if (Admin.Model.Correspondence.onInitialized)           {Admin.Model.Correspondence.onInitialized();}
        if (Admin.Model.Organization.onInitialized)             {Admin.Model.Organization.onInitialized();}
        if (Admin.Model.FunctionalAccessControl.onInitialized)  {Admin.Model.FunctionalAccessControl.onInitialized();}
        if (Admin.Model.ReportsConfiguration.onInitialized)     {Admin.Model.ReportsConfiguration.onInitialized();}
        if (Admin.Model.WorkflowConfiguration.onInitialized)    {Admin.Model.WorkflowConfiguration.onInitialized();}


        if (Admin.Model.Tree.onInitialized)                     {Admin.Model.Tree.onInitialized();}
    }

    ,Organization: {
        create : function() {
            //if (Admin.Model.Organization.ModalDialog.create)           {Admin.Model.Organization.ModalDialog.create();}
            if (Admin.Model.Organization.Tree.create)           {Admin.Model.Organization.Tree.create();}

            this.cacheGroups = new Acm.Model.CacheFifo(4);
            this.cacheSubgroups = new Acm.Model.CacheFifo(4);
            this.cacheTreeSource = new Acm.Model.CacheFifo(4);
            this.cacheMembersForTree = new Acm.Model.CacheFifo(4);
            this.cacheAcmUsersFromSolr = new Acm.Model.CacheFifo(4);
            this.cacheResult = new Acm.Model.CacheFifo(4);

            Admin.Service.Organization.retrieveGroups();

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_RETRIEVED_GROUPS, this.onModelRetrievedGroups);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_CREATED_AD_HOC_GROUP, this.onViewCreatedAdHocGroup);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_REMOVED_MEMBERS, this.onViewRemovedGroupMember);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_ADDED_MEMBERS, this.onViewAddedMembers);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_REMOVED_GROUP, this.onViewRemovedGroup);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_SEARCHED_MEMBERS, this.onViewSearchedMembers);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_SUBMITTED_QUERY          ,Admin.Model.Organization.ModalDialog.Members.Facets.onViewSubmittedQuery);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_CHANGED_FACET  ,Admin.Model.Organization.ModalDialog.Members.Facets.onViewChangedFacetSelection);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_ADDED_SUPERVISOR, this.onViewAddedSupervisor);

        }
        ,onInitialized: function() {
            //if (Admin.Model.Organization.ModalDialog.onInitialized)           {Admin.Model.Organization.ModalDialog.onInitialized();}
            if (Admin.Model.Organization.Tree.onInitialized)           {Admin.Model.Organization.Tree.onInitialized();}

        }

        //not used at the moment
        ,fn: function() {
            setTimeout(Admin.Service.Organization.retrieveGroups(),10000);
        }

        ,_hasParentNode : false
        ,getParentNodeFlag : function() {
            return this._hasParentNode;
        }
        ,setParentNodeFlag : function(parentNodeFlag) {
            this._hasParentNode = parentNodeFlag;
        }

        //use this method to make group data from normal
        // org hierarchy service call response
        ,makeGroupData: function(response){
            var group = {};
            group.title = response.name;
            group.type = response.type;
            group.parentId = response.parentId;
            group.subgroups = response.child_id_ss;
            group.members = response.members;
            if(response.supervisor && response.supervisor.userId){
                group.supervisor = response.supervisor.userId;
            }
            return group;
        }
        //use this method to make group data from
        // solr search service response
        ,makeGroupDataFromSolrResponse: function(responseSolr){
            var groupFromSolrResponse = {};
            groupFromSolrResponse.title = responseSolr.name;
            groupFromSolrResponse.type = responseSolr.object_sub_type_s;
            groupFromSolrResponse.parentId = responseSolr.parent_id_s;
            groupFromSolrResponse.subgroups = responseSolr.child_id_ss;
            groupFromSolrResponse.members = responseSolr.member_id_ss;
            groupFromSolrResponse.supervisor = responseSolr.supervisor_id_s;
            return groupFromSolrResponse;
        }
        ,onViewSearchedMembers: function(term){
            Admin.Service.Organization.retrieveGroupMembers(term);
        }
        ,onViewRemovedGroup: function(groupId){
            Admin.Service.Organization.removeGroup(groupId);
        }
        ,onViewCreatedAdHocGroup: function(group,parentId){
            Admin.Service.Organization.createAdHocGroup(group,parentId);
        }
        ,onViewRemovedGroupMember: function(removedMembers, parentGroupId){
            Admin.Service.Organization.removeGroupMember(removedMembers, parentGroupId);
        }
        ,onViewAddedMembers: function(addedMembers, parentGroupId){
            Admin.Service.Organization.addGroupMembers(addedMembers, parentGroupId);
        }
        ,onViewAddedSupervisor: function(addedSupervisor, parentGroupId){
            Admin.Service.Organization.addGroupSupervisor(addedSupervisor, parentGroupId);
        }
        ,onModelRetrievedGroups: function() {
            Admin.Service.Organization.retrieveUsers();
        }
        ,validateSolrResponse: function(response) {
            if (!Acm.Validator.validateSolrData(response)) {
                return false;
            }
            return true;
        }
        ,validateGroup: function(response){
            if (Acm.isEmpty(response)) {
                return false;
            }
            //add more checks in the future
            return true;
        }

        ,validateUsers: function(response) {
            if (Acm.isEmpty(response)) {
                return false;
            }
            if (Acm.isEmpty(response.members)) {
                return false;
            }
            //add more checks in the future
            return true;
        }
        ,Tree:{
            create: function() {
            }
            ,onInitialized: function() {
            }
            ,_sourceLoaded : false
            ,isSourceLoaded : function() {
                return this._sourceLoaded;
            }
            ,sourceLoaded : function(sourceLoaded) {
                this._sourceLoaded = sourceLoaded;
            }
            ,_parentNodeTitle : null
            ,_currentGroup: null
            ,getParentNodeTitle : function() {
                return this._parentNodeTitle;
            }
            ,setParentNodeTitle : function(parentNodeTitle) {
                this._parentNodeTitle = parentNodeTitle;
            }
            ,getCurrentGroup : function() {
                return this._currentGroup;
            }
            ,setCurrentGroup : function(nodeTitle,checkInGroup) {
                if(checkInGroup == true){
                    var groups = Admin.Model.Organization.cacheGroups.get("groups");
                    for(var i = 0; i < groups.length; i++){
                        if(nodeTitle == groups[i].title){
                            this._currentGroup = groups[i];
                            break;
                        }
                    }
                }
                else if (checkInGroup == false){
                    var subGroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
                    for(var j = 0; j < subGroups.length; j++){
                        if(nodeTitle == subGroups[j].title){
                            this._currentGroup = subGroups[j];
                            break;
                        }
                    }
                }
                Admin.Model.Organization.Tree.setParentNodeTitle(nodeTitle);
            }
        }
        ,ModalDialog:{
            create: function() {
                if (Admin.Model.Organization.ModalDialog.Members.create)           {Admin.Model.Organization.ModalDialog.Members.create();}

            }
            ,onInitialized: function() {
                if (Admin.Model.Organization.ModalDialog.Members.onInitialized)           {Admin.Model.Organization.ModalDialog.Members.onInitialized();}

            }


            ,Members:{

                create: function() {
                    if (Admin.Model.Organization.ModalDialog.Members.Facets.create)           {Admin.Model.Organization.ModalDialog.Members.Facets.create();}
                }
                ,onInitialized: function() {
                }


                ,Facets:{
                    create: function() {
                        Admin.Model.Organization.ModalDialog.Members.Facets.setSearchInfo(AcmEx.Model.Search.getDefaultSearchInfo());
                    }
                    ,onInitialized: function() {
                    }
                    ,onViewSubmittedQuery: function(term) {
                        var si = Admin.Model.Organization.ModalDialog.Members.Facets.getSearchInfo();
                        if (!Acm.compare(term, si.q)) {
                            si.q = term;
                            si.filter = [];
                            Admin.Model.Organization.ModalDialog.Members.Facets.setFacetUpToDate(false);
                        }
                    }
                    ,onViewChangedFacetSelection: function(selected) {
                        //todo: compare selected with si.filter, do nothing if same
                        Admin.Model.Organization.ModalDialog.Members.Facets.setFacetUpToDate(false);

                        var si = Admin.Model.Organization.ModalDialog.Members.Facets.getSearchInfo();
                        si.filter = [];
                        if (Acm.isArray(selected)) {
                            var cur = {key: null, values: []};

                            for (var i = 0; i < selected.length; i++) {
                                var s = selected[i];
                                if (Acm.isNotEmpty(s.name) && Acm.isNotEmpty(s.value)) {
                                    if (s.name == cur.key) {
                                        cur.values.push(s.value);
                                    } else {
                                        if (Acm.isNotEmpty(cur.key)) {
                                            si.filter.push(cur);
                                        }
                                        cur = {};
                                        cur.key = s.name;
                                        cur.values = [s.value];
                                    }
                                }
                            }
                            if (0 < i) {
                                si.filter.push(cur);
                            }
                        }
                    }

                    //
                    // filter array json format: [{key, values:['v1', 'v2', ...]}, ...]
                    //
                    ,_searchInfo: null
                    ,getSearchInfo: function() {
                        return this._searchInfo;
                    }
                    ,setSearchInfo: function(searchInfo) {
                        this._searchInfo = searchInfo;
                    }

                    ,getCachedResult: function(si) {
                        var page = si.start;
                        return Admin.Model.Organization.cacheResult.get(page);
                    }
                    ,putCachedResult: function(si, result) {
                        var page = si.start;
                        //Admin.Model.Organization.cacheResult.put(page, result);
                    }
                    //,selectFilter: function(filterType, filterKey, filterValue, select) {
                    ,addFilter: function(si, key, value) {
                        for (var i = 0; i < si.filter.length; i++) {
                            var f = si.filter[i];
                            if (f.key == key) {
                                //find value first to avoid adding duplicate
                                for (var j = 0; j < f.values.length; j++) {
                                    if (f.values[j] == value) {
                                        return;
                                    }
                                }
                                f.values.push(value);
                                return;
                            }
                        }


                        //key entry not found, create one
                        var newFilter = {};
                        newFilter.key = key;
                        newFilter.values = [];
                        newFilter.values.push(value);
                        si.filter.push(newFilter);
                    }
                    ,removeFilter: function(si, key, value) {
                        for (var i = 0; i < si.filter.length; i++) {
                            var f = si.filter[i];
                            if (f.key == key) {
                                for (var j = 0; j < f.values.length; j++) {
                                    if (f.values[j] == value) {
                                        si.filter[i].values.splice(j, 1);
                                        break;
                                    }
                                }
                                if (0 >= f.values.length) {
                                    si.filter.splice(i, 1);
                                }
                            }
                        }
                    }
                    ,findFilter: function(si, key, value) {
                        if (!Acm.isArrayEmpty(si.filter)) {
                            for (var i = 0; i < si.filter.length; i++) {
                                var f = si.filter[i];
                                if (f.key == key) {
                                    if (!Acm.isArrayEmpty(f.values)) {
                                        for (var j = 0; j < f.values.length; j++) {
                                            if (f.values[j] == value) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                                if (0 >= f.values.length) {
                                    si.filter.splice(i, 1);
                                }
                            }
                        }
                        return false;
                    }
                    ,makeFilterParam: function(si) {
                        var param = "";
                        if (!Acm.isArrayEmpty(si.filter)) {
                            for (var i = 0; i < si.filter.length; i++) {
                                if (0 == i) {
                                    param= '&filters=';
                                } else {
                                    param += '&';
                                }

                                if (!Acm.isArrayEmpty(si.filter[i].values)) {
                                    for (var j = 0; j < si.filter[i].values.length; j++) {
                                        if (0 == j) {
                                            param += 'fq="' + Acm.goodValue(si.filter[i].key) + '":';
                                        } else {
                                            param += '|';
                                        }
                                        param += Acm.goodValue(si.filter[i].values[j]);

                                    }
                                }

//                if (si.filter.length - 1 == i) {
//                    param += '"';
//                }
                            }
                        }
                        return param;
                    }

                    ,_facetUpToDate: true
                    ,isFacetUpToDate: function() {
                        return this._facetUpToDate;
                    }
                    ,setFacetUpToDate: function(facetUpToDate) {
                        this._facetUpToDate = facetUpToDate;
                    }

                    //
                    // Facet array JSON format: [{label, key, count, values:[{name, count, def}, ...]}, ...]
                    //
                    ,_facet: {
                        facet_queries: []
                        ,facet_fields: []
                        ,facet_dates : []
                        ,facet_ranges: []
                    }
                    ,getFacet: function() {
                        return this._facet;
                    }
                    ,setFacet: function(facet) {
                        this._facet = facet;
                    }
                    ,makeFacet: function(facetSearch) {
                        var facet = {
                            facet_queries  : []
                            ,facet_fields  : []
                            ,facet_dates   : []
                            ,facet_ranges  : []
                        };

                        if (Admin.Model.Organization.ModalDialog.Members.Facets.validateFacetSearchData(facetSearch)) {
                            var raw_facet_queries = this._parseFacetEntries(facetSearch.responseHeader.params["facet.query"]);
                            facet.facet_fields    = this._parseFacetEntries(facetSearch.responseHeader.params["facet.field"]);
                            facet.facet_dates     = this._parseFacetEntries(facetSearch.responseHeader.params["facet.date"]);
                            facet.facet_ranges    = this._parseFacetEntries(facetSearch.responseHeader.params["facet.range"]);

                            var label = null;
                            var labelLast = null;
                            facet.facet_queries = [];
                            var n = -1;
                            for (var i = 0; i < raw_facet_queries.length; i++) {
                                var key = raw_facet_queries[i].label;
                                if (key) {
                                    var ar = key.split(", ");
                                    if (2 == ar.length) {
                                        label = ar[0];
                                        if (label != labelLast) {
                                            n++;
                                            facet.facet_queries[n] = {};
                                            facet.facet_queries[n].label  = ar[0];
                                            facet.facet_queries[n].key    = raw_facet_queries[i].key;
                                            facet.facet_queries[n].count  = 0;
                                            facet.facet_queries[n].values = [];
                                            labelLast = label;
                                        }
                                        var value = {};
                                        value.name = ar[1];
                                        value.count = facetSearch.facet_counts.facet_queries[key];
                                        value.def = raw_facet_queries[i].def;
                                        facet.facet_queries[n].values.push(value);
                                        facet.facet_queries[n].count += value.count;
                                    }
                                }
                            }


                            for (var i = 0; i < facet.facet_fields.length; i++) {
                                facet.facet_fields[i].values = [];
                                facet.facet_fields[i].count = 0;
                                var key = (facet.facet_fields[i].label)? facet.facet_fields[i].label : facet.facet_fields[i].key;
                                if (key) {
                                    var ar = facetSearch.facet_counts.facet_fields[key];
                                    if (Acm.isArray(ar)) {
                                        for (var j = 0; j < ar.length; j+=2) {
                                            var value = {};
                                            value.name  = ar[j];
                                            value.count = ar[j+1];
                                            facet.facet_fields[i].values.push(value);
                                            facet.facet_fields[i].count += value.count;
                                        }
                                    }
                                }
                            }

                            for (var i = 0; i < facet.facet_dates.length; i++) {
                                facet.facet_dates[i].values = [];
                                facet.facet_dates[i].count = 0;
                                var key = (facet.facet_dates[i].label)? facet.facet_dates[i].label : facet.facet_dates[i].key;
                                if (key) {
                                    var map = facetSearch.facet_counts.facet_dates[key];
                                    if (Acm.isNotEmpty(map)) {
                                        for (var name in map) {
                                            if ("start" == name) {
                                                facet.facet_dates[i].start = map[name];
                                            } else if ("end" == name) {
                                                facet.facet_dates[i].end = map[name];
                                            } else if ("gap" == name) {
                                                facet.facet_dates[i].gap = map[name];
                                            } else {
                                                var value = {};
                                                value.name  = name;
                                                value.count = map[name];
                                                facet.facet_dates[i].values.push(value);
                                                facet.facet_dates[i].count += value.count;
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        this.setFacet(facet);
                        return facet;
                    }
                    ,_parseFacetEntries: function(facetParam) {
                        var facetEntries = [];
                        if (Acm.isNotEmpty(facetParam)) {
                            var stringEntries = [];
                            if (Acm.isArray(facetParam)) {
                                stringEntries = facetParam;
                            } else {
                                stringEntries.push(facetParam);
                            }
                            for (var i = 0; i < stringEntries.length; i++) {
                                var facetEntry = this._parseFacetLabelKeyDef(stringEntries[i]);
                                if (facetEntry) {
                                    facetEntries.push(facetEntry);
                                }
                            }
                        }
                        return facetEntries;
                    }
                    ,_parseFacetLabelKeyDef: function(s) {
                        var facetEntry = null;
                        if (Acm.isNotEmpty(s)) {
                            facetEntry = {};
                            var keyDef = s.split(":");
                            if (1 <= keyDef.length) {
                                var tmp = keyDef[0];
                                var idxLabel = tmp.indexOf("{!key='");
                                if (0 != idxLabel) {
                                    facetEntry.key = keyDef[0];
                                } else {
                                    tmp = tmp.substring(7);      //7 == length of "{!key='"
                                    var labelKey = tmp.split("'}");
                                    if (Acm.isArray(labelKey)) {
                                        if (2 == labelKey.length) {
                                            facetEntry.label = labelKey[0];
                                            facetEntry.key   = labelKey[1];
                                        }
                                    }
                                }
                            }
                            if (2 == keyDef.length) {
                                facetEntry.def = keyDef[1];
                            }

                        }
                        return facetEntry;
                    }

                    ,getCountFacetQueries: function(facet) {
                        var count = 0;
                        if (facet) {
                            count = this._getCountFacetArray(facet.facet_queries);
                        }
                        return count;
                    }
                    ,getCountFacetFields: function(facet) {
                        var count = 0;
                        if (facet) {
                            count = this._getCountFacetArray(facet.facet_fields);
                        }
                        return count;
                    }
                    ,getCountFacetDates: function(facet) {
                        var count = 0;
                        if (facet) {
                            count = this._getCountFacetArray(facet.facet_dates);
                        }
                        return count;
                    }
                    ,getCountFacetRanges: function(facet) {
                        var count = 0;
                        if (facet) {
                            count = this._getCountFacetArray(facet.facet_ranges);
                        }
                        return count;
                    }
                    ,_getCountFacetArray: function(ar) {
                        var count = 0;
                        if (Acm.isArray(ar)) {
                            for (var i = 0; i < ar.length; i++) {
                                if (ar[i].count) {
                                    count += ar[i].count;
                                }
                            }
                        }
                        return count;

                    }

                    ,validateFacetSearchData: function(data) {
                        if (!Acm.Validator.validateSolrData(data)) {
                            return false;
                        }

                        if ("true" != Acm.goodValue(data.responseHeader.params.facet)) {
                            return false;
                        }


                        if (Acm.isEmpty(data.facet_counts)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_counts.facet_queries)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_counts.facet_fields)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_counts.facet_dates)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_counts.facet_ranges)) {
                            return false;
                        }
                        return true;
                    }
                    ,validateSearchResult: function(data) {
                        return Acm.Validator.validateSolrData(data);
                    }
                    ,validateSearchFacet: function(data) {
                        if (!data) {
                            return false;
                        }

                        if (Acm.isEmpty(data.facet_queries)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_fields)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_dates)) {
                            return false;
                        }
                        if (Acm.isEmpty(data.facet_ranges)) {
                            return false;
                        }

                        return true;
                    }
                }

                ,_supervisorFlag : false
                ,hasSupervisorFlag : function() {
                    return this._supervisorFlag;
                }
                ,setSupervisorFlag : function(supervisorFlag) {
                    this._supervisorFlag = supervisorFlag;
                }

                ,_selectedPeople: null
                ,getSelectedPeople : function() {
                    return this._selectedPeople;
                }
                ,setSelectedPeople : function(selectedPeople) {
                    this._selectedPeople = selectedPeople;
                }
            }
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

            Admin.Service.FunctionalAccessControl.retrieveApplicationRoles();
            Admin.Service.FunctionalAccessControl.retrieveGroups();
            Admin.Service.FunctionalAccessControl.retrieveApplicationRolesToGroups();

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_SAVED_APPLICATION_ROLES_TO_GROUPS, this.onSaveFunctionalAccessControlApplicationRolesToGroups);
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

        ,onSaveFunctionalAccessControlApplicationRolesToGroups: function(applicationRolesToGroups) {
            Admin.Service.FunctionalAccessControl.saveApplicationRolesToGroups(applicationRolesToGroups);
        }
    }
    ,ReportsConfiguration:{
        create : function() {
            this.cacheReports = new Acm.Model.CacheFifo(1);
            this.cacheGroups = new Acm.Model.CacheFifo(1);
            this.cacheReportToGroupsMap = new Acm.Model.CacheFifo(1);


            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORTS, this.onModelReportConfigRetrievedReports);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REPORT_CONFIGURATION_RETRIEVED_GROUPS, this.onModelReportConfigRetrievedGroups);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP, this.onViewReportConfigSaveReportToGroupsMap);
        }
        ,onInitialized: function() {
            Admin.Service.ReportsConfiguration.retrieveReports();
        }

        ,validateReports: function(reports) {
            if (Acm.isEmpty(reports)) {
                return false;
            }
            if(!Acm.isArray(reports)){
                return false;
            }
            return true;
        }

        ,validateGroup: function(response) {
            if (!Acm.Validator.validateSolrData(response)) {
                return false;
            }
            return true;
        }

        ,validateReportToGroupsMap: function(reportsToGroups) {
            if (Acm.isEmpty(reportsToGroups)) {
                return false;
            }
            return true;
        }

        ,onModelReportConfigRetrievedReports: function() {
            Admin.Service.ReportsConfiguration.retrieveGroups();
        }

        ,onModelReportConfigRetrievedGroups: function() {
            Admin.Service.ReportsConfiguration.retrieveReportToGroupsMap();
        }

        ,onViewReportConfigSaveReportToGroupsMap: function(reportsToGroups) {
            Admin.Service.ReportsConfiguration.saveReportToGroupsMap(reportsToGroups);
        }

    }

    ,WorkflowConfiguration:{
        create: function () {

        }
        , onInitialized: function () {
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

            ,NODE_TYPE_PART_BRANCH_MAIN_PAGE                    :          "mp"
            ,NODE_TYPE_PART_LEAF_FUNCTIONAL_ACCESS_CONTROL      :          "fac"
            ,NODE_TYPE_PART_LEAF_ORGANIZATION                   :          "og"
            ,NODE_TYPE_PART_BRANCH_DASHBOARD                    :          "dsh"
            ,NODE_TYPE_PART_LEAF_DASHBOARD                      :          "dc"
            ,NODE_TYPE_PART_BRANCH_REPORTS                      :          "rpt"
            ,NODE_TYPE_PART_LEAF_REPORTS                        :          "rc"
            ,NODE_TYPE_PART_BRANCH_WORKFLOW_CONFIGURATION       :          "wfc"
            ,NODE_TYPE_PART_LEAF_WORKFLOW_CONFIGURATION         :          "wf"
            ,NODE_TYPE_PART_BRANCH_TEMPLATES                    :          "ct"
            ,NODE_TYPE_PART_LEAF_TEMPLATES                      :          "cm"




            ,_mapNodeType: [
                 {nodeType: "mp"       ,icon: "",tabIds: ["tabMainPage"]}
                ,{nodeType: "dsh"      ,icon: "",tabIds: ["tabDashboard"]}
                ,{nodeType: "rpt"      ,icon: "",tabIds: ["tabReports"]}
                ,{nodeType: "dac"      ,icon: "",tabIds: ["tabACP"]}
                ,{nodeType: "dc"       ,icon: "",tabIds: ["tabDashboard"]}
                ,{nodeType: "rc"       ,icon: "",tabIds: ["tabReports"]}
                ,{nodeType: "ct"       ,icon: "",tabIds: ["tabCorrespondenceTemplates"]}
                ,{nodeType: "cm"       ,icon: "",tabIds: ["tabCorrespondenceTemplates"]}
                ,{nodeType: "og"       ,icon: "",tabIds: ["tOrganization"]}
                ,{nodeType: "fac"      ,icon: "",tabIds: ["tabFunctionalAccessControl"]}
                ,{nodeType: "wfc"      ,icon: "",tabIds: ["tabWorkflowConfiguration"]}
                ,{nodeType: "wf"       ,icon: "",tabIds: ["tabWorkflowConfiguration"]}
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
                if (key == this.NODE_TYPE_PART_LEAF_ORGANIZATION) {
                    return this.NODE_TYPE_PART_LEAF_ORGANIZATION;
                } else if (key == this.NODE_TYPE_PART_LEAF_FUNCTIONAL_ACCESS_CONTROL) {
                    return this.NODE_TYPE_PART_LEAF_FUNCTIONAL_ACCESS_CONTROL;
                } else if (key == this.NODE_TYPE_PART_LEAF_DASHBOARD) {
                    return this.NODE_TYPE_PART_LEAF_DASHBOARD;
                } else if (key == this.NODE_TYPE_PART_BRANCH_DASHBOARD) {
                    return this.NODE_TYPE_PART_BRANCH_DASHBOARD;
                } else if (key == this.NODE_TYPE_PART_BRANCH_REPORTS) {
                    return this.NODE_TYPE_PART_BRANCH_REPORTS;
                } else if (key == this.NODE_TYPE_PART_LEAF_REPORTS) {
                    return this.NODE_TYPE_PART_LEAF_REPORTS;
                } else if (key == this.NODE_TYPE_PART_BRANCH_TEMPLATES) {
                    return this.NODE_TYPE_PART_BRANCH_TEMPLATES;
                } else if (key == this.NODE_TYPE_PART_LEAF_TEMPLATES) {
                    return this.NODE_TYPE_PART_LEAF_TEMPLATES;
                } else if (key == this.NODE_TYPE_PART_BRANCH_WORKFLOW_CONFIGURATION) {
                    return this.NODE_TYPE_PART_BRANCH_WORKFLOW_CONFIGURATION;
                } else if (key == this.NODE_TYPE_PART_LEAF_WORKFLOW_CONFIGURATION) {
                    return this.NODE_TYPE_PART_LEAF_WORKFLOW_CONFIGURATION;
                }
                return null;
            }
        }
    }

}    
