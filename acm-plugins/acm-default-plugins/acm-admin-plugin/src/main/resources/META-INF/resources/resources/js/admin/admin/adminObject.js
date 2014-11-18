/**
 * Admin.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
/**
 * Admin.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Admin.Object = {
    create : function() {

        /* this.$btnTest = $("#test");
         this.$btnTest.click(function(e) {Admin.Event.onClickBtnTest(e);});
         */

        this.$tree = $("#tree");
        this._useFancyTree(this.$tree)

        this.$tabAdminAccessControlPolicy     = $("#tabACP");
        this._createJTableAdminAccessControl(this.$tabAdminAccessControlPolicy);

    }

    ,_createJTableAdminAccessControl: function($jt) {
        var sortMap = {};
        sortMap["objectType"] = "objectType";
        sortMap["objectState"] = "objectState";
        sortMap["accessLevel"] = "accessLevel";
        sortMap["accessorType"] = "accessorType";
        sortMap["accessDecision"] = "accessDecision";
        sortMap["allowDiscretionaryUpdate"] = "allowDiscretionaryUpdate";

        AcmEx.Object.jTableCreatePaging($jt
            , {
                title: 'Data Access Control'
                ,selecting: true
                ,multiselect: false
                ,selectingCheckboxes: false

                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                            , function () {
                                var url;
                                url = App.getContextPath() + Admin.Service.API_RETRIEVE_ACCESS_CONTROL;
                                return url;
                            }
                            , function (data) {
                                var jtData = null;
                                var err = "ACL Error";
                                jtData = AcmEx.Object.jTableGetEmptyRecords();
                                if (data) {
                                    var resultPage = data.resultPage;
                                    for (var i = 0; i < resultPage.length; i++) {
                                        var Record = {};
                                        Record.objectType = resultPage[i].objectType;
                                        Record.objectState = resultPage[i].objectState;
                                        Record.accessLevel = resultPage[i].accessLevel;
                                        Record.accessorType = resultPage[i].accessorType;
                                        Record.accessDecision = resultPage[i].accessDecision;
                                        Record.allowDiscretionaryUpdate = (resultPage[i].allowDiscretionaryUpdate)? "true" : "false";
                                        Record.id = resultPage[i].id;
                                        /* Record.created = resultPage[i].created;
                                         Record.creator = resultPage[i].creator;
                                         Record.modified = resultPage[i].modified;
                                         Record.modifier = resultPage[i].modifier;*/
                                        jtData.Records.push(Record);
                                    }
                                    jtData.TotalRecordCount = data.totalCount;
                                }
                                else {
                                    if (Acm.isNotEmpty(data.error)) {
                                        err = data.error.msg + "(" + data.error.code + ")";
                                    }
                                }
                                return {jtData: jtData, jtError: err};
                            }
                        );

                    }
                    , updateAction: function (postData, jtParams) {
                        var record = Acm.urlToJson(postData);
                        //var Admin = Admin.getAdmin();
                        var rc = {"Result": "OK", "Record": {}};
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
                        , create: false
                        , edit: false
                    }, objectType: {
                        title: 'Object Type', width: '3%'
                        ,edit: false
                        //,sorting : true
                        ,options: [
                            { Value: 'Complaint', DisplayText: 'Complaint' },
                            { Value: 'Task', DisplayText: 'Task' },
                            { Value: 'caseFile', DisplayText: 'Case File' }
                        ]

                    }, objectState: {
                        title: 'State', width: '3%'
                        ,edit: false
                        ,options: [
                            { Value: 'ACTIVE', DisplayText: 'Active' },
                            { Value: 'ASSIGNED', DisplayText: 'Assigned' },
                            { Value: 'COMPLETE', DisplayText: 'Complete' },
                            { Value: 'DRAFT', DisplayText: 'Draft' },
                            { Value: 'IN APPROVAL', DisplayText: 'In Approval' },
                            { Value: 'Scheduled', DisplayText: 'Scheduled' },
                            { Value: 'UNASSIGNED', DisplayText: 'Unassigned' }
                        ]
                    }, accessLevel: {
                        title: 'Access Level', width: '5%'
                        ,edit: false
                        ,options: [
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
                        title: 'Accessor Type', width: '5%'
                        ,edit: false
                    }, accessDecision: {
                        title: 'Access Decision',
                        width: '5%'
                        ,options: [{ Value: 'GRANT', DisplayText: 'Grant' }, { Value: 'DENY', DisplayText: 'Deny' }, { Value: 'MANDATORY_DENY', DisplayText: 'Mandatory Deny' }]

                        // ,options: ['GRANT' , 'DENY', 'MANDATORY_DENY']
                    }, allowDiscretionaryUpdate: {
                        title: 'Allow Discretionary',
                        width: '10%'
                        ,options: [{ Value: 'true', DisplayText: 'True' } , { Value: 'false', DisplayText: 'False' }]
                    }

                } //end field
                ,recordUpdated: function (event, data) { //opened handler
                    Admin.setUpdatedAdminAccessList(data);
                    var dataUpdate = Admin.getUpdatedAdminAccessList();
                    Admin.Service.updateAdminAccess(dataUpdate);
                }
            }
            //end arg
            ,sortMap
        );

    }

    //  Use this to build the Admin tree structure
    //------------------ Tree  ------------------
    //
    ,getNodeTypeByKey: function(key) {
        if (Acm.isEmpty(key)) {
            return null;
        }
        if (key == "dac") {
            return "dac";
        } else if (key == "dc") {
            return "dc";
        } else if (key == "rc") {
            return "rc";
        } else if (key == "acc") {
            return "acc";
        } else if (key == "dsh") {
            return "dsh";
        } else if (key == "rpt") {
            return "rpt";
        }

        return null;
    }
    ,_mapNodeTab: {
        acc: ["tabACP"],
        dsh: ["tabDashboard"],
        rpt: ["tabReports"],
        dac: ["tabACP"],
        dc: ["tabDashboard"],
        rc: ["tabReports"]
    }
    ,_getTabIdsByKey: function(key) {
        var nodeType = this.getNodeTypeByKey(key);
        var tabIds = ["tabBlank"];
        for (var key in this._mapNodeTab) {
            if (nodeType == key) {
                tabIds = this._mapNodeTab[key];
                break;
            }
        }
        return tabIds;
    }
    ,showTab: function(key) {
        var tabIds = ["tabBlank"
            ,"tabACP"
            ,"tabDashboard"
            ,"tabReports"
            ,"tabLocks"
            ,"tabMainPage"
        ];
        var tabIdsToShow = this._getTabIdsByKey(key);
        for (var i = 0; i < tabIds.length; i++) {
            var show = this._foundItemInArray(tabIds[i], tabIdsToShow);
            Acm.Object.show($("#" + tabIds[i]), show);
        }
    }
    ,_foundItemInArray: function(item, arr) {
        for (var i = 0; i < arr.length; i++) {
            if (item == arr[i]) {
                return true;
            }
        }
        return false;
    }
    ,_useFancyTree: function($s) {

        $s.fancytree({
            activate: function(event, data){
                var node = data.node;

                Admin.Object.showTab(node.key);

                /*if(node.data.href){
                    var url = App.getContextPath() + node.data.href;
                    window.location.href=url;
                }*/
            },
            source: function() {
                return Admin.Object.treeSource();
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

            .addBranchLast({key: "al"                                                           //level 4.4.1: /Forms/Form Configuration/Form/Application Labels
                    ,title: "Application Labels"
                    ,tooltip: "Application Labels"
                    ,folder : true
                    ,expanded: true
            })
                .addLeafLast({key: "lc"                                                                 //level 4.4.1.1: /Forms/Form Configuration/Form/Application Labels/Label Configuration
                    ,title: "Label Configuration"
                    ,tooltip: "Label Configuration"
                })

        return builder.getTree();
    }

    //----------------- end of tree -----------------

    ,refreshJTableACL: function(){
        AcmEx.Object.jTableLoad(this.$tabAdminAccessControlPolicy);
    }
};




