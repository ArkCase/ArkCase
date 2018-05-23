<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page import="com.armedia.acm.plugins.onlyoffice.helpers.DocumentManager" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>ONLYOFFICE</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.servletContext.contextPath}/custom_assets/css/editor.css"/>

    <% DocumentManager.init(request, response); %>

    <script type="text/javascript" src="${docserviceApiUrl}"></script>

    <script type="text/javascript" language="javascript">

        var docEditor;
        var fileName = "${model.fileName}";
        var fileType = "${fn:replace(fileInfo.fileActiveVersionNameExtension,".", "")}";

        var innerAlert = function (message) {
            if (console && console.log)
                console.log(message);
        };

        var onReady = function () {
            innerAlert("Document editor ready");
        };

        var onDocumentStateChange = function (event) {
            var title = document.title.replace(/\*$/g, "");
            document.title = title + (event.data ? "*" : "");
        };

        var onRequestEditRights = function () {
            location.href = location.href.replace(RegExp("action=view\&?", "i"), "");
        };

        var onError = function (event) {
            if (event)
                innerAlert(event.data);
        };

        var onOutdatedVersion = function (event) {
            location.reload(true);
        };

        var сonnectEditor = function () {

            docEditor = new DocsAPI.DocEditor("iframeEditor",
                {
                    width: "100%",
                    height: "100%",
                    type: "${type}",
                    documentType: "text",

                    document: {
                        title: fileName,
                        url: "http://192.168.56.1:8080${pageContext.servletContext.contextPath}/api/v1/plugin/ecm/download?ecmFileId=${fileInfo.fileId}&acm_ticket=${token}",
                        fileType: fileType,
                        key: "${fileInfo.fileId}_${fileInfo.activeVersionTag}",
                        info: {
                            author: "${fileInfo.creator}",
                            created: "${fileInfo.created}"
                        },
                        permissions: {
                            edit: true,
                            download: true
                        }
                    },
                    editorConfig: {
                        mode: "edit",
                        lang: "en",
                        callbackUrl: "http://192.168.56.1:8080${pageContext.servletContext.contextPath}/onlyoffice/callback?acm_ticket=${token}",

                        user: {
                            id: "${user.userId}",
                            name: "${user.fullName}"
                        },

                        embedded: {
                            saveUrl: "http://192.168.56.1:8080${pageContext.servletContext.contextPath}/api/v1/plugin/ecm/download?ecmFileId=${fileInfo.fileId}?acm_ticket=${token}",
                            embedUrl: "http://192.168.56.1:8080${pageContext.servletContext.contextPath}/api/v1/plugin/ecm/download?ecmFileId=${fileInfo.fileId}?acm_ticket=${token}",
                            shareUrl: "http://192.168.56.1:8080${pageContext.servletContext.contextPath}/api/v1/plugin/ecm/download?ecmFileId=${fileInfo.fileId}?acm_ticket=${token}",
                            toolbarDocked: "top"
                        },

                        customization: {
                            about: true,
                            feedback: true,
                            goback: {
                                url: "/api/v1/plugin/ecm/download/",
                            }
                        }
                    },
                    events: {
                        "onReady": onReady,
                        "onDocumentStateChange": onDocumentStateChange,
                        'onRequestEditRights': onRequestEditRights,
                        "onError": onError,
                        "onOutdatedVersion": onOutdatedVersion,
                    }
                });
        };

        if (window.addEventListener) {
            window.addEventListener("load", сonnectEditor);
        } else if (window.attachEvent) {
            window.attachEvent("load", сonnectEditor);
        }

        function getXmlHttp() {
            var xmlhttp;
            try {
                xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (ex) {
                    xmlhttp = false;
                }
            }
            if (!xmlhttp && typeof XMLHttpRequest !== "undefined") {
                xmlhttp = new XMLHttpRequest();
            }
            return xmlhttp;
        }

    </script>

</head>
<body>
<div class="form">
    <div id="iframeEditor"></div>
</div>
</body>
</html>
