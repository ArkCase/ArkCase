<%--
  #%L
  ACM Extra Plugin: OnlyOffice Integration
  %%
  Copyright (C) 2014 - 2018 ArkCase LLC
  %%
  This file is part of the ArkCase software. 
  
  If the software was purchased under a paid ArkCase license, the terms of 
  the paid license agreement will prevail.  Otherwise, the software is 
  provided under the following open source license terms:
  
  ArkCase is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
   
  ArkCase is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
  #L%
  --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
<script src="${pageContext.servletContext.contextPath}/custom_assets/js/jquery-3.3.1.min.js"
        integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ONLYOFFICE</title>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/custom_assets/css/editor.css" />

<script type="text/javascript" src="${docserviceApiUrl}"></script>
<script type="text/javascript" language="javascript">

    var docEditor;

    var innerAlert = function (message) {
        if (console && console.log)
            console.log(message);
    };

    var onAppReady = function () {
        innerAlert("Document editorConfig ready");
    };

    var onDocumentStateChange = function (event) {
        var title = document.title.replace(/\*$/g, "");
        document.title = title + (event.data ? "*" : "");
    };

    var onRequestEditRights = function () {
        location.href = location.href.replace(RegExp("action=view\&?", "i"), "");
    };

    var onError = function (event) {
        console.log("onError");
        console.log(event);
        if (event)
            innerAlert(event.data);
    };

    var onOutdatedVersion = function (event) {
        location.reload(true);
    };

    var onCollaborativeChanges = function (event) {
        console.log("onCollaborativeChanges");
        console.log(event);
    };

    var onDocumentReady = function (event) {
        console.log("onDocumentReady");
        console.log(event);
    };

    var onDownloadAs = function (event) {
        console.log("onDownloadAs");
        console.log(event);
    };

    var onRequestClose = function (event) {
        console.log("onRequestClose");
        console.log(event);
    };

    var onRequestHistory = function (event) {
        $.getJSON("${arkcaseBaseUrl}/onlyoffice/history/${fileId}?acm_ticket=${ticket}", function (response) {
            docEditor.refreshHistory(response);
        }, function (errorResponse) {
            docEditor.refreshHistory({"error": errorResponse});
        });
    };

    var onRequestHistoryClose = function (event) {
        document.location.reload();
    };

    var onRequestHistoryData = function (event) {
        var version = event.data;
        docEditor.setHistoryData({
            "key": "${fileId}-" + version,
            "changesUrl": "${arkcaseBaseUrl}/onlyoffice/history/${fileId}-" + version + "/changes?acm_ticket=${ticket}",
            "url": "${arkcaseBaseUrl}/api/v1/plugin/ecm/download?ecmFileId=${fileId}&version=" + version + "&acm_ticket=${ticket}",
            "version": version
        });
    };

    var onWarning = function (event) {
        console.log("onWarning");
        console.log(event);
    };


    var сonnectEditor = function () {

        var config = ${config};
        config.events = {
            "onAppReady": onAppReady,
            "onDocumentStateChange": onDocumentStateChange,
            'onRequestEditRights': onRequestEditRights,
            "onError": onError,
            "onOutdatedVersion": onOutdatedVersion,
            "onCollaborativeChanges": onCollaborativeChanges,
            "onDocumentReady": onDocumentReady,
            "onDownloadAs": onDownloadAs,
            "onRequestClose": onRequestClose,
            "onRequestHistory": onRequestHistory,
            "onRequestHistoryClose": onRequestHistoryClose,
            "onRequestHistoryData": onRequestHistoryData,
            "onWarning": onWarning
        };

        docEditor = new DocsAPI.DocEditor("iframeEditor", config);
    };

    if (window.addEventListener) {
        window.addEventListener("load", сonnectEditor);
    } else if (window.attachEvent) {
        window.attachEvent("load", сonnectEditor);
    }
</script>

</head>
<body>
    <div class="form">
        <div id="iframeEditor"></div>
    </div>
</body>
</html>
