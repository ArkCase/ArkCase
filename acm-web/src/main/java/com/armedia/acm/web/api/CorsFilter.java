/**
 * 
 */
package com.armedia.acm.web.api;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author riste.tutureski
 *
 */
public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
	{
		// TODO: Create list of allowed origins
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod()))
		{
			response.addHeader("Access-Control-Allow-Method", "GET,POST,PUT,DELETE");
			response.addHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
		}
		
		filterChain.doFilter(request, response);
	}

}
