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
  <title data-i18n="login:page-title">ACM | ArkCase | User Interface</title>
</head>
<body>
    ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}

    <form action="<%= request.getContextPath()%>/j_spring_security_check" method="post">
          <c:if test="${not empty param.login_error}">
            <c:set var="username" value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"/>
          </c:if>
          <input type="text" name="j_username" value="${username}" />
          <br />
          <input type="password" name="j_password" />
          <br />
          <button type="submit">Log In</button>
    </form>
</body>
</html>
