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
    <c:if test="${warningEnabled}">

        <!-- add jquery link -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
        <link rel="stylesheet"
              href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/smoothness/jquery-ui.css">
    </c:if>

    <!-- Set the hash in localStorage, so when the user logs in the Angular application opens that state -->
    <script type="text/javascript">
        function addUrlHashToLocalStorage() {
            if (window.location.hash != '#!/welcome' && window.location.hash != '#!/goodbye') {
                //localStorage.redirectURL = window.location.hash;
                sessionStorage.redirectURL = window.location.hash;
            } else {
                //localStorage.removeItem('redirectURL');
                sessionStorage.removeItem('redirectURL');
            }
        }
        window.onload = addUrlHashToLocalStorage;

    </script>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/lib/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
</head>
<body>
<div class="login-wrapper">
    <div class="logo">
        <img src="<%= request.getContextPath()%>/branding/loginlogo.png" style="max-width: 100%;">
    </div>
    <header class="text-center">
        <strong>Enter your username and password.</strong>
    </header>
    <p></p>

    <c:if test='${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}'>
        <div class="alert alert-danger">${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</div>
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
    <form id="login-form" action="<%= request.getContextPath()%>/j_spring_security_check" method="post">
        <div class="list-group">
            <div class="list-group-item">
                <input id="j_username"
                       type="text"
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
    </form>
</div>

<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><a href="http://www.arkcase.com/"><span>ArkCase</span></a><br>&copy;<span>2014, 2015, 2016</span></small>
        </p>
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

</html>
