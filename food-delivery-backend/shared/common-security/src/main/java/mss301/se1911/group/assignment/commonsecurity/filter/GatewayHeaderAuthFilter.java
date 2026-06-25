package mss301.se1911.group.assignment.commonsecurity.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import mss301.se1911.group.assignment.commonconstants.utils.GatewayConstraints;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Bộ lọc nội bộ trích xuất thông tin định danh từ HTTP Header do API Gateway bàn giao.
 * <p>
 * Bộ lọc thực hiện giải mã các dữ liệu thuộc tính cá nhân đã được mã hóa UTF-8 an toàn từ Header,
 * xây dựng lại đối tượng ngữ cảnh hệ thống {@link UserPrincipal} và thiết lập trạng thái chứng thực
 * cho cấu hình bảo mật Spring Security cục bộ của từng Microservice.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(GatewayConstraints.HEADER_USER_ID);

        if (userId != null && !userId.isEmpty()) {
            String email = request.getHeader(GatewayConstraints.HEADER_EMAIL);
            String rawFullName = request.getHeader(GatewayConstraints.HEADER_FULL_NAME);
            String fullName = (rawFullName != null && !rawFullName.isEmpty())
                    ? URLDecoder.decode(rawFullName, StandardCharsets.UTF_8)
                    : rawFullName;

            String phoneNumber = request.getHeader(GatewayConstraints.HEADER_PHONE);

            boolean active = Boolean.parseBoolean(request.getHeader(GatewayConstraints.HEADER_ACTIVE));
            boolean enabled = Boolean.parseBoolean(request.getHeader(GatewayConstraints.HEADER_ENABLED));

            String rolesHeader = request.getHeader(GatewayConstraints.HEADER_ROLE);
            List<String> roles = (rolesHeader != null && !rolesHeader.isEmpty())
                    ? Arrays.asList(rolesHeader.split(","))
                    : Collections.emptyList();

            UserPrincipal principal = new UserPrincipal(userId, email, fullName, phoneNumber, active, enabled, roles);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}