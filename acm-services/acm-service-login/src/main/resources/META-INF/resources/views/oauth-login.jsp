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
    <!-- Set the hash in localStorage, so when the user logs in the Angular application opens that state -->
    <script type="text/javascript">
	    function addUrlHashToLocalStorageAndRedirectToOAuth2Login() {
	        if (window.location.hash != '#!/welcome' && window.location.hash != '#!/goodbye') {
				//localStorage.redirectURL = window.location.hash;
				sessionStorage.redirectURL = window.location.hash;
	        } else {
				//localStorage.removeItem('redirectURL');
				sessionStorage.removeItem('redirectURL');
	        }
	        // redirect to real OAuth2 login page that actually authenticates the user
	        window.location.href = "<%= request.getContextPath()%>/oauth2/authorization/".concat("${oidcRegistration}");
	        console.log(window.location.href);
	    }
	    window.onload = addUrlHashToLocalStorageAndRedirectToOAuth2Login;
    </script>
</head>
<body>
	<!-- This page is only used to save the hash of a not authorized request in local storage -->
</body>
</html>
