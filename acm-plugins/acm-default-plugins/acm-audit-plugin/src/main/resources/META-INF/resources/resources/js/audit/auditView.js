/**
 * Audit.View
 *
 * @author jwu
 */
Audit.View = Audit.View || {
    create: function() {
        this.$divAudit = $("#divAudit");
        this.createJTable(this.$divAudit);
    }
    ,onInitialized: function() {
    }

    ,_makeJtData: function(auditList) {
        var jtData = AcmEx.Object.JTable.getEmptyRecords();
        if (auditList) {
            for (var i = 0; i < auditList.length; i++) {
                var Record = {};
                Record.id       = auditList[i].id;
                Record.dateTime = auditList[i].dateTime;
                Record.user     = auditList[i].user;
                Record.activity = auditList[i].activity;
                Record.result   = auditList[i].result;
                Record.ip       = auditList[i].ip;
                Record.objectId = auditList[i].objectId;
                jtData.Records.push(Record);
            }
            jtData.TotalRecordCount = auditList.length;
        }
        return jtData;
    }

//    //Delete selected students
//    $('#DeleteAllButton').button().click(function () {
//        var $selectedRows = $('#StudentTableContainer').jtable('selectedRows');
//        $('#StudentTableContainer').jtable('deleteRows', $selectedRows);
//    });

    ,createJTable: function($jt) {
        var sortMap = {};
        sortMap["dateTime"] = "auditDateTime";

        AcmEx.Object.JTable.usePaging($jt
            ,{
                title: 'Audit'
                ,pageSize: 16
                ,selecting: true
                ,multiselect: true
                ,selectingCheckboxes: true
                //,defaultSorting: 'dateTime DESC'
                ,actions: {
                    pagingListAction: function (postData, jtParams, sortMap) {
                        var pageIndex = jtParams.jtStartIndex;
                        if (0 > pageIndex) {
                            return AcmEx.Object.JTable.getEmptyRecords();
                        }

                        var auditList = Audit.Model.cacheAuditList.get(pageIndex);
                        if (auditList) {
                            return Audit.View._makeJtData(auditList);

                        } else {
                            return Audit.Service.retrieveAuditListDeferred(postData
                                ,jtParams
                                ,sortMap
                                ,function(data) {
                                    var auditList = data;
                                    return Audit.View._makeJtData(auditList);
                                }
                                ,function(error) {
                                }
                            );
                        }  //end else
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
//                    ,RowCheckbox: {
//                        title: 'Selected'
//                        ,width: '12%'
//                        ,type: 'checkbox'
//                        //,values: { 'false': fale, 'true': 'Active' }
//                        ,defaultValue: 'false'
//                    }
                    ,dateTime: {
                        title: 'Date/Time'
                        ,width: '20%'
                        ,sorting: true
                    }
                    ,user: {
                        title: 'User'
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,activity: {
                        title: 'Name'
                        ,width: '20%'
                        ,sorting: false
                    }
                    ,result: {
                        title: 'Result'
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,ip: {
                        title: 'IP Address'
                        ,width: '15%'
                        ,sorting: false
                    }
                    ,objectId: {
                        title: 'Object ID'
                        ,width: '15%'
                        ,sorting: false
                    }
                } //end field
            } //end arg
            ,sortMap
        );
    }


};

