package mss301.se1911.group.assignment.commonsecurity.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp tiện ích thực hiện chuyển đổi dữ liệu từ khóa mã hóa JWT (Json Web Token) của Keycloak
 * thành danh sách các quyền hạn {@link GrantedAuthority} trong Spring Security.
 *
 * <p>Lớp này bóc tách trường "roles" nằm bên trong đối tượng "realm_access" thuộc phần
 * Payload của Keycloak Token, sau đó định dạng lại cấu trúc quyền với tiền tố "ROLE_".</p>
 *
 * <p>Sử dụng cơ chế Safe Cast (Pattern Matching cho {@code instanceof}) từ Java 16+
 * để đảm bảo an toàn kiểu dữ liệu và tránh lỗi ép kiểu trong quá trình chạy ứng dụng.</p>
 *
 * @author Thiều Văn Hiếu
 * @version 1.0.0
 * @since 2026-05-31
 */
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * Chuyển đổi đối tượng {@link Jwt} nhận được từ Resource Server thành một danh sách
     * các quyền hạn hợp lệ phục vụ cho quá trình phân quyền hệ thống (Authorization).
     *
     * @param jwt Đối tượng chứa thông tin cấu trúc token mã hóa dạng JSON được gửi từ Client.
     * @return Danh sách các quyền hạn {@link Collection} dạng {@link GrantedAuthority}.
     * Trả về một danh sách rỗng (Empty List) nếu không tìm thấy thông tin phân quyền.
     * @throws NullPointerException nếu đối tượng jwt truyền vào có giá trị null.
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        /*
         * Bước 1: Trích xuất thuộc tính "realm_access" từ danh sách thuộc tính (Claims) của JWT.
         * Giá trị trả về ban đầu được lưu ở dạng Object nguyên bản để chuẩn bị kiểm tra kiểu dữ liệu.
         */
        Object realmAccessObj = jwt.getClaims().get("realm_access");

        /*
         * Bước 2: Kiểm tra an toàn (Safe Cast) bằng Pattern Matching cho instanceof.
         * Nếu cấu trúc dữ liệu thực sự thuộc kiểu Map, Java sẽ tự động ép kiểu và gán
         * vào biến 'map' cục bộ mà không sinh cảnh báo Unchecked Cast.
         */
        if (realmAccessObj instanceof Map<?, ?> map) {

            /*
             * Bước 3: Lấy mảng dữ liệu chứa danh sách vai trò (roles) ra khỏi bản đồ thông tin.
             */
            Object rolesObj = map.get("roles");

            /*
             * Bước 4: Kiểm tra an toàn cấu trúc của danh sách vai trò.
             * Đảm bảo đối tượng nhận về thuộc kiểu List trước khi tiến hành xử lý luồng dữ liệu (Stream).
             */
            if (rolesObj instanceof List<?> rolesList) {
                return rolesList.stream()
                        /*
                         * Bước 5: Lọc bỏ các phần tử không phải định dạng văn bản (String)
                         * để phòng tránh các lỗi sai lệch dữ liệu ngoài ý muốn.
                         */
                        .filter(String.class::isInstance)
                        /*
                         * Bước 6: Chuyển đổi kiểu dữ liệu và bổ sung tiền tố "ROLE_"
                         * theo đúng tiêu chuẩn nhận diện phân quyền mặc định của Spring Security.
                         * Ví dụ: "CUSTOMER" chuyển đổi thành "ROLE_CUSTOMER".
                         */
                        .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                        /*
                         * Bước 7: Thu thập toàn bộ kết quả đã xử lý và đóng gói vào cấu trúc danh sách.
                         */
                        .collect(Collectors.toList());
            }
        }

        /*
         * Bước 8: Trả về một danh sách rỗng bất biến nếu cấu trúc Token không hợp lệ
         * hoặc không chứa bất kỳ phân quyền nào từ máy chủ Keycloak.
         */
        return Collections.emptyList();
    }
}
