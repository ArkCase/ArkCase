<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en" class="app">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@include file="/WEB-INF/tagf/global.tagf" %>
    <title>ACM | Armedia Case Management</title>
</head>
<body>
    <section id="content" class="m-t-lg wrapper-md animated fadeInUp">
        <div class="container aside-xl ">
            <a class="navbar-brand block" href="portal.html"><img src="<c:url value='/resources/images/logo.png'/>" ></a>
            <section class="m-b-lg">
                <header class="wrapper text-center">
                    <strong>Enter your username and password.</strong>
                </header>
                <p/>
                <c:if test="${not empty param.login_error}">
                    <div class="error">
                        Your login attempt was not successful, try again.<br />
                        Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                    </div>
                </c:if>

                <form action="<%= request.getContextPath()%>/j_spring_security_check" method="post">
                    <div class="list-group">
                        <div class="list-group-item">
                            <c:if test="${not empty param.login_error}">
                                <c:set var="username" value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"/>
                            </c:if>
                            <input type="text" name="j_username" value="${username}" placeholder="Username" class="form-control no-border"/>
                        </div>
                        <div class="list-group-item">
                            <input type="password" name="j_password" placeholder="Password" class="form-control no-border"/>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-lg btn-primary btn-block">Log In</button>
                    <div class="text-center m-t m-b"><a href="#"><small>Forgot password?</small></a></div>

                </form>
            </section>
        </div>
    </section>


    <!-- footer -->
    <footer id="footer">
        <div class="text-center padder">
            <p>
                <small>Armedia Case Management 3.0 <br>&copy; 2014</small>
            </p>
        </div>
    </footer>
    <!-- / footer -->
    <script src="<c:url value='/resources/js/jquery.min.js'/>"></script>
    <!-- Bootstrap -->
    <script src="<c:url value='/resources/js/bootstrap.js'/>"></script>
    <!-- App -->
    <script src="<c:url value='/resources/js/app.js'/>"></script>
    <script src="<c:url value='/resources/js/slimscroll/jquery.slimscroll.min.js'/>"></script>
    <script src="<c:url value='/resources/js/app.plugin.js'/>"></script>

</body>
</html>

