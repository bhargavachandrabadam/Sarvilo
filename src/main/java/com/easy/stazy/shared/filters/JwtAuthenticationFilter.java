package com.easy.stazy.shared.filters;


import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.shared.security.CustomUserDetails;
import com.easy.stazy.shared.security.UsersDetailsServiceImpl;
import com.easy.stazy.shared.common.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UsersDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    /**
     * Authenticates the given request using the JWT token from the Authorization header.
     * If the token is valid, the SecurityContextHolder is updated with the corresponding
     * principal and authorities.
     *
     * @param request     the HttpServletRequest to authenticate
     * @param response    the HttpServletResponse to send back
     * @param filterChain the FilterChain to continue the request
     * @throws ServletException if any error occurs during the authentication process
     * @throws IOException      if any error occurs during the authentication process
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final var jwtToken = parseJwtToken(request);
        try {
            if (jwtToken != null && jwtUtils.validateToken(jwtToken)) {
                final var username = jwtUtils.getUsernameFromToken(jwtToken);
                final var userDetails = userDetailsService.loadUserByUsername(username);
                final var principal = customUserDetailsToPrincipal((CustomUserDetails) userDetails);
                final var authentication = new UsernamePasswordAuthenticationToken(principal, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception ex) {
            log.error("JWT authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private CustomPrincipal customUserDetailsToPrincipal(CustomUserDetails customUserDetails) {
        if (customUserDetails.usersEntity() != null) {
            var user = customUserDetails.usersEntity();
            Long id = user.getId();
            String phone = user.getPhoneNumber();
            String role = (user.getRole() != null && user.getRole().toString() != null) ? user.getRole().toString() : RoleType.USER.toString();
            return new CustomPrincipal(id, phone, role);
        } else if (customUserDetails.pgOwnerEntity() != null) {
            var owner = customUserDetails.pgOwnerEntity();
            Long id = owner.getId();
            String phone = owner.getOwnerContactNumber();
            String role = RoleType.OWNER.toString();
            return new CustomPrincipal(id, phone, role);
        }
        return new CustomPrincipal(null, customUserDetails.getUsername(), "UNKNOWN");
    }

    /**
     * Parses the given request to extract the JWT token from the Authorization header
     *
     * @param request the HttpServletRequest to parse
     * @return the JWT token if found, null otherwise
     */
    private String parseJwtToken(HttpServletRequest request) {
        final var headerAuth = request.getHeader("Authorization");
        return (headerAuth != null && headerAuth.startsWith("Bearer ")
                ? headerAuth.substring(7) : null);
    }


    public record CustomPrincipal(Long id, String phoneNumber, String role) {

    }
}
