var deleteReason;
VirtualViewer.prototype.setReason = function (event, reason) {
    deleteReason = reason;
    // do not mess with jQuery events
    event.stopPropagation();
}

if (myFlexSnap) {

    // We need to make the pages list sortable, but since the pages
    // thumbnails are re-loaded by snowbound after certain events we
    // need to continue to apply the sortable functionality asynchronously
    var sortingTimer = setInterval(function() {
        var pageOrderTable = $("#vvPageThumbsInternal > tbody");
        if (pageOrderTable.length > 0) {
            pageOrderTable.sortable({ update: function(event, ui) {

                // Determines which page was moved (we need the original index)
                var pageOriginalIndex = 0;
                var pageThumbnailImg = $(ui.item).find("img");
                if (pageThumbnailImg) {
                    var thumbnailUrlSrc = pageThumbnailImg.attr("src");
                    if (thumbnailUrlSrc) {
                        var thumbnailUrlParts = thumbnailUrlSrc.split('&');
                        for (var i = 0; i < thumbnailUrlParts.length; i++) {
                            if (thumbnailUrlParts[i].indexOf("PageNumber") >= 0) {
                                pageOriginalIndex = parseInt(thumbnailUrlParts[i].split('=')[1]);
                                break;
                            }
                        }
                    }
                }

                // The moved page must be selected in order for snowbound to recognize the move operation
                myFlexSnap.addPageToSelection(pageOriginalIndex);

                // Determines the new target index of the page which was moved
                var pageThumbnails = $("#vvPageThumbsInternal > tbody > tr");
                var movedPageNewIndex = pageThumbnails.index(ui.item);

                // Registers the page move with snowbound and initiates an audit event notification
                if (myFlexSnap.cutSelection(false)) {
                    var documentModel = myFlexSnap.getDocumentModel();
                    myFlexSnap.pasteSelection(movedPageNewIndex, false);
                    //myFlexSnap.pasteSelection(movedPageNewIndex, documentModel.model.documentId);
                    myFlexSnap.arkCaseReorderDocumentPages(pageOriginalIndex, movedPageNewIndex);
                    //myFlexSnap.saveDocument(true);
                } else { // Failed to cut selection, cannot reorder successfully
                    ;
                }
            }});
            pageOrderTable.disableSelection();
        }
    }, 1000); // Uses a timer because if Snowbound regenerates the page thumbnail html (after a reorder, for example) then the jquery sortable aspect needs to be applied again

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
        myFlexSnap.initSplitDocumentDialog();
    };

    myFlexSnap.initSplitDocumentDialog = function() {
        $("#vvSplitDocumentDialog").dialog({
            modal: true,
            closeOnEscape: false,
            draggable: false,
            resizable: false,
            width: 600,
            height: 300,
            autoOpen: false,
            buttons: {
                "OK": function() {
                    var splitIndex = $("input#vvSplitDocumentDialogNameInput").val();
                    $(this).dialog("close");

                    // Splits the document in the backend and sends the sub documents to ArkCase
                    if (splitIndex && splitIndex.trim().length > 0) {
                        myFlexSnap.sendDocument(splitIndex);
                    }
                }
            }
        });
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

    // Obtains the ticket, userid, and document id from the snowbound page url
    myFlexSnap.arkCaseGetCommonUrlParams = function() {
        var argUrlSection = "";
        var snowUrl = unescape(window.location);
        var snowUrlArgSections = snowUrl.split("?");
        var argKeys = ["userid", "acm_ticket", "documentId"];
        for (var y = 0; y < snowUrlArgSections.length; y++) {
            var snowUrlArgs = snowUrlArgSections[y].split("&");
            for (var i = 0; i < snowUrlArgs.length; i++) {
                var urlKeyValuePair = snowUrlArgs[i].split("=");
                var urlArgument = urlKeyValuePair[0];
                var urlValue = urlKeyValuePair[1];
                if (urlKeyValuePair.length > 2) { // this scenario of more than one equals sign happens in the url
                    urlValue = urlKeyValuePair[urlKeyValuePair.length - 1];
                }

                // Adds valid url arguments from ArkCase to the url for the snowbound backend call
                if ($.inArray(urlArgument, argKeys) >= 0) {
                    if (urlArgument == "documentId") urlArgument = "ecmFileId"; // The backend expects "ecmFileId", not "documentId"
                    argUrlSection += urlArgument + "=" + urlValue + "&";
                }
            }
        }
        if (argUrlSection.length > 0) // removes trailing & character
            argUrlSection = argUrlSection.substring(0, argUrlSection.length - 1);
        return argUrlSection;
    };

    myFlexSnap.arkCaseSplitDocument = function() {
        var splitDialog = $("#vvSplitDocumentDialog").dialog("open");
    }

    myFlexSnap.arkCaseReorderDocumentPages = function(pageOriginalIndex, movedPageNewIndex) {

        var pageOperation = "[" + pageOriginalIndex + "-" + movedPageNewIndex + "]";

        // Sends the reorder request to the snowbound server
        var uri = new URI(vvConfig.servletPath);
        uri.addQuery("action", "arkCaseReorderDocumentPages");
        var data = uri.query();
        uri.query("");
        var dataFinal = data + "&" + myFlexSnap.arkCaseGetCommonUrlParams() + '&pageReorderOperation=' + pageOperation;
        $.ajax({
            url: uri.toString(),
            type: "POST",
            data: dataFinal
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
        var dataFinal = data + "&" + myFlexSnap.arkCaseGetCommonUrlParams() +"&" + documentId + '&pageNumbers=' + pageNumbers + '&deleteReason=' + deleteReason;
        $.ajax({
            url: uri.toString(),
            data: dataFinal,
            type: "POST"
        });
    };
}