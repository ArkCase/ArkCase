var deleteReason;
VirtualViewer.prototype.setReason = function (event, reason) {
    deleteReason = reason;
    // do not mess with jQuery events
    event.stopPropagation();
}

if (myFlexSnap) {

    // components of the document id string passed to snowbound to initialize a document in the viewer
    var ACM_TICKET_PARAM = "acm_ticket";
    var ACM_USER_PARAM = "userid";
    var ACM_FILE_PARAM = "ecmFileId";

    // Additional arguments needed for snowbound to communicate back with acm3
    var ACM_PARENT_ID = "parentObjectId";
    var ACM_PARENT_TYPE = "parentObjectType";
    var ACM_DOCUMENT_NAME = "documentName";
    var ACM_SELECTED_IDS = "selectedIds";

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
        myFlexSnap.initMergeDocumentDialog();
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
                        myFlexSnap.sendDocument("split", splitIndex);
                    }
                }
            }
        });
    };

    myFlexSnap.initMergeDocumentDialog = function() {
            $("#vvMergeDocumentDialog").dialog({
                modal: true,
                closeOnEscape: false,
                draggable: false,
                resizable: false,
                width: 600,
                height: 300,
                autoOpen: false,
                buttons: {
                    "OK": function() {
                        var mergeDocIds = $("input#vvMergeDocumentDialogNameInput").val();
                        $(this).dialog("close");

                        // Merges the specified documents in the backend and sends the merged document to ArkCase
                        if (mergeDocIds && mergeDocIds.trim().length > 0) {
                            myFlexSnap.sendDocument("merge", mergeDocIds);
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

    myFlexSnap.arkCaseSplitDocument = function() {
        var splitDialog = $("#vvSplitDocumentDialog").dialog("open");
    }

    myFlexSnap.arkCaseMergeDocument = function() {
        var mergeDialog = $("#vvMergeDocumentDialog").dialog("open");
    }

    myFlexSnap.arkCaseReorderDocumentPages = function(pageOriginalIndex, movedPageNewIndex) {

        var pageOperation = "[" + pageOriginalIndex + "-" + movedPageNewIndex + "]";

        // Sends the reorder request to the snowbound server
        var uri = new URI(vvConfig.servletPath);
        uri.addQuery("action", "arkCaseReorderDocumentPages");
        var data = uri.query();
        uri.query("");
        var dataFinal = data + "&" + myFlexSnap.getDocumentId() + '&pageReorderOperation=' + pageOperation;
        $.ajax({
            url: uri.toString(),
            type: "POST",
            data: dataFinal
        });
    };

    myFlexSnap.arkCaseViewDocument = function(documentId) {

        // Triggers audit event process for viewing this document
        var uri = new URI(vvConfig.servletPath);
        uri.addQuery("action", "arkCaseViewDocument");
        var data = uri.query();
        uri.query("");
        var dataFinal = data + "&" + documentId;
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
        var dataFinal = data + "&" + documentId + '&pageNumbers=' + pageNumbers + '&deleteReason=' + deleteReason;
        $.ajax({
            url: uri.toString(),
            data: dataFinal,
            type: "POST"
        });
    };

    myFlexSnap.arkCaseGetUrlParamMap = function(urlString) {
        var paramMap = {};
        var queryString = new URI(urlString).query();
        if (queryString && queryString.trim().length > 0) {
            queryString = queryString.replace("documentId=", "");
            var querySections = queryString.split("&");
            if (querySections) {
                $.each(querySections, function() { // Enters each argument/value set into the map
                    var argKeyValuePair = this.split("=");
                    if (argKeyValuePair && argKeyValuePair.length == 2) {
                        paramMap[argKeyValuePair[0]] = argKeyValuePair[1];
                    }
                });
            }
        }
        return paramMap;
    };

    myFlexSnap.arkCaseCreateDocumentId = function(documentIdComponents, newDocumentId) {
        // Creates a snowbound document id (includes acm user, ticket, and file id) which can have an original file id if supplied
        return ACM_FILE_PARAM + "=" + ((newDocumentId) ? newDocumentId.trim() : documentIdComponents[ACM_FILE_PARAM]) +
         "&" + ACM_TICKET_PARAM + "=" + documentIdComponents[ACM_TICKET_PARAM] +
         "&" + ACM_USER_PARAM + "=" + documentIdComponents[ACM_USER_PARAM];
    };

    myFlexSnap.arkCaseGetParentNodeArgs = function(urlString) {
        var urlArgs = myFlexSnap.arkCaseGetUrlParamMap(urlString);
        return ACM_DOCUMENT_NAME + "=" + urlArgs[ACM_DOCUMENT_NAME] +
         "&" + ACM_PARENT_ID + "=" + urlArgs[ACM_PARENT_ID] +
         "&" + ACM_PARENT_TYPE + "=" + urlArgs[ACM_PARENT_TYPE] +
         "&" + ACM_SELECTED_IDS + "=" + urlArgs[ACM_SELECTED_IDS];
    };

    myFlexSnap.arkCaseCreateInitDocumentList = function() {
        var documentsToOpen = [];

        // Obtains the parameters from the snowbound iframe and the parent window urls
        var iframeUrlArgs = myFlexSnap.arkCaseGetUrlParamMap(unescape(window.location));

        // Adds the document which was clicked on directly by the user to the list of documents which snowbound will open
        var mainDocumentId = iframeUrlArgs[ACM_FILE_PARAM];
        documentsToOpen.push(myFlexSnap.arkCaseCreateDocumentId(iframeUrlArgs, mainDocumentId));

        // If doctree sends a list of additional selected ids (attached to the parent ArkCase window url)
        // then it means that snowbound needs to open multiple documents at the same time
        var docIdList = iframeUrlArgs[ACM_SELECTED_IDS];
        if (docIdList && docIdList.trim().length > 0) {
            var docIds = docIdList.split(",");

            // Builds a list of snowbound document id strings (fileid, user, ticket) for each document to be opened
            for (var i = 0; i < docIds.length; i++) {
                var docId = docIds[i].trim();
                if (docId.length > 0 && docId != mainDocumentId) // The directly opened document was already added to the list
                    documentsToOpen.push(myFlexSnap.arkCaseCreateDocumentId(iframeUrlArgs, docIds[i]));
            }
        }
        return documentsToOpen;
    };
}