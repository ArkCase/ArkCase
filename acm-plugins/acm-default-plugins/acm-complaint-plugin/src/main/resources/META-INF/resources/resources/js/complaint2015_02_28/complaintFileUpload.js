/**
 * Complaint.FileUpload
 *
 * JTable
 *
 * @author jwu
 */
Complaint.FileUpload = {
    create : function() {
    }

    ,removeUploadFileArea: function() {
        this.$upploadList.find("li").remove();
    }
    ,_jqXHR : undefined
    ,useFileUpload: function($upload, $drop, $input, $ul, $click, urlEvaluator, formDataEvaluator, successHandler) {
        this.$upploadList = $ul;

        $(function(){
            $click.click(function(){
                // Simulate a click on the file input button
                // to show the file browser dialog
                //$(this).parent().find('input').click();
                $input.click();
            });

            // Initialize the jQuery File Upload plugin
            _jqXHR = $upload.fileupload({
                url: urlEvaluator()
                ,dropZone: $drop

                ,done: function (e, data) {
//                    var a1 = data.result
//                    var a2 = data.textStatus;
//                    var a3 = data.jqXHR;

                    if ("success" == data.textStatus) {
                        successHandler();
                    }
                }

                ,formData: function(form) {
                    return formDataEvaluator();
                }

                // This function is called when a file is added to the queue;
                // either via the browse button, or via drag/drop:
                ,add: function (e, data) {

                    var tpl = $('<li class="working"><input type="text" value="0" data-width="48" data-height="48"'+
                        ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

                    // Append the file name and file size
                    tpl.find('p').text(data.files[0].name)
                        .append('<i>' + formatFileSize(data.files[0].size) + '</i>');

                    // Add the HTML to the UL element
                    data.context = tpl.appendTo($ul);

                    // Initialize the knob plugin
                    tpl.find('input').knob();

                    // Listen for clicks on the cancel icon
                    tpl.find('span').click(function(){

                        if(tpl.hasClass('working')){
                            _jqXHR.abort();
                        }

                        tpl.fadeOut(function(){
                            tpl.remove();
                        });

                    });

                    // Automatically upload the file once it is added to the queue
                    _jqXHR = data.submit();
                }

                ,progress: function(e, data){
                    // Calculate the completion percentage of the upload
                    var progress = parseInt(data.loaded / data.total * 100, 10);

                    // Update the hidden input field and trigger a change
                    // so that the jQuery knob plugin knows to update the dial
                    data.context.find('input').val(progress).change();

                    if(progress == 100){
                        data.context.removeClass('working');
                    }
                }

                ,fail:function(e, data){
                    // Something has gone wrong!
                    data.context.addClass('error');
                }


//To Explore:
                //redirect : to complaintList
                //redirectParamName:
                //autoUpload: false
                //sequentialUploads: true
//
//check if complaintId not created, create it first
//                ,submit: function (e, data) {
//                    var input = $('#input');
//                    data.formData = {example: input.val()};
//                    if (!data.formData.example) {
//                        data.context.find('button').prop('disabled', false);
//                        input.focus();
//                        return false;
//                    }
//                }
//                ,always: function (e, data) {
//                    // data.result
//                    // data.textStatus;
//                    // data.jqXHR;
//                }

            });


            // Prevent the default action when a file is dropped on the window
            $(document).on('drop dragover', function (e) {
                e.preventDefault();
            });

            // Helper function that formats the file sizes
            function formatFileSize(bytes) {
                if (typeof bytes !== 'number') {
                    return '';
                }

                if (bytes >= 1000000000) {
                    return (bytes / 1000000000).toFixed(2) + ' GB';
                }

                if (bytes >= 1000000) {
                    return (bytes / 1000000).toFixed(2) + ' MB';
                }

                return (bytes / 1000).toFixed(2) + ' KB';
            }

        });
    }

};

//
// code copied for Complaint.Object, in case to use file upload again
//
//,initialize() {
//    //file upload without web form
//    this.$spanAddDocument.unbind("click");
//    this.$tableDocDocuments = this.$divDocuments.find("table.jtable");
//    this.$upploadList = $('#upload ul');
//    this.$upploadInput = $('#tabDocuments input[type="file"]');
//    Complaint.FileUpload.useFileUpload(this.$divDocuments, this.$tableDocDocuments, this.$upploadInput, this.$upploadList, this.$spanAddDocument
//        ,this.getUrlFileUpload
//        ,this.getFormDataFileUpload
//        ,this.onSuccessFileUpload
//    );
//}
//
//,getUrlFileUpload: function() {
//    return App.getContextPath() + Complaint.Service.API_UPLOAD_COMPLAINT_FILE;
//}
//,getFormDataFileUpload: function() {
//    var fd = [{}];
//    fd[0].name = "complaintId";
//    fd[0].value = Complaint.getComplaintId();
//    return fd;
//}
//,onSuccessFileUpload: function() {
//    Complaint.FileUpload.removeUploadFileArea();
//
//    var complaintId = Complaint.getComplaintId();
//    if (0 < complaintId) {
//        Complaint.Service.retrieveDetail(complaintId);
//    }
//}



