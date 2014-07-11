/**
 * ComplaintWizard.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
ComplaintWizard.Event = {
    initialize : function() {
    }

    ,onClickBtnSubmit : function(e) {
        var data = ComplaintWizard.Object.getComplaintData();
        ComplaintWizard.Service.submitForApproval(data);
        e.preventDefault();
    }
    ,onClickBtnSave : function(e) {
        var data = ComplaintWizard.Object.getComplaintData();
        ComplaintWizard.Service.saveComplaint(data);
        e.preventDefault();
    }


    ,onPostInit: function() {
        Acm.keepTrying(ComplaintWizard.Event._tryInitApprovers, 8, 200);
        Acm.keepTrying(ComplaintWizard.Event._tryInitComplaintTypes, 8, 200);
        Acm.keepTrying(ComplaintWizard.Event._tryInitPriorities, 8, 200);
    }

    ,_tryInitApprovers: function() {
        var data = Acm.Object.getApprovers();
        if (Acm.isNotEmpty(data)) {
            ComplaintWizard.Object.initApprovers(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitComplaintTypes: function() {
        var data = Acm.Object.getComplaintTypes();
        if (Acm.isNotEmpty(data)) {
            ComplaintWizard.Object.initComplaintTypes(data);
            return true;
        } else {
            return false;
        }
    }
    ,_tryInitPriorities: function() {
        var data = Acm.Object.getPriorities();
        if (Acm.isNotEmpty(data)) {
            ComplaintWizard.Object.initPriorities(data);
            return true;
        } else {
            return false;
        }
    }


    //-------------------------------------------------
    ,test : function(btn) {
        var data0 =
        {
            //"complaintId": null,
            "originator": {
                //"id": null,
                "title": "Mr.",
                "givenName": "John",
                "familyName": "Doe",
                "company": "Contoso"
//                ,"addresses": [
//                    {
//                        "type": "home",
//                        "streetAddress": "123 Main St.",
//                        "streetAddress2": null,
//                        "city": "Peoria",
//                        "state": "IL",
//                        "zip": "12345"
//                    }
//                ]
//                ,"contactMethods": [
//                    {
//                        "type": "Mobile Phone",
//                        "value": "123-456-7890"
//                    },
//                    {
//                        "type": "Personal E-mail",
//                        "value": "john.doe@gmail.com"
//                    }
//                ],
//                "securityTags": ["Anonymous", "Confidential", "Top Secret"]
            }
        };

        var url = "/acm" + "/api/latest/plugin/complaint";
        var param =
        {
            //"complaintId": null,
            "originator": {
            //"id": null,
                "title": "Mr.",
                "givenName": "John",
                "familyName": "Doe",
                "company": "Contoso",
                "addresses": [
                {
                    "type": "home",
                    "streetAddress": "123 Main St.",
                    "streetAddress2": null,
                    "city": "Peoria",
                    "state": "IL",
                    "zip": "12345"
                }
            ],
                "contactMethods": [
                {
                    "type": "Mobile Phone",
                    "value": "123-456-7890"
                },
                {
                    "type": "Personal E-mail",
                    "value": "john.doe@gmail.com"
                }
            ],
                "securityTags": ["Anonymous", "Confidential", "Top Secret"]
        }
        };

        var paramSmall =
        {
            "originator": {
                "title": "Mr.",
                "givenName": "John",
                "familyName": "Doe",
                "company": "Contoso"
            }
        };

        $.ajax({type: 'POST'
            ,url: url
            ,async: true
            ,data: JSON.stringify(paramSmall)
            ,dataType: 'json'
            ,contentType: "application/json; charset=utf-8"
            //,processData: false
            ,beforeSend: function(x) {
                if (x && x.overrideMimeType) {
                    x.overrideMimeType("application/json;charset=UTF-8");
                }
            }
            ,success: function(response) {
                alert("ajax success");
            }
            ,error: function(xhr, status, error) {
                var msg = xhr.responseText;
                alert("ajax error:" + msg);
            }
            ,complete: function (xhr, status)
            {
                //alert("ajax complete");
            }
        });

    }
};
