var deleteReason;
VirtualViewer.prototype.setReason = function (event, reason) {
    deleteReason = reason;
    // do not mess with jQuery events
    event.stopPropagation();
}

if (myFlexSnap) {

    // handle delete pages context menu action
    $.event.special.vvDeletePages = {
        _default: function (event) {
            // store page numbers pending for deletion
            pageNumbers = myFlexSnap.getDocumentModel().getSelectedPageNumbers();

            // open "select delete reason" dialog
            $("#vvDeletePagesReason").dialog({
                title: "Delete Pages",
                modal: true,
                closeOnEscape: false,
                draggable: false,
                resizable: false,
                width: 600,
                height: 300,
                autoOpen: true,
                buttons: {
                    "OK": function () {
                        if (deleteReason == undefined) {
                            deleteReason = $('input[name="reason"]').val();
                        }
                        $(this).dialog('close');
                        // invoke default event handler and check result...
                        if (myFlexSnap.cutSelection(true)) {
                            // success
                            documentId = myFlexSnap.getDocumentId();
                            // invoke service method, which in turn will invoke ArkCase service...
                            myFlexSnap.arkCaseDeleteDocumentPages(documentId, pageNumbers, deleteReason);
                        }
                    }
                }
            });
        }
    };

    myFlexSnap.initPatch = function () {
        myFlexSnap.initCreateStampDialog();
    };

    myFlexSnap.initCreateStampDialog = function () {
        $("#vvCreatingStampDialog").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            width: 450,
            height: 100,
            autoOpen: false,
            open: function () {
                $(this).parent().children().children(".ui-dialog-titlebar-close").hide()
            }
        });
    };

    myFlexSnap.arkCaseCreateCustomImageStamp = function () {
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
            success: function (result) {
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
            error: function (error) {
                console.log("arkCaseCreateCustomImageStamp: error")
                $("#vvCreatingStampDialog").dialog("close");
            }
        })
    };

    myFlexSnap.arkCaseDeleteDocumentPages = function (documentId, pageNumbers, deleteReason) {
        var uri = new URI(vvConfig.servletPath);
        uri.addQuery("action", "arkCaseDeleteDocumentPages");
        var data = uri.query();
        uri.query("");
        $.ajax({
            url: uri.toString(),
            data: data + '&' + documentId + '&pageNumbers=' + pageNumbers + '&deleteReason=' + deleteReason,
            type: "POST"
        })
    };
}