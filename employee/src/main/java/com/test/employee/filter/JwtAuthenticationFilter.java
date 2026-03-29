package com.test.employee.filter;

import com.test.employee.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // after validating jwt refresh token in jwtservice
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Intercept the request

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

//        if (authHeader == null && !authHeader.startsWith("Bearer ")) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt=getJwtFormRequest(request);

        // check if the token is valid
        // if not pass the request and response to next in filter chain
        if (!jwtService.validateToken(jwt)){
            filterChain.doFilter(request, response);
            return;
        }

        username=jwtService.extractUsernameFromToken(jwt);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

//            if(jwtService.validateToken(jwt, userDetails)) {
            if(jwtService.validateTokenForUsers(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new

                        UsernamePasswordAuthenticationToken(
                                userDetails,
                        null,
                                userDetails.getAuthorities()
                );

                // not for just validation but for details too
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

            }





        }

        filterChain.doFilter(request, response);

        System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        System.out.println("Principal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Do the validation

        // Authenticate the User

    }

    private String getJwtFormRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        // Bearer <token=7>
        return authHeader.substring(7);
    }



}
