/**
 * Subscription.View
 *
 * @author jwu
 */
Subscription.View = {
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
                    title: 'Name'
                    ,width: '15%'
                    ,sorting: false
                    ,display: function(data) {
                        return SearchBase.View.Results.displayName(data);
                    }
                    ,list: false
                }
                ,type: {
                    title: 'Type'
                    ,sorting: false
                    ,list: false
                }
                ,title: {
                    title: 'Event'
                    ,width: '30%'
                }
                ,parentId: {
                    title: 'Parent ID'
                    ,key: false
                    ,list: false
                    ,create: false
                    ,edit: false
                    ,sorting: false
                }
                ,parentName: {
                    title: 'Parent'
                    ,width: '15%'
                    ,sorting: false
                    ,display: function(data) {
                        return SearchBase.View.Results.displayParent(data);
                    }
                }
                ,parentType: {
                    title: 'Parent Type'
                    ,sorting: false
                }
                ,owner: {
                    title: 'Owner'
                    ,width: '15%'
                    ,sorting: false
                    ,list: false
                }
                ,modified: {
                    title: 'Modified'
                    ,type: 'textarea'
                    ,width: '20%'
                    ,sorting: false
                }
            }
        }; //end args
    }

    ,jtDataMaker: function(result) {
        var jtData = AcmEx.Object.JTable.getEmptyRecords();
        if (result) {
            for (var i = 0; i < result.docs.length; i++) {
                var Record = {};
                Record.id = result.docs[i].object_id_s;
                Record.name       = Acm.goodValue(result.docs[i].name);
                Record.type       = Acm.goodValue(result.docs[i].object_type_s);
                Record.title      = Acm.goodValue(result.docs[i].title_parseable);
                Record.parentId   = Acm.goodValue(result.docs[i].parent_id_s);
                Record.parentName = Acm.goodValue(result.docs[i].parent_number_lcs);
                Record.parentType = Acm.goodValue(result.docs[i].parent_type_s);
                Record.owner      = Acm.goodValue(result.docs[i].owner_lcs);
                Record.modified   = Acm.getDateTimeFromDatetime(result.docs[i].modified_date_tdt);
                jtData.Records.push(Record);
            }

            jtData.TotalRecordCount = result.numFound;
        }
        return jtData;
    }

};

