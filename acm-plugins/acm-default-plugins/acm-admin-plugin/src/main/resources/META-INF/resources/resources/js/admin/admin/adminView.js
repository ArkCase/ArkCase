/**
 * Created by manoj.dhungana on 12/4/2014.
 */


Admin.View = Admin.View || {
    create: function() {
        if (Admin.View.AccessControl.create)        {Admin.View.AccessControl.create();}
        if (Admin.View.Correspondence.create)       {Admin.View.Correspondence.create();}
        if (Admin.View.Tree.create)                 {Admin.View.Tree.create();}
    }
    ,onInitialized: function() {
        if (Admin.View.AccessControl.onInitialized)        {Admin.View.AccessControl.onInitialized();}
        if (Admin.View.Correspondence.onInitialized)       {Admin.View.Correspondence.onInitialized();}
        if (Admin.View.Tree.onInitialized)                 {Admin.View.Tree.onInitialized();}
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
                            var pageIndex = jtParams.jtStartIndex;
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
                .addLeaf({key: "dac"                                                        //level 1.1: /Access Control/Data Access Control
                    ,title: "Data Access Control"
                    ,tooltip: "Data Access Control"
                    ,href: "/plugin/admin/access"
                })
                .addLeaf({key: "fac"                                                        //level 1.2: /Access Control/Functional Access Control
                    ,title: "Functional Access Control"
                    ,tooltip: "Functional Access Control"
                })
                .addLeafLast({key: "ldap"                                                   //level 1.3: /Access Control/LDAP Configuration
                    ,title: "LDAP Configuration"
                    ,tooltip: "LDAP Configuration"
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
                    ,href: "/plugin/admin/dashboard"
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
