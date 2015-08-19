<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  User: riste.tutureski
  Date: 8/5/2015
  Time: 12:44
--%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/lib/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
</head>
<body>
<div class="login-wrapper">
    <div class="text-center">
        <img src="<%= request.getContextPath()%>/assets/img/logo.png">
    </div>
    <header class="text-center">
        <strong>Enter your username and password.</strong>
    </header>
    <p></p>

    <c:if test='${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}'>
    <div class="alert alert-danger">${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</div>
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
        </div>
        <button id="submit" type="submit" class="btn btn-lg btn-primary btn-block">Log In</button>
    </form>
</div>

<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><span>ArkCase</span><br>&copy;<span>2014, 2015</span></small>
        </p>
    </div>
</footer>
</body>
</html>
