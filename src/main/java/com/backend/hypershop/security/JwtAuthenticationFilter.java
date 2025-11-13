// JwtAuthenticationFilter.java
package com.backend.hypershop.security;

import com.backend.hypershop.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.hypershop.dto.schema.GlobalResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. Header se token extract karo
            String token = extractTokenFromRequest(request);

            // 2. Token hai aur valid hai
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                
                // 3. Token se userId aur role extract karo
                String userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);

                // 4. Authorities banao (NO DATABASE CALL - Pure Stateless!)
                List<SimpleGrantedAuthority> authorities = 
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // 5. Authentication object banao
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. SecurityContext me set karo
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 7. Next filter ko call karo
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            handleAuthenticationError(response, "Invalid or expired token");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        GlobalResponse<Object> errorResponse = GlobalResponse.failure(message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
