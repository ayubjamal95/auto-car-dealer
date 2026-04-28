package com.autocardealer.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter that extracts JWT token from Authorization header and authenticates the request.
 *
 * This filter runs before the TenantFilter and establishes the security context.
 * Note: Tenant ID and authentication are independent - a user can access multiple tenants
 * if properly authorized.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);

                // Extract roles from token
                // In a real system, you'd load user details from a database
                List<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Set authentication for user: {} with roles: {}", username, authorities);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from Authorization header.
     * Expected format: Bearer <token>
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Extracts authorities from JWT token.
     * In production, you might want to load full user details from database.
     */
    private List<SimpleGrantedAuthority> extractAuthorities(String jwt) {
        // This is simplified - in production you'd parse the token properly
        // or load user details from database
        try {
            io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser()
                    .verifyWith(tokenProvider.key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            String roles = claims.get("roles", String.class);
            if (roles != null && !roles.isEmpty()) {
                return Arrays.stream(roles.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error extracting authorities from token", e);
        }

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}