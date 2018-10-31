<%--
  #%L
  ACM Service: User Login and Authentication
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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="<%= request.getContextPath()%>/node_modules/@bower_components/bootstrap/dist/js/bootstrap.js"></script>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/node_modules/@bower_components/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
    <!-- custom css-->
    <link rel="stylesheet" href="<%= request.getContextPath()%>/branding/customcss">

    <script type="text/javascript">
        function checkVisibility() {
            <c:if test='${not empty embedded}'>
            ${'#embeddedCode'}.
            hide();
            </c:if>
            var factorType = $('#factor option:selected').val();
            if ('EMAIL' === factorType) {
                $('#emailField').show();
            } else {
                $('#emailField').hide();

            }
            if ('SMS' === factorType) {
                $('#smsField').show();
            } else {
                $('#smsField').hide();
            }

            if ('SOFTWARE_TOKEN' === factorType) {
                <c:if test='${not empty embedded}'>
                ${'#embeddedCode'}.
                show();
                </c:if>
            } else {
                <c:if test='${not empty embedded}'>
                ${'#embeddedCode'}.
                hide();
                </c:if>
            }
        }
    </script>
</head>
<body onload="checkVisibility();">

<div class="login-wrapper">
    <div class="logo">
        <img src="<%= request.getContextPath()%>/branding/loginlogo.png" style="max-width: 100%;">
    </div>
    <header class="text-center">
        <strong>Enroll</strong>
    </header>

    <c:if test="${not empty error}">
        <div id="errorMessage" style="color: red">
                ${error}
        </div>
    </c:if>

    <c:if test='${empty embedded}'>
        <form id="enroll-form" action="<%= request.getContextPath() %>/mfa/enroll" method="post">
            <div class="list-group">
                <div class="list-group-item">
                    <select id="factor"
                            name="factor"
                            onchange="checkVisibility();"
                            class="form-control no-border">
                        <c:forEach items="${factors}" var="factor">
                            <option value="${fn:toUpperCase(factor.factorType)}">${fn:toUpperCase(factor.factorType)}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="list-group">
                <div id="smsField" class="list-group-item" style="display:none;">
                    <label for="phoneNumber">Phone Number:</label>
                    <div class="form-group">
                        <input id="phoneNumber"
                               name="phoneNumber"
                               type="text"
                               placeholder="Enter phone number"
                               class="form-control"/>
                    </div>
                </div>

                <div id="emailField" class="list-group-item" style="display:none;">
                    <div class="form-group">
                        <label for="emailAddress">Email Address:</label>
                        <input id="emailAddress"
                               name="emailAddress"
                               type="email"
                               placeholder="Enter email address"
                               class="form-control"/>
                    </div>
                </div>
            </div>

            <button id="submit" type="submit" class="btn btn-lg btn-primary btn-block">Submit</button>
            <a href="<c:url value="/logout" />" class="btn btn-lg btn-secondary btn-block">Cancel</a>
        </form>
    </c:if>

    <c:if test='${not empty embedded}'>
        <form id="enroll-form" action="<%= request.getContextPath() %>/mfa/enroll/confirm" method="post">
            <div class="list-group">
                <div class="list-group-item">
                    <select id="factorHref"
                            name="factorHref"
                            class="form-control no-border">
                        <c:forEach items="${factors}" var="factor">
                            <option value="${factor.links.activate.href}">${fn:toUpperCase(factor.factorType)}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="list-group">
                <div id="embeddedCode" class="list-group-item">
                    <input id="embedded"
                           name="embedded"
                           type="text"
                           disabled
                           class="form-control"
                           value="${embedded}"/>
                </div>
                <div id="passCodeField" class="list-group-item">
                    <input id="passCode"
                           name="passCode"
                           type="text"
                           maxlength="30"
                           minlength="1"
                           class="form-control"/>
                </div>
            </div>

            <button id="submit" type="submit" class="btn btn-lg btn-primary btn-block">Submit</button>
            <a href="<c:url value="/logout" />" class="btn btn-lg btn-secondary btn-block">Cancel</a>
        </form>
    </c:if>

</div>

<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><a
                    href="http://www.arkcase.com/"><span>ArkCase</span></a><br>&copy;<span>2014, 2015, 2016, 2017</span>
            </small>
        </p>
    </div>
</footer>
</body>
</html>
