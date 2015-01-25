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
            this.cacheGroup = new Acm.Model.CacheFifo(4);
            this.cacheGroups = new Acm.Model.CacheFifo(4);
            this.cacheSubgroups = new Acm.Model.CacheFifo(4);
            this.cacheTreeSource = new Acm.Model.CacheFifo(4);
            this.cacheAllUsers = new Acm.Model.CacheFifo(4);
            this.cacheSelectedMembers = new Acm.Model.CacheFifo(4);
            this.cacheAcmUsersFromSolr = new Acm.Model.CacheFifo(4);
            this.cacheResult = new Acm.Model.CacheFifo(4);

            Admin.Service.Organization.retrieveGroups();

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_GROUPS, this.onModelRetrievedGroups);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_CREATED_AD_HOC_GROUP, this.onViewCreatedAdHocGroup);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_REMOVED_GROUP_MEMBER, this.onViewRemovedGroupMember);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ADDED_GROUP_MEMBERS, this.onViewAddedMembers);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_REMOVED_GROUP, this.onViewRemovedGroup);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_SEARCHED_MEMBERS, this.onViewSearchedMembers);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_SUBMITTED_QUERY          ,Admin.Model.Organization.Facets.onViewSubmittedQuery);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_CHANGED_FACET_SELECTION  ,Admin.Model.Organization.Facets.onViewChangedFacetSelection);
            Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ADDED_GROUP_SUPERVISOR, this.onViewAddedSupervisor);

        }
        ,onInitialized: function() {
        }
        ,fn: function() {
            setTimeout(Admin.Service.Organization.retrieveGroups(),10000);
        }
        ,Facets:{
            onViewSubmittedQuery: function(term) {
                var si = Admin.Model.Organization.Facets.getSearchInfo();
                if (!Acm.compare(term, si.q)) {
                    si.q = term;
                    Admin.Model.Organization.Facets.setFacetUpToDate(false);
                }
            }
            ,onViewChangedFacetSelection: function(selected) {
                //todo: compare selected with si.filter, do nothing if same

                var si = Admin.Model.Organization.Facets.getSearchInfo();
                si.filter = selected;
            }

            //
            // filter array json format: [{key, value}, ...]
            //
            ,_searchInfo: {
                q: null
                ,start: 0
                ,n: 16
                ,total: 0
                ,filter: []
                ,sort: []
            }
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
                Admin.Model.Organization.cacheResult.put(page, result);
            }
            //,selectFilter: function(filterType, filterKey, filterValue, select) {
            ,addFilter: function(si, key, value) {
                var filter = this.findFilter(si, key, value);
                if (!filter) {
                    filter = {};
                    filter.key = key;
                    filter.value = value;
                    si.filter.push(filter);
                }


                //also, in View, jQuery selector for select
            }
            ,removeFilter: function(si, key, value) {
                for (var i = 0; i < si.filter.length; i++) {
                    var f = si.filter[i];
                    if (f.key == key && f.value == value) {
                        si.filter.splice(i, 1);
                        break;
                    }
                }
            }
            ,findFilter: function(si, key, value) {
                var found = null;
                for (var i = 0; i < si.filter.length; i++) {
                    var f = si.filter[i];
                    if (f.key == key && f.value == value) {
                        found = f;
                        break;
                    }
                }
                return found;
            }

            ,_facetUpToDate: true
            ,isFacetUpToDate: function() {
                return this._facetUpToDate;
            }
            ,setFacetUpToDate: function(facetUpToDate) {
                this._facetUpToDate = facetUpToDate;
            }

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
//facetSearch
/*
                var facetSearch={
                    "responseHeader":{
                    "status":0,
                        "QTime":27,
                        "params":{
                        "facet":"true",
                            "sort":"",
                            "indent":"true",
                            "facet.query":["{!key='Create Date, Previous Week'}create_date_tdt:[NOW/DAY-7DAY TO *]",
                            "{!key='Create Date, Previous Month'}create_date_tdt:[NOW/DAY-1MONTH TO *]",
                            "{!key='Create Date, Previous Year'}create_date_tdt:[NOW/DAY-1YEAR TO *]",
                            "{!key='Due_Date, Previous Week'}dueDate_tdt:[NOW/DAY-7DAY TO *]",
                            "{!key='Due_Date, Previous Month'}dueDate_tdt:[NOW/DAY-1MONTH TO *]",
                            "{!key='Due_Date, Previous Year'}dueDate_tdt:[NOW/DAY-1YEAR TO *]",
                            "{!key='Modify Date, Previous Week'}modified_date_tdt:[NOW/DAY-1YEAR TO *]"],
                            "start":"0",
                            "q":"*:*",
                            "facet.field":["{!key='Create User'}creator_lcs",
                            "{!key='City'}location_city_lcs",
                            "{!key='Task Status'}status_s",
                            "{!key='Person,Organization Type'}type_lcs",
                            "{!key='Object Type'}object_type_s",
                            "{!key='Assignee Full Name'}assignee_full_name_lcs",
                            "{!key='Priority'}priority_lcs",
                            "{!key='Postal Code'}location_postal_code_sdo",
                            "{!key='Task Priority'}priority_s",
                            "{!key='Incident Date'}incident_date_tdt",
                            "{!key='State'}location_state_lcs",
                            "{!key='Parent Type'}parent_type_s",
                            "{!key='Parent Task Type'}parent_object_type_s",
                            "{!key='Modify User'}modifier_lcs",
                            "{!key='Incident Type'}incident_type_lcs",
                            "{!key='Status'}status_lcs"],
                            "wt":"json",
                            "fq":["{!frange l=1}sum(if(exists(protected_object_b), 0, 1), if(protected_object_b, 0, 1), if(public_doc_b, 1, 0), termfreq(allow_acl_ss, 'ann-acm'), termfreq(allow_acl_ss, 'ROLE_ADMINISTRATOR'), termfreq(allow_acl_ss, 'ACM_ADMINISTRATOR_DEV'))",
                            "-deny_acl_ss:ann-acm AND -deny_acl_ss:ROLE_ADMINISTRATOR AND -deny_acl_ss:ACM_ADMINISTRATOR_DEV"],
                            "rows":"8"}},
                    "response":{"numFound":308,"start":0,"docs":[
                    {
                        "id":"403-CONTACT-METHOD",
                        "object_id_s":"403",
                        "object_type_s":"CONTACT-METHOD",
                        "name":"123-456-7890",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "type_lcs":"Mobile Phone",
                        "value_parseable":"123-456-7890",
                        "_version_":1484041929550200832},
                    {
                        "id":"404-CONTACT-METHOD",
                        "object_id_s":"404",
                        "object_type_s":"CONTACT-METHOD",
                        "name":"john.doe@gmail.com",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "type_lcs":"Personal E-mail",
                        "value_parseable":"john.doe@gmail.com",
                        "_version_":1484041929680224256},
                    {
                        "id":"402-LOCATION",
                        "object_id_s":"402",
                        "object_type_s":"LOCATION",
                        "name":"123 Main St., Peoria, IL  12345",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "location_street_address_lcs":"123 Main St.",
                        "location_city_lcs":"Peoria",
                        "location_state_lcs":"IL",
                        "location_postal_code_sdo":"12345",
                        "_version_":1484041929729507328},
                    {
                        "id":"394-COMPLAINT",
                        "object_id_s":"394",
                        "object_type_s":"COMPLAINT",
                        "name":"20140806_197",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "title_parseable":"fire on the mountain",
                        "incident_date_tdt":"2014-09-19T00:00:00Z",
                        "priority_lcs":"Medium",
                        "incident_type_lcs":"Agricultural",
                        "status_lcs":"DRAFT",
                        "_version_":1484041929815490560},
                    {
                        "id":"407-PERSON",
                        "object_id_s":"407",
                        "object_type_s":"PERSON",
                        "name":"John Doe",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "person_title_lcs":"Mr.",
                        "first_name_lcs":"John",
                        "last_name_lcs":"Doe",
                        "organization_id_ss":["405-ORGANIZATION"],
                        "postal_address_id_ss":["402-LOCATION"],
                        "contact_method_ss":["403-CONTACT-METHOD",
                            "404-CONTACT-METHOD"],
                        "_version_":1484041930005282816},
                    {
                        "id":"405-ORGANIZATION",
                        "object_id_s":"405",
                        "object_type_s":"ORGANIZATION",
                        "name":"tech net",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "type_lcs":"sample",
                        "value_parseable":"tech net",
                        "_version_":1484041930009477120},
                    {
                        "id":"408-PERSON-ASSOCIATION",
                        "object_type_s":"PERSON-ASSOCIATION",
                        "name":"John Doe (Subject)",
                        "create_date_tdt":"2014-09-30T14:12:52Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-06T11:50:08Z",
                        "modifier_lcs":"ann-acm",
                        "description_parseable":"Simple Description",
                        "type_lcs":"Subject",
                        "child_id_s":"407",
                        "child_type_s":"PERSON",
                        "parent_id_s":"394",
                        "parent_type_s":"COMPLAINT",
                        "_version_":1484041930012622848},
                    {
                        "id":"409-COMPLAINT",
                        "object_id_s":"409",
                        "object_type_s":"COMPLAINT",
                        "name":"20140806_198",
                        "create_date_tdt":"2014-09-30T14:13:41Z",
                        "creator_lcs":"ann-acm",
                        "modified_date_tdt":"2014-11-17T11:14:03Z",
                        "modifier_lcs":"ann-acm",
                        "title_parseable":"fire on the tree top",
                        "incident_date_tdt":"2014-09-19T00:00:00Z",
                        "priority_lcs":"Medium",
                        "assignee_id_lcs":"ann-acm",
                        "assignee_first_name_lcs":"Ann",
                        "assignee_last_name_lcs":"Administrator",
                        "incident_type_lcs":"Agricultural",
                        "status_lcs":"IN APPROVAL",
                        "_version_":1485036159776063488}]
                },
                    "facet_counts":{
                    "facet_queries":{
                        "Create Date, Previous Week":3,
                            "Create Date, Previous Month":14,
                            "Create Date, Previous Year":261,
                            "Due_Date, Previous Week":0,
                            "Due_Date, Previous Month":0,
                            "Due_Date, Previous Year":19,
                            "Modify Date, Previous Week":261},
                    "facet_fields":{
                        "Create User":[
                            "ann-acm",242,
                            "acm3",7,
                            "ian-acm",4,
                            "ebmillar",1],
                            "City":[
                            "vienna",3,
                            "city",2,
                            "peoria",1],
                            "Task Status":[
                            "COMPLETE",18,
                            "CREATE",16,
                            "ASSIGNMENT",5,
                            "DELETE",3,
                            "SAVE",2,
                            "SAVED",2],
                            "Person,Organization Type":[
                            "initiator",18,
                            "complaintant",7,
                            "subject",4,
                            "contact type",2,
                            "home phone",2,
                            "org type",2,
                            "email",1,
                            "mobile phone",1,
                            "office phone",1,
                            "personal e-mail",1,
                            "sample",1,
                            "witness",1],
                            "Object Type":[
                            "DOCUMENT",127,
                            "TASK",46,
                            "PERSON",33,
                            "PERSON-ASSOCIATION",30,
                            "COMPLAINT",25,
                            "GROUP",12,
                            "CASE_FILE",10,
                            "CONTACT-METHOD",8,
                            "USER",7,
                            "LOCATION",6,
                            "ORGANIZATION",3],
                            "Assignee Full Name":[
                            "ann-acm",14],
                            "Priority":[
                            "low",25,
                            "medium",4,
                            "high",2,
                            "expedite",1],
                            "Postal Code":[
                            "",2,
                            "12345",2,
                            "21212",1,
                            "22345",1],
                            "Task Priority":[
                            "Medium",23,
                            "Low",13,
                            "High",10],
                            "Incident Date":[
                            "2014-12-10T00:00:00Z",15,
                            "2014-11-12T00:00:00Z",3,
                            "2014-12-09T00:00:00Z",3,
                            "2014-09-19T00:00:00Z",2,
                            "2014-07-29T20:00:00Z",1,
                            "2014-11-17T00:00:00Z",1],
                            "State":[
                            "va",3,
                            "state",2,
                            "il",1],
                            "Parent Type":[
                            "COMPLAINT",61,
                            "TASK",55,
                            "CASE",31,
                            "CASE_FILE",10,
                            "GROUP",6],
                            "Parent Task Type":[
                            "CASE_FILE",30,
                            "COMPLAINT",13],
                            "Modify User":[
                            "ann-acm",234,
                            "ian-acm",12,
                            "acm3",8],
                            "Incident Type":[
                            "agricultural",19,
                            "better business bureau",4,
                            "background investigation",3,
                            "better business dispute",3,
                            "arson",2,
                            "government",2,
                            "domestic dispute",1,
                            "pollution",1],
                            "Status":[
                            "active",145,
                            "draft",21,
                            "valid",7,
                            "in approval",5,
                            "closed",3]},
                    "facet_dates":{},
                    "facet_ranges":{}}};
*/

                var facet = {
                    facet_queries  : []
                    ,facet_fields  : []
                    ,facet_dates   : []
                    ,facet_ranges  : []
                };

                if (Admin.Model.Organization.Facets.validateFacetSearchData(facetSearch)) {
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
        ,Tree:{
            _sourceLoaded: false
            ,isSourceLoaded : function() {
                return this._sourceLoaded;
            }
            ,sourceLoaded : function(sourceLoaded) {
                this._sourceLoaded = sourceLoaded;
            }
            ,_parentNode: null
            ,_currentGroup: null
            ,getParentNode : function() {
                return this._parentNode;
            }
            ,setParentNode : function(parentNode) {
                this._parentNode = parentNode;
            }
            ,getCurrentGroup : function() {
                return this._currentGroup;
            }
            ,setCurrentGroup : function(groupName) {
                var groups = Admin.Model.Organization.cacheGroups.get("groups");
                var subGroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
                var foundInGroup = false;
                for(var i = 0; i < groups.length; i++){
                    if(groupName == groups[i].title){
                        this._currentGroup = groups[i];
                        foundInGroup = true;
                        break;
                    }
                }
                if(foundInGroup == false){
                    for(var j = 0; j < subGroups.length; j++){
                        if(groupName == subGroups[j].title){
                            this._currentGroup = subGroups[j];
                            break;
                        }
                    }
                }
            }

        }

        ,validateGroup: function(group) {
            if (Acm.isEmpty(group)) {
                return false;
            }
            return true;
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
        	
        	Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_SAVE_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS, this.onSaveFunctionalAccessControlApplicationRolesToGroups);
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