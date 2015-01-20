/**
 * Created by manoj.dhungana on 12/4/2014.
 */


Admin.View = Admin.View || {
    create: function() {
        if (Admin.View.AccessControl.create)        	{Admin.View.AccessControl.create();}
        if (Admin.View.Correspondence.create)       	{Admin.View.Correspondence.create();}
        if (Admin.View.Organization.create)         	{Admin.View.Organization.create();}
        if (Admin.View.FunctionalAccessControl.create)  {Admin.View.FunctionalAccessControl.create();}

        if (Admin.View.Tree.create)                 	{Admin.View.Tree.create();}
    }
    ,onInitialized: function() {
        if (Admin.View.AccessControl.onInitialized)        		{Admin.View.AccessControl.onInitialized();}
        if (Admin.View.Correspondence.onInitialized)       		{Admin.View.Correspondence.onInitialized();}
        if (Admin.View.Organization.onInitialized)         		{Admin.View.Organization.onInitialized();}
        if (Admin.View.FunctionalAccessControl.onInitialized)   {Admin.View.FunctionalAccessControl.onInitialized();}

        if (Admin.View.Tree.onInitialized)                 		{Admin.View.Tree.onInitialized();}
    }

    ,Organization:{
        create: function () {
            //if (Admin.View.Organization.Tree.create)        {Admin.View.Organization.Tree.create();}
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_GROUPS, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_REMOVED_GROUP, this.onModelRetrievedHierarchy);

            if (Admin.View.Organization.ModalDialog.create)         {Admin.View.Organization.ModalDialog.create();}

            /*Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_GROUP, this.onModelRetrievedHierarchy);
            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_GROUP_MEMBERS, this.onModelRetrievedHierarchy);*/
        }
        , onInitialized: function () {
            if (Admin.View.Organization.ModalDialog.onInitialized)         {Admin.View.Organization.ModalDialog.onInitialized();}
        }
        ,onModelRetrievedHierarchy: function(){
            if (Admin.View.Organization.Tree.create)        {Admin.View.Organization.Tree.create();}
        }
        ,findSubgroupDetails: function(subgroupName){
            var subgroups = Admin.Model.Organization.cacheSubgroups.get("subgroups");
            var subgroup = {};
            for(var i = 0; i < subgroups.length; i++){
                if(subgroups[i].name == subgroupName){
                    subgroup = subgroups[i];
                    break;
                }
            }
            return subgroup;
        }
        ,findMembers: function(group){
            var members = [];
            if(group.member_id_ss !=null){
                var memberIds = group.member_id_ss;
                for(var j = 0; j< memberIds.length; j++){
                    var member = {};
                    member.title = memberIds[j];
                    /*member.firstName = group.object_sub_type_s;
                    member.lastName = group.supervisor_id_ss;
                    member.jobTitle = group.location;
                    member.groupRole = group.role;*/
                    //member.parentId = group.parent_id_s;
                    member.parentGroupType = group.object_sub_type_s;
                    member.isMember = true;
                    member.folder = false;
                    member.children = [];
                    members.push(member);
                }
            }
            return members;
        }
        ,ModalDialog:{
            create: function () {
                this.$modalCreateAdHocGroup = $("#createAdHoc");
                this.$modalCreateAdHocGroup.on("hidden.bs.modal", function(e) {Admin.View.Organization.ModalDialog.$modalLabel.text("Add Ad-Hoc Group");});
                this.$txtGroupName = $("#groupName");
                this.$txtGroupDescription = $("#groupDescription");
                this.$adHocGroup = $("#addAdHocGroup");
                this.$adHocGroup.on("click", function(e) {Admin.View.Organization.ModalDialog.onClickBtnCreateAdHocGroup(e, this);});
                this.$modalLabel = $("#modalLabel");
                this.$modalAddPeople = $("#addPeople");
                this.$modalLabelPeople = $("#modalLabelPeople");

            }
            , onInitialized: function () {

            }
            ,onClickBtnCreateAdHocGroup:function(event, ctrl){
                event.preventDefault();
                var parentId = Admin.Model.Organization.Tree.getParentNode();
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
                    Admin.Service.Organization.createAdHocGroup(group,parentId);
                    //reset the saved parent node in preparation of next addition
                    Admin.Model.Organization.Tree.setParentNode(null);
                }
                else{
                    Acm.Dialog.info("Please enter group name.");
                };
            }

            ,clearModalContents: function(){
                Admin.View.Organization.ModalDialog.$txtGroupName.val('');
                Admin.View.Organization.ModalDialog.$txtGroupDescription.val('');
                //Admin.View.Organization.ModalDialog.$modalLabel[0].textContent.reset();
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

        }
        ,Tree:{
            create: function () {
                this.$treeOrganization = $("#treeOrganization");
                this._useFancyTree(this.$treeOrganization);
            }
            , onInitialized: function () {
            }
            ,onClickBtnRemoveItem: function(node){
                event.preventDefault();
                if(node.title != "" && node.title != null){
                    if(node.data.isMember == true){
                        var member = {};
                        member.userId = node.title;
                        var parentGroupId = node.parent.title;
                        //data should be sth like this : [{"userId":"ann-acm"}]
                        Admin.Service.Organization.removeGroupMember([member], parentGroupId);

                        /*Acm.Dialog.confirm("<b> Are you sure you want to remove  " + "'" + node.title + "'" + " from " + "'" + node.parent.title + "'" + " ? </b>"
                            ,function(result) {
                                if (result == true) {
                                    Admin.Service.Organization.removeGroupMember([member], parentGroupId);
                                }
                            }
                        );*/
                    }
                    else{
                        var groupId = node.title;
                        Admin.Service.Organization.removeGroup(groupId);
                        /*Acm.Dialog.confirm("<b> Are you sure you want to remove  " + "'" + node.title + "'" + " and all its subgroups/members ? </b>"
                            ,function(result) {
                                if (result == true) {
                                    var groupId = node.title;
                                    Admin.Service.Organization.removeGroup(groupId);
                                }
                            }
                        );*/
                    }
                }
            }
            ,onClickBtnAddSubgroup: function(node){
                event.preventDefault();
                Admin.View.Organization.ModalDialog.$modalLabel.text("Add Subgroup to " + "'" + node.title + "'");
                Admin.View.Organization.ModalDialog.$modalCreateAdHocGroup.modal('show');
            }
            ,onClickBtnAddMembers: function(node){
                event.preventDefault();
                Admin.View.Organization.ModalDialog.$modalLabelPeople.text("Add Members to " + "'" + node.title + "'");
                Admin.View.Organization.ModalDialog.$modalAddPeople.modal('show');
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

                        //group details
                        var groups = Admin.Model.Organization.cacheGroups.get("groups");
                        var source = [];
                        for(var i = 0; i < groups.length; i++) {
                            var group = {};
                            var subgroups = [];
                            group.title = groups[i].name;
                            group.type = groups[i].object_sub_type_s;
                            group.supervisor = groups[i].supervisor_id_ss;
                            group.location = groups[i].location;
                            group.expanded = true;
                            group.folder = true;
                            //find group members
                            var members = Admin.View.Organization.findMembers(groups[i]);
                            if (members.length > 0) {
                                for (var k = 0; k < members.length; k++) {
                                    subgroups.push(members[k]);
                                }
                            }

                            //subgroups details
                            if (groups[i].child_id_ss != null) {
                                var children = groups[i].child_id_ss;
                                for (var j = 0; j < children.length; j++) {
                                    var subGroupM = [];
                                    var child = {};
                                    var subgroup = Admin.View.Organization.findSubgroupDetails(children[j]);
                                    child.title = children[j];
                                    child.type = subgroup.object_sub_type_s;
                                    child.parentId = subgroup.parent_id_s;
                                    child.supervisor = subgroup.supervisor_id_ss;
                                    child.location = subgroup.location;
                                    child.folder = true;

                                    //find subgroup members
                                    var subGroupMembers = Admin.View.Organization.findMembers(subgroup);
                                    /*if (subGroupMembers.length > 0) {
                                        for (var l = 0; l < subGroupMembers.length; l++) {
                                            subGroupM.push(subGroupMembers[l]);
                                        }
                                    }*/
                                    child.children = subGroupMembers;
                                    subgroups.push(child);
                                }
                            }
                            group.children = subgroups;
                            source.push(group);
                        }
                        return source;
                    } //end source
                    ,renderColumns: function(event, data) {
                        var groups = Admin.Model.Organization.cacheGroups.get("groups");
                        var node = data.node,
                            $tdList = $(node.tr).find(">td");
                            // (index #0 is rendered by fancytree by adding the checkbox)
                            //$tdList.eq(1).text(node.getIndexHier()).addClass("alignRight");
                            // (index #2 is rendered by fancytree)

                        $tdList.eq(3).text(node.data.type);
                        $tdList.eq(4).text(node.data.supervisor);
                        $tdList.eq(5).text(node.data.location);
                        if(node.data.type == "ADHOC_GROUP"){
                            $tdList.eq(6).html("<button class='btn btn-link btn-xs' type='button' id='addNewItem' title='Add Subgroup'><i class='fa fa-users'></i></button>" +
                            "<button class='btn btn-link btn-xs' type='button' id='addMembers' title='Add Members'><i class='fa fa-user'></i></button>"
                            );
                        }
                        if(node.data.type == "ADHOC_GROUP" || node.data.parentGroupType == "ADHOC_GROUP"){
                            $tdList.eq(6).append("<button class='btn btn-link btn-xs' type='button' id='removeItem' title='Remove'><i class='fa fa-trash-o'></i></button>");
                        }
                    }
                    ,click: function(event, data) {
                        var node = data.node,
                            $tdList = $(node.tr).find(">td");
                        Admin.Model.Organization.Tree.setParentNode(node.title);
                    }
                }); //end fancytree

                $s.delegate("button[id=removeItem]", "click", function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopPropagation();
                    Admin.View.Organization.Tree.onClickBtnRemoveItem(node);
                });

                $s.delegate("button[id=addNewItem]", "click", function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopPropagation();
                    Admin.Model.Organization.Tree.setParentNode(node.title);
                    Admin.View.Organization.Tree.onClickBtnAddSubgroup(node);
                });

                $s.delegate("button[id=addMembers]", "click", function(e) {
                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopPropagation();
                    Admin.Model.Organization.Tree.setParentNode(node.title);
                    Admin.View.Organization.Tree.onClickBtnAddMembers(node);
                });

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
        }
    }
    ,AccessControl : {
        create: function () {

            this.$divAdminAccessControlPolicy = $("#divACP");
            this.createJTableAdminAccessControl(this.$divAdminAccessControlPolicy);

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_UPDATED_ACCESS_CONTROL, this.onModelUpdatedAccessControlList);

        }
        , onInitialized: function () {
        }


        , onModelUpdatedAccessControlList: function () {
            AcmEx.Object.JTable.load(Admin.View.AccessControl.$divAdminAccessControlPolicy);
        }
        , _makeJtData: function (accessControlList) {
            var jtData = AcmEx.Object.JTable.getEmptyRecords();
            if (accessControlList) {
                for (var i = 0; i < accessControlList.length; i++) {
                    var Record = {};
                    Record.objectType = accessControlList[i].objectType;
                    Record.objectState = accessControlList[i].objectState;
                    Record.accessLevel = accessControlList[i].accessLevel;
                    Record.accessorType = accessControlList[i].accessorType;
                    Record.accessDecision = accessControlList[i].accessDecision;
                    Record.allowDiscretionaryUpdate = (accessControlList[i].allowDiscretionaryUpdate);
                    Record.id = accessControlList[i].id;
                    jtData.Records.push(Record);
                }
                jtData.TotalRecordCount = Admin.Model.getTotalCount();
            }
            return jtData;
        }
        , createJTableAdminAccessControl: function ($jt) {
            var sortMap = {};
            sortMap["dateTime"] = "auditDateTime";

            AcmEx.Object.JTable.usePaging($jt
                , {
                    title: 'Data Access Control'
                    , selecting: true
                    , multiselect: false
                    , selectingCheckboxes: false
                    , actions: {
                        pagingListAction: function (postData, jtParams, sortMap) {
                            //var pageIndex = jtParams.jtStartIndex;
                            var pageIndex = jtParams.jtPageSize.toString() + jtParams.jtStartIndex.toString();
                            if (0 > pageIndex) {
                                return AcmEx.Object.JTable.getEmptyRecords();
                            }
                            var accessControlList = Admin.Model.AccessControl.cacheAccessControlList.get(pageIndex);
                            if (accessControlList) {
                                return Admin.View.AccessControl._makeJtData(accessControlList);

                            } else {
                                return Admin.Service.AccessControl.retrieveAccessControlListDeferred(postData
                                    , jtParams
                                    , sortMap
                                    , function (data) {
                                        var accessControlList = data;
                                        return Admin.View.AccessControl._makeJtData(accessControlList);
                                    }
                                    , function (error) {
                                    }
                                );
                            }  //end else
                        }
                        , updateAction: function (postData, jtParams) {
                            var record = Acm.urlToJson(postData);
                            var rc = {"Result": "OK", "Record": {}};
                            rc = AcmEx.Object.JTable.getEmptyRecord();
                            rc.Record.accessDecision = record.accessDecision;
                            rc.Record.allowDiscretionaryUpdate = record.allowDiscretionaryUpdate;
                            return rc;

//                        return {
//                            "Result": "OK", "Record": { "id": 3, "objectType": "Dr.", "objectState": "Joe", "accessLevel": "Lee", "accessorType": "Witness", "accessDecision": "someone", "allowDiscretionaryUpdate": "dd" }
//                        };
                            //                    var rc = {"Result": "OK", "Record": {id:123, objectType:"hello", objectState:"st", accessLevel: "lv", accessDecision:"ds", allowDiscretionaryUpdate:"ad"}};
                            //                    return rc;
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID', key: true, type: 'hidden'
                            //   ,list: true
                            , create: false, edit: false
                        }, objectType: {
                            title: 'Object Type', width: '3%', edit: false
                            //,sorting : true
                            , options: [
                                { Value: 'Complaint', DisplayText: 'Complaint' },
                                { Value: 'Task', DisplayText: 'Task' },
                                { Value: 'caseFile', DisplayText: 'Case File' }
                            ]

                        }, objectState: {
                            title: 'State', width: '3%', edit: false, options: [
                                { Value: 'ACTIVE', DisplayText: 'Active' },
                                { Value: 'ASSIGNED', DisplayText: 'Assigned' },
                                { Value: 'COMPLETE', DisplayText: 'Complete' },
                                { Value: 'DRAFT', DisplayText: 'Draft' },
                                { Value: 'IN APPROVAL', DisplayText: 'In Approval' },
                                { Value: 'Scheduled', DisplayText: 'Scheduled' },
                                { Value: 'UNASSIGNED', DisplayText: 'Unassigned' }
                            ]
                        }, accessLevel: {
                            title: 'Access Level', width: '5%', edit: false, options: [
                                { Value: 'Add Document', DisplayText: 'Add Document' },
                                { Value: 'Add Item', DisplayText: 'Add Item' },
                                { Value: 'Approve Complaint', DisplayText: 'Approve Complaint' },
                                { Value: 'delete', DisplayText: 'Delete' },
                                { Value: 'read', DisplayText: 'Read' },
                                { Value: 'Save', DisplayText: 'Save' },
                                { Value: 'Submit for Approval', DisplayText: 'Submit for Approval' },
                                { Value: 'update', DisplayText: 'Update' }
                            ]
                        }, accessorType: {
                            title: 'Accessor Type', width: '5%', edit: false
                        }, accessDecision: {
                            title: 'Access Decision',
                            width: '5%', options: [
                                { Value: 'GRANT', DisplayText: 'Grant' },
                                { Value: 'DENY', DisplayText: 'Deny' },
                                { Value: 'MANDATORY_DENY', DisplayText: 'Mandatory Deny' }
                            ]

                            // ,options: ['GRANT' , 'DENY', 'MANDATORY_DENY']
                        }, allowDiscretionaryUpdate: {
                            title: 'Allow Discretionary',
                            width: '10%', options: [
                                { Value: 'true', DisplayText: 'True' } ,
                                { Value: 'false', DisplayText: 'False' }
                            ]
                        }

                    } //end field
                    ,recordUpdated: function (event, data) { //opened handler
                        var adminAccessUpdated = {};
                        adminAccessUpdated.id = data.record.id;
                        adminAccessUpdated.objectType = data.record.objectType;
                        adminAccessUpdated.objectState = data.record.objectState;
                        adminAccessUpdated.accessLevel = data.record.accessLevel;
                        adminAccessUpdated.accessorType = data.record.accessorType;
                        adminAccessUpdated.accessDecision = data.record.accessDecision;
                        if ("true" == data.record.allowDiscretionaryUpdate) {
                            adminAccessUpdated.allowDiscretionaryUpdate = true;
                        } else {
                            adminAccessUpdated.allowDiscretionaryUpdate = false;
                        }
                        Admin.Service.AccessControl.updateAdminAccess(adminAccessUpdated);
                    }
                } //end arg
                , sortMap
            );
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

            Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_CORRESPONDENCE_TEMPLATES, this.onModelRetrievedCorrespondenceTemplates);

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
                        , width: '30%'
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
        	Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES, this.onModelRetrievedFunctionalAccessControlApplicationRoles);
        	Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_GROUPS, this.onModelRetrievedFunctionalAccessControlGroups);
        	Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_RETRIEVED_FUNCTIONAL_ACCESS_CONTROL_APPLICATION_ROLES_TO_GROUPS, this.onModelRetrievedFunctionalAccessControlApplicationRolesToGroups);
        }
        , onInitialized: function () {
            
        }
        
        ,onClickBtnGo: function(e) {
        	// When "Go" button is clicked, just refresh the not authorized and authorized groups for selected role
        	Admin.View.FunctionalAccessControl.refresh();
        }
        
        ,onClickBtnMoveRight: function(e) {
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
        		Admin.Service.FunctionalAccessControl.saveApplicationRolesToGroups(Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0));

        		// Refresh not authorized and authorized groups on the screen
        		Admin.View.FunctionalAccessControl.refresh();
        	}
        }
        
        ,onClickBtnMoveLeft: function(e) {
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
        		authGroups = Admin.View.FunctionalAccessControl.removeElements(authGroups, selectedGroups);
        		Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0)[selectedRole] = authGroups;
        		
        		// Save authorized groups changes on ACM side
        		Admin.Service.FunctionalAccessControl.saveApplicationRolesToGroups(Admin.Model.FunctionalAccessControl.cacheApplicationRolesToGroups.get(0));

        		// Refresh not authorized and authorized groups on the screen
        		Admin.View.FunctionalAccessControl.refresh();
        	}
        }
        
        ,onChangeSelectRoles: function(e) {
        	// Remove data from not authorized and authorized section when role is changed
        	Admin.View.FunctionalAccessControl.createOptions(Admin.View.FunctionalAccessControl.$selectAuthorized, []);
        	Admin.View.FunctionalAccessControl.createOptions(Admin.View.FunctionalAccessControl.$selectNotAuthorized, []);
        }
        
        ,onModelRetrievedFunctionalAccessControlApplicationRoles: function() {
        	// Get roles from cached data
        	var roles = Admin.Model.FunctionalAccessControl.cacheApplicationRoles.get(0);
        	
        	// Show roles on the view
        	Admin.View.FunctionalAccessControl.createOptions(Admin.View.FunctionalAccessControl.$selectRoles, roles);
        }
        
        ,onModelRetrievedFunctionalAccessControlGroups: function() {
        	// Do nothing
        }
        
        ,onModelRetrievedFunctionalAccessControlApplicationRolesToGroups: function() {
        	// Do nothing
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
        	Admin.View.FunctionalAccessControl.createOptions(Admin.View.FunctionalAccessControl.$selectAuthorized, authGroups);
        	Admin.View.FunctionalAccessControl.createOptions(Admin.View.FunctionalAccessControl.$selectNotAuthorized, notAuthGroups);
        }
        
        ,createOptions: function(element, optionsArray) {
        	var options = '';
        	if (optionsArray) {
        		for (var i = 0; i < optionsArray.length; i++) {
        		   options += '<option value="' + optionsArray[i] + '">' + optionsArray[i] + '</option>';
        		} 
        	}
        	element.html(options);        		
        }
        
        ,removeElements: function(elements, elementsToRemove) {
        	var output = [];
        	
        	if (elements) {
        		if (elementsToRemove) {
        			for (var i = 0; i < elements.length; i++) {
        				var found = false;
        				for (var j = 0; j < elementsToRemove.length; j++) {
        					if (elements[i] === elementsToRemove[j]) {
        						found = true;
        						break;
        					}
        				}
        				if (!found) {
        					output.push(elements[i]);
        				}
        			}
        		}else{
        			return elements;
        		}
        	}
        	
        	return output;
        }
    }

    ,Tree:{
        create: function () {
            this.$btnCreateAdHocGroup = $("#btnCreateAdHoc");
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
                if(show == true && tabIdsToShow == "tOrganization"){
                    this.$btnCreateAdHocGroup.show();
                }
                else{
                    this.$btnCreateAdHocGroup.hide();
                }
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
                .addLeaf({key: "dac"                                                        //level 1.1: /Access Control/Data Access Control
                    ,title: "Data Access Control"
                    ,tooltip: "Data Access Control"
                })
                .addLeaf({key: "fac"                                                        //level 1.2: /Access Control/Functional Access Control
                    ,title: "Functional Access Control"
                    ,tooltip: "Functional Access Control"
                })
                .addLeaf({key: "ldap"                                                   //level 1.3: /Access Control/LDAP Configuration
                    ,title: "LDAP Configuration"
                    ,tooltip: "LDAP Configuration"
                })
                .addLeafLast({key: "og"                                                        //level 1.1: /Access Control/Data Access Control
                    ,title: "Organizational Hierarchy"
                    ,tooltip: "Organizational Hierarchy"
                })

            builder.addBranch({key: "dsh"                                               //level 2: /Dashboard
                ,title: "Dashboard"
                ,tooltip: "Dashboard"
                ,folder : true
                ,expanded: true
            })
                .addLeafLast({key: "dc"                                                 //level 2.1: /Dashboard/Dashboard Configuration
                    ,title: "Dashboard Configuration"
                    ,tooltip: "Dashboard Configuration"
                })

            builder.addBranch({key: "rpt"                                               //level 3: /Reports
                ,title: "Reports"
                ,tooltip: "Reports"
                ,folder : true
                ,expanded: true
            })
                .addLeafLast({key: "rc"                                                     //level 3.1: /Reports/Reports Configuration
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
                .addLeafLast({key: "wfc"                                                                //level 4.2.1.1: /Forms/Form Configuration/Form/Workflow Link/Link Forms/Workflows
                    ,title: "Link Forms/Workflows"
                    ,tooltip: "Link Forms/Workflows"
                })

                .addBranch({key: "bo"                                                               //level 4.3.1: /Forms/Form Configuration/Form/Business Objects
                    ,title: "Business Objects"
                    ,tooltip: "Business Objects"
                    ,folder : true
                    ,expanded: true
                })
                .addLeafLast({key: "wfc"                                                                    //level 4.3.1.1: /Forms/Form Configuration/Form/Business Objects/Business Object Configuration
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
