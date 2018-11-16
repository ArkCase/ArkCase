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
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/node_modules/@bower_components/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
    <!-- custom css-->
    <link rel="stylesheet" href="<%= request.getContextPath()%>/branding/customcss">
</head>
<body>
<div class="login-wrapper">
    <div class="logo">
        <img src="<%= request.getContextPath()%>/branding/loginlogo.png" style="max-width: 100%;">
    </div>
    <header class="text-center">
        <strong>Change password!</strong>
    </header>
    <p></p>
    <c:if test="${error}">
        <div class="alert alert-danger">${errorMsg}</div>
    </c:if>
    <c:choose>
        <c:when test="${success}">
            <div class="alert alert-success">Your password has been changed successfully!</div>
            <div><a href="<%= request.getContextPath()%>/login">Proceed to login</a></div>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${isTokenValid}">
                    <form name="reset-password" action="<%= request.getContextPath()%>/reset-password" method="post">
                        <input type="hidden" name="token" value="${token}"/>
                        <input type="hidden" name="directory" value="${directory}"/>
                        <div class="list-group">
                            <div class="list-group-item">
                                <input
                                        type="password"
                                        name="password"
                                        placeholder="New Password"
                                        class="form-control no-border">
                            </div>
                            <div class="list-group-item">
                                <input
                                        type="password"
                                        name="confirmPassword"
                                        placeholder="Confirm New Password"
                                        class="form-control no-border">
                            </div>
                        </div>
                        <button type="submit" class="btn btn-lg btn-primary btn-block">Reset Password</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-danger">Invalid token!</div>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</div>

<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><a href="http://www.arkcase.com/"><span>ArkCase</span></a><br>&copy;<span>2014, 2015, 2016, 2017</span></small>
        </p>
    </div>
</footer>
</body>
</html>
