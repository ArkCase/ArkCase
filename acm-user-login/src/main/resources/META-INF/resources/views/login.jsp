<%@include file="/fragments/header.jspf" %>
</head>
<body>

    <div class="span-10 login">
        <div class="login-box main-content">
            <header>
                <img src="<c:url value='/resources/images/acm-logo.png'/>"
                     width="103" height="103" align="center"
                     alt="Armedia Case Management"/>
            </header>
            <p/>
            <c:if test="${not empty param.login_error}">
                <div class="error">
                    Your login attempt was not successful, try again.<br />
                    Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                </div>
            </c:if>

            <div class="section">
                <div class="message info full">

                    Enter your username and password.
                </div>
                <form name="loginForm" action="<%= request.getContextPath()%>/j_spring_security_check" method="post" class="clearfix">
                    <p>
                        <c:if test="${not empty param.login_error}">
                            <c:set var="username" value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"/>
                        </c:if>

                        <input type="text" name="j_username" value="${username}" placeholder="Username" class="full"/>
                    </p>
                    <p>
                        <input type="password" name="j_password" placeholder="Password" class="full"/>
                    </p>
                    <p>
                        <button class="button button-gray fr" type="submit">Login</button>
                        <!--<input name="submit" type="submit" value="Login" />-->
                    </p>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
