/**
 * Created by manoj.dhungana on 12/4/2014.
 */


Admin.View = Admin.View || {
    create: function() {
        if (Admin.View.Correspondence.create)       	{Admin.View.Correspondence.create();}
        if (Admin.View.LDAPConfiguration.create)        {Admin.View.LDAPConfiguration.create();}
        if (Admin.View.LabelConfiguration.create)       {Admin.View.LabelConfiguration.create();}
        if (Admin.View.Organization.create)         	{Admin.View.Organization.create();}
        if (Admin.View.FunctionalAccessControl.create)  {Admin.View.FunctionalAccessControl.create();}
        if (Admin.View.RolesPrivileges.create)          {Admin.View.RolesPrivileges.create();}
        if (Admin.View.ReportsConfiguration.create)     {Admin.View.ReportsConfiguration.create();}
        if (Admin.View.WorkflowConfiguration.create)    {Admin.View.WorkflowConfiguration.create();}
        if (Admin.View.Forms.create)    				{Admin.View.Forms.create();}
        if (Admin.View.LinkFormsWorkflows.create)       {Admin.View.LinkFormsWorkflows.create();}
        if (Admin.View.Logo.create)                     {Admin.View.Logo.create();}
        if (Admin.View.CustomCss.create)                {Admin.View.CustomCss.create();}


        if (Admin.View.Tree.create)                 	{Admin.View.Tree.create();}
    }
    ,onInitialized: function() {
        if (Admin.View.Correspondence.onInitialized)       		{Admin.View.Correspondence.onInitialized();}
        if (Admin.View.LDAPConfiguration.onInitialized)       	{Admin.View.LDAPConfiguration.onInitialized();}
        if (Admin.View.LabelConfiguration.onInitialized)       	{Admin.View.LabelConfiguration.onInitialized();}
        if (Admin.View.Organization.onInitialized)         		{Admin.View.Organization.onInitialized();}
        if (Admin.View.FunctionalAccessControl.onInitialized)   {Admin.View.FunctionalAccessControl.onInitialized();}
        if (Admin.View.RolesPrivileges.onInitialized)           {Admin.View.RolesPrivileges.onInitialized();}
        if (Admin.View.ReportsConfiguration.onInitialized)      {Admin.View.ReportsConfiguration.onInitialized();}
        if (Admin.View.LinkFormsWorkflows.onInitialized)        {Admin.View.LinkFormsWorkflows.onInitialized();}
        if (Admin.View.Logo.onInitialized)                      {Admin.View.Logo.onInitialized();}
        if (Admin.View.CustomCss.onInitialized)                 {Admin.View.CustomCss.onInitialized();}
        if (Admin.View.WorkflowConfiguration.onInitialized)     {Admin.View.WorkflowConfiguration.onInitialized();}
        if (Admin.View.Forms.onInitialized)    					{Admin.View.Forms.onInitialized();}


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

                this.$btnAddPeople = $("#btnAddPeople");
                this.$btnAddPeople.on("click", function(e) {Admin.View.Organization.ModalDialog.Members.onClickBtnAddPeople(e, this);});

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
                Admin.Model.Organization.Tree.setSupervisorFlag(false);

                SearchBase.Dialog.create({name: "pickMember"
                    ,title: "Add Members"
                    ,prompt: "Enter to search for members."
                    ,btnGoText: "Go!"
                    ,btnOkText: "Add"
                    ,btnCancelText: "Cancel"
                    ,filters: [{key: "Object Type", values: ["USER"]}]
                    ,onClickBtnPrimary : function(event, ctrl) {
                        var people = [];
                        SearchBase.View.Results.getSelectedRows().each(function () {
                            var record = $(this).data('record');
                            if(!Acm.isEmpty(record) && !Acm.isEmpty(record.id)){
                                var acmUser = Admin.View.Organization.ModalDialog.makeAcmUser(record.id);
                                if(!Acm.isEmpty(acmUser)){
                                    people.push(acmUser);
                                }
                            }
                        });
                        if(!Acm.isArrayEmpty(people)){
                            var parentGroupId = Admin.Model.Organization.Tree.getParentNodeTitle();
                            Admin.Controller.viewAddedMembers(people,parentGroupId);
                        }
                    }
                }).show();
            }
            ,onClickAddSupervisors: function(node){
                Admin.Model.Organization.setParentNodeFlag(true);
                Admin.Model.Organization.Tree.setSupervisorFlag(true);

                SearchBase.Dialog.create({name: "pickSupervisor"
                    ,title: "Add Supervisor"
                    ,prompt: "Enter to search for supervisor."
                    ,btnGoText: "Go!"
                    ,btnOkText: "Add"
                    ,btnCancelText: "Cancel"
                    ,filters: [{key: "Object Type", values: ["USER"]}]
                    ,onClickBtnPrimary : function(event, ctrl) {
                        var people = [];
                        SearchBase.View.Results.getSelectedRows().each(function () {
                            var record = $(this).data('record');
                            if(!Acm.isEmpty(record) && !Acm.isEmpty(record.id)){
                                var acmUser = Admin.View.Organization.ModalDialog.makeAcmUser(record.id);
                                if(!Acm.isEmpty(acmUser)){
                                    people.push(acmUser);
                                }
                            }
                        });
                        if(!Acm.isArrayEmpty(people) && people.length > 1){
                            Acm.Dialog.info("Please select one supervisor");
                        }
                        else if(!Acm.isArrayEmpty(people)){
                            var parentGroupId = Admin.Model.Organization.Tree.getParentNodeTitle();
                            Admin.Controller.viewAddedSupervisor(people[0],parentGroupId);
                        }
                    }
                }).show();
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

                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();

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

                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
                    Admin.Model.Organization.Tree.setCurrentGroup(node.parent.title,node.parent.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickRemoveMember(node);
                });

                $s.delegate("button[name=addSubgroup]", "click", function(e){

                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
                    Admin.Model.Organization.Tree.setCurrentGroup(node.title,node.data.isInGroupCache);
                    Admin.View.Organization.Tree.onClickAddSubgroup(node);
                });


                $s.delegate("button[name=addMembers]", "click", function(e){

                    var node = $.ui.fancytree.getNode(e),
                        $input = $(e.target);
                    e.stopImmediatePropagation();
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

    ,LDAPConfiguration: {
        create: function() {
            this.$divLDAPDirectories    = $("#divLDAPDirectories");
            this.createJTableLDAPDirectories(this.$divLDAPDirectories);
        }
        , onInitialized: function(){

        }
        , createJTableLDAPDirectories: function($s) {
            var context = $s;
            $s.jtable({
                title: 'LDAP Directories'
                ,paging: false
                ,sorting: true
                ,pageSize: 10 //Set page size (default: 10)
                ,messages: {
                    addNewRecord: 'Add New Directory'
                }
                ,actions: {
                    listAction: function(postData, jtParams) {
                        return $.Deferred(function ($dfd){
                            var rc = {};
                            Admin.Service.LDAPConfiguration.retrieveDirectories()
                                .done(function(data) {
                                    rc = {
                                        Result: 'OK',
                                        Records: data
                                    }
                                    $dfd.resolve(rc);
                                })
                                .fail(function(){
                                    rc = {
                                        Result: 'OK',
                                        Records: []
                                    }
                                    $dfd.resolve(rc);
                                });
                        });
                    }
                    ,createAction: function(postData, jtParams) {
                        return $.Deferred(function ($dfd) {
                            var record = Acm.urlToJson(postData);
                            Admin.Service.LDAPConfiguration.createDirectory(record)
                                .done(function(data){
                                    var rc = {
                                        Result: 'OK',
                                        Record: data
                                    }
                                    $dfd.resolve(rc);
                                })
                                .fail(function(response){
                                    var rc = {
                                        Result: 'ERROR',
                                        Record: [],
                                        Message: (response && response.errorMsg) ? response.errorMsg : 'Can\'t create LDAP Directory'
                                    }
                                    $dfd.resolve(rc);
                                });
                        });
                    }
                    ,updateAction: function(postData, jtParams) {
                        console.log($s);
                        return $.Deferred(function ($dfd) {
                            var record = Acm.urlToJson(postData);
                            Admin.Service.LDAPConfiguration.updateDirectory(record['ldapConfig.id'], record)
                                .done(function(data){
                                    var rc = {
                                        Result: 'OK',
                                        Record: data
                                    }
                                    $dfd.resolve(rc);
                                })
                                .fail(function(response){
                                    var rc = {
                                        Result: 'ERROR',
                                        Record: [],
                                        Message: (response && response.errorMsg) ? response.errorMsg : 'Can\'t update LDAP Directory'
                                    }
                                    $dfd.resolve(rc);
                                });
                        });
                    }
                    ,deleteAction: function(postData, jtParams) {
                        return $.Deferred(function($dfd) {
                            var id = postData['ldapConfig.id'];
                            Admin.Service.LDAPConfiguration.deleteDirectory(id)
                                .done(function(data){
                                    var rc = {
                                        Result: 'OK'
                                    };
                                    $dfd.resolve(rc);
                                })
                                .fail(function(response){
                                    var rc = {
                                        Result: 'ERROR',
                                        Message: (response && response.errorMsg) ? response.errorMsg : 'Can\'t delete LDAP Directory'
                                    };
                                    $dfd.resolve(rc);
                                });
                        });
                    }
                }

                ,fields: {
                    'ldapConfig.id': {
                        title: 'ID'
                        ,key: true
                        ,list: false
                        ,create: true
                        ,edit: false
                    }
                    ,'ldapConfig.name': {
                        title: 'Name'
                        ,visibility: 'fixed'
                    }
                    ,'ldapConfig.ldapUrl': {
                        title: 'LDAP Url'
                    }
                    ,'ldapConfig.base': {
                        title: 'Base'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.directoryName': {
                        title: 'Directory Name'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.authUserDn': {
                        title: 'Auth User Dn'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.authUserPassword': {
                        title: 'Auth User Password'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.userSearchBase': {
                        title: 'User Search Base'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.groupSearchBase': {
                        title: 'Group Search Base'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.groupSearchBaseOU': {
                        title: 'Group Search Base OU'
                        ,visibility: 'hidden'
                    }
                    ,'ldapConfig.userIdAttributeName': {
                        title: 'User Id Attribute Name'
                        ,visibility: 'hidden'
                    }
                }
                ,formCreated: function(e, data) {
                    data.form.css('width','350px');
                    data.form.find('input[type="text"]').css('width','350px');
                    data.form.find('input[name="ldapConfig.id"]').addClass('validate[required, custom[onlyLetterNumber]]');
                    data.form.find('input[name="ldapConfig.name"]').addClass('validate[required]');
                    data.form.validationEngine();

                }
                ,formSubmitting: function(e, data) {
                    return data.form.validationEngine('validate');
                }
                ,formClosed: function (event, data) {
                    data.form.validationEngine('hide');
                    data.form.validationEngine('detach');
                }

            });
            $s.jtable('load');
        }
    }

    ,LabelConfiguration: {
        create: function () {
            this.$btnApplyDefaultLanguage = $("#labelConfigurationApplyDefaultLanguage");
            this.$btnApplyDefaultLanguage.click($.proxy(this.onClickApplyDefaultLanguageBtn, this));
        }
        , onInitialized: function () {
            var context = Admin.View.LabelConfiguration;
            var settingsDeferred = Admin.Service.LabelConfiguration.retrieveSettings();
            var langsDeferred = Admin.Service.LabelConfiguration.retrieveLanguages();
            var nsDeferred = Admin.Service.LabelConfiguration.retrieveNamespaces();
            $.when(
                settingsDeferred,
                langsDeferred,
                nsDeferred
            ).done(function(settings, languages, namespaces) {
                // Fill Languages options
                context.settings = settings;
                var langOptions = [];
                var defLangOptions = [];
                Admin.Service.LabelConfiguration._namespaces = namespaces;


                _.forEach(languages, function(langItem){
                    // Select default language in options
                    var selected = (settings.defaultLang === langItem) ? 'selected' : '';
                    langOptions.push($('<option value="{0}" {1}>{0}</option>'.format(langItem, selected )));
                    defLangOptions.push($('<option value="{0}" {1}>{0}</option>'.format(langItem, selected)));
                });

                $('#labelConfigurationDefaultLanguage').html(defLangOptions);
                $('#labelConfigurationDefaultLanguage').prop('disabled', false);

                $('#labelConfigurationApplyDefaultLanguage').prop('disabled', false);

                $('#labelConfigurationLanguage').html(langOptions);
                $('#labelConfigurationLanguage').prop('disabled', false);

                $('#labelConfigurationResetAllResources').prop('disabled', false);
                $('#labelConfigurationResetCurrentResources').prop('disabled', false);

                // Generate namespaces options
                var nsOptions = [];
                _.forEach(namespaces, function(nsItem){
                    nsOptions.push($('<option value="{0}">{1}</option>'.format(nsItem.id, nsItem.name)));
                });
                $('#labelConfigurationNamespace').html(nsOptions);
                $('#labelConfigurationNamespace').prop('disabled', false);


                // Create jTable data
                context.$divLabelConfiguration = $("#divLabelConfiguration");
                context.createJTableLabelConfiguration(context.$divLabelConfiguration);
            });
        }

        ,onLabelConfigurationFilterChanged: function(e) {
            // Create keyup  processing delay to prevent often jTable rendering
            if (this._changeDelayTimer) {
                window.clearTimeout(Admin.View.LabelConfiguration._changeFilterDelayTimer)
            }
            Admin.View.LabelConfiguration._changeFilterDelayTimer = window.setTimeout(function(){
                var $s = e.data.$s;
                var loadData = e.data.loadData;
                $s.jtable('load', {loadData: loadData});
            }, 300);
        }

        ,onLangNamespaceChanged: function(e){
            var $s = e.data.$s;
            var loadData = e.data.loadData;
            $s.jtable('load', {loadData: loadData});
        }

        ,onClickApplyDefaultLanguageBtn: function(e) {
            e.preventDefault();
            var newDefaultLang = $('#labelConfigurationDefaultLanguage').val();
            this.settings.defaultLang = newDefaultLang;
            Admin.Service.LabelConfiguration.updateSettings(this.settings);
        }

        ,onClickResetAllResourcesBtn: function(e) {
            var namespaces = _.pluck(Admin.Service.LabelConfiguration._namespaces, 'id');
            var $s = e.data.$s;
            Admin.Service.LabelConfiguration.resetResource($('#labelConfigurationLanguage').val(), namespaces)
                .done(function(){
                    $s.jtable('load', {loadData: true});
                });
        }

        ,onClickResetCurrentResourcesBtn: function(e) {
            var $s = e.data.$s;
            Admin.Service.LabelConfiguration.resetResource($('#labelConfigurationLanguage').val(), $('#labelConfigurationNamespace').val())
                .done(function(){
                    $s.jtable('load', {loadData: true});
                });
        }

        ,createJTableLabelConfiguration: function ($s) {
            $('#labelConfigurationIdFilter, #labelConfigurationValueFilter').bind('keyup change', {$s: $s, loadData: false}, this.onLabelConfigurationFilterChanged);
            $('#labelConfigurationNamespace, #labelConfigurationLanguage').change({$s: $s, loadData: true}, this.onLangNamespaceChanged);

            $("#labelConfigurationResetAllResources").click({$s: $s}, $.proxy(this.onClickResetAllResourcesBtn, this));
            $("#labelConfigurationResetCurrentResources").click({$s: $s}, $.proxy(this.onClickResetCurrentResourcesBtn, this));


            var tableData = null;
            var context = Admin.View.LabelConfiguration;

            $s.jtable({
                //title: 'Label Configuration'
                sorting: true
                ,defaultSorting: 'value ASC'
                ,actions: {
                    listAction: function (postData, jtParams) {

                        var idFilter = $('#labelConfigurationIdFilter').val();
                        var valueFilter = $('#labelConfigurationValueFilter').val();
                        var editLanguage = $('#labelConfigurationLanguage').val();
                        var editNamespace = $('#labelConfigurationNamespace').val();


                        var processRecords = function (records) {
                            var processedRecords = [];
                            if (records) {
                                // Apply filters to records
                                processedRecords = _.filter(records, function(item){
                                    var idFilterPassed = true;
                                    var valueFilterPassed = true;

                                    if (idFilter) {
                                        idFilterPassed = (item.id.toLocaleLowerCase().indexOf(idFilter.toLowerCase()) != -1);
                                    }

                                    if (valueFilter) {
                                        valueFilterPassed = (item.value.toLocaleLowerCase().indexOf(valueFilter.toLowerCase()) != -1);
                                    }

                                    return idFilterPassed && valueFilterPassed;
                                });

                                // Add reset value
                                for (var i = 0; i < processedRecords.length; i++) {
                                    processedRecords[i].reset = (processedRecords[i].value != processedRecords[i].defaultValue);
                                }

                                // Sort records if required
                                if (jtParams.jtSorting) {
                                    var params = jtParams.jtSorting.split(' ');
                                    var fieldId = params[0];
                                    var sortDir = params[1];
                                    processedRecords = _.sortBy(processedRecords, fieldId);
                                    if (sortDir === 'DESC') {
                                        processedRecords.reverse();
                                    }
                                } else {
                                    processedRecords = records;
                                }
                            }
                            return (processedRecords);
                        }

                        // Prevent server request for filtering ans sorting
                        if (!Admin.Service.LabelConfiguration._data || postData && postData.loadData) {
                            return $.Deferred(function ($dfd){
                                var rc = {};
                                Admin.Service.LabelConfiguration.retrieveResource(editLanguage, editNamespace)
                                    .done(function(data) {
                                        Admin.Service.LabelConfiguration._data = data;
                                        rc = {
                                            Result: 'OK',
                                            Records: []
                                        }
                                        $dfd.resolve(rc);
                                        // This HACK helps to clear lastPostData of jTable to prevent excess server request.
                                        $s.jtable('load', {loadData: false});
                                    })
                                    .fail(function(){
                                        rc = {
                                            Result: 'OK',
                                            Records: []
                                        }
                                        $dfd.resolve(rc);
                                    });
                            });
                        } else {
                            return {
                                Result: 'OK',
                                Records: processRecords(Admin.Service.LabelConfiguration._data)
                            }
                        }
                    }, createAction: function (postData, jtParams) {
                        return {
                            "Result": "OK"
                        };
                    }
                }, fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , edit: false
                        , width: '25%'
                    }, value: {
                        title: 'Value'
                        , edit: false
                        , width: '25%'
                        , display: function(data){
                            var modifiedClass = (data.record.value !== data.record.defaultValue)? 'editable-unsaved': '';
                            var valueEl = $([
                                '<a href="#" data-id="', data.record.id, '"class="resource-value ', modifiedClass   ,'" >',
                                data.record.value,
                                '</a>'
                            ].join(''));

                            AcmEx.Object.XEditable.useEditable(valueEl, {
                                success: function(response, newValue) {
                                    var editLanguage = $('#labelConfigurationLanguage').val();
                                    var editNamespace = $('#labelConfigurationNamespace').val();
                                    var id = $(this).data('id');
                                    var row = $s.jtable('getRowByKey', id);
                                    if (row) {
                                        var record = row.data().record;
                                        Admin.Service.LabelConfiguration.updateResource(
                                            editLanguage,
                                            editNamespace,
                                            {
                                                id: id,
                                                value: newValue,
                                                description: record.description
                                            }
                                        )
                                        .done(function(){
                                            $s.jtable('updateRecord', {
                                                record: {
                                                    id: id,
                                                    value: newValue,
                                                    defaultValue: record.defaultValue,
                                                    description: record.description
                                                },
                                                clientOnly: true
                                            });
                                        })
                                        .fail(function(){
                                            Acm.Dialog.error('Can\'t save resource');
                                        });
                                    } else {
                                        console.error('Row '+ id +' was not found');
                                    }
                                }
                            });

                            return valueEl;
                        }
                    }, description: {
                        title: 'Description'
                        ,edit: 'false'
                        ,width: '35%'
                        ,display: function(data){
                            var valueEl = $([
                                '<a href="#" data-id="', data.record.id, '">',
                                data.record.description,
                                '</a>'
                            ].join(''));

                            AcmEx.Object.XEditable.useEditable(valueEl, {
                                success: function(response, newDescription) {
                                    var editLanguage = $('#labelConfigurationLanguage').val();
                                    var editNamespace = $('#labelConfigurationNamespace').val();
                                    var id = $(this).data('id');
                                    var row = $s.jtable('getRowByKey', id);
                                    if (row) {
                                        var record = row.data().record;
                                        Admin.Service.LabelConfiguration.updateResource(
                                            editLanguage,
                                            editNamespace,
                                            {
                                                id: id,
                                                value: record.value,
                                                description: newDescription
                                            }
                                        )
                                        .done(function(){
                                            $s.jtable('updateRecord', {
                                                record: {
                                                    id: id,
                                                    value: record.value,
                                                    defaultValue: record.defaultValue,
                                                    description: newDescription
                                                },
                                                clientOnly: true
                                            });
                                        })
                                        .fail(function(){
                                            Acm.Dialog.error('Can\'t save resource');
                                        });
                                    } else {
                                        console.error('Row '+ id +' was not found');
                                    }
                                }
                            });

                            return valueEl;
                        }

                    }, reset: {
                        title: 'Default Value',
                        width: '15%',
                        display: function(data) {
                            var revertEl = '';
                            data.record.reset = 'A';
                            if  (data.record.value != data.record.defaultValue) {
                                data.record.reset = 'B';
                                revertEl = $([
                                    '<a href="#" data-id="', data.record.id ,'">',
                                    'Reset',
                                    '</a>'
                                ].join(''));

                                revertEl.click(function(e){
                                    e.preventDefault();
                                    var editLanguage = $('#labelConfigurationLanguage').val();
                                    var editNamespace = $('#labelConfigurationNamespace').val();
                                    var id = $(this).data('id');
                                    var row = $s.jtable('getRowByKey', id);
                                    if (row) {
                                        var record = row.data().record;

                                        Admin.Service.LabelConfiguration.updateResource(
                                            editLanguage,
                                            editNamespace,
                                            {
                                                id: id,
                                                value: record.defaultValue,
                                                description: record.description
                                            }
                                        )
                                        .done(function(){
                                            $s.jtable('updateRecord', {
                                                record: {
                                                    id: id,
                                                    value: record.defaultValue,
                                                    defaultValue: record.defaultValue,
                                                    description: record.description
                                                },
                                                clientOnly: true
                                            });
                                        })
                                        .fail(function(){
                                            Acm.Dialog.error('Can\'t reset to default value');
                                        });
                                    } else {
                                        console.error('Row '+ id +' was not found');
                                    }
                                });

                            }

                            return revertEl;
                        }
                    }
                }
            });
            $s.jtable('load', {loadData: true});
        }
    }

    ,Logo: {
        create: function() {
            $('#btnUploadLogos').click($.proxy(this.uploadLogos, this));
            this.$headerLogo = $('#customHeaderLogo');
            this.$loginLogo = $('#customLoginLogo');
        }
        ,onInitialized : function(){

        },

        uploadLogos: function(e) {
            var isValid = false;
            var fd = new FormData();
            if (this.$headerLogo[0].files.length > 0) {
                fd.append('headerLogo', this.$headerLogo[0].files[0]);
                isValid = true;
            }

            if (this.$loginLogo[0].files.length > 0) {
                fd.append('loginLogo', this.$loginLogo[0].files[0]);
                isValid = true;
            }

            // Show error message and return if no images selected
            if (!isValid) {
                Acm.Dialog.error('Select image to upload');
                return;
            }




            var context = this;
            Admin.Service.Logo.uploadLogos(fd)
                .done(function(){
                    // clear form values and update images
                    context.$headerLogo.val('');
                    context.$loginLogo.val('');
                    var headerLogoSrc = $('#imgCustomHeaderLogo').attr('src');
                    var d = (new Date()).getTime();
                    if (headerLogoSrc.indexOf('?') != -1) {
                        headerLogoSrc = headerLogoSrc.slice(0, headerLogoSrc.indexOf('?'));
                    }

                    headerLogoSrc += '?d=' + d;
                    $('#imgCustomHeaderLogo').attr('src', headerLogoSrc);

                    var loginLogoSrc = $('#imgCustomLoginLogo').attr('src');
                    if (loginLogoSrc.indexOf('?') != -1) {
                        loginLogoSrc = loginLogoSrc.slice(0, loginLogoSrc.indexOf('?'));
                    }
                    loginLogoSrc += '?d=' + d;
                    $('#imgCustomLoginLogo').attr('src', loginLogoSrc);

                    Acm.Dialog.info("Custom logo files updated. Refresh browser page to see result.");
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });
        }
    }

    ,CustomCss: {
        create: function() {
            this.cssEditor = ace.edit("customCssTextArea");
            this.cssEditor.setTheme("ace/theme/chrome");
            this.cssEditor.getSession().setMode("ace/mode/css");

            $('#btnSaveCustomCss').click($.proxy(this.updateCustomCss, this));

            // Load Custom Css
            var context = this;
            Admin.Service.CustomCss.retrieveCustomCss()
                .done(function(cssText){
                    context.cssEditor.setValue(cssText);
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });
        }
        ,onInitialized : function() {
        }

        ,updateCustomCss: function(e) {
            e.preventDefault();

            var cssText = this.cssEditor.getValue();
            Admin.Service.CustomCss.updateCustomCss(cssText)
                .done(function(){
                    Acm.Dialog.info("Custom CSS updated. Refresh browser page to see result.");
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });
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
                                record.title = Acm.goodValue(template.name);
                                record.created =  (Acm.getDateFromDatetime(template.created,$.t("common:date.short")));
                                record.creator = Acm.goodValue(template.creator);
                                record.path = Acm.goodValue(template.path);
                                record.modified =  (Acm.getDateFromDatetime(template.modified,$.t("common:date.short")));
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

    ,RolesPrivileges: {
        create: function() {
            // Initialize select HTML elements for roles, not authorized and authorized groups
            this.$editRoleBtn = $("#editRoleBtn");
            this.$selectRoles = $("#selectApplicationRoles");
            this.$selectAvailablePrivileges = $("#selectAvailablePrivileges");
            this.$selectPrivileges = $("#selectPrivileges");
            this.$createNewRoleBtn = $("#createNewRoleBtn");
            this.$editRoleName = $("#editRoleName");
            this.$newRoleName = $("#newRoleName");
            this.$editRoleDialog = $("#editRoleDialog");
            this.$applyChangeRoleBtn = $("#applyChangeRoleBtn");

            // Initialize buttons
            this.$btnRolePrivilegesGo = $("#btnRolePrivilegesGo");
            this.$btnRolePrivilegesMoveRight = $("#btnRolePrivilegesMoveRight");
            this.$btnRolePrivilegesMoveLeft = $("#btnRolePrivilegesMoveLeft");

            // Add listeners for buttons and roles select element
            this.$btnRolePrivilegesGo.click($.proxy(Admin.View.RolesPrivileges.onClickBtnGo, this));
            this.$btnRolePrivilegesMoveRight.click($.proxy(Admin.View.RolesPrivileges.onClickBtnMoveRight, this));
            this.$btnRolePrivilegesMoveLeft.click($.proxy(Admin.View.RolesPrivileges.onClickBtnMoveLeft, this));
            this.$createNewRoleBtn.click($.proxy(Admin.View.RolesPrivileges.onCreateNewRole, this));
            this.$applyChangeRoleBtn.click($.proxy(Admin.View.RolesPrivileges.onApplyChangesRole, this));
            this.$selectRoles.change($.proxy(Admin.View.RolesPrivileges.onChangeSelectRoles, this));


            this.$editRoleDialog.on("show.bs.modal", $.proxy(Admin.View.RolesPrivileges.onEditRoleDialogShow, this));
        }
        ,onInitialized: function () {
            // Load roles
            Admin.View.RolesPrivileges.loadRoles();
            Admin.View.RolesPrivileges.loadPrivileges();
        }

        ,onEditRoleDialogShow: function(e){
            this.$editRoleName.val(this.$selectRoles.val());
        }

        ,onClickBtnGo: function(event){
            Admin.View.RolesPrivileges.updatePrivilegesLists();
        }
        ,onClickBtnMoveRight: function(event) {
            // Move Privileges from all privileges to role privileges
            var selectedPrivelegesIds = this.$selectAvailablePrivileges.val();
            $('option', this.$selectAvailablePrivileges).each(function(){
                if (_.contains(selectedPrivelegesIds, $(this).val())) {
                    $(this).remove();
                }
            });

            // Add options to the Selected  Privileges
            var selectedOptions = _.pick(this.privileges, selectedPrivelegesIds);

            for (var privilegeId in  selectedOptions) {
                if (privilegeId) {
                    this.$selectPrivileges.append('<option value="{0}">{1}</option>'.format(privilegeId, selectedOptions[privilegeId]));
                }
            }

            Admin.View.RolesPrivileges.saveRolePrivileges();
        }
        ,onClickBtnMoveLeft: function(event) {
            // Move Privileges from selected privileges to available role privileges list
            var selectedPrivelegesIds = this.$selectPrivileges.val();
            $('option', this.$selectPrivileges).each(function(){
                if (_.contains(selectedPrivelegesIds, $(this).val())) {
                    $(this).remove();
                }
            });

            // Add options to the Available Privileges
            var selectedOptions = _.pick(this.privileges, selectedPrivelegesIds);

            for (var privilegeId in  selectedOptions) {
                if (privilegeId) {
                    this.$selectAvailablePrivileges.append('<option value="{0}">{1}</option>'.format(privilegeId, selectedOptions[privilegeId]));
                }
            }

            Admin.View.RolesPrivileges.saveRolePrivileges();
        },

        onChangeSelectRoles: function(event) {
            Admin.View.RolesPrivileges.clearPrivilegesLists();
            this.$editRoleBtn.attr('disabled', false);
        }

        ,onModelError: function(errorMsg) {
            Acm.Dialog.error(errorMsg);
        }

        ,onCreateNewRole: function() {
            var context = this;
            var newRoleName = this.$newRoleName.val();
            Admin.Service.RolesPrivileges.createApplicationRole(newRoleName)
                .done(function(){
                    $('#newRoleDialog').modal('hide');
                    context.loadRoles();
                    context.clearPrivilegesLists();
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });
        }

        ,onApplyChangesRole: function() {
            var context = this;
            var oldRoleName = this.$selectRoles.val();
            var newRoleName = this.$editRoleName.val();
            Admin.Service.RolesPrivileges.updateApplicationRole(oldRoleName, newRoleName)
                .done(function(){
                    $('#editRoleDialog').modal('hide');
                    context.loadRoles();
                    context.clearPrivilegesLists();
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });
        }

        ,clearPrivilegesLists: function(){
            this.$selectPrivileges.children().remove('option');
            this.$selectAvailablePrivileges.children().remove('option');
        }

        ,saveRolePrivileges: function() {
            var selectedRole = this.$selectRoles.val();
            var privileges = [];
            this.$selectPrivileges.find('option').each(function(idx, item) {
                if ($(item).val()) {
                    privileges.push($(item).val());
                }
            });
            Admin.Service.RolesPrivileges.saveApplicationRolePrivileges(selectedRole, privileges);
        }

        ,updatePrivilegesLists: function(){
            var context = this;
            var selectedRole = this.$selectRoles.val();
            if (selectedRole) {
                // Get Role's privileges
                Admin.Service.RolesPrivileges.retrieveApplicationRolePrivileges(selectedRole)
                    .done(function(rolePrivileges){
                        // Fill Role Privileges list by retrivied data
                        Acm.Object.createOptions(context.$selectPrivileges, rolePrivileges);

                        // Create available privileges list
                        var availablePrivileges = _.omit(context.privileges ,_.keys(rolePrivileges));
                        Acm.Object.createOptions(context.$selectAvailablePrivileges, availablePrivileges);

                    })
                    .fail(function(errorMsg){
                        Acm.Dialog.error(errorMsg);
                    });
            }

        }

        ,loadRoles: function (){
            this.$editRoleBtn.attr('disabled', true);
            var context = this;
            Admin.Service.RolesPrivileges.retrieveApplicationRoles()
                .done(function(roles){
                    Acm.Object.createOptions(context.$selectRoles, roles);
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });

        },

        loadPrivileges: function(){
            var context = this;
            Admin.Service.RolesPrivileges.retrieveApplicationPrivileges()
                .done(function(privileges){
                    context.privileges = privileges;
                })
                .fail(function(errorMsg){
                    Acm.Dialog.error(errorMsg);
                });
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
            
            var reportsAsOptions = {};
            if (reports) {
            	for (var key in reports) {
            		reportsAsOptions[key] = reports[key].title;
            	}
            }

            // Show reports on the view
            Acm.Object.createOptions(Admin.View.ReportsConfiguration.$selectReport, reportsAsOptions);
        }

        ,onModelReportConfigError: function(errorMsg) {
            Acm.Dialog.error(errorMsg);
        }

        ,onModelReportConfigSavedReportToGroupsMap:function(success){
            if(true == success){
            	// Save autherized reports and remove not atherized from properties file
            	Admin.View.ReportsConfiguration.updateReports();
            	
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
        
        ,updateReports: function() {
        	var reports = Admin.Model.ReportsConfiguration.cacheReports.get("reports");
        	var reportsToGroup = Admin.Model.ReportsConfiguration.cacheReportToGroupsMap.get("reportToGroupsMap");
        	var toSave = [];
        	
        	if (reports && reportsToGroup) {
        		for (var key1 in reports) {
        			for (var key2 in reportsToGroup) {
        				if (key1 === key2) {
        					var injected = reportsToGroup[key2].length === 0 ? false : true;
        					reports[key1].injected = injected;
        					toSave.push(reports[key1]);
        					break;
        				}
        			}
        		}
        		
        		if (toSave && toSave.length > 0) {
        			Admin.Service.ReportsConfiguration.saveReports(toSave);
        		}
        	}
        }
    }

    ,WorkflowConfiguration:{
        create: function () {
            this.$divWorkflowConfiguration = $("#divWorkflowConfiguration");
            this.createJTableWorkflowConfiguration(this.$divWorkflowConfiguration);
            this.$BPMNHistory = $("#BPMNHistory");
            this.$btnMakeProcessActive = $("#makeProcessActive");
            this.$btnMakeProcessActive.on("click", function(e) {Admin.View.WorkflowConfiguration.onMakeActiveProcess(e, this);})

            this.$modalUploadBPMN = $("#uploadBPMNModal");
            this.$formUploadBPMN = $("#formUploadBPMN");
            this.$filesSelection = $("#filesSelection");
            this.$btnUploadBPMNConfirm = $("#btnUploadBPMNConfirm");
            this.$btnUploadBPMNConfirm.on("click", function(e) {Admin.View.WorkflowConfiguration.onSubmitUploadBPMN(e, this);});



            // Modal window shoe event handler
            this.$BPMNHistory.on("show.bs.modal", Admin.View.WorkflowConfiguration.onHistoryDialogShow)
        }
        , onInitialized: function () {
            if (Admin.View.WorkflowConfiguration.History.onInitialized)        	{Admin.View.WorkflowConfiguration.History.onInitialized();}
        }

        ,onHistoryDialogShow: function(e) {
            // Get key and version from A tag attributes
            var key = $(e.relatedTarget).data('key');
            var version = $(e.relatedTarget).data('version');

            if (Admin.View.WorkflowConfiguration.History.create)        	{Admin.View.WorkflowConfiguration.History.create(key, version);}
        }

        ,onMakeActiveProcess: function(e){
            // Get selected process
            var $selectedCheckbox = $('input[name="activeBPMN"]:checked', Admin.View.WorkflowConfiguration.History.$divBPMNHistory);

            if ($selectedCheckbox.length > 0) {
                var key = $selectedCheckbox.data('key');
                var version = $selectedCheckbox.data('version');
                Admin.Service.WorkflowConfiguration.makeActive(key, version)
                    .done (function(){
                    Admin.View.WorkflowConfiguration.$BPMNHistory.modal('hide');

                    // Update Workflows table
                    Admin.View.WorkflowConfiguration.$divWorkflowConfiguration.jtable('load');
                })
                    .fail (function(){
                    Acm.Dialog.error('Can\'t make this process active');
                });
            } else {
                Acm.Dialog.error('There are no selected processes');
            }
        }

        ,onSubmitUploadBPMN: function(event, ctrl) {
            event.preventDefault();
            var count = Admin.View.WorkflowConfiguration.$filesSelection[0].files.length;
            if(count > 0){
                var fd = new FormData();
                fd.append('file', Admin.View.WorkflowConfiguration.$filesSelection[0].files[0])

                Admin.Service.WorkflowConfiguration.uploadWorkflowFile(fd)
                    .done(function(){
                        Admin.View.WorkflowConfiguration.$formUploadBPMN[0].reset();
                        Admin.View.WorkflowConfiguration.$modalUploadBPMN.modal('hide');
                    })
                    .fail(function(){
                        Acm.Dialog.error('Can\'t upload new workflow file');
                    });

            }
        }
        ,History: {
            create: function (key, version) {
                this.$divBPMNHistory = $("#divBPMNHistory");
                this.createJTableBPMNHistory(this.$divBPMNHistory, key, version);
            }
            , onInitialized: function () {
            }
            ,createJTableBPMNHistory: function ($s, key, version) {
                $s.jtable({
                    actions: {
                        listAction: function(postData, jtParams) {
                            return $.Deferred(function ($dfd){
                                var rc = {};
                                Admin.Service.WorkflowConfiguration.retrieveHistory(postData.key, postData.version)
                                    .done(function(data) {
                                        rc = {
                                            Result: 'OK',
                                            Records: data
                                        }
                                        $dfd.resolve(rc);
                                    })
                                    .fail(function(){
                                        rc = {
                                            Result: 'OK',
                                            Records: []
                                        }
                                        $dfd.resolve(rc);
                                    });
                            });
                        }
                    }
                    , fields: {
                        id: {
                            title: 'ID'
                            , key: true
                            , list: false
                            , create: false
                            , edit: false
                        }, key: {
                            title: 'Key'
                            , list: false
                            , create: false
                            , edit: false

                        }, version: {
                            title: 'Version'
                            , list: false
                            , create: false
                            , edit: false
                        }, active: {
                            title: 'Active'
                            , list: false
                            , create: false
                            , edit: false

                        }, actions: {
                            title: 'Active'
                            , width: '3%'
                            , edit: false
                            , display: function (data) {
                                if (data.record) {
                                    var isActive = data.record.active ? 'checked': '';
                                    // custom action.
                                    return '<input type="radio" '+ isActive +' name="activeBPMN" data-key="' +  data.record.key + '" data-version="' + data.record.version + '" />';
                                }
                            }
                        }, name: {
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
                            , display: function(data){
                                return (Acm.getDateFromDatetime(data.record.modified,$.t("common:date.short")));
                            }
                        }, creator: {
                            title: 'Author'
                            , width: '15%'
                            , edit: false
                        }
                    }
                });
                $s.jtable('load', {key: key, version: version});
            }

        }
        ,createJTableWorkflowConfiguration: function ($s) {
            $s.jtable({
                title:'Workflow Configuration'
                , actions: {
                    listAction: function(postData, jtParams) {
                        return $.Deferred(function ($dfd){
                            var rc = {};
                            Admin.Service.WorkflowConfiguration.retrieveWorkflows()
                                .done(function(data) {
                                    rc = {
                                        Result: 'OK',
                                        Records: data
                                    }
                                    $dfd.resolve(rc);
                                })
                                .fail(function(){
                                    rc = {
                                        Result: 'OK',
                                        Records: []
                                    }
                                    $dfd.resolve(rc);
                                });
                        });
                    }
                }
                , fields: {
                    id: {
                        title: 'ID'
                        , key: true
                        , list: false
                        , create: false
                        , edit: false
                    }, key: {
                        title: 'Key'
                        , list: false
                        , create: false
                        , edit: false

                    }, version: {
                        title: 'Version'
                        , list: false
                        , create: false
                        , edit: false
                    }, name: {
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
                        , display: function(data){
                            return (Acm.getDateFromDatetime(data.record.modified,$.t("common:date.short")));
                        }
                    }, creator: {
                        title: 'Author'
                        , width: '15%'
                        , edit: false
                    }, actions: {
                        title: 'Active'
                        , width: '30%'
                        , edit: false
                        , display: function (data) {
                            if (data.record) {
                                var downloadLink = Admin.Service.WorkflowConfiguration.getFileLink(data.record.key, data.record.version);
                                return '<a href="' + downloadLink + '" target="_blank" class="active"><i class="fa fa-download text-active"> Download </i></a>'
                                    + ' | <a href="#" class="active" data-toggle="modal" data-target="#uploadBPMNModal"><i class="fa fa-upload text-active"> Replace File </i></a>'
                                    + ' | <a href="#" class="active"   data-toggle="modal" data-target="#BPMNHistory" data-key="' + data.record.key + '" data-version="' + data.record.version + '"><i class="fa fa-retweet text-active"> Version History </i></a>';
                            }
                        }
                    }
                }
            });
            $s.jtable('load');
        }
    }

    ,LinkFormsWorkflows: {
        create:  function() {
            this.COLUMN_MULTIPLE_COEF = 2.5;
            this.$spreadsheetDiv = $("#divLinkFormsWorkflowsSpreadSheet");
            this.createSpreadSheet(this.$spreadsheetDiv);
            this.spreadSheet = null;

            $('#btnLinkFormsWorkflowsSave').click($.proxy(this.onSaveSpreadSheet, this));
            $('#btnLinkFormsWorkflowsUndo').click($.proxy(this.onUndoEdit, this));
        }

        ,onInitialized: function(){
        }

        ,onSaveSpreadSheet: function (e) {
            var data = this.spreadSheet.getData();
            Admin.Service.LinkFormsWorkflows.updateConfiguration(data)
                .done(function(){
                    Acm.Dialog.info("Link Forms/Workflows configuration saved successfully");
                })
                .fail(function(err){
                    Acm.Dialog.error("Can't save Link Forms/Workflows configuration: " + err);
                });
        }

        ,onUndoEdit: function (e) {
            if (this.spreadSheet) {
                this.spreadSheet.undo();
            }
        }

        ,createSpreadSheet: function($s){

            Admin.Service.LinkFormsWorkflows.retrieveConfiguration()
                .done(function(data){

                    // Get cells values
                    var cells = [];
                    for(var i = 0; i < data.cells.length; i++) {
                        var rowValues = _.pluck(data.cells[i], 'value');
                        cells.push(rowValues);
                    }


                    // Apply styles to cells
                    var cellRenderer = function(instance, td, row, col, prop, value, cellProperties){
                        Handsontable.renderers.TextRenderer.apply(this, arguments);

                        var cellInfo = data.cells[row][col];
                        if (cellInfo) {

                            td.style.wordWrap = 'break-word';

                            if (cellInfo.bgColor) {
                                td.style.background = cellInfo.bgColor;
                            }

                            if (cellInfo.color ){
                                td.style.color = cellInfo.color;
                            }

                            if (cellInfo.readonly) {
                                cellProperties.readOnly = true;
                            }

                            if (cellInfo.fontSize) {
                                td.style.fontSize = cellInfo.fontSize + "px";
                            }
                        }
                    };

                    // Set Column Width
                    var columnsWidths = [];
                    for (var i = 0; i < data.columnsWidths.length; i++) {
                        columnsWidths[i] = data.columnsWidths[i] * Admin.View.LinkFormsWorkflows.COLUMN_MULTIPLE_COEF;
                    }


                    Admin.View.LinkFormsWorkflows.spreadSheet = new Handsontable($s[0], {
                        data: cells,
                        height: 750,
                        colWidths: columnsWidths,
                        colHeaders: true,
                        rowHeaders: true,
                        stretchH: 'all',
                        fillHandle: false,
                        columnSorting: false,
                        contextMenu: false,
                        manualColumnResize: true,
                        cells: function(row, col, prop) {
                            var cellProperties = {};
                            var cellType = data.cells[row][col].type;

                            // Add data for dropdown control if required
                            if (cellType && data.meta[cellType]) {
                                cellProperties.type = 'dropdown';
                                cellProperties.source = data.meta[cellType];
                            } else if (cellType == 'priority') {
                                cellProperties.type = 'numeric'
                                cellProperties.allowInvalid = false;
                                cellProperties.validator = function (value, callback){
                                    callback((value >= 0) && (value <= 100) && (value % 1 === 0));
                                }
                            }

                            cellProperties.renderer = cellRenderer;
                            return cellProperties;
                        }
                    });

                })
                .fail(function(){
                    Acm.Dialog.error('Can\'t retrieve link forms workflows configuration');
                });
        }
    }
    
    ,Forms:{
        create: function () {
            if (Admin.View.Forms.PlainForms.create)        	{Admin.View.Forms.PlainForms.create();}
        }
        , onInitialized: function () {
            if (Admin.View.Forms.PlainForms.onInitialized)        	{Admin.View.Forms.PlainForms.onInitialized();}
        }
        ,PlainForms: {
            create: function () {
            	this.$btnAddPlainForm = $("#btnAddPlainForm");	
            	this.$btnAddPlainForm.on("click", function(e) {Admin.View.Forms.PlainForms.onClickBtnAddPlainForm(e, this);});
            	
            	this.$plainFormTarget = $("#plainFormTarget");
            	
                this.$divPlainForms = $("#divPlainForms");
                this.createJTablePlainForms(this.$divPlainForms);
                
                Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FORMS_CONFIGURATION_RETRIEVED_PLAIN_FORMS, this.onModelFormConfigRetrievedPlainForms);
                Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FORMS_CONFIGURATION_DELETED_PLAIN_FORM, this.onModelFormConfigDeletedPlainForm);
                Acm.Dispatcher.addEventListener(Admin.Controller.MODEL_FORMS_CONFIGURATION_RETRIEVED_PLAIN_FORM_TARGETS, this.onModelFormConfigRetrievedPlainFormTargets);
            }
            , onInitialized: function () {
            }
            
            ,onClickBtnAddPlainForm: function(event, ctrl) {
            	var target = Admin.View.Forms.PlainForms.$plainFormTarget.val();
            	if (!Acm.isEmpty(target)) {
            		var plainConfigurationFormUrl = Acm.Object.MicroData.get("plainConfigurationFormUrl");
            		plainConfigurationFormUrl = plainConfigurationFormUrl.replace("_data=(", "_data=(target:'" + target + "',");
	                if (Acm.isNotEmpty(plainConfigurationFormUrl)) {
	                	Acm.Dialog.openWindow(plainConfigurationFormUrl, "", 1060, 700
	                        ,function() {
	                            setTimeout(function(){
	                            	Admin.Service.Forms.PlainForms.retrievePlainForms();
	                            }, 2000);
	                        }
	                    );
	                }
            	} else {
            		Acm.Dialog.error($.t("admin:forms.plainforms.select-target-msg"));
            	}
            }
            
            ,onClickBtnEditPlainForm: function (key, target) {
            	if (!Acm.isEmpty(key) && !Acm.isEmpty(target)) {
            		var plainConfigurationFormUrl = Acm.Object.MicroData.get("plainConfigurationFormUrl");
            		plainConfigurationFormUrl = plainConfigurationFormUrl.replace("_data=(", "_data=(formKey:'" + key + "',formTarget:'" + target + "',mode:'edit',");
	                if (Acm.isNotEmpty(plainConfigurationFormUrl)) {
	                	Acm.Dialog.openWindow(plainConfigurationFormUrl, "", 1060, 700
	                        ,function() {
		                		setTimeout(function(){
	                            	Admin.Service.Forms.PlainForms.retrievePlainForms();
	                            }, 2000);
	                        }
	                    );
	                }
            	} else {
            		Acm.Dialog.error($.t("admin:forms.plainforms.select-target-msg"));
            	}
            }
            
            ,onClickBtnDeletePlainForm: function (key, target) {
            	Admin.Service.Forms.PlainForms.deletePlainForm(key, target);
            }
            
            ,onModelFormConfigRetrievedPlainForms: function() {
            	AcmEx.Object.JTable.load(Admin.View.Forms.PlainForms.$divPlainForms);
            }
            
            ,onModelFormConfigDeletedPlainForm: function() {
            	setTimeout(function(){
                	Admin.Service.Forms.PlainForms.retrievePlainForms();
                }, 2000);
            }
            
            ,onModelFormConfigRetrievedPlainFormTargets: function() {
            	var options = [];
            	options.push($('<option value="{0}">{1}</option>'.format("", $.t("admin:forms.plainforms.select-target"))));
            	
            	var targets = Admin.Model.Forms.PlainForms.getPlainFormTargets();
            	if (Acm.isNotEmpty(targets) && !Acm.isArrayEmpty(targets)) {
            		for (var i = 0; i < targets.length; i++) {
            			var target = targets[i];
            			if (Acm.isNotEmpty(target)) {
            				var targetArray = target.split("=");
            				if (!Acm.isArrayEmpty(targetArray) && targetArray.length === 2) {
            					options.push($('<option value="{0}">{1}</option>'.format(targetArray[0], targetArray[1])));
            				}
            			}
            		} 
            	}
            	
            	Admin.View.Forms.PlainForms.$plainFormTarget.html(options);
            }
            
            ,getFormKey: function(recordId){
            	var key = '';
            	if (Acm.isNotEmpty(recordId)) {
    				var idArray = recordId.split('_');
    				if (Acm.isArray(idArray) && idArray.length > 1) {
    					for (var i = 1; i < idArray.length; i++) {
    						if ((i + 1) == idArray.length) {
    							key += idArray[i].trim()
    						} else {
    							key += idArray[i].trim() + '_';
    						}
    							
    					}
    				}
    			}
            	return key;
            }
            
            ,createJTablePlainForms: function ($s) {
            	AcmEx.Object.JTable.useBasic($s, {
                	title:$.t("admin:forms.plainforms.title")
            		,paging: false // For now make these to false
                    ,sorting: false // For now make these to false
                    ,pageSize: 10 //Set page size (default: 10)
                    ,actions: {
                        listAction: function (postData, jtParams) {
                            var rc = AcmEx.Object.jTableGetEmptyRecords();
                            
                            var plainForms =  Admin.Model.Forms.PlainForms.getPlainForms();
                            if (Admin.Model.Forms.PlainForms.validatePlainForms(plainForms)) {
                            	for (var i = 0; i < plainForms.length; i++) {
                            		rc.Records.push({
                                        id:     i + "_" + plainForms[i].key
                                        ,name:      plainForms[i].name
                                        ,description:  plainForms[i].description
                                        ,target: plainForms[i].target
                                    });
                            	}
                            }
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
                        }, name: {
                            title: 'Form Name'
                            , width: '25%'
                            , edit: false
                        }, description: {
                            title: 'Description'
                            , width: '15%'
                            , edit: false
                        }, target: {
                            title: 'Target'
                            , width: '15%'
                            , edit: false
                        }, customEditAction:{
		                	title: ''
		                	,width: '1%'
		                	,sorting: false
		                	,create: false
		                	,edit: false
		                	,list: true
		                	,display: function(data) {
		                		if (data.record) {
		                			var key = Admin.View.Forms.PlainForms.getFormKey(data.record.id);
		                			var target = data.record.target;
		                			var expression = "Admin.View.Forms.PlainForms.onClickBtnEditPlainForm('" + key + "', '" + target + "');";
		                			return '<button title="Edit Record" class="jtable-command-button jtable-edit-command-button" onclick="' + expression + ' return false;"><span>Edit Record</span></button>';
		                		}
		                	}
                        }, customDeleteAction:{
		                	title: ''
		                	,width: '1%'
		                	,sorting: false
		                	,create: false
		                	,edit: false
		                	,list: true
		                	,display: function(data) {
		                		if (data.record) {
		                			var key = Admin.View.Forms.PlainForms.getFormKey(data.record.id);
		                			var target = data.record.target;
		                			var expression = "Admin.View.Forms.PlainForms.onClickBtnDeletePlainForm('" + key + "', '" + target + "');";
		                			return '<button title="Delete" class="jtable-command-button jtable-delete-command-button" onclick="' + expression + ' return false;"><span>Delete</span></button>';
		                		}
		                	}
			            }
                    }
                });
            }
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
                .addLeaf({key: "og"                                                         //level 1.3: /Security/Organization Hierarchy
                    ,title: "Organizational Hierarchy"
                    ,tooltip: "Organizational Hierarchy"
                })
                .addLeafLast({key: "rp"                                                         //level 1.3: /Security/Organization Hierarchy
                    ,title: "Create Role/Select Privileges"
                    ,tooltip: "Create Role/Select Privileges"
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

//                .addBranch({key: "bo"                                                               //level 4.3.1: /Forms/Form Configuration/Form/Business Objects
//                    ,title: "Business Objects"
//                    ,tooltip: "Business Objects"
//                    ,folder : true
//                    ,expanded: true
//                })
//                .addLeafLast({key: "boc"                                                                    //level 4.3.1.1: /Forms/Form Configuration/Form/Business Objects/Business Object Configuration
//                    ,title: "Business Object Configuration"
//                    ,tooltip: "Business Object Configuration"
//                })

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

                .addBranch({key: "br"                                                           //level 4.4.1: /Forms/Form Configuration/Form/Application Labels
                    ,title: "Branding"
                    ,tooltip: "Branding"
                    ,folder : true
                    ,expanded: true
                })
                .addLeaf({key: "brl"                                                                 //level 4.4.1.1: /Forms/Form Configuration/Form/Application Labels/Label Configuration
                    ,title: "Logo"
                    ,tooltip: "Logo"
                })
                .addLeafLast({key: "brcss"                                                                 //level 4.4.1.1: /Forms/Form Configuration/Form/Application Labels/Label Configuration
                    ,title: "Custom CSS"
                    ,tooltip: "Custom CSS"
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
