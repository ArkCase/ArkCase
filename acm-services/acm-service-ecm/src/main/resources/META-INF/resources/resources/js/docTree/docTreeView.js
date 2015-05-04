/**
 * DocTree.View
 *
 * @author jwu
 */
DocTree.View = DocTree.View || {
    create : function(args) {
        this.parentType = args.parentType;
        this.parentId = args.parentId;

        this.doUploadForm = args.uploadForm;
        this.fileTypes  = args.fileTypes;

        this.$tree = (args.$tree)? args.$tree : $("#treeDoc");
        this.createDocTree(args.treeArgs);

        this.makeDownloadDocForm(this.$tree);
        this.makeUploadDocForm(this.$tree);

        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_PARENT           ,this.onViewChangedParent);
        Acm.Dispatcher.addEventListener(DocTree.Controller.VIEW_CHANGED_TREE             ,this.onViewChangedTree);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_UPLOADED_FILES          ,this.onModelUploadedFiles);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_REPLACED_FILE           ,this.onModelReplacedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_RETRIEVED_FOLDERLIST    ,this.onModelRetrievedFolderList);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_CREATED_FOLDER          ,this.onModelCreatedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_DELETED_FOLDER          ,this.onModelDeletedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_DELETED_FILE            ,this.onModelDeletedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_RENAMED_FOLDER          ,this.onModelRenamedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_RENAMED_FILE            ,this.onModelRenamedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_MOVED_FILE              ,this.onModelMovedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_COPIED_FILE             ,this.onModelCopiedFile);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_MOVED_FOLDER            ,this.onModelMovedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_COPIED_FOLDER           ,this.onModelCopiedFolder);
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_SET_ACTIVE_VERSION      ,this.onModelSetActiveVersion);

        //----------
        Acm.Dispatcher.addEventListener(DocTree.Controller.MODEL_ADDED_DOCUMENT          ,this.onModelAddedDocument);


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
            DocTree.View.doUploadForm(formType, function() {
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

    ,_addFileNode: function(folderNode, name, type) {
        var fileNode = folderNode.addChildren({"title": "Uploading " + name + "...", "name": name, "type": type, "loadStatus": "loading", "action": DocTree.View.Source.getHtmlAction()});
        //fileNode.setStatus("loading");
        DocTree.View.markNodePending(fileNode);
        return fileNode;
    }
    ,_addingFileNodes: function(folderNode, names, type) {
        var deferred = $.Deferred();
        if (folderNode.lazy && !folderNode.children) {
            folderNode.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
                var fileNodes = [];
                for (var i = 0; i < names.length; i++) {
                    var fileNode = DocTree.View._addFileNode(folderNode, names[i], type);
                    fileNodes.push(fileNode);
                }
                deferred.resolve(fileNodes);
            });
        } else {
            var fileNodes = [];
            for (var i = 0; i < names.length; i++) {
                var fileNode = DocTree.View._addFileNode(folderNode, names[i], type);
                fileNodes.push(fileNode);
            }
            deferred.resolve(fileNodes);
        }
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
            var promiseUploadFile = DocTree.Service.checkUploadForm(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderNode.data.objectId, folderNode.data.startRow, folderNode, fileType);
            $.when(promiseUploadFile, promiseAddNode).done(function(uploadedFiles, fileNodes){
                if (!Acm.isArrayEmpty(uploadedFiles) && DocTree.View.validateNodes(fileNodes)) {
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
                            var fileNode = folderNode.addChildren({"title": Acm.goodValue(uploadedFile.name), "action": DocTree.View.Source.getHtmlAction()});
                            DocTree.View._fileDataToNodeData(uploadedFile, fileNode);
                            fileNode.renderTitle();
                        }
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

        var folderNode = DocTree.View.uploadToFolderNode;
        var fileType = DocTree.View.uploadFileType;
        var files = DocTree.View.$fileInput[0].files;
        var names = [];
        for(var i = 0; i < files.length; i++ ){
            names.push(files[i].name);
        }

        var fd = new FormData();
        fd.append("parentObjectType", DocTree.Model.getObjType());
        fd.append("parentObjectId", DocTree.Model.getObjId());
        if (!DocTree.View.isTopNode(folderNode)) {
            //fd.append("parentFolderId", folderNode.data.objectId);
            fd.append("folderId", folderNode.data.objectId);
        }
        fd.append("fileType", fileType);
        fd.append("category", "Document");
        for(var i = 0; i < files.length; i++ ){
            fd.append("files[]", files[i]);
        }

        var cacheKey = DocTree.View.getCacheKey(folderNode);
        if (DocTree.View.uploadFileNew) {
            DocTree.View._doUploadFiles(fd, cacheKey, folderNode, names, fileType);
        } else {
            var replaceNode = DocTree.View.replaceFileNode;
            DocTree.View._doReplaceFile(fd, replaceNode.data.objectId, cacheKey, replaceNode, names[0]);
        }
    }
    ,_doUploadFiles: function(fd, cacheKey, folderNode, names, fileType) {
        var promiseAddNode = DocTree.View._addingFileNodes(folderNode, names, fileType);
        var promiseUploadFile = DocTree.Service.uploadFiles(fd, cacheKey, folderNode);
        $.when(promiseUploadFile, promiseAddNode).done(function(uploadedFiles, fileNodes){
            if (!Acm.isArrayEmpty(uploadedFiles) && DocTree.View.validateNodes(fileNodes)) {
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
            }
        });
    }
    ,_doReplaceFile: function(fd, fileId, cacheKey, fileNode, name) {
        DocTree.View.markNodePending(fileNode);

        var promiseReplaceFile = DocTree.Service.replaceFile(fd, fileId, cacheKey, fileNode);
        $.when(promiseReplaceFile).done(function(replacedFile){
            if (replacedFile && fileNode) {
                fileNode.data.version = replacedFile.version;
                fileNode.data.versionList = replacedFile.versionList;
                fileNode.renderTitle();
                fileNode.setStatus("ok");
            }
        });
    }
    ,_getNameOrig: function(name) {
        if (Acm.isEmpty(name)) {
            return name;
        }

        var nameOrig = "";
        var und_parts = name.split("_");
        if (und_parts && 2 <= und_parts.length) {
            nameOrig += name.substring(0, name.length - und_parts[und_parts.length - 1].length - 1);
        } else {
            return name;
        }
        var dot_parts = und_parts[und_parts.length - 1].split(".");
        if (dot_parts && 2 <= dot_parts.length) {
            nameOrig += "." + dot_parts[dot_parts.length - 1];
        }
        if (Acm.isEmpty(nameOrig)) {
            nameOrig = name;
        }
        return nameOrig;
    }
    ,_matchFileNode: function(type, name, fileNodes) {
        var fileNode = null;
        for (var i = 0; i < fileNodes.length; i++) {
            var nameOrig = this._getNameOrig(name);
            if (fileNodes[i].data.name == nameOrig && fileNodes[i].data.type == type) {
                fileNode = fileNodes[i];
                break;
            }
        }
        return fileNode;
    }

    ,onViewChangedParent: function(objType, objId) {
        DocTree.View.switchObject(objType, objId);
    }

    // The gear button click events toggle the menu popup. For some unknown reason, the events are fired
    // multiple times rapidly, which mess up the menu toggle logic. _contextMenuIsOpening flag is used
    // to ignore click events during a 100ms time window.
    ,_contextMenuIsOpening: false
    ,onViewChangedTree: function() {
        var $btnTreeBody = DocTree.View.$tree.find("tbody");
        var $btnTreeActions = DocTree.View.$tree.find("button");
        $btnTreeActions.on("click", function(e) {
            if (DocTree.View._contextMenuIsOpening) {
                return;
            }

            var $treeBody = DocTree.View.$tree.find("tbody");
            //var isOpen = $btnTreeBody.contextmenu("isOpen");   //This does not work as expected
            var isOpen = Acm.Object.isVisible($(".ui-menu"));
            if (isOpen) {
                $btnTreeBody.contextmenu("close");

            } else {
                DocTree.View._contextMenuIsOpening = true;
                setTimeout(function(){
                    DocTree.View._contextMenuIsOpening = false;
                }, 100);

                $btnTreeBody.contextmenu("open", $(this));
            }
        });

    }
    ,onModelUploadedFiles: function(uploadInfo, folderNode) {
        if (uploadInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when uploading files", Acm.goodValue(uploadInfo.errorMsg));

        }
    }
    ,onModelReplacedFile: function(replaceInfo, fileId, fileNode) {
        if (replaceInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when replacing file", Acm.goodValue(replaceInfo.errorMsg));
            DocTree.View.markNodeError(fileNode);
        }
    }

    ,onModelRetrievedFolderList: function(folderList, objType, objId, folderId, pageId, folderNode) {
        if (folderList.hasError) {
            App.View.MessageBoard.show("Error occurred when uploading files", Acm.goodValue(uploadInfo.errorMsg));
            DocTree.View.markNodeError(folderNode);

        } else if (DocTree.Model.validateFolderList(folderList) && DocTree.View.validateNode(folderNode)) {
            folderNode.data.objectId = folderList.folderId;
            folderNode.data.totalChildren = folderList.totalChildren;
            folderNode.renderTitle();
        }
    }
    ,onModelCreatedFolder: function(createdFolder, parentId, folderName, cacheKey, node) {
        if (createdFolder.hasError) {
            App.View.MessageBoard.show("Error occurred when creating folder", Acm.goodValue(createdFolder.errorMsg));
            DocTree.View.markNodeError(node);

        } else {
            if (DocTree.View.validateNode(node)) {
                //DocTree.View._fileDataToNodeData(createdFolder, node);
                DocTree.View._folderDataToNodeData(createdFolder, node);
                DocTree.View.markNodeOk(node);
                node.renderTitle();
            }
        }
    }
    ,onModelDeletedFolder: function(deletedInfo, folderId, cacheKey, node) {
        if (deletedInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when deleting folder", Acm.goodValue(deletedInfo.errorMsg));
            DocTree.View.markNodeError(node);
        }
    }
    ,onModelDeletedFile: function(deletedInfo, fileId, cacheKey, node) {
        if (deletedInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when deleting file", Acm.goodValue(deletedInfo.errorMsg));
            DocTree.View.markNodeError(node);
        }
    }
    ,onModelRenamedFolder: function(renamedInfo, folderName, folderId, cacheKey, node) {
        if (renamedInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when renaming folder " + folderName, Acm.goodValue(renamedInfo.errorMsg));
            DocTree.View.markNodeError(node);
        } else {
            DocTree.View.markNodeOk(node);
        }
    }
    ,onModelRenamedFile: function(renamedInfo, fileName, fileId, cacheKey, node) {
        if (renamedInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when renaming file " + fileName, Acm.goodValue(renamedInfo.errorMsg));
            DocTree.View.markNodeError(node);
        } else {
            DocTree.View.markNodeOk(node);
        }
    }
    ,onModelMovedFile: function(moveFileInfo, objType, objId, folderId, fileId, frCacheKey, toCacheKey, node) {
        if (moveFileInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when moving file ", Acm.goodValue(moveFileInfo.errorMsg));
            DocTree.View.markNodeError(node);
        } else {
            DocTree.View.markNodeOk(node);
            DocTree.View.checkNodes(DocTree.View.CLIPBOARD.data, true);

        }
    }
    ,onModelCopiedFile: function(copyFileInfo, objType, objId, folderId, fileId, toCacheKey, node) {
        if (copyFileInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when copying file", Acm.goodValue(copyFileInfo.errorMsg));
            DocTree.View.markNodeError(node);
        } else if (DocTree.View.validateNode(node)) {
            DocTree.View._fileDataToNodeData(copyFileInfo, node);
            DocTree.View.markNodeOk(node);
            node.renderTitle();

            //DocTree.View.checkNodes(DocTree.View.CLIPBOARD.data, true);
            if (Acm.isArray(DocTree.View.CLIPBOARD.data)) {
                var clipBoardNodes = DocTree.View.CLIPBOARD.data;
                for (var i = 0; i < clipBoardNodes.length; i++) {
                    if (Acm.isNotEmpty(clipBoardNodes[i].data)) {
                        if (Acm.goodValue(clipBoardNodes[i].data.name) == Acm.goodValue(node.data.name)) {
                            node.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
    }
    ,onModelMovedFolder: function(moveFolderInfo, subFolderId, folderId, frCacheKey, toCacheKey, node) {
        if (moveFolderInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when moving folder", Acm.goodValue(moveFolderInfo.errorMsg));
            DocTree.View.markNodeError(node);
        } else {
            DocTree.View.markNodeOk(node);
        }
    }
    ,onModelCopiedFolder: function(copyFolderInfo, subFolderId, folderId, toCacheKey, node) {
        if (copyFolderInfo.hasError) {
            App.View.MessageBoard.show("Error occurred when copying folder", Acm.goodValue(copyFolderInfo.errorMsg));
            DocTree.View.markNodeError(node);
        } else {
            DocTree.View.markNodeOk(node);
        }
    }
    ,onModelSetActiveVersion: function(version, fileId, cacheKey, node) {
        if (version.hasError) {
            App.View.MessageBoard.show("Error occurred when setting active version", Acm.goodValue(version.errorMsg));
            DocTree.View.markNodeError(node);
        } else if (DocTree.View.validateNode(node)) {
            node.data.activeVertionTag = Acm.goodValue(version);
            DocTree.View.markNodeOk(node);
        }
    }

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
                        Acm.Dialog.confirm("Are you sure to set back to older version?"
                             ,function(result) {
                                 if (result == true) {
                                     DocTree.View.markNodePending(node);
                                     DocTree.Controller.viewChangedVersion(node.data.objectId, verSelected, cacheKey, node);
                                 } else {
                                     node.renderTitle();
                                 }
                             }
                        );
                    } else {
                        DocTree.View.markNodePending(node);
                        DocTree.Controller.viewChangedVersion(node.data.objectId, verSelected, cacheKey, node);
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
            node.setSelected(check);
        });
    }
    ,checkNodes: function(nodes, check) {
        if (!Acm.isArrayEmpty(nodes)) {
            for (var i = 0; i < nodes.length; i++) {
                nodes[i].setSelected(check);
            }
        }
    }


    //------------------
    ,onModelAddedDocument: function(node, parentId, folder) {
        //var $divError = $("#divError");
        //$divError.slideDown("slow");

//        App.View.MessageBoard.showBtnDetail(true);
//        App.View.MessageBoard.showDivBoard(false);
//        App.View.MessageBoard.showBtnDetail(false);
//        App.View.MessageBoard.showDivBoard(true);
//        App.View.MessageBoard.showBtnDetail(true);
//        App.View.MessageBoard.showBtnDetail(false);

//        App.View.MessageBoard.show("hello doc");
        var z = 1;
    }

//    ,showDialog: function(args) {
//        if (Acm.isEmpty(args.$dlgDocumentPicker)) {
//            args.$dlgDocumentPicker = $("#dlgDocumentPicker");
//        }
//        if (Acm.isNotEmpty(args.title)) {
//            args.$dlgDocumentPicker.find('.modal-title').text(args.title);
//        }
//        if (Acm.isNotEmpty(args.btnOkText)) {
//            args.$dlgDocumentPicker.find('button.btn-primary').text(args.btnOkText);
//        }
//        if (Acm.isNotEmpty(args.btnCancelText)) {
//            args.$dlgDocumentPicker.find('button.btn-default').text(args.btnCancelText);
//        }
//        Acm.Dialog.modal(args.$dlgDocumentPicker, args.onClickBtnPrimary, args.onClickBtnDefault);
//    }
    ,getSelectedNodes: function() {
        return this.tree.getSelectedNodes();
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
            ,select: function(event, data) {
                var selNodes = data.tree.getSelectedNodes();
                var selKeys = $.map(selNodes, function(node){
                    return "[" + node.key + "]: '" + node.title + "'";
                });
                var a = selKeys.join(", ");
                var z = 1;
            }
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


                //if (DocTree.View.isTopNode(node)) {
                if (DocTree.View.isFolderNode(node)) {
                    ;
                } else if (DocTree.View.isFileNode(node)) {
                    $tdList.eq(3).text(node.data.type);
                    $tdList.eq(4).text(Acm.getDateFromDatetime(node.data.created));
                    $tdList.eq(5).text(Acm.__FixMe__getUserFullName(node.data.creator));


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
                    //$tdList.eq(8).html(node.data.action);

                    $tdList.eq(1).addClass("");

//                $tdList.eq(8).html(DocTree.View.Source.getHtmlAction());
                } else {  //non file, non folder
                    $tdList.eq(0).text("");
                }


                //$tdList.eq(3).text(node.key);
                //$tdList.eq(4).html("<input type='checkbox' name='like' value='" + node.key + "'>");
            }

            ,createNode: function(event, data) {
                var node = data.node;
//                if (DocTree.View.isFileNode(node) || DocTree.View.isFolderNode(node)) {
//                    $(node.tr).addClass("hasMenu");
//                }
//                if (node.folder) {
//                    var $tdEq3 = $(node.tr).find(">td:eq(3)");
//                    var $tdGt3 = $(node.tr).find(">td:gt(3)");
//                    $tdEq3.attr("colspan", 5);
//                    $tdGt3.remove();
//                }
                var z = 1;
            }
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
                        var cacheKey = DocTree.View.getCacheKey(parent);
                        var name = data.input.val();
                        if (data.isNew) {
                            if (DocTree.View.isFolderNode(data.node)) {
                                //var pageId = Acm.goodValue(parent.data.startRow, 0);
                                var parentId = parent.data.objectId;
                                //var cacheKey = DocTree.Model.getCacheKey(DocTree.View.isTopNode(parent)? 0 : parentId , pageId);
                                DocTree.Controller.viewAddedFolder(parentId, name, cacheKey, data.node);
//                            } else {
//                                DocTree.Controller.viewAddedDocument(data.node, parentId, name);
                            }

                        } else {
                            var id = data.node.data.objectId;
                            if (DocTree.View.isFolderNode(data.node)) {
                                DocTree.Controller.viewRenamedFolder(name, id, cacheKey, data.node);
                            } else if (DocTree.View.isFileNode(data.node)) {
                                DocTree.Controller.viewRenamedFile(name, id, cacheKey, data.node);
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
                autoExpandMS: 400,
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
                    if (data.node.folder) {
                        return true;
                    } else if (DocTree.Model.NODE_TYPE_PREV == data.node.data.objectType) {
                        return ["after"];
                    } else if (DocTree.Model.NODE_TYPE_NEXT == data.node.data.objectType) {
                        return ["before"];
                    } else {
                        return ["before", "after"];  // Don't allow dropping *over* a document node (would create a child)
                    }
                },
                dragDrop: function(node, data) {
                    if (node.lazy && !node.children) {
                        node.setExpanded(true).always(function(){
                            DocTree.View._doDrop(data.otherNode, node, data.hitMode);
                        });
                    } else {
                        DocTree.View._doDrop(data.otherNode, node, data.hitMode);
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
        DocTree.View.Menu.useContextMenu($treeBody);
        DocTree.View.ExternalDnd.useExternalDnd($treeBody);

        $treeBody.delegate("select.docversion", "change", DocTree.View.onChangeVersion);
        $treeBody.delegate("select.docversion", "dblclick", DocTree.View.onDblClickVersion);

        var $treeHead = $tree.find("thead");
        $treeHead.find("input:checkbox").on("click", function(e) {DocTree.View.onClickBtnChkAllDocument(e, this);});

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

        ,_borderSave: null
        ,onDragEnter: function(e) {
            e.stopPropagation();
            e.preventDefault();
            if (null == DocTree.View.ExternalDnd._borderSave) {
                DocTree.View.ExternalDnd._borderSave = $(this).css('border');
            }
            $(this).css('border', '2px solid #0B85A1');
        }
        ,onDragOver: function(e) {
            e.stopPropagation();
            e.preventDefault();
            $(this).css('border', '2px solid #0B85A1');
        }
        ,onDragLeave: function(e) {
            e.stopPropagation();
            e.preventDefault();

            if (null != DocTree.View.ExternalDnd._borderSave) {
                $(this).css('border', DocTree.View.ExternalDnd._borderSave);
            }
        }
        ,onDragDrop: function(e) {
            //e.stopPropagation();
            e.preventDefault();
            if (null != DocTree.View.ExternalDnd._borderSave) {
                $(this).css('border', DocTree.View.ExternalDnd._borderSave);
            }

            var node = $.ui.fancytree.getNode(e);
            var files = e.originalEvent.dataTransfer.files;

            var tree = DocTree.View.tree;
            var node2 = tree.getActiveNode();
            var a = this;

            var $tdList = $(this).find(">td");
            var $td = $tdList.eq(1);
            var $a = $tdList.eq(1).find("a");
            var a2 = $a.attr("href");
            var a3 = $a.text();

            var $dlg = $("#emailDocs");
            Acm.Dialog.modal($dlg ,function() {
                alert("OKed");
            });

            var z = 1;
        }
    }

    ,Menu: {
        useContextMenu: function($s) {
            if (!this.docSubMenu) {
                this.docSubMenu = this.makeDocSubMenu(DocTree.View.fileTypes);
            }

            $s.contextmenu({
                menu: []
                //,delegate: "span.fancytree-node"
                ,delegate: "tr"
                ,beforeOpen: function(event, ui) {
                    var selNodes = DocTree.View.tree.getSelectedNodes();
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
            var menu = [];
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

                if (0 < countFile && 0 >= countFolder) {
                    menu = [
                        {title: "Email", cmd: "email", uiIcon: "ui-icon-mail-closed" }
                        ,{title: "Print <kbd>Ctrl+P</kbd>", cmd: "print", uiIcon: "ui-icon-print" }
                        ,{title: "----" }
                        ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                }
//                if (0 < countFile || 0 < countFolder) {
//                    menu = [
//                        {title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
//                        ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
//                        ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
//                    ];
//                }
            }
            return menu;
        }

        ,getContextMenu: function(node) {
            var menu = [];
            if (node) {
                if (DocTree.View.isTopNode(node)) {
                    menu = [
                        {title: "New Folder <kbd>[Ctrl+N]</kbd>", cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        ,{title: "New Document</kbd>", children: DocTree.View.Menu.docSubMenu}
                        ,{title: "----" }
                        ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                    ];
                } else if (DocTree.View.isFolderNode(node)) {
                    menu = [
                        //{title: "New sibling <kbd>[Ctrl+N]</kbd>", cmd: "addSibling", uiIcon: "ui-icon-plus" }
                        //,{title: "New child <kbd>[Ctrl+Shift+N]</kbd>", cmd: "addChild", uiIcon: "ui-icon-arrowreturn-1-e" }
                        {title: "New Folder <kbd>[Ctrl+N]</kbd>", cmd: "newFolder", uiIcon: "ui-icon-plus" }
                        //,{title: "New Document <kbd>[Ctrl+Shift+N]</kbd>", cmd: "newDocument", uiIcon: "ui-icon-arrowreturn-1-e" }
                        ,{title: "New Document</kbd>", children: DocTree.View.Menu.docSubMenu}
                        ,{title: "----" }
                        ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: "----" }
                        ,{title: "Rename <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                    ];
                } else if (DocTree.View.isFileNode(node)) {
                    menu = [
                        {title: "Open", cmd: "open", uiIcon: "ui-icon-folder-open" }
                        ,{title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil" }
                        ,{title: "Email", cmd: "email", uiIcon: "ui-icon-mail-closed" }
                        ,{title: "Print <kbd>Ctrl+P</kbd>", cmd: "print", uiIcon: "ui-icon-print" }
                        ,{title: "----" }
                        ,{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut", uiIcon: "ui-icon-scissors" }
                        ,{title: "Copy <kbd>Ctrl-C</kbd>", cmd: "copy", uiIcon: "ui-icon-copy" }
                        ,{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: true }
                        ,{title: "----" }
                        ,{title: "Rename <kbd>[F2]</kbd>", cmd: "rename", uiIcon: "ui-icon-pencil" }
                        ,{title: "Delete <kbd>[Del]</kbd>", cmd: "remove", uiIcon: "ui-icon-trash" }
                        ,{title: "----" }
                        ,{title: "Download", cmd: "download", uiIcon: "ui-icon-arrowthickstop-1-s" }
                        ,{title: "Replace", cmd: "replace", uiIcon: "" }
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
                        ,"action": DocTree.View.Source.getHtmlAction()
                    })
                    .getTree();
            }
            return src;
        }
        ,getHtmlDocLink: function(node) {
            var $div = $("<div/>")
            .addClass("btn-group");
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
        ,getHtmlDocLink_html: function(node) {
            var html = "<div></div>";
            var itemId = node.data.objectId
            if (itemId) {
                var url = "#";
                if (DocTree.View.isFileNode(node)) {
                    url = App.getContextPath() + "/plugin/document/" + itemId;
                }
                html = "<div class='btn-group'><a href='" + url + "'>" + itemId + "</a></div>";
            }
            return html;
        }
        ,getHtmlAction: function() {
            return "<div class='btn-group'><button type='button'> <i class='fa fa-cog'></i> </button></div>";
        }

        ,_makeChildNodes: function(folderList) {
            var builder = AcmEx.FancyTreeBuilder.reset();
            if (DocTree.Model.validateFolderList(folderList)) {
                var  startRow = Acm.goodValue(folderList.startRow, 0);
                var  maxRows = Acm.goodValue(folderList.maxRows, 0);
                var  totalChildren = Acm.goodValue(folderList.totalChildren, -1);
                var  folderId = Acm.goodValue(folderList.folderId, 0);

                if (0 < startRow) {
                    builder.addLeaf({key: folderId + ".prev"
                        ,title: startRow + " items above..."
                        ,tooltip: "Review previous items"
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
                        nodeData.action = DocTree.View.Source.getHtmlAction();
                        builder.addLeaf(nodeData);
                    }
                }

                if ((0 > totalChildren) || (totalChildren - maxRows > startRow)) {//unknown size or more page
                    var title = (0 > totalChildren)? "More items..." : (totalChildren - startRow - maxRows) + " more items...";
                    builder.addLeafLast({key: Acm.goodValue(folderId, 0) + ".next"
                        ,title: title
                        ,tooltip: "Load more items"
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
            nodeData.action = DocTree.View.Source.getHtmlAction();
            return nodeData;
        }
        ,lazyLoad: function(event, data) {
            var objType = DocTree.Model.getObjType();
            var objId   = DocTree.Model.getObjId();
            var node = data.node;
            var folderId = Acm.goodValue(node.data.objectId, 0);
            if (DocTree.View.isTopNode(node)) {
                folderId = 0;
            }
            var pageId = Acm.goodValue(node.data.startRow);
            var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
            if (DocTree.Model.validateFolderList(folderList)) {
                data.result = DocTree.View.Source._makeChildNodes(folderList);
                setTimeout(function() {
                    DocTree.Controller.viewChangedTree();
                }, 500);

            } else {
                data.result = DocTree.Service.retrieveFolderListDeferred(DocTree.Model.getObjType(), DocTree.Model.getObjId(), folderId, pageId, data.node
                    ,function(folderList) {
                        data.node.data.startRow = Acm.goodValue(folderList.startRow, 0);
                        data.node.data.totalChildren = Acm.goodValue(folderList.totalChildren, -1);
                        var rc = DocTree.View.Source._makeChildNodes(folderList);
                        setTimeout(function() {
                            DocTree.Controller.viewChangedTree();
                        }, 500);
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
            var tree = $(this).fancytree("getTree");
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
                    DocTree.View._doBatRemove(nodes);
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
                        node.editCreateNode("child", nodeData);

                        //node.editCreateNode("child", {"title": "New Folder", "folder": true, "action": DocTree.View.Source.getHtmlAction()});
                    }
                    break;
                case "newDocument":
                    if (!DocTree.View.isEditing()) {
//                    if (!DocTree.View.uploadFolderNode) {
////                        node.editCreateNode("child", {"title": "New Document", "action": DocTree.View.Source.getHtmlAction()});
//////                        DocTree.View.uploadFolderNode = node;
//                        setTimeout(function() {
//                            DocTree.View.uploadToFolder(node);
//                            //DocTree.View.$fileInput.click();
//                        }, 200);
//                    }

                        //node.editCreateNode("child", "New Document");
                        //node.editCreateNode("child", {"title": "New Document", "action": DocTree.View.Source.getHtmlAction()});
                        DocTree.View.uploadFile(node);
//
//                    DocTree.View.$fileInput.click();
//                    //DocTree.View.$fileInput.click();
//
//                    if (node.lazy && !node.children) {
//                        node.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
//                            DocTree.View._doNewFile(node);
//                        });
//                    } else {
//                        DocTree.View._doNewFile(node);
//                    }
                    }
                    break;

                case "cut":
                    var nodes = (batch)? selNodes : [node];
                    DocTree.View.checkNodes(nodes, false);
                    DocTree.View.CLIPBOARD = {mode: data.cmd, data: nodes};
                    break;
                case "copy":
                    var nodes = (batch)? selNodes : [node];
                    DocTree.View.checkNodes(nodes, false);
                    for (var i = 0; i < nodes.length; i++) {
                        nodes[i] = nodes[i].toDict(function(n){
                            delete n.key;
                        });
                    }
                    DocTree.View.CLIPBOARD = {mode: data.cmd, data: nodes};
//                    DocTree.View.CLIPBOARD = {
//                        mode: data.cmd,
//                        data: node.toDict(function(n){
//                            delete n.key;
//                        })
//                    };
                    break;
                case "clear":
                    DocTree.View.CLIPBOARD = null;
                    break;
                case "paste":
                    if (node.lazy && !node.children) {
                        node.setExpanded(true).always(function(){// Wait until expand finished, then do the paste
                            DocTree.View._doPaste(node);
                        });
                    } else {
                        DocTree.View._doPaste(node);
                    }
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
    }

    ,Dnd: {

    }

    ,_doDownload: function(node) {
        DocTree.View.$formDownloadDoc.attr("action", App.getContextPath() + DocTree.Service.API_DOWNLOAD_DOCUMENT_ + node.data.objectId);
        DocTree.View.$formDownloadDoc.submit();
    }
    ,_doBatRemove: function(nodes) {
        if (!Acm.isArrayEmpty(nodes)) {
            for (var i = 0; i < nodes.length; i++) {
                this._doRemove(nodes[i]);
            }
        }
    }
    ,_doRemove: function(node) {
        if (DocTree.View.isFolderNode(node) || DocTree.View.isFileNode(node)) {
            var parent = node.parent;
            if (parent) {
//                var pageId = Acm.goodValue(parent.data.startRow, 0);
//                var parentId = parent.data.objectId;
//                var cacheKey = DocTree.Model.getCacheKey(DocTree.View.isTopNode(parent)? 0 : parentId , pageId);
                var cacheKey = DocTree.View.getCacheKey(parent);

                var refNode = node.getNextSibling() || node.getPrevSibling() || node.getParent();
                node.remove();
                if( refNode ) {
                    refNode.setActive();
                }

                if (DocTree.View.isFolderNode(node)) {
                    DocTree.Controller.viewRemovedFolder(node.data.objectId, cacheKey, node);
                } else if (DocTree.View.isFileNode(node)) {
                    DocTree.Controller.viewRemovedFile(node.data.objectId, cacheKey, node);
                }
            }
        }
    }
    ,_doPaste: function(node) {
        var mode =  DocTree.View.isFolderNode(node)? "child" : "after";
        if( DocTree.View.CLIPBOARD.mode === "cut" ) {
            DocTree.View._doBatMove(DocTree.View.CLIPBOARD.data, node, mode);
        } else if( DocTree.View.CLIPBOARD.mode === "copy" ) {
            DocTree.View._doBatCopy(DocTree.View.CLIPBOARD.data, node, mode);
        }
    }
    ,_doDrop: function(frNode, toNode, mode) {
        DocTree.View._doMove(frNode, toNode, mode);
    }
    ,_doBatMove: function(frNodes, toNode, mode) {
        if (!Acm.isArrayEmpty(frNodes)) {
            for (var i = 0; i < frNodes.length; i++) {
                this._doMove(frNodes[i], toNode, mode);
            }
        }
    }
    ,_doMove: function(frNode, toNode, mode) {
        if (DocTree.View.isFolderNode(frNode) || DocTree.View.isFileNode(frNode)) {
            var toFolderNode = DocTree.View.isFolderNode(toNode)? toNode : toNode.parent;
            if (toFolderNode) {
                //var toFolderPage = Acm.goodValue(toFolderNode.data.startRow, 0);
                var toFolderId = toFolderNode.data.objectId;
                //var toCacheKey = DocTree.Model.getCacheKey(DocTree.View.isTopNode(toFolderNode)? 0 : toFolderId , toFolderPage);
                var toCacheKey = DocTree.View.getCacheKey(toFolderNode);

                var frFolderNode = frNode.parent;
                //var frFolderPage = Acm.goodValue(frFolderNode.data.startRow, 0);
                var frFolderId = frFolderNode.data.objectId;
                //var frCacheKey = DocTree.Model.getCacheKey(DocTree.View.isTopNode(frFolderNode)? 0 : frFolderId , frFolderPage);
                var frCacheKey = DocTree.View.getCacheKey(frFolderNode);

                frNode.moveTo(toNode, mode);
                frNode.setActive();
                DocTree.View.markNodePending(frNode);

                if (DocTree.View.isFolderNode(frNode)) {
                    DocTree.Controller.viewMovedFolder(frNode.data.objectId, toFolderId, frCacheKey, toCacheKey, frNode);
                } else if (DocTree.View.isFileNode(frNode)) {
                    DocTree.Controller.viewMovedFile(frNode.data.objectId, toFolderId, frCacheKey, toCacheKey, frNode);
                }
            }
        }
    }
    ,_doBatCopy: function(frNodes, toNode, mode) {
        if (!Acm.isArrayEmpty(frNodes)) {
            for (var i = 0; i < frNodes.length; i++) {
                this._doCopy(frNodes[i], toNode, mode);
            }
        }
    }
    ,_doCopy: function(frNode, toNode, mode) {
        if (DocTree.View.isFolderNode(frNode) || DocTree.View.isFileNode(frNode)) {
            var toFolderNode = DocTree.View.isFolderNode(toNode)? toNode : toNode.parent;
            if (toFolderNode) {
                var newNode = null;
                if (DocTree.View.isFolderNode(toNode)) {
                    newNode = toNode.addChildren(frNode);
                } else {
                    //toNode = node.addNode(frNode, "after")
                    newNode = toNode.addNode(frNode, mode)
                }
                newNode.setActive();
                DocTree.View.markNodePending(newNode);

                //var toFolderPage = Acm.goodValue(toFolderNode.data.startRow, 0);
                var toFolderId = toFolderNode.data.objectId;
                //var toCacheKey = DocTree.Model.getCacheKey(DocTree.View.isTopNode(toFolderNode)? 0 : toFolderId , toFolderPage);
                var toCacheKey = DocTree.View.getCacheKey(toFolderNode);

                if (DocTree.View.isFolderNode(frNode)) {
                    DocTree.Controller.viewCopiedFolder(frNode.data.objectId, toFolderId, toCacheKey, newNode);
                } else if (DocTree.View.isFileNode(frNode)) {
                    DocTree.Controller.viewCopiedFile(frNode.data.objectId, toFolderId, toCacheKey, newNode);
                }
            }
        }
    }

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
    ,expandTopNode: function() {
        var node = DocTree.View.$tree.fancytree("getRootNode");
        if (node) {
            var topNode = node.children[0];
            if (!topNode.children) {
                topNode.setExpanded(true);
            }
        }
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
        $(node.span).addClass("pending");
        node.setStatus("loading");
    }
    ,markNodeOk: function(node) {
        $(node.span).removeClass("pending");
        node.setStatus("ok");
    }
    ,markNodeError: function(node) {
        $(node.span).addClass("pending");
        //node.setStatus("error");
        node.setStatus("ok");
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
            this.$radReplace = this.$radOperation.eq(0);
            this.$radCopy    = this.$radOperation.eq(1);

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
            var replaceChecked = this.isCheckedRadReplace();
            var copyChecked = this.isCheckedRadCopy();
            var fileType = this.getValueSelFileType();

        }
        ,show: function(onClickBtnPrimary) {
            this.showDivFileType(false);
            this.setEnableBtnOk(false);

            Acm.Dialog.modal(this.$dlgDocTreeDnd, onClickBtnPrimary);
        }

        ,getValueSelFileType: function() {
            return Acm.Object.getSelectValue(this.$selFileTypes);
        }
        ,setEnableBtnOk: function(enable) {
            Acm.Object.setEnable(this.$btnOk.enable);
        }
        ,isCheckedRadReplace: function() {
            return Acm.Object.isChecked(this.$radOperation.eq(0));
        }
        ,setCheckedRadReplace: function(check) {
            Acm.Object.setChecked(this.$radOperation.eq(0), check);
        }
        ,isCheckedRadCopy: function() {
            return Acm.Object.isChecked(this.$radOperation.eq(1));
        }
        ,setCheckedRadCopy: function(check) {
            Acm.Object.setChecked(this.$radOperation.eq(1), check);
        }
        ,showDivFileType: function(show) {
            Acm.Object.show(this.$divFileType, show);
        }
    }
};

