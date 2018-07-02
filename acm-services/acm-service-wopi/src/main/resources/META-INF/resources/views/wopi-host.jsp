<%--
  #%L
  ACM Service: Wopi service
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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<!doctype html>
<head>
    <meta charset="utf-8">

    <!-- Enable IE Standards mode -->
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Simultaneous editing</title>
    <style type="text/css">
        body {
            margin: 0;
            padding: 0;
            overflow:hidden;
            -ms-content-zooming: none;
        }

        #office_frame {
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            margin: 0;
            border: none;
            display: block;
        }
    </style>
</head>
<body>
    <iframe id="office_frame" src="${url}"></iframe>
</body>
</html>
