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
	<title>You have been logged out. | ArkCase</title>

	<link rel="stylesheet" href="<%= request.getContextPath()%>/lib/bootstrap/dist/css/bootstrap.css">
	<link rel="stylesheet" href="<%= request.getContextPath()%>/assets/css/login.css">
</head>
<body>
<div class="login-wrapper">
    <div class="logo">
        <img src="<%= request.getContextPath()%>/branding/loginlogo.png" style="max-width: 100%;">
    </div>

    <header class="text-center">
        <div class="alert alert-danger">
            <c:choose>
                <c:when test="${'2'.equals(param.login_error)}">
                    Your session has been invalidated due to concurrent session limit!
                </c:when>
                <c:otherwise>
                    You have been logged out.
                </c:otherwise>
            </c:choose>
        </div>
    </header>

	<p></p>

	<p class="text-center"><a href="<c:url value="/" />">Click here to return to ArkCase</a></p>

</div>

<footer id="footer">
	<div class="text-center padder">
		<p>
			<small><span>ArkCase</span><br>&copy;<span>2014, 2015, 2016</span></small>
		</p>
	</div>
</footer>
</body>
</html>
