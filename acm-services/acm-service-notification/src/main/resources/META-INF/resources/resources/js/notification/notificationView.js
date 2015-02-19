/**
 * AcmNotification.View
 *
 * @author jwu
 */
AcmNotification.View = {
    create : function() {
    }
    ,onInitialized: function() {
    }

    ,getJtArgs: function() {
        return {
            fields: {
                id: {
                    title: 'ID'
                    ,key: true
                    ,list: false
                    ,create: false
                    ,edit: false
                    ,sorting: false
                }
                ,name: {
                    title: 'Object Number'
                    ,width: '15%'
                    ,sorting: false
                    ,display: function(data) {
                        return SearchBase.View.Results.displayName(data);
                    }
                    ,list: true
                }
                ,type: {
                    title: 'Object Type'
                    ,sorting: false
                    ,list: true
                }
                ,description: {
                    title: 'Description'
                    ,width: '40%'
                    ,sorting: false
                    ,list: true
                }
                ,modified: {
                    title: 'Modified'
                    ,type: 'textarea'
                    ,width: '20%'
                    ,sorting: false
                    ,list : true
                }
            }//end field
        };
    }

    ,jtDataMaker: function(result) {
        var jtData = AcmEx.Object.JTable.getEmptyRecords();
        if (result) {
            for (var i = 0; i < result.docs.length; i++) {
                var Record = {};
                Record.id               = result.docs[i].parent_id_s;
                Record.name             = Acm.goodValue(result.docs[i].parent_number_lcs);
                Record.type             = Acm.goodValue(result.docs[i].parent_type_s);
                Record.description      = Acm.goodValue(result.docs[i].description_parseable);
                Record.modified         = Acm.getDateTimeFromDatetime(result.docs[i].modified_date_tdt);
                jtData.Records.push(Record);
            }
            jtData.TotalRecordCount = result.numFound;
        }
        return jtData;
    }
};

