/**
 * AdminAccess.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
/**
 * AdminAccess.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
AdminAccess.Object = {
    initialize : function() {

        /* this.$btnTest = $("#test");
        this.$btnTest.click(function(e) {AdminAccess.Event.onClickBtnTest(e);});
        */



        this.$divAdminAccessControlPolicy     = $("#divAdminAccessControlPolicy");
        this._createJTableAdminAccessControl(this.$divAdminAccessControlPolicy);

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
                title: 'Access Control Policy'
                ,selecting: true
                ,multiselect: false
                ,selectingCheckboxes: false

                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        return AcmEx.Object.jTableDefaultPagingListAction(postData, jtParams, sortMap
                            , function () {
                                var url;
                                url = App.getContextPath() + AdminAccess.Service.API_RETRIEVE_ACCESS_CONTROL;
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
                        //var adminAccess = AdminAccess.getAdminAccess();
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

                    }, objectState: {
                        title: 'State', width: '3%'
                        ,edit: false
                        ,options: [
                                   { Value: 'ACTIVE', DisplayText: 'Active' }, 
                                   { Value: 'ASSIGNED', DisplayText: 'Assigned' }, 
                                   { Value: 'COMPLETE', DisplayText: 'Complete' }, 
                                   { Value: 'DRAFT', DisplayText: 'Draft' },
                                   { Value: 'IN APPROVAL', DisplayText: 'In Approval' },
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
                        title: 'Allow Discretionary Update',
                        width: '5%'
                        ,options: [{ Value: 'true', DisplayText: 'True' } , { Value: 'false', DisplayText: 'False' }]
                    }

                } //end field
                ,recordUpdated: function (event, data) { //opened handler
                    AdminAccess.setUpdatedAdminAccessList(data);
                    var dataUpdate = AdminAccess.getUpdatedAdminAccessList();
                    AdminAccess.Service.updateAdminAccess(dataUpdate);
                }
            }
            //end arg
            ,sortMap
        );

    }
};




