/**
 * DocTree.View
 *
 * @author jwu
 */
DocTree.View = DocTree.View || {
    create : function(args) {
        this.parentType = args.parentType;
        this.parentId = args.parentId;
        this.arkcaseUrl = args.arkcaseUrl;
        this.arkcasePort = args.arkcasePort;

        this.doUploadForm = args.uploadForm;
        this.fileTypes  = args.fileTypes;

        this.$tree = (args.$tree)? args.$tree : $("#treeDoc");
        this.createDocTree(args.treeArgs);

        this.makeDownloadDocForm(this.$tree);
        this.makeUploadDocForm(this.$tree);

        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_PARENT           ,this.onViewChangedParent);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_RETRIEVED_FOLDERLIST    ,this.onModelRetrievedFolderList);
        //Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_TREE             ,this.onViewChangedTree);

        if (this.DialogDnd.create) {this.DialogDnd.create(args);}
    }
    ,onInitialized: function() {
        if (DocTree.View.DialogDnd.onInitialized) {DocTree.View.DialogDnd.onInitialized();}
    }

    ,makeUploadDocForm: function($s) {
        this.$formUploadDoc = $("<form/>")
            .attr("id"       , "formUploadDoc")
            .attr("style"    , "display:none;")
            .appendTo($s);
        this.$fileInput = $("<input/>")
            .attr("type"     , "file")
            .attr("id"       , "file")
            .attr("name"     , "files[]")
            .attr("multiple" , "")
            .appendTo(this.$formUploadDoc);

        this.$fileInput.on("change", function(e) {
            DocTree.View.$formUploadDoc.submit();
        });
        this.$formUploadDoc.submit(function(e) {
            DocTree.View.onSubmitFormUploadFile(e, this);
        });
    }
    ,makeDownloadDocForm: function($s) {
        this.$formDownloadDoc = $("<form/>")
            .attr("id"     , "formDownloadDoc")
            .attr("action" , "#")
            .attr("style"  , "display:none;")
            .appendTo($s);
    }

    ,uploadForm: function(node, formType) {
        DocTree.View.uploadToFolderNode = node;
        DocTree.View.uploadFileType = formType;
        if (DocTree.View.doUploadForm) {
            DocTree.View.doUploadForm(formType, node.data.objectId, function() {
                DocTree.View.onLoadingFrevvoForm();
            });
        }
    }
    ,uploadFile: function(node, fileType) {
        DocTree.View.uploadToFolderNode = node;
        DocTree.View.uploadFileType = fileType;
        DocTree.View.uploadFileNew = true;

        DocTree.View.$fileInput.attr("multiple", '');
        DocTree.View.$fileInput.click();
    }
    ,replaceFile: function(node) {
//        var fileType = null;
//        if (Acm.isArray(this.fileTypes)) {
//            for (var i = 0; i < this.fileTypes.length; i++) {
//                if (Acm.goodValue(this.fileTypes[i].type) == Acm.goodValue(node.data.filetype)) {
//                    filetype = Acm.goodValue(node.data.filetype);
//                    break;
//                }
//            }
//        }
        var fileType = Acm.goodValue(node.data.type);
        if (Acm.isNotEmpty(fileType)) {
            DocTree.View.replaceFileNode = node;
            DocTree.View.uploadToFolderNode = node.parent;
            DocTree.View.uploadFileType = fileType;
            DocTree.View.uploadFileNew = false;

            DocTree.View.$fileInput.removeAttr("multiple");
            DocTree.View.$fileInput.click();
        }
    }

    ,Email:{
        _isDlgComponentsCreated: false
        ,isDlgComponentsCreated: function() {
            return this._isDlgComponentsCreated;
        }
        ,setDlgComponentsCreated: function(isDlgComponentsCreated) {
            this._isDlgComponentsCreated = isDlgComponentsCreated;
        }
        ,showEmailDialog: function(nodes){
            //prepare to create dialog box
            var args = {
                name: "Send Email"
                , title: $.t("doctree:email.title")
                , prompt: $.t("doctree:email.prompt")
                , btnGoText: $.t("doctree:email.btnTextGo")
                , btnOkText: $.t("doctree:email.btnTextOk")
                , filters: [{key: "Object Type", values: ["USER"]}]
                , onClickBtnPrimary: function (event, ctrl) {
                    var emailAddresses = ($dlgSelector.find("#recipientsList").val().split(";"));
                    if(Acm.isArrayEmpty(emailAddresses) && emailAddresses[0] != ""){
                        Acm.Dialog.info($.t("doctree:email.select-user-info"));
                        dlgSendEmail.show();
                    }
                    else{
                        var emailNotifications = DocTree.View.Email.makeEmailData(emailAddresses, nodes);
                        //DocTree.Controller.viewSentEmail(emailNotifications);
                        DocTree.Model.sendEmail(emailNotifications).fail(function(failed){
                            Acm.MessageBoard.show($.t("doctree:error.email-delivery") + failed + "\n" + $.t("doctree:error.email-retry"));
                        });
                    }
                }
            }

            var dlgSendEmail = SearchBase.Dialog.create(args);

            //get dialog selector
            var $dlgSelector = dlgSendEmail.getSelector();

            //inject buttons and input area
            if(!this.isDlgComponentsCreated()) {
                //create form and add input area for
                //email addresses
                var $emailFormGroup = $("<div/>")
                    .addClass("form-group acm-emailFormGroup")
                    //.css({"display": "inline-block", "width": "100%", "text-align": "left"})
                    .text($.t("doctree:email.instruct-email"))
                    .prependTo($dlgSelector.find('.modal-footer'));

                var $edtEmailRecipients = $("<input/>")
                    .attr("type", "text")
                    .attr("id", "recipientsList")
                    .addClass("form-control")
                    .css("width", "100%")
                    .appendTo($emailFormGroup)
                    .val('');

                //create button to add users to recipients
                var $btnAddUsersToRecipients = $("<button/>")
                    .attr("type", "button")
                    .attr("id", "addUsersToRecipients")
                    .addClass("btn btn-default")
                    .html("Add Selected Users");

                $dlgSelector.find('button.btn-primary').before($btnAddUsersToRecipients);

                //create a send later checkbox and
                //send later date picker and time fields
                var $labelForSendLater = $("<label>")
                    .attr('for', "sendLater")
                    .addClass("pull-left")
                    .text('Send Later');

                var $chkSendLater = $("<input/>")
                    .attr("type", "checkbox")
                    .attr("id", "sendLater")
                    .addClass("pull-left");

                $chkSendLater.appendTo($labelForSendLater);
                $labelForSendLater.appendTo($dlgSelector.find('.modal-footer'));

                var $edtSendLaterDateTime = $("<span/>")
                    .attr("id", "datetime")
                    .addClass("pull-left")
                    .css("display", "none")
                    .html("&nbsp;&nbsp;&nbsp");

                var $edtSendLaterDate = $("<input/>")
                    .attr("type", "text")
                    .attr("id", "sendLaterDate")
                    .attr("placeholder", $.t("doctree:email.instruct-date"))
                    .addClass("input-s-sm");

                var $edtSendLaterTime = $("<p/><input/>")
                    .attr("type", "text")
                    .attr("id", "sendLaterTime")
                    .attr("placeholder", $.t("doctree:email.instruct-time"))
                    .addClass("input-s-sm");

                $edtSendLaterDate.appendTo($edtSendLaterDateTime);
                $edtSendLaterTime.appendTo($edtSendLaterDateTime);
                $edtSendLaterDateTime.appendTo($dlgSelector.find('.modal-footer'));
                $edtSendLaterDate.datepicker();
                this.setDlgComponentsCreated(true);
            }
            //finally display the dialog box
            dlgSendEmail.show();

            //event handlers for dialog actions
            $dlgSelector.find('.modal-footer').on('click', "#addUsersToRecipients", function(e) {
                DocTree.View.Email._showSelectedEmails(e,$btnAddUsersToRecipients,$edtEmailRecipients,dlgSendEmail);
            });

            $dlgSelector.find('.modal-footer').on('click', "#sendLater", function(e) {
                $dlgSelector.find("#datetime").slideToggle();
            });

            $dlgSelector.on("hidden.bs.modal", function(e) {
                $dlgSelector.find("#recipientsList").val('');
            });
        }
        ,_showSelectedEmails: function(event,$btnAddUsersToRecipients,$edtEmailRecipients,dlgSendEmail){
            //prevent event from bubbling up the dom
            event.stopImmediatePropagation();
            var val= $edtEmailRecipients.val();
            dlgSendEmail.getSelectedRows().each(function () {
                var record = $(this).data('record');
                if(Acm.isNotEmpty(record.email)){
                    if(Acm.isNotEmpty(val) && val.substr(val.length-1)!= ";") {
                        val += ";" + Acm.goodValue(record.email);
                    }
                    else{
                        val += Acm.goodValue(record.email);
                    }
                    $edtEmailRecipients.val(val);
                }
            });
        }
        ,makeEmailData: function(emailAddresses, nodes, title){
            var emailNotifications = [];
            var emailData = {};
            emailData.emailAddresses = emailAddresses;
            emailData.title = Acm.goodValue(title, "ArkCase Documents");
            emailData.note = DocTree.View.Email._makeEmailNote(nodes);
            emailNotifications.push(emailData);
            return emailNotifications;
        }
        ,_makeEmailNote: function(nodes){
            var firstLine = "Please follow the links below to view the document(s): " + "\n\n";
            var note="";
            if(Acm.isArray(nodes)){
                for(var i = 0; i < nodes.length; i++){
                    var title = Acm.goodValue(nodes[i].data.name) + "\n";
                    var url = Acm.goodValue(DocTree.View.arkcaseUrl);
                    if(Acm.isNotEmpty(DocTree.View.arkcasePort)){
                        url += ":" + Acm.goodValue(DocTree.View.arkcasePort);
                    }
                    url+= App.getContextPath() + "/plugin/document/" + Acm.goodValue(nodes[i].data.objectId);
                    note += title + url + "\n\n";
                }
            }
            else{
                var title = Acm.goodValue(nodes.data.name) + "\n";
                var url = Acm.goodValue(DocTree.View.arkcaseUrl);
                if(Acm.isNotEmpty(DocTree.View.arkcasePort)){
                    url += ":" + Acm.goodValue(DocTree.View.arkcasePort);
                }
                url+= App.getContextPath() + "/plugin/document/" + Acm.goodValue(nodes.data.objectId);
                note += title + url + "\n\n";
            }
            return firstLine + note;
        }
    }

    ,_addFileNode: function(folderNode, name, type) {
        var fileNode = folderNode.addChildren({"title": $.t("doctree:wait-upload") + " " + name + "...", "name": name, "type": type, "loadStatus": "loading"});
        //fileNode.setStatus("loading");
        DocTree.View.markNodePending(fileNode);
        return fileNode;
    }
    ,_addingFileNodes: function(folderNode, names, type) {
        var deferred = $.Deferred();
        DocTree.View.expandNode(folderNode).done(function() {
            var fileNodes = [];
            for (var i = 0; i < names.length; i++) {
                var fileNode = DocTree.View._addFileNode(folderNode, names[i], type);
                fileNodes.push(fileNode);
            }
            deferred.resolve(fileNodes);
        });
        return deferred.promise();
    }

    ,onFailedAddingFileNode: function() {
        var z = 1;
    }
    ,onLoadingFrevvoForm: function() {
        var folderNode = DocTree.View.uploadToFolderNode;
        var fileType = DocTree.View.uploadFileType;
        var names = [fileType + " form"];
        var promiseAddNode = DocTree.View._addingFileNodes(folderNode, names, fileType);

        setTimeout(function(){
            //var promiseRetrieveLatest = DocTree.Service.checkUploadForm(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderNode.data.objectId, folderNode.data.startRow, folderNode, fileType);

            //yyyy
            var promiseRetrieveLatest = DocTree.View.Op.retrieveFolderList(folderNode
                ,function(folderListLatest) {
//                    var mock = {};
//                    var i = folderListLatest.children.length - 1;
//                    mock.objectId   = folderListLatest.children[i].objectId + 1001;
//                    mock.objectType = folderListLatest.children[i].objectType;
//                    mock.created    = folderListLatest.children[i].created;
//                    mock.creator    = folderListLatest.children[i].creator;
//                    mock.modified   = folderListLatest.children[i].modified;
//                    mock.modifier   = folderListLatest.children[i].modifier;
//                    mock.name       = "Mock";
//                    mock.type       = fileType;
//                    mock.status     = folderListLatest.children[i].status;
//                    mock.category   = folderListLatest.children[i].category;
//                    mock.version    = "1.1";
//                    mock.versionList  = [{versionTag:"1.0"},{versionTag:"1.1"}];
//                    folderListLatest.children.push(mock);
//                    folderListLatest.totalChildren++;
//                    mock = {};
//                    i = folderListLatest.children.length - 1;
//                    mock.objectId   = folderListLatest.children[i].objectId + 1002;
//                    mock.objectType = folderListLatest.children[i].objectType;
//                    mock.created    = folderListLatest.children[i].created;
//                    mock.creator    = folderListLatest.children[i].creator;
//                    mock.modified   = folderListLatest.children[i].modified;
//                    mock.modifier   = folderListLatest.children[i].modifier;
//                    mock.name       = "Mock2";
//                    mock.type       = fileType;
//                    mock.status     = folderListLatest.children[i].status;
//                    mock.category   = folderListLatest.children[i].category;
//                    mock.version    = "1.2";
//                    mock.versionList  = [{versionTag:"1.0"}, {versionTag:"1.1"}, {versionTag:"1.2"}];
//                    folderListLatest.children.push(mock);
//                    folderListLatest.totalChildren++;

                    var uploadedFiles = null;
                    if (DocTree.Model.validateFolderList(folderListLatest)) {
                        var newChildren = [];
                        for (var i = folderListLatest.children.length - 1; 0 <= i; i--) {
                            if (folderListLatest.children[i].type == fileType) {
                                if (!DocTree.View.findChildNodeById(folderNode, folderListLatest.children[i].objectId)) { //not found in the tree node, must be newly created
                                    newChildren.push(folderListLatest.children[i]);
                                }
                            }
                        }
                        if (!Acm.isArrayEmpty(newChildren)) {
                            //var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
                            var cacheKey = DocTree.View.getCacheKey(folderNode);
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                uploadedFiles = [];
                                for (var i = 0; i < newChildren.length; i++) {
                                    var uploadedFile = DocTree.Model.fileToSolrData(newChildren[i]);
                                    uploadedFiles.push(uploadedFile);
                                    //folderList.children.push(uploadedFile);
                                    //folderList.totalChildren++;
                                }
                            } //end if validateFolderList
                        } //end if (!Acm.isArrayEmpty(newChildren))
                    }
                    return uploadedFiles;
                }
            );

            $.when(promiseRetrieveLatest, promiseAddNode).done(function(uploadedFiles, fileNodes){
                if (!Acm.isArrayEmpty(uploadedFiles) && DocTree.View.validateFancyTreeNodes(fileNodes)) {
                    for (var i = 0; i < uploadedFiles.length; i++) {
                        var uploadedFile = uploadedFiles[i];
                        var emptyNode = null;
                        if (0 == i) {
                            emptyNode = DocTree.View._findEmptyNode(folderNode, fileType);
                            if (emptyNode) {
                                DocTree.View._fileDataToNodeData(uploadedFile, emptyNode);
                                emptyNode.renderTitle();
                                emptyNode.setStatus("ok");
                            }
                        }


                        if (!emptyNode) {
                            var fileNode = folderNode.addChildren({"title": Acm.goodValue(uploadedFile.name)});
                            DocTree.View._fileDataToNodeData(uploadedFile, fileNode);
                            fileNode.renderTitle();
                        }
                    }
                } else { // most likely the Frevvo form was canceled or closed, remove previously created nodes (identified with spinner icon)
                    for (var i = 0; i < fileNodes.length; i++) {
                        folderNode.removeChild(fileNodes[i]);
                    }
                }
            });
        }, 5000);

    }
    ,_folderDataToNodeData: function(folderData, nodeData) {
        if (folderData && nodeData) {
            if (!nodeData.data) {
                nodeData.data = {};
            }
            nodeData.key             = Acm.goodValue(folderData.objectId, 0);
            nodeData.title           = Acm.goodValue(folderData.name);
            nodeData.tooltip         = Acm.goodValue(folderData.name);
            nodeData.data.name       = Acm.goodValue(folderData.name);
            nodeData.data.objectId   = Acm.goodValue(folderData.objectId, 0);
            nodeData.data.objectType = Acm.goodValue(folderData.objectType);
            nodeData.data.created    = Acm.goodValue(folderData.created);
            nodeData.data.creator    = Acm.goodValue(folderData.creator);

        }
        return nodeData;
    }
    ,_fileDataToNodeData: function(fileData, nodeData) {
        if (fileData && nodeData) {
            if (!nodeData.data) {
                nodeData.data = {};
            }
            nodeData.key             = Acm.goodValue(fileData.objectId, 0);
            nodeData.title           = Acm.goodValue(fileData.name);
            nodeData.tooltip         = Acm.goodValue(fileData.name);
            nodeData.data.name       = Acm.goodValue(fileData.name);
            nodeData.data.type       = Acm.goodValue(fileData.type);
            nodeData.data.objectId   = Acm.goodValue(fileData.objectId, 0);
            nodeData.data.objectType = Acm.goodValue(fileData.objectType);
            nodeData.data.created    = Acm.goodValue(fileData.created);
            nodeData.data.creator    = Acm.goodValue(fileData.creator);
            nodeData.data.status     = Acm.goodValue(fileData.status);
            nodeData.data.category   = Acm.goodValue(fileData.category);
            nodeData.data.version    = Acm.goodValue(fileData.version);
            if (Acm.isArray(fileData.versionList)) {
                nodeData.data.versionList = [];
                for (var i = 0; i < fileData.versionList.length; i++) {
                    var version = {};
                    version.versionTag = Acm.goodValue(fileData.versionList[i].versionTag);
                    nodeData.data.versionList.push(version);
                }
            }
        }
        return nodeData;
    }
    ,_findEmptyNode: function(folderNode, fileType) {
        var node = null;
        for (var i = folderNode.children.length - 1; 0 <= i; i--) {
            if (fileType == folderNode.children[i].data.type) {
                if (Acm.isEmpty(folderNode.children[i].data.objectId)) {
                    node = folderNode.children[i];
                    break;
                }
            }
        }
        return node;
    }
    ,onSubmitFormUploadFile: function(event, ctrl) {
        event.preventDefault();
        var files = DocTree.View.$fileInput[0].files;
        DocTree.View.doSubmitFormUploadFile(files);
    }
    ,doSubmitFormUploadFile: function(files) {
        var folderNode = DocTree.View.uploadToFolderNode;
        var fileType = DocTree.View.uploadFileType;
        var fd = new FormData();
        fd.append("parentObjectType", DocTree.Model.getObjType());
        fd.append("parentObjectId", DocTree.Model.getObjId());
        if (!DocTree.View.isTopNode(folderNode)) {
            //fd.append("parentFolderId", folderNode.data.objectId);
            fd.append("folderId", folderNode.data.objectId);
        }
        fd.append("fileType", fileType);
        fd.append("category", "Document");
        var names = [];
        for(var i = 0; i < files.length; i++ ){
            names.push(files[i].name);
            fd.append("files[]", files[i]);
            if (0 == i && !DocTree.View.uploadFileNew) {    //for replace operation, only take one file
                break;
            }
        }

        var cacheKey = DocTree.View.getCacheKey(folderNode);
        if (DocTree.View.uploadFileNew) {
            DocTree.View.Op.uploadFiles(fd, folderNode, names, fileType);
        } else {
            var replaceNode = DocTree.View.replaceFileNode;
            DocTree.View.Op.replaceFile(fd, replaceNode, names[0]);
        }
    }
    ,_matchFileNode: function(type, name, fileNodes) {
        var fileNode = null;
        for (var i = 0; i < fileNodes.length; i++) {
            //var nameOrig = this._getNameOrig(name);
            var nameNode = fileNodes[i].data.name;
            //nameNode = nameNode.replace(/ /g, "_");
            if (nameNode == name && fileNodes[i].data.type == type) {
                fileNode = fileNodes[i];
                break;
            }
        }
        return fileNode;
    }


//    // The gear button click events toggle the menu popup. For some unknown reason, the events are fired
//    // multiple times rapidly, which mess up the menu toggle logic. _contextMenuIsOpening flag is used
//    // to ignore click events during a 100ms time window.
////    ,_contextMenuIsOpening: false
//    ,onViewChangedTree: function() {
//        var $btnTreeBody = DocTree.View.$tree.find("tbody");
//        var $btnTreeActions = DocTree.View.$tree.find("button");
//        $btnTreeActions.on("click", function(e) {
//            if (DocTree.View._contextMenuIsOpening) {
//                return;
//            }
//
//            var $treeBody = DocTree.View.$tree.find("tbody");
//            //var isOpen = $btnTreeBody.contextmenu("isOpen");   //This does not work as expected
//            var isOpen = Acm.Object.isVisible($(".ui-menu"));
//            if (isOpen) {
//                $btnTreeBody.contextmenu("close");
//
//            } else {
//                DocTree.View._contextMenuIsOpening = true;
//                setTimeout(function(){
//                    DocTree.View._contextMenuIsOpening = false;
//                }, 100);
//
//                $btnTreeBody.contextmenu("open", $(this));
//            }
//        });
//    }

    ,onViewChangedParent: function(objType, objId) {
        DocTree.View.switchObject(objType, objId);
    }

//    ,onModelRetrievedFolderList: function(folderList, objType, objId, folderId, pageId, folderNode) {
//        if (folderList.hasError) {
//            App.View.MessageBoard.show($.t("doctree:error.retrieve-folder-list"), Acm.goodValue(uploadInfo.errorMsg));
//            DocTree.View.markNodeError(folderNode);
//
//        } else if (DocTree.Model.validateFolderList(folderList) && DocTree.View.validateNode(folderNode)) {
//            folderNode.data.objectId = folderList.folderId;
//            folderNode.data.totalChildren = folderList.totalChildren;
//            folderNode.renderTitle();
//        }
//    }
    ,onChangeVersion: function(event) {
        var node = DocTree.View.tree.getActiveNode();
        if (node) {
            var parent = node.parent;
            if (parent) {
                var cacheKey = DocTree.View.getCacheKey(parent);

                var verSelected = Acm.Object.getSelectValue($(this));
                var verCurrent = Acm.goodValue(node.data.version, "0");
                if (verSelected != verCurrent) {
                    if (verSelected < verCurrent) {
                        Acm.Dialog.confirm($.t("doctree:confirm-version")
                             ,function(result) {
                                 if (result) {
                                     DocTree.View.Op.setActiveVersion(node, verSelected);
                                 } else {
                                     node.renderTitle();
                                 }
                             }
                        );
                    } else {
                        DocTree.View.Op.setActiveVersion(node, verSelected);
                    }
                }
            } //end if (parent)
        }
    }

    //
    // This prevent going to detail page when user checking version drop down too fast
    //
    ,onDblClickVersion: function(event, data) {
        event.stopPropagation();
    }

    ,onClickBtnChkAllDocument: function(event, ctrl) {
        var checked = Acm.Object.isChecked($(ctrl));
        DocTree.View.tree.visit(function(node){
            node.setSelected(checked);
        });
    }
    ,checkNodes: function(nodes, check) {
        if (!Acm.isArrayEmpty(nodes)) {
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].setSelected(check);
            }
        }
    }

    ,getSelectedNodes: function() {
        var nodes = null;
        if (this.tree) {
            nodes = this.tree.getSelectedNodes();
        }
        return nodes;
    }
    ,getEffectiveNodes: function() {
        var nodes = null;
        if (this.tree) {
            var selNodes = this.tree.getSelectedNodes();
            var node = this.tree.getActiveNode();
            nodes= (!Acm.isArrayEmpty(selNodes))? selNodes : ((Acm.isNotEmpty(node))? [node] : []);
        }
        return nodes;
    }

    ,_isEditing: false
    ,isEditing: function() {
        return this._isEditing;
    }
    ,setEditing: function(isEditing) {
        this._isEditing = isEditing;
    }

    ,CLIPBOARD : null
    ,_getDefaultTreeArgs: function() {
        return {
            extensions: ["table", "gridnav", "edit", "dnd"]
            ,checkbox: true
            ,selectMode: 2
//            ,select: function(event, data) {
//                var selNodes = data.tree.getSelectedNodes();
//                var selKeys = $.map(selNodes, function(node){
//                    return "[" + node.key + "]: '" + node.title + "'";
//                });
//                var a = selKeys.join(", ");
//                var z = 1;
//            }
//            ,beforeExpand: function(event, data) {
//                var z = 2;
//                return false;
//            }
//            ,expand: function(event, data) {
//                var z = 2;
//                return false;
//            }
            ,table: {
                indentation: 10,      // indent 20px per node level
                nodeColumnIdx: 2,     // render the node title into the 2nd column
                checkboxColumnIdx: 0  // render the checkboxes into the 1st column
            }
            ,gridnav: {
                autofocusInput: false,
                handleCursorKeys: true
            }
            ,renderColumns: function(event, data) {
                var node = data.node;
                var $tdList = $(node.tr).find(">td");
                // (index #0 is rendered by fancytree by adding the checkbox)
                // (index #2 is rendered by fancytree)

                //$tdList.eq(1).html(DocTree.View.Source.getHtmlDocLink(node));
                var $td1 = $("<td/>");
                DocTree.View.Source.getHtmlDocLink(node).appendTo($td1);
                $tdList.eq(1).replaceWith($td1);


                if (DocTree.View.isFolderNode(node)) {
                    ;
                } else if (DocTree.View.isFileNode(node)) {
                    $tdList.eq(3).text(node.data.type);
                    $tdList.eq(4).text(Acm.getDateFromDatetime(node.data.created,$.t("common:date.short")));
                    $tdList.eq(5).text(App.Model.Users.getUserFullName(Acm.goodValue(node.data.creator)));


                    var $td6 = $("<td/>");
                    var $span = $("<span/>").appendTo($td6);
                    var $select = $("<select/>")
                        .addClass('docversion inline')
                        .appendTo($span)
                        ;

                    if (Acm.isArray(node.data.versionList)) {
                        for (var i = 0; i < node.data.versionList.length; i++) {
                            var versionTag = node.data.versionList[i].versionTag;
                            var $option = $("<option/>")
                                    .val(versionTag)
                                    .text(versionTag)
                                    .appendTo($select)
                                ;

                            if (Acm.goodValue(node.data.version) == versionTag) {
                                $option.attr("selected", true);
                            }
                        }
                    }
                    $tdList.eq(6).replaceWith($td6);

                    $tdList.eq(7).text(node.data.status);

                    $tdList.eq(1).addClass("");

                } else {  //non file, non folder
                    $tdList.eq(0).text("");
                }
            }

//            ,createNode: function(event, data) {
//                var node = data.node;
////                if (DocTree.View.isFileNode(node) || DocTree.View.isFolderNode(node)) {
////                    $(node.tr).addClass("hasMenu");
////                }
////                if (node.folder) {
////                    var $tdEq3 = $(node.tr).find(">td:eq(3)");
////                    var $tdGt3 = $(node.tr).find(">td:gt(3)");
////                    $tdEq3.attr("colspan", 5);
////                    $tdGt3.remove();
////                }
//                var z = 1;
//            }
            ,renderNode: function(event, data) {
                var node = data.node;
                var acmIcon = null;
                var nodeType = Acm.goodValue(node.data.objectType);
                if (DocTree.Model.NODE_TYPE_PREV == nodeType) {
                    acmIcon = "<i class='i i-arrow-up'></i>" //"i-notice icon"
                } else if (DocTree.Model.NODE_TYPE_NEXT == nodeType) {
                    acmIcon = "<i class='i i-arrow-down'></i>";
                }
                if (acmIcon) {
                    var span = node.span;
                    var $spanIcon = $(span.children[1]);
                    $spanIcon.removeClass("fancytree-icon");
                    $spanIcon.html(acmIcon);
                }
            }
            ,click    : DocTree.View.onClick
            ,dblclick : DocTree.View.onDblClick
            ,keydown  : DocTree.View.Command.onKeyDown
            ,source   : DocTree.View.Source.source()
            ,lazyLoad : DocTree.View.Source.lazyLoad
            ,edit: {
                triggerStart: ["f2", "shift+click", "mac+enter"]
                ,beforeEdit: function(event, data){
                    if (DocTree.View.isTopNode(data.node) || DocTree.View.isSpecialNode(data.node)) {
                        return false;// Return false to prevent edit mode
                    }
                    if (data.node.isLoading()) {
                        return false;
                    }
                    DocTree.View.setEditing(true);
                    var z = 1;
                }
                ,edit: function(event, data){
                    data.input.select();
                    var z = 1;
                }
                ,beforeClose: function(event, data){
                    // Return false to prevent cancel/save (data.input is available)
                    var z = 1;
                }
                ,save: function(event, data){
                    var parent = data.node.getParent();
                    if (parent) {
                        var name = data.input.val();

                        if (DocTree.View.findSiblingNodeByName(data.node, name)) {
                            Acm.Dialog.alert($.t("doctree:error.duplicate-name"));
                            data.node.remove();
                            return false;
                        }

                        if (data.isNew) {
                            if (DocTree.View.isFolderNode(data.node)) {
                                 DocTree.View.Op.createFolder(data.node, name);
                            } else {
//                                DocTree.Controller.viewAddedDocument(data.node, parentId, name);
                            }

                        } else {

                            if (DocTree.View.isFolderNode(data.node)) {
                                DocTree.View.Op.renameFolder( data.node, name);
                            } else if (DocTree.View.isFileNode(data.node)) {
                                DocTree.View.Op.renameFile( data.node, name);
                            }
                        }
                    }


                    return true;        // We return true, so ext-edit will set the current user input as title
                }
                ,close: function(event, data){
                    // Editor was removed
                    if( data.save ) {
                        DocTree.View.markNodePending(data.node);
                    }
                    DocTree.View.setEditing(false);
                }
            }
            ,dnd: {
                //autoExpandMS: 400,
                autoExpandMS: 1600000,
                focusOnClick: true,
                preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                dragStart: function(node, data) {
                    if (DocTree.View.isTopNode(data.node) || DocTree.View.isSpecialNode(data.node)) {
                        return false;
                    }
                    if (DocTree.View.isEditing()) {
                        return false;
                    }
                    return true;
                },
                dragEnter: function(node, data) {
                    if(node == data.otherNode){
                        return ["before", "after"];     //Cannot drop to oneself
                    } else if (DocTree.View.isTopNode(data.node)) {
                        if(node == data.otherNode.parent){
                            return false;
                        } else {
                            return ["over"];
                        }
                    } else if(node == data.otherNode.parent){
                        return ["before", "after"];     //Drop over ones own parent doesn't make sense
                    } else if (DocTree.Model.NODE_TYPE_PREV == data.node.data.objectType) {
                        return ["after"];
                    } else if (DocTree.Model.NODE_TYPE_NEXT == data.node.data.objectType) {
                        return ["before"];
                    } else if (DocTree.View.isFolderNode(data.node)) {
                        return true;
                    } else {
                        return ["before", "after"];  // Don't allow dropping *over* a document node (would create a child)
                    }
                },
                dragDrop: function(node, data) {
                    if (("before" != data.hitMode && "after" != data.hitMode) && DocTree.View.isFolderNode(node)) {
                        DocTree.View.expandNode(node).done(function() {
                            if (DocTree.View.isFolderNode(data.otherNode)) {
                                DocTree.View.Op.moveFolder(data.otherNode, node, data.hitMode);
                            } else if (DocTree.View.isFileNode(data.otherNode)) {
                                DocTree.View.Op.moveFile(data.otherNode, node, data.hitMode);
                            }
                        });
                    } else {
                        if (DocTree.View.isFolderNode(data.otherNode)) {
                            DocTree.View.Op.moveFolder(data.otherNode, node, data.hitMode);
                        } else if (DocTree.View.isFileNode(data.otherNode)) {
                            DocTree.View.Op.moveFile(data.otherNode, node, data.hitMode);
                        }
                    }
                }
            }

        };
    }
    ,createDocTree: function(treeArgs) {
        var treeArgsToUse = this._getDefaultTreeArgs();
        for (var arg in treeArgs) {
            treeArgsToUse[arg] = treeArgs[arg];
        }

        var $tree = this.$tree;
        $tree.fancytree(treeArgsToUse)
            .on("command"   , DocTree.View.Command.onCommand)
            .on("mouseenter", ".fancytree-node", function(event){
                var node = $.ui.fancytree.getNode(event);
                if (node) {
                    if (DocTree.View.isSpecialNode(node)) {
                        //node.info(event.type + node.data.objectType);
                        DocTree.View.Paging.alertPaging(node);
                    }
                }
            })
            .on("mouseleave", ".fancytree-node", function(event){
                var node = $.ui.fancytree.getNode(event);
                if (node) {
                    if (DocTree.View.isSpecialNode(node)) {
                        //node.info(event.type + node.data.objectType);
                        DocTree.View.Paging.relievePaging();
                    }
                }
            })
        ;

        this.tree = $tree.fancytree("getTree");
        var $treeBody = $tree.find("tbody");
        DocTree.View.Menu.useContextMenu($treeBody, false);
        DocTree.View.ExternalDnd.useExternalDnd($treeBody);

        $treeBody.delegate("select.docversion", "change", DocTree.View.onChangeVersion);
        $treeBody.delegate("select.docversion", "dblclick", DocTree.View.onDblClickVersion);

        var $treeHead = $tree.find("thead");
        $treeHead.find("input:checkbox").on("click", function(e) {DocTree.View.onClickBtnChkAllDocument(e, this);});

    }
    
    ,refreshDocTree: function() {
    	var $tree = this.$tree;
    	var $treeBody = $tree.find("tbody");
    	DocTree.View.Menu.useContextMenu($treeBody, true);
    }

    ,ExternalDnd: {
        useExternalDnd: function($treeBody) {
            //this._borderSave = $treeBody.find(">tr")[0].css("border");
//            var c1 = $treeBody;
//            var c2 = $treeBody.find(">tr");
//            var c3 = $treeBody.find(">tr")[0];
//            var c4 = c3.css("border");
//            this._borderSave = $treeBody.find(">tr")[0].css("border");

            $treeBody.delegate("tr", "dragenter", this.onDragEnter);
            $treeBody.delegate("tr", "dragleave", this.onDragLeave);
            $treeBody.delegate("tr", "dragover", this.onDragOver);
            $treeBody.delegate("tr", "drop", this.onDragDrop);

            $(document).on('dragenter', function (e) {
                e.stopPropagation();
                e.preventDefault();
            });
            $(document).on('dragover', function (e) {
                e.stopPropagation();
                e.preventDefault();
            });
            $(document).on('drop', function (e) {
                e.stopPropagation();
                e.preventDefault();
            });
        }

        ,onDragEnter: function(e) {
            e.stopPropagation();
            e.preventDefault();
            $(this).addClass("dragover");
        }
        ,onDragOver: function(e) {
            e.stopPropagation();
            e.preventDefault();
            $(this).addClass("dragover");
        }
        ,onDragLeave: function(e) {
            e.stopPropagation();
            e.preventDefault();
            $(this).removeClass("dragover");
        }
        ,onDragDrop: function(e) {
            //e.stopPropagation();
            e.preventDefault();
            $(this).removeClass("dragover");

            var node = $.ui.fancytree.getNode(e);
            var files = e.originalEvent.dataTransfer.files;
            if (files instanceof FileList) {
                if (DocTree.View.isFolderNode(node)) {
                    DocTree.View.DialogDnd.showIfDropToFolderNode(function() {
                        //var replace = DocTree.View.DialogDnd.isCheckedRadReplace();
                        //var toParent = DocTree.View.DialogDnd.isCheckedRadUploadToParent();
                        var toFolder = DocTree.View.DialogDnd.isCheckedRadUploadToFolder();
                        var fileType = DocTree.View.DialogDnd.getValueSelFileType();
                        if (toFolder && Acm.isNotEmpty(fileType)) {
                            DocTree.View.uploadToFolderNode = node;
                            DocTree.View.uploadFileType = fileType;
                            DocTree.View.uploadFileNew = true;
                            DocTree.View.doSubmitFormUploadFile(files);
                        }
                    });

                } else if (DocTree.View.isFileNode(node)) {
                    DocTree.View.DialogDnd.showIfDropToFileNode(function() {
                        var replace = DocTree.View.DialogDnd.isCheckedRadReplace();
                        var toParent = DocTree.View.DialogDnd.isCheckedRadUploadToParent();
                        //var toFolder = DocTree.View.DialogDnd.isCheckedRadUploadToFolder();
                        var fileType = DocTree.View.DialogDnd.getValueSelFileType();
                        if (replace) {
                            DocTree.View.replaceFileNode = node;
                            DocTree.View.uploadToFolderNode = node.parent;
                            DocTree.View.uploadFileType = Acm.goodValue(node.data.type);
                            DocTree.View.uploadFileNew = false;
                            DocTree.View.doSubmitFormUploadFile(files);

                        } else if (toParent && Acm.isNotEmpty(fileType)) {
                            DocTree.View.uploadToFolderNode = node.parent;
                            DocTree.View.uploadFileType = fileType;
                            DocTree.View.uploadFileNew = true;
                            DocTree.View.doSubmitFormUploadFile(files);
                        }
                    });
                }
            }

        }
    }

    ,Menu: {
        useContextMenu: function($s, refresh) {
            if (!this.docSubMenu || refresh) {
                this.docSubMenu = this.makeDocSubMenu(DocTree.View.fileTypes);
            }

            $s.contextmenu({
                menu: []
                //,delegate: "span.fancytree-node"
                ,delegate: "tr"
                ,beforeOpen: function(event, ui) {
                    var selNodes = DocTree.View.getSelectedNodes();
                    if (!Acm.isArrayEmpty(selNodes)) {
                        $s.contextmenu("replaceMenu", DocTree.View.Menu.getBatchMenu(selNodes));
                        return true;
                    }

                    var node = $.ui.fancytree.getNode(ui.target);
                    if (DocTree.View.isSpecialNode(node)) {
                        return false;
                    }
                    $s.contextmenu("replaceMenu", DocTree.View.Menu.getContextMenu(node));
                    $s.contextmenu("enableEntry", "paste", !!DocTree.View.CLIPBOARD);
                    node.setActive();
                }
                ,select: function(event, ui) {
                    // delay the event, so the menu can close and the click event does
                    // not interfere with the edit control
                    var that = this;
                    setTimeout(function(){
                        $(that).trigger("command", {cmd: ui.cmd});
                    }, 100);
                }
            });
        }

        ,getBatchMenu: function(nodes) {
            var menu = [{title: $.t("doctree:menu.title-no-op"), cmd: "noop", uiIcon: "" }];
            if (DocTree.View.validateNodes(nodes)) {
                var countFolder = 0;
                var countFile = 0;
                for (var i = 0; i < nodes.length; i++) {
                    if (DocTree.View.isFolderNode(nodes[i])) {
                        countFolder++;
                    } else if (DocTree.View.isFileNode(nodes[i])) {
                        countFile++;
                    }
                }

                if (0 < countFile && 0 >= countFolder) {              //file only menu
                    menu = [{title: $.t("doctree:menu.title-email"),  cmd: "email", uiIcon: "ui-icon-mail-closed" }
                        ,{title: $.t("doctree:menu.title-print"),     cmd: "print", uiIcon: "ui-icon-print" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),       cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),      cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-delete"),    cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                } else if (0 >= countFile || 0 < countFolder) {       //folder only menu
                    menu = [{title: $.t("doctree:menu.title-cut"),    cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),      cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-delete"),    cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                } else if (0 < countFile || 0 < countFolder) {        //mix file and folder menu
                    menu = [{title: $.t("doctree:menu.title-cut"),    cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),      cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-delete"),    cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                }
            }
            return menu;
        }
        ,getContextMenu: function(node) {
            var menu = [{title: $.t("doctree:menu.title-no-op"), cmd: "noop", uiIcon: "" }];
            if (node) {
                if (DocTree.View.isTopNode(node)) {
                    menu = [{title: $.t("doctree:menu.title-new-folder"), cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        ,{title: $.t("doctree:menu.title-new-file"),      children: DocTree.View.Menu.docSubMenu}
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                    ];
                } else if (DocTree.View.isFolderNode(node)) {
                    menu = [{title: $.t("doctree:menu.title-new-folder"), cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        ,{title: $.t("doctree:menu.title-new-file"),      children: DocTree.View.Menu.docSubMenu}
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),           cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),          cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-rename"),        cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-delete"),        cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                } else if (DocTree.View.isFileNode(node)) {
                    menu = [{title: $.t("doctree:menu.title-open"),       cmd: "open", uiIcon: "ui-icon-folder-open" }
                        ,{title: $.t("doctree:menu.title-edit"),          cmd: "edit", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-email"),         cmd: "email", uiIcon: "ui-icon-mail-closed" }
                        ,{title: $.t("doctree:menu.title-print"),         cmd: "print", uiIcon: "ui-icon-print" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-cut"),           cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: $.t("doctree:menu.title-copy"),          cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: $.t("doctree:menu.title-paste"),         cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-rename"),        cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: $.t("doctree:menu.title-delete"),        cmd: "remove", uiIcon: "ui-icon-trash" }
                        ,{title: $.t("doctree:menu.title-separator") }
                        ,{title: $.t("doctree:menu.title-download"),      cmd: "download", uiIcon: "ui-icon-arrowthickstop-1-s" }
                        ,{title: $.t("doctree:menu.title-replace"),       cmd: "replace", uiIcon: "" }
                    ];
                }
            }
            return menu;
        }


        // To create a menu like this:
        //        var menu = [
        //            {title: "Electronic Communication", cmd: "form/electronicCommunicationFormUrl"}
        //            ,{title: "Report of Investigation", cmd: "form/roiFormUrl"}
        //            ,{title: "Medical Release", cmd: "file/mr"}
        //            ,{title: "General Release", cmd: "file/gr"}
        //            ,{title: "eDelivery", cmd: "file/ev"}
        //            ,{title: "SF86 Signature", cmd: "file/sig"}
        //            ,{title: "Notice of Investigation", cmd: "file/noi"}
        //            ,{title: "Witness Interview Request", cmd: "file/wir"}
        //            ,{title: "Other", cmd: "file/other"}
        //        ];
        ,makeDocSubMenu: function(fileTypes) {
            var menu = [], item;
            if (Acm.isArray(fileTypes)) {
                for (var i = 0; i < fileTypes.length; i++) {
                    item = {};
                    if (Acm.isNotEmpty(fileTypes[i].label) && Acm.isNotEmpty(fileTypes[i].type)) {
                        item.title = fileTypes[i].label;
                        if (Acm.isNotEmpty(fileTypes[i].form)) {
                            item.cmd = "form/" + fileTypes[i].type;
                        } else {
                            item.cmd = "file/" + fileTypes[i].type;
                        }
                    }
                    menu.push(item);
                }
            }
            return menu;
        }
    }

    ,Paging: {
        _triggerNode: null
        ,alertPaging: function(node) {
            DocTree.View.Paging._triggerNode = node;
            setTimeout(function(){
                var node = DocTree.View.Paging._triggerNode;
                DocTree.View.Paging.doPaging(node);
            }, 2500);
        }
        ,relievePaging: function() {
            DocTree.View.Paging._triggerNode = null;
        }
        ,doPaging: function(node) {
            if (!node) {
                return;
            }
            var parent = node.getParent();
            if (!parent) {
                return;
            }

            if (DocTree.Model.NODE_TYPE_PREV == node.data.objectType) {
                var startRow = Acm.goodValue(parent.data.startRow, 0) - Acm.goodValue(parent.data.maxRows, DocTree.Model.Config.getMaxRows());
                if (0 > startRow) {
                    startRow = 0;
                }
                parent.data.startRow = startRow;
                parent.resetLazy();
                parent.setExpanded(true);
            } else if (DocTree.Model.NODE_TYPE_NEXT == node.data.objectType) {
                var startRow = Acm.goodValue(parent.data.startRow, 0) + Acm.goodValue(parent.data.maxRows, DocTree.Model.Config.getMaxRows());
                var totalChildren = Acm.goodValue(parent.data.totalChildren, -1);
                if (0 <= totalChildren) {   // -1 is a special value for unknown totalChildren; keep increasing in this case
                    if (totalChildren <= startRow) {
                        startRow = totalChildren - 1;
                        if (0 > startRow) {
                            startRow = 0;
                        }
                    }
                }
                parent.data.startRow = startRow;
                parent.resetLazy();
                parent.setExpanded(true);
            }
        }
    }

    ,Source: {
        source: function() {
            var src = [];
            var containerObjectType = (this.parentType)? this.parentType : DocTree.Model.getObjType();
            var containerObjectId   = (this.parentId)  ? this.parentId   : DocTree.Model.getObjId();
            if (Acm.isNotEmpty(containerObjectType) && Acm.isNotEmpty(containerObjectId)) {
                src = AcmEx.FancyTreeBuilder
                    .reset()
                    .addBranchLast({key: containerObjectType + "." + containerObjectId
                        //,title          : containerObjectType + " (" + containerObjectId + ") /"
                        ,title          : "/"
                        ,tooltip        : "root"
                        ,expanded: false
                        ,folder: true
                        ,lazy: true
                        ,cache: false
                        ,objectId: 0
                        ,root: true
                        ,startRow: 0
                        //,folderId: 0
                        ,containerObjectType: containerObjectType
                        ,containerObjectId: containerObjectId
                        ,totalChildren: -1
                        //,"action": DocTree.View.Source.getHtmlAction()
                    })
                    .getTree();
            }
            return src;
        }
        ,getHtmlDocLink: function(node) {
            var $div = $("<div/>").addClass("btn-group");
            var itemId = node.data.objectId;
            if (itemId) {
                var url = "#";
                if (DocTree.View.isFileNode(node)) {
                    url = App.getContextPath() + "/plugin/document/" + itemId;
                }
                var $a = $("<a/>")
                    .attr("href", url)
                    .text(itemId)
                    .appendTo($div);
            }
            return $div;
        }
//        ,getHtmlDocLink_html: function(node) {
//            var html = "<div></div>";
//            var itemId = node.data.objectId
//            if (itemId) {
//                var url = "#";
//                if (DocTree.View.isFileNode(node)) {
//                    url = App.getContextPath() + "/plugin/document/" + itemId;
//                }
//                html = "<div class='btn-group'><a href='" + url + "'>" + itemId + "</a></div>";
//            }
//            return html;
//        }
//        ,getHtmlAction: function() {
//            return "<div class='btn-group'><button type='button'> <i class='fa fa-cog'></i> </button></div>";
//        }

        ,_makeChildNodes: function(folderList) {
            var builder = AcmEx.FancyTreeBuilder.reset();
            if (DocTree.Model.validateFolderList(folderList)) {
                var  startRow = Acm.goodValue(folderList.startRow, 0);
                var  maxRows = Acm.goodValue(folderList.maxRows, 0);
                var  totalChildren = Acm.goodValue(folderList.totalChildren, -1);
                var  folderId = Acm.goodValue(folderList.folderId, 0);

                if (0 < startRow) {
                    builder.addLeaf({key: folderId + ".prev"
                        ,title: startRow + $.t("doctree:tree.prev-items")
                        ,tooltip: $.t("doctree:tree.tooltip-prev-items")
                        ,expanded: false
                        ,folder: false
                        ,objectType: DocTree.Model.NODE_TYPE_PREV
                    });
                }

                for (var i = 0; i < folderList.children.length; i++) {
                    var child = folderList.children[i];
                    if (DocTree.Model.NODE_TYPE_FOLDER == Acm.goodValue(child.objectType)) {
                        var nodeData = DocTree.View.Source.getDefaultFolderNode();
                        DocTree.View._folderDataToNodeData(child, nodeData);
//                        nodeData.lazy = true;
//                        nodeData.expanded = false;
//                        nodeData.cache = false;
//                        nodeData.startRow = 0;
//                        nodeData.totalChildren = -1;
                        builder.addLeaf(nodeData);

                    } if (DocTree.Model.NODE_TYPE_FILE == Acm.goodValue(child.objectType)) {
                        var nodeData = {};
                        DocTree.View._fileDataToNodeData(child, nodeData);
                        nodeData.folder = false;
                        //nodeData.action = DocTree.View.Source.getHtmlAction();
                        builder.addLeaf(nodeData);
                    }
                }

                if ((0 > totalChildren) || (totalChildren - maxRows > startRow)) {//unknown size or more page
                    var title = (0 > totalChildren)? $.t("doctree:tree.more-items-begin") : (totalChildren - startRow - maxRows) + $.t("doctree:tree.more-items");
                    builder.addLeafLast({key: Acm.goodValue(folderId, 0) + ".next"
                        ,title: title
                        ,tooltip: $.t("doctree:tree.tooltip-more-items")
                        ,expanded: false
                        ,folder: false
                        ,objectType: DocTree.Model.NODE_TYPE_NEXT
                    });
                }
            }
            return builder.getTree();
        }
        ,getDefaultFolderNode: function() {
            var nodeData = {};
            nodeData.expanded = false;
            nodeData.folder = true;
            nodeData.lazy = true;
            nodeData.cache = false;
            nodeData.totalChildren = -1;
            nodeData.children = [];
            //nodeData.action = DocTree.View.Source.getHtmlAction();
            return nodeData;
        }
        ,lazyLoad: function(event, data) {
            var folderNode = data.node;
            var folderId = Acm.goodValue(folderNode.data.objectId, 0);
            if (0 >= folderId && !DocTree.View.isTopNode(folderNode)) {
                data.result = [];
                return;
            }

            var cacheKey = DocTree.View.getCacheKey(folderNode);
            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
            if (DocTree.Model.validateFolderList(folderList)) {
                data.result = DocTree.View.Source._makeChildNodes(folderList);

            } else {
                data.result = DocTree.View.Op.retrieveFolderList(folderNode
                    ,function(folderList) {
                        folderNode.data.startRow = Acm.goodValue(folderList.startRow, 0);
                        folderNode.data.totalChildren = Acm.goodValue(folderList.totalChildren, -1);
                        var rc = DocTree.View.Source._makeChildNodes(folderList);
                        return rc;
                    }
                );
            }
        }
    }

    ,Command: {
        onCommand: function(event, data){
            var refNode;
            var moveMode;
            //var tree = $(this).fancytree("getTree");
            var tree = DocTree.View.tree;
            var selNodes = tree.getSelectedNodes();
            var node = tree.getActiveNode();
            var batch = !Acm.isArrayEmpty(selNodes);
            if (batch) {
                if (!DocTree.View.validateNodes(selNodes)) {
                    return;
                }
            } else if (!DocTree.View.validateNode(node)) {
                return;
            }


            if (0 == data.cmd.indexOf("form/")) {
                var fileType =  data.cmd.substring(5);
                DocTree.View.uploadForm(node, fileType);
                return;
            }
            if (0 == data.cmd.indexOf("file/")) {
                var fileType =  data.cmd.substring(5);
                DocTree.View.uploadFile(node, fileType);
                return;
            }
            switch( data.cmd ) {
                case "moveUp":
                    refNode = node.getPrevSibling();
                    if( refNode ) {
                        node.moveTo(refNode, "before");
                        node.setActive();
                    }
                    break;
                case "moveDown":
                    refNode = node.getNextSibling();
                    if( refNode ) {
                        node.moveTo(refNode, "after");
                        node.setActive();
                    }
                    break;
                case "indent":
                    refNode = node.getPrevSibling();
                    if( refNode ) {
                        node.moveTo(refNode, "child");
                        refNode.setExpanded();
                        node.setActive();
                    }
                    break;
                case "outdent":
                    if( !node.isTopLevel() ) {
                        node.moveTo(node.getParent(), "after");
                        node.setActive();
                    }
                    break;
                case "rename":
                    node.editStart();
                    break;
                case "remove":
                    var nodes = (batch)? selNodes : [node];
                    DocTree.View.Op.batchRemove(nodes);
                    break;
                case "addChild":
                    node.editCreateNode("child", "");
                    break;
                case "addSibling":
                    node.editCreateNode("after", "");
                    break;
                case "newFolder":
                    if (!DocTree.View.isEditing()) {
                        //node.editCreateNode("child", "New Folder");
                        var nodeData = DocTree.View.Source.getDefaultFolderNode();
                        nodeData.title = "New Folder";
                        if (DocTree.View.isFileNode(node)) {
                            node = node.getParent();
                        }
                        node.editCreateNode("child", nodeData);
                    }
                    break;
                case "newDocument":
                    if (!DocTree.View.isEditing()) {
                        DocTree.View.uploadFile(node);
                    }
                    break;

                case "cut":
                    var nodes = (batch)? selNodes : [node];
                    if (batch) {
                        DocTree.View.checkNodes(nodes, false);
                    }
                    DocTree.View.CLIPBOARD = {mode: data.cmd, batch: batch, data: nodes};
                    break;
                case "copy":
                    var nodes = (batch)? selNodes : [node];
                    if (batch) {
                        DocTree.View.checkNodes(nodes, false);
                    }
                    var clones = [];
                    for (var i = 0; i < nodes.length; i++) {
                        var clone = nodes[i].toDict(false, function(n){
                            delete n.key;
                        });
                        clones.push(clone);
                    }
                    DocTree.View.CLIPBOARD = {mode: data.cmd, batch: batch, data: clones, src: nodes};
                    break;
                case "clear":
                    DocTree.View.CLIPBOARD = null;
                    break;
                case "paste":
                    DocTree.View.expandNode(node).done(function() {
                        var mode =  DocTree.View.isFolderNode(node)? "child" : "after";
                        if( DocTree.View.CLIPBOARD.mode === "cut" ) {
                            DocTree.View.Op.batchMove(DocTree.View.CLIPBOARD.data, node, mode);
                        } else if( DocTree.View.CLIPBOARD.mode === "copy" ) {
                            DocTree.View.Op.batchCopy(DocTree.View.CLIPBOARD.src, DocTree.View.CLIPBOARD.data, node, mode);
                        }
                    });
                    break;
                case "download":
                    DocTree.View._doDownload(node);
                    break;
                case "replace":
                    DocTree.View.replaceFile(node);
                    break;
                case "open":
                    var url = App.getContextPath() + "/plugin/document/" + node.data.objectId;
                    window.open(url);
                    break;
                case "edit":
                    break;
                case "email":
                    if(batch){
                        DocTree.View.Email.showEmailDialog(selNodes);
                    }
                    else{
                        DocTree.View.Email.showEmailDialog(node);
                    }
                    break;
                case "print":
                    break;
                default:
                    Acm.log("Unhandled command: " + data.cmd);
                    return;
            }
        }
        ,onKeyDown: function(event, data){
            var cmd = null;

            // console.log(event.type, $.ui.fancytree.eventToString(event));
            switch( $.ui.fancytree.eventToString(event) ) {
                case "ctrl+shift+n":
                case "meta+shift+n": // mac: cmd+shift+n
                    cmd = "addChild";
                    break;
                case "ctrl+c":
                case "meta+c": // mac
                    cmd = "copy";
                    break;
                case "ctrl+v":
                case "meta+v": // mac
                    cmd = "paste";
                    break;
                case "ctrl+x":
                case "meta+x": // mac
                    cmd = "cut";
                    break;
                case "ctrl+n":
                case "meta+n": // mac
                    cmd = "addSibling";
                    break;
                case "del":
                case "meta+backspace": // mac
                    cmd = "remove";
                    break;
                // case "f2":  // already triggered by ext-edit pluging
                //   cmd = "rename";
                //   break;
                case "ctrl+up":
                    cmd = "moveUp";
                    break;
                case "ctrl+down":
                    cmd = "moveDown";
                    break;
                case "ctrl+right":
                case "ctrl+shift+right": // mac
                    cmd = "indent";
                    break;
                case "ctrl+left":
                case "ctrl+shift+left": // mac
                    cmd = "outdent";
                case "ctrl+p":
                case "meta+p": // mac
                    cmd = "print";
                    break;
            }
            if( cmd ){
                $(this).trigger("command", {cmd: cmd});
                // event.preventDefault();
                // event.stopPropagation();
                return false;
            }
        }
        ,getCommandObject: function(cmd) {
            return {cmd: cmd};
        }
        ,trigger: function(cmd) {
            DocTree.View.$tree.trigger("command", {cmd: cmd});
        }
    }

    ,Dnd: {

    }

    ,Op: {
        retrieveFolderList: function(folderNode, callbackSuccess) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFolderNode(folderNode)) {
                $dfd.reject();

            } else {
                var pageId = Acm.goodValue(folderNode.data.startRow, 0);
                var folderId = Acm.goodValue(folderNode.data.objectId, 0);
                if (DocTree.View.isTopNode(folderNode)) {
                    folderId = 0;
                }
                DocTree.Model.retrieveFolderList(pageId, folderId)
                    .done(function(folderList) {
                        folderNode.data.objectId = folderList.folderId;
                        folderNode.data.totalChildren = folderList.totalChildren;
                        folderNode.renderTitle();
                        DocTree.View.markNodeOk(folderNode);
                        var rc = callbackSuccess(folderList);
                        $dfd.resolve(rc);
                    })
                    .fail(function(response) {
                        App.View.MessageBoard.show($.t("doctree:error.retrieve-folder-list"), Acm.goodValue(response.errorMsg));
                        DocTree.View.markNodeError(folderNode);
                        $dfd.reject();
                    })
                ;

            }

            return $dfd.promise();
        }
        ,createFolder: function(newNode, folderName) {
            var $dfd = $.Deferred();
            var parent = newNode.getParent();
            if (!DocTree.View.isFolderNode(parent)) {
                $dfd.reject();

            } else {
                if (!newNode) {
                    var nodeData = DocTree.View.Source.getDefaultFolderNode();
                    nodeData.title = folderName;
                    newNode = parent.addChildren(nodeData);
                    newNode.setActive();
                    DocTree.View.markNodePending(newNode);
                }

                var cacheKey = DocTree.View.getCacheKey(parent);
                var parentId = parent.data.objectId;

                DocTree.Model.createFolder(parentId, folderName, cacheKey)
                    .done(function(createdFolder){
                        DocTree.View._folderDataToNodeData(createdFolder, newNode);
                        DocTree.View.markNodeOk(newNode);
                        newNode.renderTitle();
                        $dfd.resolve(newNode);
                    })
                    .fail(function(response){
                        App.View.MessageBoard.show($.t("doctree:error.create-folder"), Acm.goodValue(response.errorMsg));
                        DocTree.View.markNodeError(newNode);
                        $dfd.reject();
                    });
            }
            return $dfd.promise();
        }
        ,uploadFiles: function(formData, folderNode, names, fileType) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFolderNode(folderNode)) {
                $dfd.reject();

            } else {
                var promiseAddNodes = DocTree.View._addingFileNodes(folderNode, names, fileType);

                var cacheKey = DocTree.View.getCacheKey(folderNode);
                var promiseUploadFiles = DocTree.Model.uploadFiles(formData, cacheKey)
                    .fail(function(response) {
                        App.View.MessageBoard.show($.t("doctree:error.upload-files"), Acm.goodValue(response.errorMsg));
                        $dfd.reject();
                    });

                $.when(promiseUploadFiles, promiseAddNodes).done(function(uploadedFiles, fileNodes){
                    if (!Acm.isArrayEmpty(uploadedFiles) && DocTree.View.validateFancyTreeNodes(fileNodes)) {
                        for (var i = 0; i < uploadedFiles.length; i++) {
                            var uploadedFile = uploadedFiles[i];
                            var type = Acm.goodValue(uploadedFile.type);
                            var name = Acm.goodValue(uploadedFile.name);
                            var fileNode = DocTree.View._matchFileNode(type, name, fileNodes);
                            if (fileNode) {
                                DocTree.View._fileDataToNodeData(uploadedFile, fileNode);
                                fileNode.renderTitle();
                                fileNode.setStatus("ok");
                            }
                        } //end for
                        $dfd.resolve();
                    }
                });
            }
            return $dfd.promise();
        }
        ,replaceFile: function(formData, fileNode, name) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFileNode(fileNode)) {
                $dfd.reject();

            } else {
                DocTree.View.markNodePending(fileNode);

                var folderNode = fileNode.getParent();
                var cacheKey = DocTree.View.getCacheKey(folderNode);
                DocTree.Model.replaceFile(formData, fileNode.data.objectId, cacheKey)
                    .done(function(replacedFile) {
                        if (replacedFile && fileNode) {
                            fileNode.data.version = replacedFile.version;
                            fileNode.data.versionList = replacedFile.versionList;
                            fileNode.renderTitle();
                            fileNode.setStatus("ok");
                        }
                        $dfd.resolve();
                    })
                    .fail(function(response) {
                        App.View.MessageBoard.show($.t("doctree:error.replace-file"), Acm.goodValue(response.errorMsg));
                        DocTree.View.markNodeError(fileNode);
                        $dfd.reject();
                    })
                ;
            }
            return $dfd.promise();
        }
        ,copyFolder: function(srcNode, frNode, toNode, mode) {
            var $dfd = $.Deferred();

            //var toFolderNode = DocTree.View.isFolderNode(toNode)? toNode : toNode.parent;
            var toFolderNode = toNode;
            if (DocTree.View.isFileNode(toNode) || "after" == mode || "before" == mode) {
                toFolderNode = toNode.parent;
            }

            if (!toFolderNode) {
                $dfd.reject();

            } else if (!DocTree.View.isFolderNode(srcNode)) {
                $dfd.reject();

            } else {
                var newNode = null;
                if (DocTree.View.isFolderNode(toNode)) {
                    newNode = toNode.addChildren(frNode);
                } else {
                    //toNode = node.addNode(frNode, "after")
                    newNode = toNode.addNode(frNode, mode)
                }
                newNode.setActive();

//todo: copy to same parent, need to rename a "fn" to "fn (n)"
//                if (frNode.parent == toFolderNode) {
//                    //copy to another folder name
//
//                } else {}

                DocTree.View.markNodePending(newNode);
                var toFolderId = toFolderNode.data.objectId;
                var toCacheKey = DocTree.View.getCacheKey(toFolderNode);
                var frCacheKey = DocTree.View.getCacheKey(srcNode.parent);
                DocTree.Model.copyFolder(frNode.data.objectId, toFolderId, frCacheKey, toCacheKey)
                    .done(function(copyFolderInfo) {
                        DocTree.View._folderDataToNodeData(copyFolderInfo, newNode);
                        DocTree.View.markNodeOk(newNode);
                        newNode.setExpanded(false);
                        newNode.resetLazy();
                        newNode.renderTitle();
                        $dfd.resolve(copyFolderInfo);
                    })
                    .fail(function(data) {
                        App.View.MessageBoard.show($.t("doctree:error.copy-folder"), Acm.goodValue(data.errorMsg));
                        DocTree.View.markNodeError(newNode);
                        $dfd.reject();
                    })
                ;
            }
            return $dfd.promise();

        }
        ,copyFile: function(srcNode, frNode, toNode, mode) {
            var $dfd = $.Deferred();

            //var toFolderNode = DocTree.View.isFolderNode(toNode)? toNode : toNode.parent;
            var toFolderNode = toNode;
            if (DocTree.View.isFileNode(toNode) || "after" == mode || "before" == mode) {
                toFolderNode = toNode.parent;
            }

            if (!toFolderNode) {
                $dfd.reject();

            } else if (!DocTree.View.isFileNode(srcNode)) {
                $dfd.reject();

            } else {
                var newNode = null;
                if (DocTree.View.isFolderNode(toNode)) {
                    newNode = toNode.addChildren(frNode);
                } else {
                    //toNode = node.addNode(frNode, "after")
                    newNode = toNode.addNode(frNode, mode)
                }
                newNode.setActive();

//todo: copy to same parent, need to rename a "fn" to "fn (n)"
//                if (frNode.parent == toFolderNode) {
//                    //copy to another folder name
//
//                } else {}

                DocTree.View.markNodePending(newNode);
                var toFolderId = toFolderNode.data.objectId;
                var toCacheKey = DocTree.View.getCacheKey(toFolderNode);
                var frCacheKey = DocTree.View.getCacheKey(srcNode.parent);
                DocTree.Model.copyFile(frNode.data.objectId, toFolderId, toCacheKey)
                    .done(function(copyFileInfo) {
                        DocTree.View._fileDataToNodeData(copyFileInfo, newNode);
                        DocTree.View.markNodeOk(newNode);
                        newNode.renderTitle();
                        $dfd.resolve(copyFileInfo);
                    })
                    .fail(function(response) {
                        App.View.MessageBoard.show($.t("doctree:error.copy-file"), Acm.goodValue(response.errorMsg));
                        DocTree.View.markNodeError(newNode);
                        $dfd.reject();
                    });
            }
            return $dfd.promise();
        }
        ,_findNodesById: function(id, inNodes) {
            var find = null;
            for (var i = 0; i < inNodes.length; i++) {
                if (inNodes[i].data.objectId == id) {
                    find = inNodes[i];
                    break;
                }
            }
            return find;
        }
        ,batchCopy: function(srcNodes, frNodes, toNode, mode) {
            var $dfd = $.Deferred();
            if (Acm.isArrayEmpty(srcNodes) || Acm.isArrayEmpty(frNodes)) {
                $dfd.resolve();

            } else {
                var srcNodesToCopy = DocTree.View.getTopMostNodes(srcNodes);
                var frNodesToCopy = [];
                for (var i = 0; i < srcNodesToCopy.length; i++) {
                    var find = DocTree.View.Op._findNodesById(srcNodesToCopy[i].data.objectId, frNodes);
                    frNodesToCopy.push(find);
                }

                var requests = [];
                for (var i = 0; i < srcNodesToCopy.length; i++) {
                    if (DocTree.View.isFolderNode(srcNodesToCopy[i])) {
                        requests.push(DocTree.View.Op.copyFolder(srcNodesToCopy[i], frNodesToCopy[i], toNode, mode));
                    } else if (DocTree.View.isFileNode(srcNodesToCopy[i])) {
                        requests.push(DocTree.View.Op.copyFile(srcNodesToCopy[i], frNodesToCopy[i], toNode, mode));
                    }
                }

                Acm.Promise.resolvePromises(requests)
                    .done(function() {
                        if (DocTree.View.CLIPBOARD && DocTree.View.CLIPBOARD.src && DocTree.View.CLIPBOARD.batch) {
                            DocTree.View.checkNodes(DocTree.View.CLIPBOARD.src, true);
                        }
                        $dfd.resolve();
                    })
                    .fail(function() {
                        $dfd.reject();
                    });
            }
            return $dfd.promise();
        }
        ,moveFolder: function(frNode, toNode, mode) {
            var $dfd = $.Deferred();
            //var toFolderNode = DocTree.View.isFolderNode(toNode)? toNode : toNode.parent;
            var toFolderNode = toNode;
            if (DocTree.View.isFileNode(toNode) || "after" == mode || "before" == mode) {
                toFolderNode = toNode.parent;
            }

            if (!toFolderNode) {
                $dfd.reject();

            } else if (!DocTree.View.isFolderNode(frNode)) {
                $dfd.reject();

            } else if (frNode.parent == toFolderNode) {
                frNode.moveTo(toNode, mode);
                frNode.setActive();
                $dfd.resolve();

            } else if ((frNode.parent == toFolderNode.parent) && ("before" == mode || "after" == mode)) {
                frNode.moveTo(toNode, mode);
                frNode.setActive();
                $dfd.resolve();

            } else {
                var toFolderId = toFolderNode.data.objectId;
                var toCacheKey = DocTree.View.getCacheKey(toFolderNode);

                var frFolderNode = frNode.parent;
                var frFolderId = frFolderNode.data.objectId;
                var frCacheKey = DocTree.View.getCacheKey(frFolderNode);

                frNode.moveTo(toNode, mode);
                frNode.setActive();

                DocTree.View.markNodePending(frNode);
                DocTree.Model.moveFolder(frNode.data.objectId, toFolderId, frCacheKey, toCacheKey)
                    .done(function(moveFolderInfo) {
                        DocTree.View.markNodeOk(frNode);
                        $dfd.resolve(moveFolderInfo);
                    })
                    .fail(function(response) {
                        App.View.MessageBoard.show($.t("doctree:error.move-folder"), Acm.goodValue(response.errorMsg));
                        DocTree.View.markNodeError(frNode);
                        $dfd.reject();
                    })
                ;
            }
            return $dfd.promise();
        }
        ,moveFile: function(frNode, toNode, mode) {
            var $dfd = $.Deferred();
            //var toFolderNode = DocTree.View.isFolderNode(toNode)? toNode : toNode.parent;
            var toFolderNode = toNode;
            if (DocTree.View.isFileNode(toNode) || "after" == mode || "before" == mode) {
                toFolderNode = toNode.parent;
            }

            if (!toFolderNode) {
                $dfd.reject();

            } else if (!DocTree.View.isFileNode(frNode)) {
                $dfd.reject();

            } else if (frNode.parent == toFolderNode) {
                    frNode.moveTo(toNode, mode);
                    frNode.setActive();
                    $dfd.resolve();

            } else {
                var toFolderId = toFolderNode.data.objectId;
                var toCacheKey = DocTree.View.getCacheKey(toFolderNode);

                var frFolderNode = frNode.parent;
                //var frFolderId = frFolderNode.data.objectId;
                var frCacheKey = DocTree.View.getCacheKey(frFolderNode);

                frNode.moveTo(toNode, mode);
                frNode.setActive();

                DocTree.View.markNodePending(frNode);
                DocTree.Model.moveFile(frNode.data.objectId, toFolderId, frCacheKey, toCacheKey)
                    .done(function(moveFileInfo) {
                        DocTree.View.markNodeOk(frNode);
//                            if (DocTree.View.CLIPBOARD && DocTree.View.CLIPBOARD.data && DocTree.View.CLIPBOARD.batch) {
//                                DocTree.View.checkNodes(DocTree.View.CLIPBOARD.data, true);
//                            }
                        $dfd.resolve(moveFileInfo);
                    })
                    .fail(function(response) {
                        App.View.MessageBoard.show($.t("doctree:error.move-file"), Acm.goodValue(response.errorMsg));
                        DocTree.View.markNodeError(frNode);
                        $dfd.reject();
                    })
                ;
            }

            return $dfd.promise();
        }
        ,batchMove: function(frNodes, toNode, mode) {
            var $dfd = $.Deferred();
            if (Acm.isArrayEmpty(frNodes)) {
                $dfd.resolve();

            } else {
                var moveNodes = DocTree.View.getTopMostNodes(frNodes);
                var requests = [];
                for (var i = 0; i < moveNodes.length; i++) {
                    if (DocTree.View.isFolderNode(moveNodes[i])) {
                        requests.push(DocTree.View.Op.moveFolder(moveNodes[i], toNode, mode));
                    } else if (DocTree.View.isFileNode(moveNodes[i])) {
                        requests.push(DocTree.View.Op.moveFile(moveNodes[i], toNode, mode));
                    }
                }

                Acm.Promise.resolvePromises(requests)
                    .done(function() {
                        if (DocTree.View.CLIPBOARD && DocTree.View.CLIPBOARD.data && DocTree.View.CLIPBOARD.batch) {
                            DocTree.View.checkNodes(DocTree.View.CLIPBOARD.data, true);
                        }
                        $dfd.resolve();
                    })
                    .fail(function() {
                        $dfd.reject();
                    });
            }
            return $dfd.promise();
        }
        ,deleteFolder: function(node) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFolderNode(node)) {
                $dfd.reject();

            } else {
                var parent = node.parent;
                if (!DocTree.View.validateNode(parent)) {
                    $dfd.reject();

                } else {
                    var cacheKey = DocTree.View.getCacheKey(parent);
                    var refNode = node.getNextSibling() || node.getPrevSibling() || node.getParent();
                    node.remove();
                    if( refNode ) {
                        refNode.setActive();
                    }

                    DocTree.Model.deleteFolder(node.data.objectId, cacheKey)
                        .done(function(deletedFolderId) {
                            $dfd.resolve(deletedFolderId);
                        })
                        .fail(function(response) {
                            App.View.MessageBoard.show($.t("doctree:error.delete-folder"), Acm.goodValue(response.errorMsg));
                            DocTree.View.markNodeError(node);
                            $dfd.reject();
                        })
                    ;
                }
            }
            return $dfd.promise();
        }
        ,deleteFile: function(node) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFileNode(node)) {
                $dfd.reject();

            } else {
                var parent = node.parent;
                if (!DocTree.View.validateNode(parent)) {
                    $dfd.reject();

                } else {
                    var cacheKey = DocTree.View.getCacheKey(parent);
                    var refNode = node.getNextSibling() || node.getPrevSibling() || node.getParent();
                    node.remove();
                    if( refNode ) {
                        refNode.setActive();
                    }

                    DocTree.Model.deleteFile(node.data.objectId, cacheKey)
                        .done(function(deletedFileId) {
                            $dfd.resolve(deletedFileId);
                        })
                        .fail(function(response) {
                            App.View.MessageBoard.show($.t("doctree:error.delete-file"), Acm.goodValue(response.errorMsg));
                            DocTree.View.markNodeError(node);
                            $dfd.reject();
                        })
                    ;
                }
            }
            return $dfd.promise();
        }
        ,batchRemove: function(nodes) {
            var $dfd = $.Deferred();
            if (Acm.isArrayEmpty(nodes)) {
                $dfd.resolve();

            } else {
                var removeNodes = DocTree.View.getTopMostNodes(nodes);
                var requests = [];
                for (var i = 0; i < removeNodes.length; i++) {
                    if (DocTree.View.isFolderNode(removeNodes[i])) {
                        requests.push(DocTree.View.Op.deleteFolder(removeNodes[i]));
                    } else if (DocTree.View.isFileNode(removeNodes[i])) {
                        requests.push(DocTree.View.Op.deleteFile(removeNodes[i]));
                    }
                }

                Acm.Promise.resolvePromises(requests)
                    .done(function() {
                        $dfd.resolve();
                    })
                    .fail(function() {
                        $dfd.reject();
                    });
            }
            return $dfd.promise();
        }
        ,renameFolder: function(node, folderName) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFolderNode(node)) {
                $dfd.reject();

            } else {
                var parent = node.getParent();
                if (!DocTree.View.isFolderNode(parent)) {
                    $dfd.reject();

                } else {
                    var cacheKey = DocTree.View.getCacheKey(parent);
                    DocTree.Model.renameFolder(folderName, node.data.objectId, cacheKey)
                        .done(function(renamedInfo) {
                            DocTree.View.markNodeOk(node);
                            $dfd.resolve(renamedInfo);
                        })
                        .fail(function(response) {
                            App.View.MessageBoard.show($.t("doctree:error.rename-folder") + folderName, Acm.goodValue(response.errorMsg));
                            DocTree.View.markNodeError(node);
                            $dfd.reject();
                        })
                    ;
                }
            }
            return $dfd.promise();
        }
        ,renameFile: function(node, fileName) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFileNode(node)) {
                $dfd.reject();

            } else {
                var parent = node.getParent();
                if (!DocTree.View.isFolderNode(parent)) {
                    $dfd.reject();

                } else {
                    var cacheKey = DocTree.View.getCacheKey(parent);
                    DocTree.Model.renameFile(fileName, node.data.objectId, cacheKey)
                        .done(function(renamedInfo) {
                            DocTree.View.markNodeOk(node);
                            $dfd.resolve(renamedInfo);
                        })
                        .fail(function(response) {
                            App.View.MessageBoard.show($.t("doctree:error.rename-file") + fileName, Acm.goodValue(response.errorMsg));
                            DocTree.View.markNodeError(node);
                            $dfd.reject();
                        })
                    ;
                }
            }
            return $dfd.promise();
        }
        ,setActiveVersion: function(fileNode, version) {
            var $dfd = $.Deferred();
            if (!DocTree.View.isFileNode(fileNode)) {
                $dfd.reject();

            } else {
                var parent = fileNode.getParent();
                if (!DocTree.View.isFolderNode(parent)) {
                    $dfd.reject();

                } else {
                    DocTree.View.markNodePending(fileNode);
                    var cacheKey = DocTree.View.getCacheKey(parent);
                    DocTree.Model.setActiveVersion(fileNode.data.objectId, version, cacheKey)
                        .done(function(activeVersion) {
                            fileNode.data.activeVertionTag = Acm.goodValue(activeVersion);
                            DocTree.View.markNodeOk(fileNode);
                            $dfd.resolve();
                        })
                        .fail(function(response) {
                            App.View.MessageBoard.show($.t("doctree:error.set-version"), Acm.goodValue(response.errorMsg));
                            DocTree.View.markNodeError(fileNode);
                            $dfd.reject();
                        })
                    ;
                }
            }
            return $dfd.promise();
        }
        ,lodgeDocuments: function(folderNames, docIds, frFolderNode) {
            var $dfd = $.Deferred();

            //make a copy
            var findNames = [];
            for (var i = 0; i < folderNames.length; i ++) {
                findNames.push(folderNames[i]);
            }


            var node = DocTree.View.findNodeByPathNames(findNames);
            if (DocTree.View.validateNode(node)) {
                DocTree.View.markNodePending(node);
            }

            DocTree.Model.lodgeDocuments(folderNames, docIds)
                .done(function(createdFolder) {

//                    //
//                    // remove files from original folder cache
//                    //
//                    var frCacheKey = DocTree.View.getCacheKey(frFolderNode);
//                    var frFolderList = DocTree.Model.cacheFolderList.get(frCacheKey);
//                    for (var i = 0; i < docIds.length; i++) {
//                        var idx = DocTree.Model.findFolderItemIdx(docIds[i], frFolderList);
//                        if (0 <= idx) {
//                            frFolderList.children.splice(idx, 1);
//                            frFolderList.totalChildren--;
//                        }
//                    }
//                    DocTree.Model.cacheFolderList.put(frCacheKey, frFolderList);

                    //
                    // fix target folders
                    //
                    var node = DocTree.View.findNodeByPathNames(findNames);
                    if (DocTree.View.validateNode(node)) {
                        var cacheKey = DocTree.View.getCacheKey(node);
                        DocTree.Model.cacheFolderList.remove(cacheKey);
                        node.setExpanded(false);
                        node.resetLazy();
                        DocTree.View.markNodeOk(node);
                    }

                    while (2 < findNames.length) {
                        node = DocTree.View.findNodeByPathNames(findNames);
                        if (DocTree.View.validateNode(node)) {
                            var parent = node.parent;
                            var cacheKey = DocTree.View.getCacheKey(parent);
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            var idx = DocTree.Model.findFolderItemIdx(node.data.objectId, folderList);
                            if (0 > idx) {
                                //not found, this must be newly created folder, no folder info available for now, so we can only close parent
                                DocTree.Model.cacheFolderList.remove(cacheKey);
                                parent.setExpanded(false);
                                parent.resetLazy();
                            }
                        }
                        findNames.pop();
                    }


                    $dfd.resolve(createdFolder.objectId);
                })
                .fail(function(response) {
                    $dfd.reject(response);
                })
            ;
            return $dfd.promise();
        }


    }

    ,getNodePathNames: function(node) {
        var names = [];
        if (DocTree.View.validateNode(node)) {
            var n = node;
            while (n) {
                names.unshift(Acm.goodValue(n.title));
                n = n.parent;
            }
            names.shift(); //remove the hidden Root node
        }
        return names;
    }
    ,findChildNodeByName: function(parentNode, name) {
        var found = null;
        if (DocTree.View.validateFancyTreeNode(parentNode)) {
            if (!Acm.isArrayEmpty(parentNode.children)) {
                for (var i = 0; i < parentNode.children.length; i++) {
                    if (parentNode.children[i].title == name) {
                        found = parentNode.children[i];
                        break;
                    }
                }
            }
        }
        return found;
    }
    ,findSiblingNodeByName: function(node, name) {
        var found = null;
        var parentNode = node.getParent();
        if (DocTree.View.validateFancyTreeNode(parentNode)) {
            if (!Acm.isArrayEmpty(parentNode.children)) {
                for (var i = 0; i < parentNode.children.length; i++) {
                    if (parentNode.children[i].title == name) {
                        if (node.key != parentNode.children[i]) {   //cannot be self
                            found = parentNode.children[i];
                            break;
                        }
                    }
                }
            }
        }
        return found;
    }
    ,findChildNodeById: function(parentNode, id) {
        var found = null;
        for (var j = parentNode.children.length - 1; 0 <= j; j--) {
            if (parentNode.children[j].data.objectId == id) {
                found = parentNode.children[j];
                break;
            }
        }
        return found;
    }
    ,findNodeByPathNames: function(names) {
        var found = null;
        if (!Acm.isArrayEmpty(names)) {
            var node = DocTree.View.tree.getRootNode();
            for (var i = 0; i < names.length; i++) {
                found = this.findChildNodeByName(node, names[i]);
                if (found) {
                    node = found;
                } else {
                    break;
                }
            }
        }
        return found;
    }

    ,_doDownload: function(node) {
        DocTree.View.$formDownloadDoc.attr("action", App.getContextPath() + DocTree.Model.API_DOWNLOAD_DOCUMENT_ + node.data.objectId);
        DocTree.View.$formDownloadDoc.submit();
    }

    // Find oldest parent in the array(not include top node).
    // In inNodes array, Parent nodes need to be before child nodes.
    ,_findOldestParent: function(node, inNodes) {
        var found = null;
        if (DocTree.View.validateNode(node) && DocTree.View.validateNodes(inNodes)) {
            for (var i = 0; i < inNodes.length; i++) {
                if (!DocTree.View.isTopNode(inNodes[i])) {
                    var parent = node.parent;
                    while (parent && !DocTree.View.isTopNode(parent)) {
                        if (parent.data.objectId == inNodes[i].data.objectId) {
                            found = parent;
                            break;
                        }
                        parent = parent.parent;
                    }
                }
            }
        }
        return found;
    }
    // ignore children
    ,getTopMostNodes: function(nodes) {
        var topMostNodes = [];
        for (var i = 0; i < nodes.length; i++) {
            if (!DocTree.View.isTopNode(nodes[i])) {
                var parent = DocTree.View._findOldestParent(nodes[i], nodes);
                if (!parent) {
                    topMostNodes.push(nodes[i]);
                }
            }
        }
        return topMostNodes;
    }

//    ,onViewCopiedFile: function(fileId, folderId, toCacheKey, node) {
//        DocTree.Service.copyFile(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderId, fileId, toCacheKey, node);
//    }
    ,onDblClick: function(event, data) {
        var tree = $(this).fancytree("getTree"),
            node = tree.getActiveNode();
        if (!DocTree.View.isEditing()) {
            if (DocTree.View.isFileNode(node)) {
                $(this).trigger("command", {cmd: "open"});
            }
        }
        //return false;
    }
    ,onClick: function(event, data) {
        if (DocTree.View.isSpecialNode(data.node)) {
            DocTree.View.Paging.doPaging(data.node);
        }
        return true;
    }

    ,isTopNode: function(node) {
        if (node) {
            if (node.data.root) { //not fancy tree root node, which is the invisible parent of the top node
                return true;
            }
        }
        return false;
    }
    ,isFolderNode: function(node) {
        if (node) {
            if (node.folder) {
                return true;
            }
        }
        return false;
    }
    ,isFileNode: function(node) {
        if (node) {
            if (node.data) {
                if (DocTree.Model.NODE_TYPE_FILE == Acm.goodValue(node.data.objectType)) {   //if (!node.isFolder()) {
                    return true;
                }
            }
        }
        return false;
    }
    ,isSpecialNode: function(node) {
        if (node) {
            if (node.data) {
                if (DocTree.Model.NODE_TYPE_FILE != Acm.goodValue(node.data.objectType) && !node.folder) {
                    return true;
                }
            }
        }
        return false;
    }


    ,getCacheKey: function(folderNode) {
        var pageId = Acm.goodValue(folderNode.data.startRow, 0);
        var folderId = folderNode.data.objectId;
        var cacheKey = DocTree.Model.getCacheKey(DocTree.View.isTopNode(folderNode)? 0 : folderId , pageId);
        return cacheKey;
    }
    ,getTopNode: function() {
        var topNode = null;
        if (DocTree.View.tree) {
            var rootNode = DocTree.View.tree.getRootNode();
            if (rootNode) {
                topNode = rootNode.children[0];
            }
        }
        return topNode;
    }

    ,expandNodesByNames: function(names, src) {
        var $dfdAll = $.Deferred();
        DocTree.View._expandFirstNodeByName(DocTree.View.getTopNode(), names, $dfdAll, src);
        return $dfdAll.promise();
    }
    ,_expandFirstNodeByName: function(node, names, $dfdAll, src) {
        var $dfd = $.Deferred();

        if (Acm.isEmpty(node) || Acm.isArrayEmpty(names)) {
            $dfdAll.resolve(src);


        } else {
            if (node.title == names[0]) {
                DocTree.View.expandNode(node).done(function(){
                    names.shift();
                    if (Acm.isArrayEmpty(names)) {
                        node = null;
                    } else {
                        node = DocTree.View.findChildNodeByName(node, names[0]);
                    }
                    DocTree.View._expandFirstNodeByName(node, names, $dfdAll, src);
                });
            } else {
                $dfdAll.reject();
            }
        }

        return $dfd.promise;
    }

    ,expandNode: function(node) {
        var $dfd = $.Deferred();
        if (node.lazy && !node.children) {
            node.setExpanded(true).always(function(){
                $dfd.resolve(node);
            });
        } else {
            $dfd.resolve(node);
        }
        return $dfd.promise();
    }
    ,expandTopNode: function() {
        var $promise = $.when();
        var node = DocTree.View.$tree.fancytree("getRootNode");
        if (node) {
            var topNode = node.children[0];
            $promise = this.expandNode(topNode);
//            if (!topNode.children) {
//                topNode.setExpanded(true);
//            }
        }
        return $promise;
    }
    ,refreshTree: function() {
        var objType = DocTree.Model.getObjType();
        var objId = DocTree.Model.getObjId();
        if (Acm.isNotEmpty(objType) && Acm.isNotEmpty(objId)) {
            //remove tree cache for current obj
            DocTree.Model.cacheTree.remove(objType + "." + objId);
            //remove individual folder cache for current obj
            var cacheFolderList = DocTree.Model.cacheFolderList.cache;
            if(Acm.isNotEmpty(cacheFolderList)) {
                for(var cacheKey in cacheFolderList){
                    if(cacheFolderList.hasOwnProperty(cacheKey)){
                        var cacheKeySplit = cacheKey.split(".");
                        if(Acm.isArray(cacheKeySplit)){
                            // cache keys have following format :
                            // CASE_FILE.1258.0.0.name.ASC.16
                            // ojType.objId.folderId.pageId.soryBy.sortDirection.maxSize
                            var cacheKeyObjId = cacheKeySplit[1];
                            if(Acm.isNotEmpty(cacheKeyObjId)){
                                if(Acm.goodValue(cacheKeyObjId) == Acm.goodValue(objId)){
                                    DocTree.Model.cacheFolderList.remove(cacheKey);
                                }
                            }
                        }
                    }
                }
            }
        }
        DocTree.View.tree.reload(DocTree.View.Source.source());
    }
    ,switchObject: function(activeObjType, activeObjId) {
        if (!DocTree.View.tree) {
            return;
        }

        var dict = null;
        var topNode = DocTree.View.getTopNode();
        if (topNode) {
            var previousObjType = DocTree.Model.getObjType();
            var previousObjId = DocTree.Model.getObjId();
            if (previousObjType != activeObjType || previousObjId != activeObjId) {
                var dictTree = DocTree.View.tree.toDict();
                if (!Acm.isArrayEmpty(dictTree)) {
                    dict = dictTree[0];
                    if (dict && dict.data && dict.data.containerObjectType == previousObjType && dict.data.containerObjectId == previousObjId) {
                        DocTree.Model.cacheTree.put(previousObjType + "." + previousObjId, dict);
                    }
                }
            }
        }

        DocTree.Model.setObjType(activeObjType);
        DocTree.Model.setObjId(activeObjId);
        dict = DocTree.Model.cacheTree.get(activeObjType + "." + activeObjId);
        if (dict && topNode) {
            topNode.removeChildren();
            topNode.resetLazy();
            topNode.fromDict(dict);
        } else {
            DocTree.View.tree.reload(DocTree.View.Source.source());
        }
        Acm.deferred(DocTree.Controller.viewChangedTree);
    }


    ,markNodePending: function(node) {
        if (this.validateFancyTreeNode(node)) {
            $(node.span).addClass("pending");
            node.setStatus("loading");
        }
    }
    ,markNodeOk: function(node) {
        if (this.validateFancyTreeNode(node)) {
            $(node.span).removeClass("pending");
            node.setStatus("ok");
        }
    }
    ,markNodeError: function(node) {
        if (this.validateFancyTreeNode(node)) {
            $(node.span).addClass("pending");
            node.title = $.t("doctree:error.node-title");
            node.renderTitle();
            //node.setStatus("error");
            node.setStatus("ok");
        }
    }
    ,validateNodes: function(data) {
        if (Acm.isNotArray(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateNode(data[i])) {
                return false;
            }
        }
        return true;
    }
    ,validateNode: function(data) {
        if (!this.validateFancyTreeNode(data)) {
            return false;
        }
        if (Acm.isEmpty(data.data.objectId)) {
            return false;
        }
        return true;
    }
    ,validateFancyTreeNodes: function(data) {
        if (Acm.isNotArray(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateFancyTreeNode(data[i])) {
                return false;
            }
        }
        return true;
    }
    ,validateFancyTreeNode: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.tree)) {
            return false;
        }
        if (Acm.isEmpty(data.data)) {
            return false;
        }
        if (Acm.isEmpty(data.key)) {
            return false;
        }
        return true;
    }

    ,DialogDnd: {
        create : function(args) {
            this.$dlgDocTreeDnd = $("#dlgDocTreeDnd");
            this.$radOperation = this.$dlgDocTreeDnd.find("input:radio");
            this.$radOperation.on("click", function(e) {DocTree.View.DialogDnd.onClickRadOperation(e, this);});
//            this.$radReplace       = this.$radOperation.eq(0);
//            this.$radUploadParent  = this.$radOperation.eq(1);
//            this.$radUpload        = this.$radOperation.eq(2);

            this.$selFileTypes = this.$dlgDocTreeDnd.find("select");
            this.fillSelFileTypes(args.fileTypes);
            this.$selFileTypes.on("change", function(e) {DocTree.View.DialogDnd.onChangeFileTypes(e, this);});
            this.$divFileType = this.$selFileTypes.closest("div");
            this.$btnOk = this.$dlgDocTreeDnd.find("button.btn-primary");
        }
        ,onInitialized: function() {
        }

        ,onClickRadOperation: function(event, ctrl) {
            DocTree.View.DialogDnd.update();
        }
        ,onChangeFileTypes: function(event, ctrl) {
            DocTree.View.DialogDnd.update();
        }
        ,fillSelFileTypes: function(fileTypes) {
            for (var i = 0; i < fileTypes.length; i++) {
                var fileType = fileTypes[i];
                if (Acm.isEmpty(fileType.form)) {
                    var $option = $("<option/>")
                        .val(Acm.goodValue(fileType.type))
                        .text(Acm.goodValue(fileType.label))
                        .appendTo(this.$selFileTypes)
                        ;
                }
            }
        }
        ,update: function() {
            var replace = this.isCheckedRadReplace();
            var toParent = this.isCheckedRadUploadToParent();
            var toFolder = this.isCheckedRadUploadToFolder();
            if (replace) {
                this.showDivFileType(false);
                this.setEnableBtnOk(true);

            } else if (toParent) {
                this.showDivFileType(true);
                this.setEnableBtnOk(Acm.isNotEmpty(this.getValueSelFileType()));

            } else if (toFolder) {
                this.showDivFileType(true);
                this.setEnableBtnOk(Acm.isNotEmpty(this.getValueSelFileType()));

            } else {
                Acm.log("should never get here");
                this.showDivFileType(false);
                this.setEnableBtnOk(false);
            }
        }
        ,showIfDropToFolderNode: function(onClickBtnPrimary) {
            this.setCheckedRadReplace(false);
            this.setCheckedRadUploadToParent(false);
            this.setCheckedRadUploadToFolder(true);

            this.showRadReplace(false);
            this.showRadUploadToParent(false);
            this.showRadUploadToFolder(false);

            this.showDivFileType(true);
            this.setValueSelFileType("");
            this.setEnableBtnOk(false);

            Acm.Dialog.modal(this.$dlgDocTreeDnd, onClickBtnPrimary);
        }
        ,showIfDropToFileNode: function(onClickBtnPrimary) {
            this.setCheckedRadReplace(false);
            this.setCheckedRadUploadToParent(false);
            this.setCheckedRadUploadToFolder(false);

            this.showRadReplace(true);
            this.showRadUploadToParent(true);
            this.showRadUploadToFolder(false);

            this.showDivFileType(false);
            this.setValueSelFileType("");
            this.setEnableBtnOk(false);

            Acm.Dialog.modal(this.$dlgDocTreeDnd, onClickBtnPrimary);
        }

        ,isCheckedRadReplace: function() {
            return Acm.Object.isChecked(this.$radOperation.eq(0));
        }
        ,setCheckedRadReplace: function(check) {
            Acm.Object.setChecked(this.$radOperation.eq(0), check);
        }
        ,showRadReplace: function(show) {
            Acm.Object.show(this.$radOperation.eq(0).closest("label"), show);
        }

        ,isCheckedRadUploadToParent: function() {
            return Acm.Object.isChecked(this.$radOperation.eq(1));
        }
        ,setCheckedRadUploadToParent: function(check) {
            Acm.Object.setChecked(this.$radOperation.eq(1), check);
        }
        ,showRadUploadToParent: function(show) {
            Acm.Object.show(this.$radOperation.eq(1).closest("label"), show);
        }

        ,isCheckedRadUploadToFolder: function() {
            return Acm.Object.isChecked(this.$radOperation.eq(2));
        }
        ,setCheckedRadUploadToFolder: function(check) {
            Acm.Object.setChecked(this.$radOperation.eq(2), check);
        }
        ,showRadUploadToFolder: function(show) {
            Acm.Object.show(this.$radOperation.eq(2).closest("label"), show);
        }

        ,getValueSelFileType: function() {
            return Acm.Object.getSelectValue(this.$selFileTypes);
        }
        ,setValueSelFileType: function(value) {
            return Acm.Object.setSelectValue(this.$selFileTypes, value);
        }
        ,showDivFileType: function(show) {
            Acm.Object.show(this.$divFileType, show);
        }

        ,setEnableBtnOk: function(enable) {
            Acm.Object.setEnable(this.$btnOk, enable);
        }
    }
};

