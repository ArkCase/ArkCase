/**
 * Created by manoj.dhungana on 12/4/2014.
 */


Admin.View = Admin.View || {
    create: function() {
        if (Admin.View.Correspondence.create)       	{Admin.View.Correspondence.create();}
        if (Admin.View.Organization.create)         	{Admin.View.Organization.create();}
        if (Admin.View.FunctionalAccessControl.create)  {Admin.View.FunctionalAccessControl.create();}
        if (Admin.View.ReportsConfiguration.create)     {Admin.View.ReportsConfiguration.create();}
        if (Admin.View.WorkflowConfiguration.create)    {Admin.View.WorkflowConfiguration.create();}


        if (Admin.View.Tree.create)                 	{Admin.View.Tree.create();}
    }
    ,onInitialized: function() {
        if (Admin.View.Correspondence.onInitialized)       		{Admin.View.Correspondence.onInitialized();}
        if (Admin.View.Organization.onInitialized)         		{Admin.View.Organization.onInitialized();}
        if (Admin.View.FunctionalAccessControl.onInitialized)   {Admin.View.FunctionalAccessControl.onInitialized();}
        if (Admin.View.ReportsConfiguration.onInitialized)      {Admin.View.ReportsConfiguration.onInitialized();}
        if (Admin.View.WorkflowConfiguration.onInitialized)    {Admin.View.WorkflowConfiguration.onInitialized();}


        if (Admin.View.Tree.onInitialized)                 		{Admin.View.Tree.onInitialized();}
    }

    ,Organization:{
        create: function () {
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_REMOVED_MEMBER , this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_CREATED_ADHOC_GROUP, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_REMOVED_ADHOC_GROUP, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_RETRIEVED_USERS, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_ADDED_MEMBERS, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_ADDED_SUPERVISOR, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_RETRIEVED_ERROR, this.onModelRetrievedError);


            if (Admin.View.Organization.ModalDialog.create)         {Admin.View.Organization.ModalDialog.create();}
        }
        , onInitialized: function () {
            if (Admin.View.Organization.ModalDialog.onInitialized)         {Admin.View.Organization.ModalDialog.onInitialized();}
        }
        ,onModelRetrievedHierarchy: function(){
            if (Admin.View.Organization.Tree.create)        {Admin.View.Organization.Tree.create();}
            if (Admin.View.Organization.Tree.onInitialized)        {Admin.View.Organization.Tree.onInitialized();}
        }
        ,onModelRetrievedError: function(errorMsg){
            Acm.Dialog.error(errorMsg);
        }

        ,ModalDialog:{
            create: function () {
                if (Admin.View.Organization.ModalDialog.Members.create)         {Admin.View.Organization.ModalDialog.Members.create();}

                this.$btnCreateAdHocGroup = $("#btnCreateAdHoc");
                this.$btnCreateAdHocGroup.on("click", function(e) {
                    Admin.Model.Organization.setParentNodeFlag(false);
                });

                this.$modalCreateAdHocGroup = $("#createAdHoc");
                //clear existing fields after modal dialog is closed or hidden
                this.$modalLabelCreateAdHocGroup = $("#modalLabelCreateAdHoc");

                this.$modalCreateAdHocGroup.on("hidden.bs.modal", function(e) {
                    Admin.View.Organization.ModalDialog.$modalLabelCreateAdHocGroup.text("Add Ad-Hoc Group");
                    Admin.View.Organization.ModalDialog.clearModalContents();
                });
                this.$txtGroupName = $("#groupName");
                this.$txtGroupDescription = $("#groupDescription");
                this.$btnAddAdHocGroup = $("#btnAddAdHocGroup");
                this.$btnAddAdHocGroup.on("click", function(e) {Admin.View.Organization.ModalDialog.onClickBtnCreateAdHocGroup(e, this);});



                this.$modalAddPeople = $("#addPeople");
                this.$modalLabelPeople = $("#modalLabelPeople");
                this.$btnAddPeople = $("#btnAddPeople");
                this.$btnAddPeople.on("click", function(e) {Admin.View.Organization.ModalDialog.Members.onClickBtnAddPeople(e, this);});

            }
            , onInitialized: function () {
                if (Admin.View.Organization.ModalDialog.Members.onInitialized)         {Admin.View.Organization.ModalDialog.Members.onInitialized();}

            }
            ,onClickBtnCreateAdHocGroup:function(event, ctrl){
                event.preventDefault();
                var parentId = null;
                var hasParentFlag = Admin.Model.Organization.getParentNodeFlag();
                if(hasParentFlag == true){
                    var parentId = Admin.Model.Organization.Tree.getParentNodeTitle();
                }
                var groupName = Admin.View.Organization.ModalDialog.getTextGroupName();
                var groupDescription = Admin.View.Organization.ModalDialog.getTextGroupDescription();
                if(groupName != null && groupName != ""){
                    var group = {};
                    group.name = groupName;
                    if(groupDescription != null && groupDescription != ""){
                        group.description = groupDescription;
                    }
                    Admin.View.Organization.ModalDialog.hideCreateAdHocGroupModal();
                    Admin.View.Organization.ModalDialog.clearModalContents();
                    Admin.Controller.viewCreatedAdHocGroup(group,parentId);
                    Admin.Model.Organization.Tree.setParentNodeTitle(null);
                }
                else{
                    Acm.Dialog.info("Please enter group name.");
                };
            }
            ,clearModalContents: function(){
                Admin.View.Organization.ModalDialog.$txtGroupName.val('');
                Admin.View.Organization.ModalDialog.$txtGroupDescription.val('');
            }
            ,hideCreateAdHocGroupModal: function() {
                this.$modalCreateAdHocGroup.modal('hide');
            }
            ,getTextGroupName: function() {
                return Acm.Object.getValue(this.$txtGroupName);
            }
            ,getTextGroupDescription: function() {
                return Acm.Object.getValue(this.$txtGroupDescription);
            }
            ,Members : {
                create: function () {
                    if (Admin.View.Organization.ModalDialog.Members.Results.create)         {Admin.View.Organization.ModalDialog.Members.Results.create();}
                    if (Admin.View.Organization.ModalDialog.Members.Facets.create)         {Admin.View.Organization.ModalDialog.Members.Facets.create();}
                    if (Admin.View.Organization.ModalDialog.Members.Query.create)         {Admin.View.Organization.ModalDialog.Members.Query.create();}

                }
                , onInitialized: function () {
                }
                ,makeAcmUser: function(selectedMember){
                    var acmUsersFromSolr = Admin.Model.Organization.cacheAcmUsersFromSolr.get("acmUsersFromSolr");
                    var userFound = Admin.View.Organization.Tree.findMembers(selectedMember,acmUsersFromSolr);
                    var acmUser = {};
                    acmUser.userId=userFound.object_id_s;
                    acmUser.fullName=userFound.name;
                    acmUser.firstName=userFound.first_name_lcs;
                    acmUser.lastName=userFound.last_name_lcs;
                    acmUser.userDirectoryName=userFound.userDirectoryName;
                    acmUser.created=userFound.create_date_tdt;
                    acmUser.modified=userFound.modified_date_tdt;
                    acmUser.userState=userFound.status_lcs;
                    acmUser.mail=userFound.email_lcs;
                    acmUser.distinguishedName=userFound.distinguishedName;
                    return acmUser;
                }
                ,onClickBtnAddPeople: function(){
                    var selectedPeople = Admin.Model.Organization.ModalDialog.Members.getSelectedPeople();
                    if(selectedPeople != null){
                        var people = [];
                        var parentGroupId = Admin.Model.Organization.Tree.getParentNodeTitle();

                        for(var i = 0 ; i < selectedPeople.length; i++){
                            var selectedPerson = selectedPeople[i];
                            var acmUser = Admin.View.Organization.ModalDialog.Members.makeAcmUser(selectedPerson);
                            people.push(acmUser);
                        }

                        if(Admin.View.Organization.ModalDialog.$modalLabelPeople){
                            var supervisorFlag = Admin.Model.Organization.ModalDialog.Members.hasSupervisorFlag();
                            if(supervisorFlag == true){
                                if(people.length > 1){
                                    Acm.Dialog.info("Please select one supervisor");
                                }
                                else{
                                    Admin.Controller.viewAddedSupervisor(people[0],parentGroupId);
                                }
                            }
                            else if(supervisorFlag == false){
                                Admin.Controller.viewAddedMembers(people,parentGroupId);
                            }
                        }
                    }
                    Admin.View.Organization.ModalDialog.$modalAddPeople.modal('hide');
                }
                ,Query: {
                    create: function() {
                        this.$edtFindMembers = $("#findMember");
                        this.$btnSearch = this.$edtFindMembers.next().find("button");
                        this.$btnSearch.on("click", function(e) {Admin.View.Organization.ModalDialog.Members.Query.onClickBtnFindMembers(e, this);});
                        this.$edtFindMembers.keyup(function(event){
                            if(13 == event.keyCode){
                                Admin.View.Organization.ModalDialog.Members.Query.$btnSearch.click();
                            }
                        });
                        /*this.$modalBtnFindMembers = $("#btnFindMembers");
                         this.$modalBtnFindMembers.on("click", function(e) {Admin.View.Organization.ModalDialog.Members.Query.onClickBtnFindMembers(e, this);});*/
                    }
                    ,onInitialized: function() {
                    }

                    ,onClickBtnFindMembers: function(event,ctrl){
                        event.preventDefault();
                        var term = Admin.View.Organization.ModalDialog.Members.Query.getTextFindMember();
                        Admin.Controller.viewSubmittedQuery(term);
                    }
                    ,getTextFindMember: function(){
                        return Acm.Object.getValue(this.$edtFindMembers);
                    }

                }
                ,Facets: {
                    create: function(){
                        this.$divFacets = $("#divFacets");
                        Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_ORG_HIERARCHY_CHANGED_FACET  ,this.onModelChangedFacet);

                    }
                    ,onInitialized: function(){}
                    ,onClickCheckBox: function(event, ctrl) {
                        var selected = [];
                        var $checked = Admin.View.Organization.ModalDialog.Members.Facets.$divFacets.find("input:checked");
                        $checked.each(function(){
                            var s = {};
                            s.value = $(this).val();
                            s.name = $(this).parent().attr("name");
                            s.type = $(this).parent().parent().attr("name");
                            selected.push(s);
                        });

                        Admin.Controller.viewChangedFacetSelection(selected);
                    }

                    ,onModelChangedFacet: function(facet) {
                        if (facet.hasError) {
                            //alert("View: onModelChangedFacet, hasError, errorMsg:" + facet.errorMsg);
                        }
                        Admin.View.Organization.ModalDialog.Members.Facets.buildFacetPanel(facet);
                    }

                    ,_getFacetDisplay: function(label, key) {
                        if (Acm.isNotEmpty(label)) {
                            return Acm.goodValue(label);
                        } else {
                            return Acm.goodValue(key);
                        }
                    }

                    ,buildFacetPanel: function(facet) {
                        var html = "";
                        var si = Admin.Model.Organization.ModalDialog.Members.Facets.getSearchInfo();

                        if (Admin.Model.Organization.ModalDialog.Members.Facets.validateSearchFacet(facet)) {
                            if (0 < Admin.Model.Organization.ModalDialog.Members.Facets.getCountFacetFields(facet)){
                                html += "<div name='facet_fields'>";
                                for(var i = 0; i < facet.facet_fields.length; i++) {
                                    if (0 < Acm.goodValue(facet.facet_fields[i].count, 0)) {
                                        if (Acm.isArray(facet.facet_fields[i].values)) {
                                            var display = this._getFacetDisplay(facet.facet_fields[i].label, facet.facet_fields[i].key);
                                            html += "<div name='" + display + "'>";
                                            html += "<label class='label'>" + display + "</label>";
                                            for (var j = 0; j < facet.facet_fields[i].values.length; j++) {
                                                if (0 < Acm.goodValue(facet.facet_fields[i].values[j].count, 0)) {
                                                    html += "</br><input type='checkbox' value='" + Acm.goodValue(facet.facet_fields[i].values[j].name) + "'";
                                                    if (Admin.Model.Organization.ModalDialog.Members.Facets.findFilter(si, display, Acm.goodValue(facet.facet_fields[i].values[j].name))) {
                                                        html += " checked";
                                                    }
                                                    html += ">" + Acm.goodValue(facet.facet_fields[i].values[j].name)
                                                    + "(<span>" + facet.facet_fields[i].values[j].count + "</span>)</input>";
                                                }
                                            }
                                            html += "</div>";
                                        }
                                    }
                                }
                                html += "</div>";
                            }


                            if (0 < Admin.Model.Organization.ModalDialog.Members.Facets.getCountFacetQueries(facet)){
                                html += "<div name='facet_queries'>";
                                for(var i = 0; i < facet.facet_queries.length; i++) {
                                    if (0 < Acm.goodValue(facet.facet_queries[i].count, 0)) {
                                        if (Acm.isArray(facet.facet_queries[i].values)) {
                                            var display = this._getFacetDisplay(facet.facet_queries[i].label, facet.facet_queries[i].key);
                                            html += "<div name='" + display + "'>";
                                            html += "<label class='label'>" + display + "</label>";
                                            for (var j = 0; j < facet.facet_queries[i].values.length; j++) {
                                                if (0 < Acm.goodValue(facet.facet_queries[i].values[j].count, 0)) {
                                                    html += "</br><input type='checkbox' value='" + Acm.goodValue(facet.facet_queries[i].values[j].name) + "'";
                                                    if (Admin.Model.Organization.ModalDialog.Members.Facets.findFilter(si, display, Acm.goodValue(facet.facet_queries[i].values[j].name))) {
                                                        html += " checked";
                                                    }
                                                    html += ">" + Acm.goodValue(facet.facet_queries[i].values[j].name)
                                                    + "(<span>" + facet.facet_queries[i].values[j].count + "</span>)</input>";
                                                }
                                            }
                                            html += "</div>";
                                        }
                                    }
                                }
                                html += "</div>";
                            }


                            if (0 < Admin.Model.Organization.ModalDialog.Members.Facets.getCountFacetDates(facet)){
                                html += "<div name='facet_dates'>";
                                for(var i = 0; i < facet.facet_dates.length; i++) {
                                    if (0 < Acm.goodValue(facet.facet_dates[i].count, 0)) {
                                        if (Acm.isArray(facet.facet_dates[i].values)) {
                                            var display = this._getFacetDisplay(facet.facet_dates[i].label, facet.facet_dates[i].key);
                                            html += "<div name='" + display + "'>";
                                            html += "<label class='label'>" + display + "</label>";
                                            for (var j = 0; j < facet.facet_dates[i].values.length; j++) {
                                                if (0 < Acm.goodValue(facet.facet_dates[i].values[j].count, 0)) {
                                                    html += "</br><input type='checkbox' value='" + Acm.goodValue(facet.facet_dates[i].values[j].name) + "'";
                                                    if (Admin.Model.Organization.ModalDialog.Members.Facets.findFilter(si, display, Acm.goodValue(facet.facet_dates[i].values[j].name))) {
                                                        html += " checked";
                                                    }
                                                    html += ">" + Acm.goodValue(facet.facet_dates[i].values[j].name)
                                                    + "(<span>" + facet.facet_dates[i].values[j].count + "</span>)</input>";
                                                }
                                            }
                                            html += "</div>";
                                        }
                                    }
                                }
                                html += "</div>";
                            }
                        }

                        this.setHtmlDivFacet(html);

                        Admin.View.Organization.ModalDialog.Members.Facets.$divFacets.find("input[type='checkbox']").on("click", function(e) {Admin.View.Organization.ModalDialog.Members.Facets.onClickCheckBox(e, this);});
                    }

                    ,setHtmlDivFacet: function(val) {
                        return Acm.Object.setHtml(Admin.View.Organization.ModalDialog.Members.Facets.$divFacets, val);
                    }
                }

                ,Results: {
                    create: function() {
                        this.$divResults = $("#divMembers");
                        this.createJTableMembers(this.$divResults);

                        Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_SUBMITTED_QUERY         ,this.onViewSubmittedQuery        ,Acm.Dispatcher.PRIORITY_LOW);
                        Acm.Dispatcher.addEventListener(Admin.Controller.VIEW_ORG_HIERARCHY_CHANGED_FACET ,this.onViewChangedFacetSelection ,Acm.Dispatcher.PRIORITY_LOW);
                    }
                    ,onInitialized: function() {
                    }

                    ,onViewSubmittedQuery: function(term) {
                        AcmEx.Object.JTable.load(Admin.View.Organization.ModalDialog.Members.Results.$divResults);
                    }
                    ,onViewChangedFacetSelection: function(selected) {
                        //todo: compare selected with si.filter, do nothing if same

                        AcmEx.Object.JTable.load(Admin.View.Organization.ModalDialog.Members.Results.$divResults);
                    }

                    ,_makeJtData: function(result) {
                        var jtData = AcmEx.Object.JTable.getEmptyRecords();
                        if (result) {
                            for (var i = 0; i < result.docs.length; i++) {
                                var Record = {};
                                Record.id = result.docs[i].object_id_s;
                                Record.name    = Acm.goodValue(result.docs[i].name);
                                Record.type    = Acm.goodValue(result.docs[i].object_type_s);
                                Record.title   = Acm.goodValue(result.docs[i].title_t);
                                Record.owner   = Acm.goodValue(result.docs[i].owner_s);
                                Record.created = Acm.goodValue(result.docs[i].create_dt);
                                jtData.Records.push(Record);
                            }

                            jtData.TotalRecordCount = result.numFound;
                        }
                        return jtData;
                    }
                    ,createJTableMembers: function($jt) {
                        var sortMap = {};
                        sortMap["title"] = "title_t";

                        AcmEx.Object.JTable.usePaging($jt
                            ,{
                                title: 'Search Results'
                                ,multiselect: true
                                ,selecting: true
                                ,selectingCheckboxes: true
                                ,paging: true
                                ,sorting: true
                                ,actions: {
                                    pagingListAction: function (postData, jtParams, sortMap) {
                                        var si = Admin.Model.Organization.ModalDialog.Members.Facets.getSearchInfo();
                                        if (Acm.isEmpty(si.q)) {
                                            return AcmEx.Object.JTable.getEmptyRecords();
                                        }
                                        si.start = Acm.goodValue(jtParams.jtStartIndex, 0);

                                        if (Admin.Model.Organization.ModalDialog.Members.Facets.isFacetUpToDate()) {
                                            //var page = si.start;
                                            //var result = Search.Model.cacheResult.get(page);
                                            var result = Admin.Model.Organization.ModalDialog.Members.Facets.getCachedResult(si);
                                            if (result) {
                                                return Admin.View.Organization.ModalDialog.Members.Results._makeJtData(result);
                                            }
                                        }

                                        return Admin.Service.Organization.facetSearchDeferred(si
                                            ,postData
                                            ,jtParams
                                            ,sortMap
                                            /*,function(data) {
                                             var result = data;
                                             return Admin.View.Organization.ModalDialog.Members.Results._makeJtData(result);
                                             }
                                             ,function(error) {
                                             }*/
                                            ,function(data) {
                                                var result = data;

                                                var title = si.total + ' results of "' + si.q + '"';
                                                AcmEx.Object.JTable.setTitle($jt, title);

                                                return Admin.View.Organization.ModalDialog.Members.Results._makeJtData(result);
                                            }
                                            ,function(error) {
                                                AcmEx.Object.JTable.setTitle($jt, "Error occurred");
                                            }
                                        );

                                    }
                                }

                                ,fields: {
                                    id: {
                                        title: 'ID'
                                        ,key: true
                                        ,list: false
                                        ,create: false
                                        ,edit: false
                                        ,sorting: false
                                    }
                                    ,name: {
                                        title: 'Name'
                                        ,width: '15%'
                                        ,sorting: false
                                    }
                                    ,type: {
                                        title: 'Type'
                                        ,sorting: false
                                    }
                                    ,title: {
                                        title: 'Title'
                                        ,width: '30%'
                                    }
                                    ,owner: {
                                        title: 'Owner'
                                        ,width: '15%'
                                        ,sorting: false
                                    }
                                    ,created: {
                                        title: 'Created'
                                        ,type: 'textarea'
                                        ,width: '20%'
                                        ,sorting: false
                                    }
                                } //end field
                                //Register to selectionChanged event to hanlde events
                                ,selectionChanged: function () {
                                    //Get all selected rows
                                    var $selectedRows = Admin.View.Organization.ModalDialog.Members.Results.$divResults.jtable('selectedRows');
                                    if ($selectedRows.length > 0) {
                                        //Show selected rows
                                        var selectedPeople = [];
                                        $selectedRows.each(function () {
                                            var record = $(this).data('record');
                                            selectedPeople.push(record.id);
                                            Admin.Model.Organization.ModalDialog.Members.setSelectedPeople(selectedPeople);
                                        });
                                    }
                                    else if($selectedRows.length == 0){
                                        Admin.Model.Organization.ModalDialog.Members.setSelectedPeople(null);
                                    }
                                }
                            } //end arg
                            ,sortMap
                        );
                    }
                }
            }

        }
        ,Tree:{
            create: function () {
                this.$treeOrganization = $("#treeOrganization");
                this._useFancyTree(this.$treeOrganization);
            }
            , onInitialized: function () {
            }
            ,findSubgroup: function(subgroupName){
                var subgroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
                if(subgroups != null){
                    for(var i = 0; i < subgroups.length; i++){
                        if(subgroups[i].title == subgroupName){
                            var subgroup = {};
                            subgroup = subgroups[i];
                            return subgroup;
                        }
                    }
                }
            }
            ,findMembers: function(memberId, users){
                if(users != null){
                    for(var i = 0; i < users.length; i++){
                        if(users[i].object_id_s == memberId){
                            var member = {};
                            member = users[i];
                            return member;
                        }
                    }
                }
            }

            ,findAllSubgroupsRecursively: function(group, children){
                //this one is recursive
                if(group.subgroups && group.subgroups !=null){
                    for (var i = 0; i < group.subgroups.length; i++) {
                        var subgroupName = group.subgroups[i];
                        var subgroup = Admin.View.Organization.Tree.findSubgroup(subgroupName);
                        if(subgroup != null) {
                            subgroup.folder = true;
                            children.push(subgroup);
                            if(!subgroup.children){
                                subgroup.children = [];
                            }
                            else if(subgroup.children){
                                subgroup.children.splice(0,subgroup.children.length);
                            }
                            Admin.View.Organization.Tree.findAllSubgroupsRecursively(subgroup,subgroup.children);
                            Admin.View.Organization.Tree.findPeopleInvolvedInGroup(subgroup);
                        }
                    }
                }
            }
            ,findPeopleInvolvedInGroup: function(group){
                //check for subgroup members and supervisors
                var membersForTree = Admin.Model.Organization.cacheMembersForTree.get("membersForTree");
                if(group.members && group.members !=null){
                    for(var j = 0; j < group.members.length ; j++){
                        var groupMemberId = group.members[j];
                        var groupMember = Admin.View.Organization.Tree.findMembers(groupMemberId,membersForTree);
                        if(groupMember != null){
                            group.children.push(groupMember)
                        }
                    }
                }
                if(group.supervisor && group.supervisor !=null){
                    var supervisor = Admin.View.Organization.Tree.findMembers(group.supervisor,membersForTree);
                    if(supervisor){
                        group.supervisor = supervisor.title;
                    }
                }
            }
            ,onClickRemoveGroup: function(node){
                if(node.title != "" && node.title != null){
                    var groupId = node.title;
                    Admin.Controller.viewRemovedGroup(groupId);
                }
            }
            ,onClickRemoveMember: function(node){
                if(node.title != "" && node.title != null){
                    if(node.data.isMember == true){
                        var member = {};
                        member.userId = node.data.object_id_s;
                        var parentGroupId = node.parent.title;
                        //data should be sth like this : [{"userId":"ann-acm"}]
                        var members = [member];
                        Admin.Controller.viewRemovedGroupMember(members, parentGroupId);
                    }
                }
            }
            ,onClickAddSubgroup: function(node){
                Admin.Model.Organization.setParentNodeFlag(true);
                Admin.View.Organization.ModalDialog.$modalLabelCreateAdHocGroup.text("Add Subgroup to " + "'" + node.title + "'");
                Admin.View.Organization.ModalDialog.$modalCreateAdHocGroup.modal('show');
            }
            ,onClickAddMembers: function(node){
                Admin.Model.Organization.setParentNodeFlag(true);
                Admin.Model.Organization.ModalDialog.Members.setSupervisorFlag(false);
                Admin.View.Organization.ModalDialog.$modalLabelPeople.text("Add Members to " + "'" + node.title + "'");
                Admin.View.Organization.ModalDialog.$modalAddPeople.modal('show');
            }
            ,onClickAddSupervisors: function(node){
                Admin.Model.Organization.setParentNodeFlag(true);
                Admin.Model.Organization.ModalDialog.Members.setSupervisorFlag(true);
                Admin.View.Organization.ModalDialog.$modalLabelPeople.text("Add Supervisor to " + "'" + node.title + "'");
                Admin.View.Organization.ModalDialog.$modalAddPeople.modal('show');
            }
            ,onClickButtonsCancelEventBubble: function (e) {
                var evt = e ? e:window.event;
                if (evt.stopPropagation)    evt.stopPropagation();
                if (evt.cancelBubble!=null) evt.cancelBubble = true;
            }

            ,_useFancyTree: function($s) {
                $s.fancytree({
                    extensions: ["table"],
                    checkbox: false,
                    table: {
                        indentation: 16,      // indent 20px per node level
                        nodeColumnIdx: 2,     // render the node title into the 2nd column
                        checkboxColumnIdx: null  // render the checkboxes into the 1st column
                    }
                    ,source: function() {
                        return Admin.View.Organization.Tree.treeSource();
                    } //end source

                    ,renderColumns: function(event, data) {
                        var groups = Admin.Model.Organization.cacheGroups.get("groups");
                        var node = data.node,
                            $tdList = $(node.tr).find(">td");
                        // (index #0 is rendered by fancytree by adding the checkbox)
                        //$tdList.eq(1).text(node.getIndexHier()).addClass("alignRight");
                        // (index #2 is rendered by fancytree)

                        if(node.data.type != null){
                            $tdList.eq(3).text(node.data.type);
                        }
                        if(node.data.supervisor){
                            $tdList.eq(4).text(node.data.supervisor);
                        }
                        if(node.data.type == "ADHOC_GROUP"){

                            $tdList.eq(6).html("<button class='btn btn-link btn-xs pull-left' type='button' name='addSupervisor' title='Add/Edit Supervisor'><i class='fa fa-edit'></i></button>" +
                                "<button class='btn btn-link btn-xs' type='button' name='addSubgroup' title='Add Subgroup'><i class='fa fa-users'></i></button>" +
                                "<button class='btn btn-link btn-xs' type='button' name='addMembers' title='Add Members'><i class='fa fa-user'></i></button>" +
                                "<button class='btn btn-link btn-xs' type='button' name='removeGroup' title='Remove Group'><i class='fa fa-trash-o'></i></button>"
                            );
                        }
                        if(node.data.isMember == true && node.parent.data.type == "ADHOC_GROUP"){
                            $tdList.eq(6).append("<button class='btn btn-link btn-xs' type='button' name='removeMember' title='Remove Member'><i class='fa fa-trash-o'></i></button>");
                        }
                    }
                }); //end fancytree

                $s.delegate("button[name=addSupervisor]", "click", function(e){

                    //$("button[name='addSupervisor']").off("click").on("click",function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    //e.stopPropagation();
                    e.stopImmediatePropagation();

                    //Admin.View.Organization.Tree.onClickButtonsCancelEventBubble(e);
                    Admin.Model.Organization.Tree.setCurrentGroup(node.title,node.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickAddSupervisors(node);
                });



                //$s.undelegate("click").delegate("button[name=removeGroup]", "click", function(e) {
                //$("button[name='removeGroup']").one("click",function(e){
                $s.delegate("button[name=removeGroup]", "click", function(e){
                    //$("button[name='removeGroup']").off("click").on("click",function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
                    //Admin.View.Organization.Tree.onClickButtonsCancelEventBubble(e);
                    Admin.Model.Organization.Tree.setCurrentGroup(node.title,node.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickRemoveGroup(node);
                });

                $s.delegate("button[name=removeMember]", "click", function(e){

                    //$("button[name='removeMember']").off("click").on("click",function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
                    //Admin.View.Organization.Tree.onClickButtonsCancelEventBubble(e);
                    Admin.Model.Organization.Tree.setCurrentGroup(node.parent.title,node.parent.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickRemoveMember(node);
                });

                $s.delegate("button[name=addSubgroup]", "click", function(e){

                    //$("button[name='addSubgroup']").off("click").on("click",function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
                    //Admin.View.Organization.Tree.onClickButtonsCancelEventBubble(e);
                    Admin.Model.Organization.Tree.setCurrentGroup(node.title,node.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickAddSubgroup(node);
                });


                //$("button[name='addMembers']").off("click").on("click",function(e) {
                $s.delegate("button[name=addMembers]", "click", function(e){

                    //$("button[name='addMembers']").off("click").on("click",function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
                    //Admin.View.Organization.Tree.onClickButtonsCancelEventBubble(e);
                    Admin.Model.Organization.Tree.setCurrentGroup(node.title,node.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickAddMembers(node);
                });

            }
            ,treeSource: function(){
                if(Admin.Model.Organization.Tree.isSourceLoaded() == false){
                    var source = [];

                    //group details
                    var groups = Admin.Model.Organization.cacheGroups.get("groups");
                    for(var i = 0; i < groups.length; i++) {
                        var group = groups[i];
                        var children = [];
                        group.expanded = true;
                        group.folder = true;
                        group.children = [];
                        if(group.subgroups != null) {
                            Admin.View.Organization.Tree.findAllSubgroupsRecursively(group, group.children);
                        }

                        //check for group members and supervisor
                        Admin.View.Organization.Tree.findPeopleInvolvedInGroup(group);
                        source.push(group);
                    }
                    Admin.Model.Organization.cacheTreeSource.put("source", source);
                    Admin.Model.Organization.Tree.sourceLoaded(true);
                }

                return Admin.Model.Organization.cacheTreeSource.get("source");

            }
        }
    }

    ,Correspondence : {
        create: function () {

            this.$divCorrespondenceTemplates = $("#divCorrespondenceTemplates");
            this.createJTableCorrespondenceTemplates(this.$divCorrespondenceTemplates);
            this.$btnNewTemplate = $("#addNewTemplate");
            this.$formNewTemplate = $("#formAddNewTemplate");

            this.$btnNewTemplate.on("change", function(e) {Admin.View.Correspondence.onChangeFileInput(e, this);});
            this.$formNewTemplate.submit(function(e) {Admin.View.Correspondence.onSubmitAddTemplate(e, this);});

            AcmEx.Object.JTable.clickAddRecordHandler(this.$divCorrespondenceTemplates,this.onClickSpanAddNewTemplate);

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_CORRESPONDENCE_TEMPLATES_RETRIEVED_TEMPLATES, this.onModelRetrievedCorrespondenceTemplates);

        }
        , onInitialized: function () {
        }
        ,onClickSpanAddNewTemplate: function(event, ctrl) {
            Admin.View.Correspondence.$btnNewTemplate.click();
        }
        ,onChangeFileInput: function(event, ctrl) {
            Admin.View.Correspondence.$formNewTemplate.submit();
        }
        ,onSubmitAddTemplate: function(event, ctrl) {
            event.preventDefault();
            var count = Admin.View.Correspondence.$btnNewTemplate[0].files.length;
            var fd = new FormData();
            for(var i = 0; i < count; i++ ){
                fd.append("files[]", Admin.View.Correspondence.$btnNewTemplate[0].files[i]);
            }
            Admin.Service.Correspondence.uploadTemplateFile(fd);
            Admin.View.Correspondence.$formNewTemplate[0].reset();
        }
        , onModelRetrievedCorrespondenceTemplates: function () {
            AcmEx.Object.JTable.load(Admin.View.Correspondence.$divCorrespondenceTemplates);
        }
        ,createJTableCorrespondenceTemplates: function ($s) {
            $s.jtable({
                title: 'Correspondence Management', messages: {
                    addNewRecord: 'Add New Template'
                }, actions: {
                    listAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        var templates = Admin.Model.Correspondence.cacheTemplatesList.get(0);
                        if (templates) {
                            for (var i = 0; i < templates.length; i++) {
                                var template = templates[i];
                                var record = {};
                                //record.id = Acm.goodValue(template.id, 0);
                                record.title = Acm.goodValue(template.name);
                                record.created = Acm.getDateFromDatetime(template.created);
                                record.creator = Acm.goodValue(template.creator);
                                record.path = Acm.goodValue(template.path);
                                record.modified = Acm.getDateFromDatetime(template.modified);
                                rc.Records.push(record);
                            }
                        }
                        return rc;
                    }, createAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                    }, title: {
                        title: 'Title'
                        , width: '15%'
                        , display: function (commData) {
                            var a = "<a href='" + App.getContextPath() + Admin.Service.Correspondence.API_DOWNLOAD_TEMPLATE
                                + commData.record.path + "'>" + commData.record.title + "</a>";
                            return $(a);
                        }
                    }, created: {
                        title: 'Created'
                        , width: '15%'
                        , edit: false
                    }, modified: {
                        title: 'Modified'
                        , width: '15%'
                        , edit: false
                    }, creator: {
                        title: 'Creator'
                        , width: '15%'
                        , edit: false
                    }
                }
            });
            $s.jtable('load');
        }
    }

    ,FunctionalAccessControl:{
        create: function () {
            // Initialize select HTML elements for roles, not authorized and authorized groups
            this.$selectRoles = $("#selectRoles");
            this.$selectNotAuthorized = $("#selectNotAuthorized");
            this.$selectAuthorized = $("#selectAuthorized");

            // Initialize buttons
            this.$btnGo = $("#btnGo");
            this.$btnMoveRight = $("#btnMoveRight");
            this.$btnMoveLeft = $("#btnMoveLeft");

            // Add listeners for buttons and roles select element
            this.$btnGo.on("click", function(e) {Admin.View.FunctionalAccessControl.onClickBtnGo(e, this);});
            this.$btnMoveRight.on("click", function(e) {Admin.View.FunctionalAccessControl.onClickBtnMoveRight(e, this);});
            this.$btnMoveLeft.on("click", function(e) {Admin.View.FunctionalAccessControl.onClickBtnMoveLeft(e, this);});
            this.$selectRoles.on("change", function(e) {Admin.View.FunctionalAccessControl.onChangeSelectRoles(e, this);});

            // Add listeners for retriving information like roles, groups and roles to groups mapping
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_APPLICATION_ROLES, this.onModelRetrievedFunctionalAccessControlApplicationRoles);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_APPLICATION_ROLES_ERROR, this.onModelError);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_GROUPS, this.onModelRetrievedFunctionalAccessControlGroups);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_ACCESS_CONTROL_GROUPS_ERROR, this.onModelError);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_APPLICATION_ROLES_TO_GROUPS_MAP, this.onModelRetrievedFunctionalAccessControlApplicationRolesToGroups);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_APPLICATION_ROLES_TO_GROUPS_MAP_ERROR, this.onModelError);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FAC_RETRIEVED_SAVE_APPLICATION_ROLES_TO_GROUPS_ERROR, this.onModelError);
        }
        , onInitialized: function () {

        }

        ,onClickBtnGo: function(event, ctrl) {
            // When "Go" button is clicked, just refresh the not authorized and authorized groups for selected role
            Admin.View.FunctionalAccessControl.refresh();
        }

        ,onClickBtnMoveRight: function(event, ctrl) {
            // Get selected role and selected groups from not authorized section
            var selectedRole = Admin.View.FunctionalAccessControl.$selectRoles.val();
            var selectedGroups = Admin.View.FunctionalAccessControl.$selectNotAuthorized.val();

            if (selectedRole && selectedGroups && selectedGroups.length > 0) {
                // Get authorized groups from the cached data
                var authGroups = [];
                if (Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0) &&
                    Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole]) {
                    authGroups = Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole];
                }

                // Update authorized groups in the cached data (add selected groups)
                authGroups = authGroups.concat(selectedGroups);
                Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole] = authGroups;

                // Save authorized groups changes on ACM side
                Admin.Controller.modelSaveFunctionalAccessControlApplicationRolesToGroups(Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0));

                // Refresh not authorized and authorized groups on the screen
                Admin.View.FunctionalAccessControl.refresh();
            }
        }

        ,onClickBtnMoveLeft: function(event, ctrl) {
            // Get selected role and selected groups from authorized section
            var selectedRole = Admin.View.FunctionalAccessControl.$selectRoles.val();
            var selectedGroups = Admin.View.FunctionalAccessControl.$selectAuthorized.val();

            if (selectedRole && selectedGroups && selectedGroups.length > 0) {
                // Get authorized groups from the cached data
                var authGroups = [];
                if (Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0) &&
                    Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole]) {
                    authGroups = Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole];
                }

                // Update authorized groups in the cached data (remove selected groups)
                authGroups = Acm.Object.removeElements(authGroups, selectedGroups);
                Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole] = authGroups;

                // Save authorized groups changes on ACM side
                Admin.Controller.modelSaveFunctionalAccessControlApplicationRolesToGroups(Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0));

                // Refresh not authorized and authorized groups on the screen
                Admin.View.FunctionalAccessControl.refresh();
            }
        }

        ,onChangeSelectRoles: function(event, ctrl) {
            // Remove data from not authorized and authorized section when role is changed
            Acm.Object.createOptions(Admin.View.FunctionalAccessControl.$selectAuthorized, []);
            Acm.Object.createOptions(Admin.View.FunctionalAccessControl.$selectNotAuthorized, []);
        }

        ,onModelRetrievedFunctionalAccessControlApplicationRoles: function() {
            // Get roles from cached data
            var roles = Admin.Model.FunctionalAccessControl.cacheApplicationRoles.get(0);

            // Show roles on the view
            Acm.Object.createOptions(Admin.View.FunctionalAccessControl.$selectRoles, roles);
        }

        ,onModelRetrievedFunctionalAccessControlGroups: function() {
            // Do nothing
        }

        ,onModelRetrievedFunctionalAccessControlApplicationRolesToGroups: function() {
            // Do nothing
        }

        ,onModelError: function(errorMsg) {
            Acm.Dialog.error(errorMsg);
        }

        ,refresh: function() {
            // Initialize authorized and not authorized groups to empty arrays
            var authGroups = [];
            var notAuthGroups = [];

            // Get selected role
            var selected = Admin.View.FunctionalAccessControl.$selectRoles.val();

            if (selected && selected != '') {
                // Get all groups
                var groups = Admin.Model.FunctionalAccessControl.cacheGroups.get(0);

                // Get authorized groups for given role
                if (Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0) &&
                    Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selected]) {
                    authGroups = Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selected];
                }

                // Get not authorized groups
                if (groups) {
                    for (var i = 0; i < groups.length; i++) {
                        var found = false;
                        for (var j = 0; j < authGroups.length; j++) {
                            if (groups[i].name === authGroups[j]) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            notAuthGroups.push(groups[i].name);
                        }
                    }
                }
            }

            // Show authorized and not authorized groups on the screen
            Acm.Object.createOptions(Admin.View.FunctionalAccessControl.$selectAuthorized, authGroups);
            Acm.Object.createOptions(Admin.View.FunctionalAccessControl.$selectNotAuthorized, notAuthGroups);
        }
    }

    ,ReportsConfiguration:{
        create: function () {
            // Initialize select HTML elements for reports, not authorized and authorized groups
            this.$selectReport = $("#selectReport");
            this.$selectNotAuthorized = $("#selectNotAuthorizedReport");
            this.$selectAuthorized = $("#selectAuthorizedReport");

            // Initialize buttons
            this.$btnSelectReport = $("#btnSelectReport");
            this.$btnAuthorize = $("#btnAuthorize");
            this.$btnUnauthorize = $("#btnUnauthorize");

            // Add listeners for buttons and reports select element
            this.$btnSelectReport.on("click", function(e) {Admin.View.ReportsConfiguration.onClickBtnSelectReport(e, this);});
            this.$btnAuthorize.on("click", function(e) {Admin.View.ReportsConfiguration.onClickBtnAuthorize(e, this);});
            this.$btnUnauthorize.on("click", function(e) {Admin.View.ReportsConfiguration.onClickBtnUnauthorize(e, this);});
            this.$selectReport.on("change", function(e) {Admin.View.ReportsConfiguration.onChangeSelectReport(e, this);});

            // Add listeners for retrieving information like reports, groups and reports to groups Map
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REPORT_CONFIGURATION_RETRIEVED_REPORTS, this.onModelReportConfigRetrievedReports);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REPORT_CONFIGURATION_RETRIEVED_ERROR, this.onModelReportConfigError);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REPORT_CONFIGURATION_SAVED_REPORT_TO_GROUPS_MAP, this.onModelReportConfigSavedReportToGroupsMap);


        }
        , onInitialized: function () {

        }

        ,onClickBtnSelectReport: function(event, ctrl) {
            // When "Go" button is clicked, just refresh the not authorized and authorized groups for selected report  && selectedGroups.length > 0
            Admin.View.ReportsConfiguration.refresh();
        }

        ,onClickBtnAuthorize: function(event, ctrl) {
            // Get selected reports and selected groups from not authorized section
            var selectedReport = Admin.View.ReportsConfiguration.$selectReport.val();
            var selectedGroups = Admin.View.ReportsConfiguration.$selectNotAuthorized.val();

            if (selectedReport && selectedGroups && selectedGroups.length > 0) {
                // Get authorized groups from the cached data
                var authGroups = [];
                if (Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap") &&
                    Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selectedReport]) {
                    authGroups = Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selectedReport];
                }

                // Update authorized groups in the cached data (add selected groups)
                authGroups = authGroups.concat(selectedGroups);
                Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selectedReport] = authGroups;

                // Save authorized groups changes on ACM side
                Admin.Controller.viewSavedReportToGroupsMap(Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap"));
            }
        }

        ,onClickBtnUnauthorize: function(event, ctrl) {
            // Get selected report and selected groups from authorized section
            var selectedReport = Admin.View.ReportsConfiguration.$selectReport.val();
            var selectedGroups = Admin.View.ReportsConfiguration.$selectAuthorized.val();

            if (selectedReport && selectedGroups && selectedGroups.length > 0) {
                // Get authorized groups from the cached data
                var authGroups = [];
                if (Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap") &&
                    Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selectedReport]) {
                    authGroups = Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selectedReport];
                }

                // Update authorized groups in the cached data (remove selected groups)
                authGroups = Acm.Object.removeElements(authGroups, selectedGroups);
                Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selectedReport] = authGroups;

                // Save authorized groups changes on ACM side
                Admin.Controller.viewSavedReportToGroupsMap(Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap"));
            }
        }

        ,onChangeSelectReport: function(event, ctrl) {
            // Remove data from not authorized and authorized section when report is changed
            Acm.Object.createOptions(Admin.View.ReportsConfiguration.$selectAuthorized, []);
            Acm.Object.createOptions(Admin.View.ReportsConfiguration.$selectNotAuthorized, []);
        }

        ,onModelReportConfigRetrievedReports: function() {
            // Get reports from cached data
            var reports = Admin.Model.ReportsConfiguration.cacheReports.get("reports");

            // Show reports on the view
            Acm.Object.createOptions(Admin.View.ReportsConfiguration.$selectReport, reports);
        }

        ,onModelReportConfigError: function(errorMsg) {
            Acm.Dialog.error(errorMsg);
        }

        ,onModelReportConfigSavedReportToGroupsMap:function(success){
            if(true == success){
                // Refresh not authorized and authorized groups on the screen
                Admin.View.ReportsConfiguration.refresh();
            }
        }
        ,refresh: function() {
            // Initialize authorized and not authorized groups to empty arrays
            var authGroups = [];
            var notAuthGroups = [];

            // Get selected report
            var selected = Admin.View.ReportsConfiguration.$selectReport.val();

            if (selected && selected != '') {
                // Get all groups
                var groups = Admin.Model.ReportsConfiguration.cacheGroups.get("groups");

                // Get authorized groups for given report
                if (Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap") &&
                    Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selected]) {
                    authGroups = Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap")[selected];
                }

                // Get not authorized groups
                if (groups) {
                    for (var i = 0; i < groups.length; i++) {
                        var found = false;
                        for (var j = 0; j < authGroups.length; j++) {
                            if (groups[i].name === authGroups[j]) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            notAuthGroups.push(groups[i].name);
                        }
                    }
                }
            }
            // Show authorized and not authorized groups on the screen
            Acm.Object.createOptions(Admin.View.ReportsConfiguration.$selectAuthorized, authGroups);
            Acm.Object.createOptions(Admin.View.ReportsConfiguration.$selectNotAuthorized, notAuthGroups);
        }
    }

    ,WorkflowConfiguration:{
        create: function () {
            if (Admin.View.WorkflowConfiguration.History.create)        	{Admin.View.WorkflowConfiguration.History.create();}

            this.$divWorkflowConfiguration = $("#divWorkflowConfiguration");
            this.createJTableWorkflowConfiguration(this.$divWorkflowConfiguration);

            this.$modalUploadBPMN = $("#uploadBPMNModal");
            this.$formUploadBPMN = $("#formUploadBPMN");
            this.$filesSelection = $("#filesSelection");
            this.$btnUploadBPMNConfirm = $("#btnUploadBPMNConfirm");
            this.$btnUploadBPMNConfirm.on("click", function(e) {Admin.View.WorkflowConfiguration.onSubmitUploadBPMN(e, this);});
        }
        , onInitialized: function () {
            if (Admin.View.WorkflowConfiguration.History.onInitialized)        	{Admin.View.WorkflowConfiguration.History.onInitialized();}
        }
        ,onSubmitUploadBPMN: function(event, ctrl) {
            event.preventDefault();
            var count = Admin.View.WorkflowConfiguration.$filesSelection[0].files.length;
            if(count > 0){
                var fd = new FormData();
                for(var i = 0; i < count; i++ ){
                    fd.append("files[]", Admin.View.WorkflowConfiguration.$filesSelection[0].files[i]);
                }

                //todo: fire an event here with fd as content once service is available

                Admin.View.WorkflowConfiguration.$formUploadBPMN[0].reset();
                Admin.View.WorkflowConfiguration.$modalUploadBPMN.modal('hide');
            }
        }
        ,History: {
            create: function () {
                this.$divBPMNHistory = $("#divBPMNHistory");
                this.createJTableBPMNHistory(this.$divBPMNHistory);
            }
            , onInitialized: function () {
            }
            ,createJTableBPMNHistory: function ($s) {
                $s.jtable({
                    actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            //show some dummy records for now
                            rc.Records = [
                                 {"id": 1, "title": "BPMN", "description": "BPMN description", "author": "ArkCase", "modified": "02/12/2015"}
                                ,{"id": 2, "title": "BPMN_1", "description": "BPMN_1 description", "author": "ArkCase", "modified": "02/13/2015"}
                            ];
                            return rc;
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID'
                            , key: true
                            , list: false
                            , create: false
                            , edit: false
                        }, actions: {
                            title: 'Active'
                            , width: '3%'
                            , edit: false
                            , display: function (data) {
                                if (data.record) {
                                    // custom action.
                                    return '<input type="radio" name="activeBPMN" checked/>';
                                }
                            }
                        }, title: {
                            title: 'Business Process'
                            , width: '25%'
                        }, description: {
                            title: 'Description'
                            , width: '15%'
                            , edit: false
                        }, modified: {
                            title: 'Modified'
                            , width: '15%'
                            , edit: false
                        }, author: {
                            title: 'Author'
                            , width: '15%'
                            , edit: false
                        }
                    }
                });
                $s.jtable('load');
            }

        }
        ,createJTableWorkflowConfiguration: function ($s) {
            $s.jtable({
                title:'Workflow Configuration'
                , actions: {
                    listAction: function (postData, jtParams) {
                        var rc = AcmEx.Object.jTableGetEmptyRecords();
                        rc.Records = [
                            {"id": 1, "title": "BPMN", "description": "BPMN description", "author": "ArkCase", "modified": "02/12/2015"}
                            ,{"id": 2, "title": "BPMN_1", "description": "BPMN_1 description", "author": "ArkCase", "modified": "02/13/2015"}
                        ];
                        return rc;
                    }
                }
                , fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                    }, title: {
                        title: 'Business Process'
                        , width: '20%'
                    }, description: {
                        title: 'Description'
                        , width: '15%'
                        , edit: false
                    }, modified: {
                        title: 'Modified'
                        , width: '15%'
                        , edit: false
                    }, author: {
                        title: 'Author'
                        , width: '15%'
                        , edit: false
                    }, actions: {
                        title: 'Active'
                        , width: '30%'
                        , edit: false
                        , display: function (data) {
                            if (data.record) {
                                // custom action.
                                return '<a href="#" class="active"><i class="fa fa-download text-active"> Download </i></a>'
                                    + ' | <a href="#" class="active" data-toggle="modal" data-target="#uploadBPMNModal"><i class="fa fa-upload text-active"> Replace File </i></a>'
                                    + ' | <a href="#" class="active"   data-toggle="modal" data-target="#BPMNHistory"><i class="fa fa-retweet text-active"> Version History </i></a>';
                            }
                        }
                    }
                }
            });
            $s.jtable('load');
        }
    }

    ,Tree:{
        create: function () {
            this.$tree = $("#tree");
            this._useFancyTree(this.$tree);
        }
        , onInitialized: function () {
        }
        ,showPanel: function(key) {
            var tabIds = Admin.Model.Tree.Key.getTabIds();
            var tabIdsToShow = Admin.Model.Tree.Key.getTabIdsByKey(key);
            for (var i = 0; i < tabIds.length; i++) {
                var show = Acm.isItemInArray(tabIds[i], tabIdsToShow);
                Acm.Object.show($("#" + tabIds[i]), show);
            }
        }
        ,_useFancyTree: function($s) {

            $s.fancytree({
                activate: function(event, data){
                    var node = data.node;
                    Admin.View.Tree.showPanel(node.key);
                },
                source: function() {
                    return Admin.View.Tree.treeSource();
                } //end source

            }); //end fancytree

            $s.contextmenu({
                //delegate: "span.fancytree-title",
                delegate: ".fancytree-title",
                beforeOpen: function(event, ui) {
                    var node = $.ui.fancytree.getNode(ui.target);
                    node.setActive();
                },
                select: function(event, ui) {
                    var node = $.ui.fancytree.getNode(ui.target);
                    alert("select " + ui.cmd + " on " + node);
                }
            });

        }

        ,treeSource: function() {
            var builder = AcmEx.FancyTreeBuilder.reset();

            builder.addBranch({key: "acc"                                                   //level 1: /Access Control
                ,title: "Security"
                ,tooltip: "Security"
                ,folder : true
                ,expanded: true
            })
                .addLeaf({key: "fac"                                                            //level 1.1: /Security/Functional Access Control
                    ,title: "Functional Access Control"
                    ,tooltip: "Functional Access Control"
                })
                .addLeaf({key: "ldap"                                                           //level 1.2: /Security/LDAP Configuration
                    ,title: "LDAP Configuration"
                    ,tooltip: "LDAP Configuration"
                })
                .addLeafLast({key: "og"                                                         //level 1.3: /Security/Organization Hierarchy
                    ,title: "Organizational Hierarchy"
                    ,tooltip: "Organizational Hierarchy"
                })

            builder.addBranch({key: "dsh"                                                       //level 2: /Dashboard
                ,title: "Dashboard"
                ,tooltip: "Dashboard"
                ,folder : true
                ,expanded: true
            })
                .addLeafLast({key: "dc"                                                         //level 2.1: /Dashboard/Dashboard Configuration
                    ,title: "Dashboard Configuration"
                    ,tooltip: "Dashboard Configuration"
                })

            builder.addBranch({key: "rpt"                                                       //level 3: /Reports
                ,title: "Reports"
                ,tooltip: "Reports"
                ,folder : true
                ,expanded: true
            })
                .addLeafLast({key: "rc"                                                         //level 3.1: /Reports/Reports Configuration
                    ,title: "Reports Configuration"
                    ,tooltip: "Reports Configuration"
                })


            //for demo purposes
            builder.addBranch({key: "forms"                                               //level 4: /Forms
                ,title: "Forms"
                ,tooltip: "Forms"
                ,folder : true
                ,expanded: true
            })
                .addLeafLast({key: "fc"                                                           //level 4.1: /Forms/Form Configuration
                    ,title: "Form Configuration"
                    ,tooltip: "Form Configuration"
                })
                .addBranch({key: "wf"                                                               //level 4.1.1: /Forms/Form Configuration/Workflows
                    ,title: "Workflows"
                    ,tooltip: "Workflows"
                    ,folder : true
                    ,expanded: true
                })
                .addLeafLast({key: "wfc"                                                                //level 4.1.1.1: /Forms/Form Configuration/Workflows/Workflow Configuration
                    ,title: "Workflow Configuration"
                    ,tooltip: "Workflow Configuration"
                })

                .addBranch({key: "wfl"                                                              //level 4.2.1: /Forms/Form Configuration/Form/Workflow Link
                    ,title: "Form/Workflow Link"
                    ,tooltip: "Form/Workflow Link"
                    ,folder : true
                    ,expanded: true
                })
                .addLeafLast({key: "wflc"                                                                //level 4.2.1.1: /Forms/Form Configuration/Form/Workflow Link/Link Forms/Workflows
                    ,title: "Link Forms/Workflows"
                    ,tooltip: "Link Forms/Workflows"
                })

                .addBranch({key: "bo"                                                               //level 4.3.1: /Forms/Form Configuration/Form/Business Objects
                    ,title: "Business Objects"
                    ,tooltip: "Business Objects"
                    ,folder : true
                    ,expanded: true
                })
                .addLeafLast({key: "boc"                                                                    //level 4.3.1.1: /Forms/Form Configuration/Form/Business Objects/Business Object Configuration
                    ,title: "Business Object Configuration"
                    ,tooltip: "Business Object Configuration"
                })

                .addBranch({key: "al"                                                           //level 4.4.1: /Forms/Form Configuration/Form/Application Labels
                    ,title: "Application Labels"
                    ,tooltip: "Application Labels"
                    ,folder : true
                    ,expanded: true
                })
                .addLeafLast({key: "lc"                                                                 //level 4.4.1.1: /Forms/Form Configuration/Form/Application Labels/Label Configuration
                    ,title: "Label Configuration"
                    ,tooltip: "Label Configuration"
                })
                .addBranchLast({key: "cm"                                                           //level 4.5.1: /Forms/Form Configuration/Form/Correspondence Management
                    ,title: "Correspondence Management"
                    ,tooltip: "Correspondence Management"
                    ,folder : true
                    ,expanded: true
                })
                .addLeafLast({key: "ct"                                                                 //level 4.5.1.1: /Forms/Form Configuration/Form/Correspondence Templates
                    ,title: "Correspondence Templates"
                    ,tooltip: "Correspondence Templates"
                })

            return builder.getTree();
        }
    }

};
