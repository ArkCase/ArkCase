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
<%--
User: riste.tutureski
Date: 8/5/2015
Time: 12:44
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>
    <!-- Fav Icon -->
    <link href="<%= request.getContextPath()%>/modules/core/img/brand/favicon.png" rel="shortcut icon" type="image/x-png">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="<%= request.getContextPath()%>/node_modules/@bower_components/bootstrap/dist/js/bootstrap.js"></script>
    <c:if test="${warningEnabled}">
        <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
        <link rel="stylesheet"
              href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css">
    </c:if>

    <!-- Set the hash in localStorage, so when the user logs in the Angular application opens that state -->
    <script type="text/javascript">
        function addUrlHashToLocalStorage() {
            if (window.location.hash != '#!/welcome' && window.location.hash != '#!/goodbye') {
                sessionStorage.redirectURL = window.location.hash;
            } else {
                sessionStorage.removeItem('redirectURL');
            }
        }

        window.onload = addUrlHashToLocalStorage;
    </script>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/node_modules/@bower_components/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
    <!-- custom css-->
    <link rel="stylesheet" href="<%= request.getContextPath()%>/branding/customcss">
</head>
<body>

<!-- Forgot Username Modal -->
<div class="modal fade" id="forgot-username-modal" role="dialog">
    <div class="modal-dialog modal-sm">
        <!-- Modal content-->
        <div class="modal-content">
            <form id="forgot-username">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Forgot Username</h4>
                </div>
                <div class="modal-body">
                    <p>Please enter your email address associated with your account and we will email your username.</p>
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" class="form-control" id="email" placeholder="Enter Email Address" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success pull-right" id="forgot-username-btn">Forgot Username</button>
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Forgot Password Modal -->
<div class="modal fade" id="forgot-password-modal" role="dialog">
    <div class="modal-dialog modal-sm">
        <!-- Modal content-->
        <div class="modal-content">
            <form id="forgot-password">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4 class="modal-title">Forgot Password</h4>
                </div>
                <div class="modal-body">
                    <p>Please enter your username address and we will email you a reset password link.</p>
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" class="form-control" id="username" placeholder="Enter Username">
                    </div>
                    <div class="form-group">
                        <label for="mail">Email Address</label>
                        <input type="email" class="form-control" id="mail" placeholder="Enter Email Address" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success pull-right" id="forgot-password-btn">Forgot Password</button>
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="login-wrapper">
    <div class="logo">
        <img src="<%= request.getContextPath()%>/branding/loginlogo.png" style="max-width: 100%;">
    </div>
    <header class="text-center">
        <strong>Enter your username and password.</strong>
    </header>
    <p></p>

    <c:if test='${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}'>

        <c:choose>
            <c:when test='${"BadCredentialsException: Empty Username".equals(sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message)}'>
                <div class="alert alert-danger">Must enter user name</div>
            </c:when>

            <c:when test='${"BadCredentialsException: Empty Password".equals(sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message)}'>
                <div class="alert alert-danger">Must enter a password</div>
            </c:when>

            <c:when test='${"BadCredentialsException: Bad credentials".equals(sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message)}'>
                <div class="alert alert-danger">Bad credentials. Please try again</div>
            </c:when>

            <c:otherwise>
                <div class="alert alert-danger">${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</div>
            </c:otherwise>

        </c:choose>

        <c:remove var="SPRING_SECURITY_LAST_EXCEPTION" scope="session"/>

    </c:if>

    <c:if test="${'1'.equals(param.login_error)}">
        <div class="alert alert-danger">Your session has expired!</div>
    </c:if>

    <c:if test="${'2'.equals(param.login_error)}">
        <div class="alert alert-danger">Your session has been invalidated due to concurrent session limit!</div>
    </c:if>

    <c:if test="${param.logout != null}">
        <div class="alert alert-success">You have been logged out successfully.</div>
    </c:if>

    <c:if test="${warningEnabled}">
        <div id="dialog" class="content-one"><p class="modal-body">${warningMessage}</p></div>
    </c:if>

    <div id="forgot-username-success" style="display:none" class="alert alert-success">
        We sent your username to the address you provided. If the address is valid, you should receive it in a few minutes.
    </div>

    <div id="forgot-username-error" style="display:none" class="alert alert-danger">
        Valid user with this email does not exist in the system.
    </div>

    <div id="forgot-password-success" style="display:none" class="alert alert-success">
        We sent you reset password link. You should receive it in a few minutes.
    </div>

    <div id="forgot-password-error" style="display:none" class="alert alert-danger">
        Valid user with this username and email address does not exist in the system.
    </div>

    <div id="forgot-generic-error" style="display:none" class="alert alert-danger">
      Server error.
    </div>


    <form id="login-form" action="<%= request.getContextPath()%>/j_spring_security_check" method="post">
        <div class="list-group">
            <div class="list-group-item">
                <input id="j_username"
                       type="text"
                       pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$"
                       title="You must provide a domain, for example user@example.com"
                       name="j_username"
                       value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"
                       placeholder="Username"
                       class="form-control no-border">
            </div>
            <div class="list-group-item">
                <input
                        id="j_password"
                        type="password"
                        name="j_password"
                        placeholder="Password"
                        class="form-control no-border"
                >
            </div>
            <c:if test="${warningEnabled}">

                <!-- add an input checkbox -->
                <div class="list-group-item">
                    <input type="checkbox" id="j_terms"/>
                    <label for="j_terms">I acknowledge <a href="#" class="expand-one">this warning.</a></label>
                </div>
            </c:if>
        </div>

        <button id="submit" type="submit" class="btn btn-lg btn-primary btn-block">Log In</button>
        <c:if test="${!isSsoEnv}">
            <p></p>
            <div class="pull-left">
                <a data-toggle="modal" href="#forgot-username-modal">Forgot Username</a>
            </div>
            <div class="pull-right">
                <a data-toggle="modal" href="#forgot-password-modal">Forgot Password</a>
            </div>
        </c:if>
    </form>
</div>

<div class="text-center padder">
    <p><small><a href="http://www.arkcase.com/"><span>ArkCase</span></a></small></p>
</div>

<footer id="footer">
    <div class="text-center padder">
        <p>Product Version: ${version["Implementation-Version"]}</p>
        <c:if test="${not empty version['extensionVersion']}">
            <p>Extension Version: ${version["extensionVersion"]}</p>
        </c:if>
    </div>
</footer>
</body>
<c:if test="${warningEnabled}">
    <script type="text/javascript">

        $(function () {
            $('#submit').attr('disabled', 'disabled');
            $('#j_terms').click(function () {
                if (!$(this).is(':checked')) {
                    sessionStorage.removeItem('warningAccepted');
                    $('#submit').attr('disabled', 'disabled');
                } else {
                    sessionStorage.setItem('warningAccepted', true);
                    $('#submit').removeAttr('disabled');
                }
            });
            $('.expand-one').click(function () {
                showPopup();
            });
            showPopup();
        });

        function showPopup() {
            $("#dialog").dialog({
                modal: true,
                width: '80%'
            });
        }
    </script>
</c:if>
<script type="text/javascript">
    $(function () {

        $('#forgot-username-modal').on('shown.bs.modal', function () {
            $('#forgot-username-success').hide();
            $('#forgot-username-error').hide();
            $('#forgot-password-success').hide();
            $('#forgot-password-error').hide();
            $('#forgot-generic-error').hide();
        });

        $('#forgot-username').on('submit', function (e) {
            e.preventDefault();
            $('#forgot-username-success').hide();
            $('#forgot-username-error').hide();
            $('#forgot-username-modal').modal('hide');
            var email = $("#email").val();
            $.post("<%= request.getContextPath()%>/forgot-username", {email: email})
                .always(function (data) {
                    if (data.status === 200) {
                        $('#forgot-username-success').show();
                    } else if (data.status === 404) {
                        $('#forgot-username-error').show();
                    } else {
                        $('#forgot-generic-error').show();
                    }
                });
        });

        $('#forgot-password-modal').on('shown.bs.modal', function () {
            $('#forgot-password-success').hide();
            $('#forgot-password-error').hide();
            $('#forgot-username-success').hide();
            $('#forgot-username-error').hide();
            $('#forgot-generic-error').hide();
        });

        $('#forgot-password').on('submit', function (e) {
            e.preventDefault();
            $('#forgot-password-modal').modal('hide');
            var username = $("#username").val();
            var email = $("#mail").val();
            $.post("<%= request.getContextPath()%>/forgot-password", {userId: username, email: email})
                .always(function (data) {
                    if (data.status === 200) {
                        $('#forgot-password-success').show();
                    } else if (data.status === 404) {
                        $('#forgot-password-error').show();
                    } else {
                        $('#forgot-generic-error').show();
                    }
                });
        });
    });

</script>

</html>
