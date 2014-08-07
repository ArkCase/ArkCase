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

        Acm.Object.jTableCreateSortable($jt
            , {
                title: 'Access Control Policy'
                ,selecting: true
                ,multiselect: false
                ,selectingCheckboxes: false

                ,actions: {
                    listActionSortable: function (postData, jtParams, sortMap) {
                        return Acm.Object.jTableDefaultListAction(postData, jtParams, sortMap
                            , function () {
                                var url;
                                url = App.getContextPath() + AdminAccess.Service.API_RETRIEVE_ACCESS_CONTROL;
                                return url;
                            }
                            , function (data) {
                                var jtData = null;
                                var err = "ACL Error";
                                jtData = Acm.Object.jTableGetEmptyResult();
                                if (data) {
                                    var resultPage = data.resultPage;
                                    for (var i = 0; i < resultPage.length; i++) {
                                        var Record = {};
                                        Record.objectType = resultPage[i].objectType;
                                        Record.objectState = resultPage[i].objectState;
                                        Record.accessLevel = resultPage[i].accessLevel;
                                        Record.accessorType = resultPage[i].accessorType;
                                        Record.accessDecision = resultPage[i].accessDecision;
                                        Record.allowDiscretionaryUpdate = resultPage[i].allowDiscretionaryUpdate;
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

                    }, createAction: function (postData, jtParams) {
                        return Acm.Object.jTableGetEmptyResult();
                    }
                }, fields: {
                    id: {
                        title: 'ID', key: true, type: 'hidden'
                        //   ,list: true
                        , create: false, edit: false
                    }, objectType: {
                        title: 'Object Type', width: '3%'
                    }, objectState: {
                        title: 'State', width: '3%'
                    }, accessLevel: {
                        title: 'Access Level', width: '5%'
                    }, accessorType: {
                        title: 'Accessor Type', width: '5%'
                    }, accessDecision: {
                        title: 'Access Decision', width: '5%'
                    }, allowDiscretionaryUpdate: {
                        title: 'Allow Discretionary Update', width: '5%'
                    }

                } //end field
            } //end arg
        );
    }
};




