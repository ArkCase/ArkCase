<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>ACM | ArkCase | User Interface</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="<%= request.getContextPath()%>/lib/bootstrap/dist/js/bootstrap.js"></script>

    <link rel="stylesheet" href="<%= request.getContextPath()%>/lib/bootstrap/dist/css/bootstrap.css">
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
        <strong>Select a Factor</strong>
    </header>

    <c:if test="${not empty error}">
        <div id="errorMessage" style="color: red">
                ${error}
        </div>
    </c:if>

    <form id="login-form" action="<%= request.getContextPath()%>/mfa/auth" method="post">
        <div class="list-group">
            <div class="list-group-item">
                <select id="factorId"
                        name="factorId"
                        class="form-control no-border">
                    <c:forEach items="${factors}" var="factor">
                        <option value="${factor.id}">${factor.factorSummary}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="hidden">
                <input id="userId"
                       type="hidden"
                       name="userId"
                       class="form-control no-border"
                       value="${user.id}"/>
            </div>
        </div>

        <button id="submit" type="submit" class="btn btn-lg btn-primary btn-block">Select</button>
        <p></p>
        <div class="pull-left">
            <a href="<c:url value="/logout"/>">Cancel</a>
        </div>
        <div class="pull-right">
            <a href="<c:url value="${enrollmentUrl}"/>">Enroll Another Method?</a>
        </div>
    </form>
</div>

<footer id="footer">
    <div class="text-center padder">
        <p>
            <small><a
                    href="http://www.arkcase.com/"><span>ArkCase</span></a>
                ( <a href="${supportLink}">Contact Support</a> )
                <br>&copy;<span>2014, 2015, 2016, 2017</span>
            </small>
        </p>
    </div>
</footer>
</body>
</html>
