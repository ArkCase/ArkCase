if (myFlexSnap) {

    myFlexSnap.initPatch = function (){
        myFlexSnap.initCreateStampDialog();
    };

    myFlexSnap.initCreateStampDialog = function() {
        $("#vvCreatingStampDialog").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            width: 450,
            height: 100,
            autoOpen: false,
            open: function() {
                $(this).parent().children().children(".ui-dialog-titlebar-close").hide()
            }
        });
    };

    myFlexSnap.arkCaseCreateCustomImageStamp = function() {
        var uri = new URI(vvConfig.servletPath);
        uri.addQuery("action", "arkCaseCreateCustomImageStamp");
        var data = uri.query();
        uri.query("");
        $("#vvCreatingStampDialog").dialog("open");
        $.ajax({
            url: uri.toString(),
            type: "POST",
            data: data,
            dataType: "json",
            success: function(result) {
                console.log("arkCaseCreateCustomImageStamp: success")
                if (result.status === "OK") {
                    $("#vvImageRubberStampContextMenuList").empty();
                    myFlexSnap.getServerConfig();
                } else if (result.status === "ERROR") {
                    console.log("arkCaseCreateCustomImageStamp: error")
                } else {
                    console.log("arkCaseCreateCustomImageStamp: unknown")
                }
                $("#vvCreatingStampDialog").dialog("close");
            },
            error: function(error) {
                console.log("arkCaseCreateCustomImageStamp: error")
                $("#vvCreatingStampDialog").dialog("close");
            }
        })
    };
}