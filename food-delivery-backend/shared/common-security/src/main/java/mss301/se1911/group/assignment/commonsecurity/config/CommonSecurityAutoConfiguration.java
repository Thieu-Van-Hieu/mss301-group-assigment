package mss301.se1911.group.assignment.commonsecurity.config;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.commonsecurity.filter.GatewayHeaderAuthFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <h2>Common Security Auto-Configuration</h2>
 * <p>
 * Cấu hình bảo mật tự động (Auto-Configuration) dùng chung cho toàn bộ các Microservices
 * trong hệ thống. Lớp này thiết lập một cơ chế Stateless Security dựa trên filter
 * {@link GatewayHeaderAuthFilter} để xác thực các request đi qua API Gateway.
 * </p>
 *
 * <h3>1. Hướng dẫn cấu hình qua file YAML (Khuyên dùng)</h3>
 * <p>
 * Các service con chỉ cần thêm dependency của module {@code commonsecurity} và cấu hình danh sách
 * các endpoint được phép truy cập tự do (permitAll) trong file {@code application.yml} hoặc {@code application.properties}:
 * </p>
 * <pre>{@code
 * security:
 * permit-all-paths:
 * - "/api/v1/auth/**"
 * - "/api/v1/public/**"
 * - "/v3/api-docs/**"
 * - "/swagger-ui/**"
 * }</pre>
 * <p><i>Lưu ý:</i> Nếu không cấu hình, danh sách mặc định sẽ luôn chứa {@code /api/v1/auth/**}.</p>
 *
 * <h3>2. Hướng dẫn Ghi đè (Override) bằng Code</h3>
 * <p>
 * Trong trường hợp một service con có logic phân quyền phức tạp hơn (ví dụ: cần phân quyền theo Role,
 * cấu hình OAuth2, Cors,...), bạn hoàn toàn có thể tự định nghĩa một {@link SecurityFilterChain} riêng
 * tại service đó. Nhờ có cấu hình {@link ConditionalOnMissingBean}, Spring Boot sẽ ưu tiên Bean của
 * service con và tự động vô hiệu hóa cấu hình mặc định này.
 * </p>
 * <p>Ví dụ cách ghi đè tại service con:</p>
 * <pre>{@code
 * @Configuration
 * @EnableWebSecurity
 * public class LocalSecurityConfig {
 *
 *     @Bean
 *     public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
 *         http
 *                 .csrf(AbstractHttpConfigurer::disable)
 *                 .authorizeHttpRequests(auth -> auth
 *                         .requestMatchers("/api/v1/special/**").hasRole("ADMIN")
 *                         .anyRequest().authenticated()
 *                 )
 *                 .sessionManagement(session -> session
 *                         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
 *                 )
 *                 .addFilterBefore(new GatewayHeaderAuthFilter(), UsernamePasswordAuthenticationFilter.class);
 *
 *         return http.build();
 *     }
 * }
 * }</pre>
 *
 * @author Thiều Văn Hiếu
 * @see CommonSecurityProperties
 * @see GatewayHeaderAuthFilter
 * @since 1.0.0
 */
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(CommonSecurityProperties.class)
@RequiredArgsConstructor
public class CommonSecurityAutoConfiguration {

    private final CommonSecurityProperties properties;

    /**
     * Cấu hình chuỗi lọc bảo mật mặc định (Default Security Filter Chain).
     * <p>
     * Hàm này thực hiện các nhiệm vụ:
     * <ul>
     * <li>Vô hiệu hóa CSRF (do hệ thống sử dụng Stateless API).</li>
     * <li>Cấu hình Session Management là {@link SessionCreationPolicy#STATELESS}.</li>
     * <li>Nạp động danh sách các endpoint được {@code permitAll} từ cấu hình YAML.</li>
     * <li>Bắt buộc tất cả các request còn lại phải được xác thực.</li>
     * <li>Tích hợp {@link GatewayHeaderAuthFilter} vào trước filter xác thực mặc định của Spring Security.</li>
     * </ul>
     * </p>
     *
     * @param http đối tượng {@link HttpSecurity} để xây dựng các cấu hình bảo mật.
     * @return {@link SecurityFilterChain} chuỗi lọc bảo mật đã được cấu hình hoàn chỉnh.
     * @throws Exception nếu có lỗi xảy ra trong quá trình cấu hình HTTP Security.
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain commonSecurityFilterChain(HttpSecurity http) throws Exception {

        // Chuyển đổi List<String> từ file yml thành mảng String cho requestMatchers
        String[] paths = properties.getPermitAllPaths().toArray(new String[0]);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(paths).permitAll() // Nạp danh sách động từ YAML
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new GatewayHeaderAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}