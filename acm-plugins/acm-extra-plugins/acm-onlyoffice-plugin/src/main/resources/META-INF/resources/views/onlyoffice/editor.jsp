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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>ONLYOFFICE</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon"/>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.servletContext.contextPath}/custom_assets/css/editor.css"/>

    <script type="text/javascript" src="${docserviceApiUrl}"></script>
    <script type="text/javascript" language="javascript">

        var docEditor;

        var innerAlert = function (message) {
            if (console && console.log)
                console.log(message);
        };

        var onReady = function () {
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
            if (event)
                innerAlert(event.data);
        };

        var onOutdatedVersion = function (event) {
            location.reload(true);
        };

        var сonnectEditor = function () {

            var config = ${config};
            config.events = {
                "onReady": onReady,
                "onDocumentStateChange": onDocumentStateChange,
                'onRequestEditRights': onRequestEditRights,
                "onError": onError,
                "onOutdatedVersion": onOutdatedVersion
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
