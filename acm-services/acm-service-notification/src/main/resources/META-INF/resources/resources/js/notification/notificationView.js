/**
 * AcmNotification.View
 *
 * @author jwu
 */
AcmNotification.View = {
    create : function() {
    }
    ,onInitialized: function() {
        SearchBase.View.Query.submit("*");
    }

    ,getJtArgs: function() {
        return {
            sortMap: {name   : "name"
                ,type        : "object_type_s"
                ,description : "description_parseable"
                ,parentName  : "parent_number_lcs"
                ,parentType  : "parent_type_s"
                ,modified    : "modified_date_tdt"
            }
            ,dataMaker: function(result) {
                var jtData = AcmEx.Object.JTable.getEmptyRecords();
                if (result && Acm.isArray(result.docs)) {
                    for (var i = 0; i < result.docs.length; i++) {
                        var Record = {};
                        Record.id          = Acm.goodValue(result.docs[i].object_id_s);
                        Record.name        = Acm.goodValue(result.docs[i].name);
                        Record.type        = Acm.goodValue(result.docs[i].object_type_s);
                        Record.description = Acm.goodValue(result.docs[i].description_parseable);
                        Record.parentId    = Acm.goodValue(result.docs[i].parent_id_s);
                        Record.parentName  = Acm.goodValue(result.docs[i].parent_number_lcs);
                        Record.parentType  = Acm.goodValue(result.docs[i].parent_type_s);
                        Record.modified    = Acm.getDateTimeFromDatetime(result.docs[i].modified_date_tdt,$.t("common:date.full"));
                        jtData.Records.push(Record);
                    }

                    jtData.TotalRecordCount = Acm.goodValue(result.numFound, 0);
                }
                return jtData;
            }
            ,fields: {
                id: {
                    title: $.t("notification:table.field.id")
                    ,key: true
                    ,list: false
                    ,create: false
                    ,edit: false
                    //,sorting: false
                    ,defaultSorting: 'modified DESC'
                }
                ,name: {
                    title: $.t("notification:table.field.name")
                    ,width: '15%'
                    ,list: false
                    //,sorting: false
                    ,display: function(data) {
                        return SearchBase.View.Results.displayName(data);
                    }
                }
                ,type: {
                    title: $.t("notification:table.field.type")
                    ,sorting: false
                    ,list: false
                }
                ,description: {
                    title: $.t("notification:table.field.description")
                    ,width: '40%'
                    //,sorting: false
                    ,list: true
                }
                ,parentId: {
                    title: $.t("notification:table.field.parent-id")
                    ,key: false
                    ,list: false
                    ,create: false
                    ,edit: false
                    ,sorting: false
                }
                ,parentName: {
                    title: $.t("notification:table.field.parent-name")
                    ,width: '15%'
                    //,sorting: false
                    ,display: function(data) {
                        return SearchBase.View.Results.displayParent(data);
                    }
                }
                ,parentType: {
                    title: $.t("notification:table.field.parent-type")
                    //,sorting: false
                }
                ,modified: {
                    title: $.t("notification:table.field.modified")
                    ,type: 'textarea'
                    ,width: '20%'
                    //,sorting: false
                    ,list : true
                }
            }//end field
        };
    }

//    ,jtDataMaker: function(result) {
//        var jtData = AcmEx.Object.JTable.getEmptyRecords();
//        if (result) {
//            for (var i = 0; i < result.docs.length; i++) {
//                var Record = {};
//                Record.id         = Acm.goodValue(result.docs[i].object_id_s);
//                Record.name       = Acm.goodValue(result.docs[i].name);
//                Record.type       = Acm.goodValue(result.docs[i].object_type_s);
//                Record.description      = Acm.goodValue(result.docs[i].description_parseable);
//                Record.parentId   = Acm.goodValue(result.docs[i].parent_id_s);
//                Record.parentName = Acm.goodValue(result.docs[i].parent_number_lcs);
//                Record.parentType = Acm.goodValue(result.docs[i].parent_type_s);
//                Record.modified   = Acm.getDateTimeFromDatetime(result.docs[i].modified_date_tdt,$.t("common:date.full"));
//                jtData.Records.push(Record);
//            }
//
//            jtData.TotalRecordCount = result.numFound;
//        }
//        return jtData;
//    }
};

