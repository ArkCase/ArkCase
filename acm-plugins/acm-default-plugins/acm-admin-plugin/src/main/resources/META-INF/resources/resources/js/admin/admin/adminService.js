/**
 * Admin.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
Admin.Service = {
    create : function() {
    }

    // Admin Access Control Policy related services

    ,API_RETRIEVE_ACCESS_CONTROL       : "/api/latest/plugin/dataaccess/accessControlDefaults"

    ,API_UPDATE_ACCESS_CONTROL         : "/api/latest/plugin/dataaccess/default/"

    ,API_DOWNLOAD_TEMPLATE             : "/api/latest/plugin/admin/template?filePath="

    ,API_UPLOAD_TEMPLATE             : "/api/latest/plugin/admin/template"

    ,API_LIST_TEMPLATES             : "/api/latest/plugin/admin/template/list"

    ,updateAdminAccess : function(data) {
        Acm.Ajax.asyncPost(App.getContextPath() + this.API_UPDATE_ACCESS_CONTROL + data.id
            ,JSON.stringify(data)
            ,Admin.Callback.EVENT_ADMIN_ACCESS_UPDATED
        );
    }

    ,retrieveTemplates : function() {
        Acm.Ajax.asyncGet(App.getContextPath()+ this.API_LIST_TEMPLATES
            ,Admin.Callback.EVENT_TEMPLATES_RETRIEVED
        );
    }

    ,uploadTemplateFile : function(formData){
        var url = App.getContextPath() + this.API_UPLOAD_TEMPLATE;
        Acm.Service.ajax({
            url: url
            ,data: formData
            ,processData: false
            ,contentType: false
            ,type: 'POST'
            ,success: function(response){
                if (response.hasError) {
                    //do something
                    Acm.Dialog.info("ERROR");
                } else {
                    if(response!= null){
                        /*for(var i = 0; i < response.length; i++){
                            var template = {};
                            template.id = response.id;
                            template.name = response.name;
                            template.creator = response.creator;
                            template.created = response.created;
                            template.path = response.path;

                            prevTemplatesList.push(template);
                        }*/
                        Admin.cacheTemplates.put(0, response);
                        Admin.Object.refreshJTableTemplates();
                    }
                }
            }
        });
    }

};

