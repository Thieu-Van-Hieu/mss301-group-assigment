package mss301.se1911.group.assignment.identityservice.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonsecurity.dto.request.ExchangeCodeRequest;
import mss301.se1911.group.assignment.commonsecurity.dto.response.TokenResponse;
import mss301.se1911.group.assignment.commonsecurity.dto.response.UserValidateResponse;
import mss301.se1911.group.assignment.commonsecurity.filter.UserPrincipal;
import mss301.se1911.group.assignment.identityservice.api.dto.request.UserRegisterRequest;
import mss301.se1911.group.assignment.identityservice.api.dto.response.LoginUrlResponse;
import mss301.se1911.group.assignment.identityservice.application.command.LogoutCommand;
import mss301.se1911.group.assignment.identityservice.application.command.RegisterUserCommand;
import mss301.se1911.group.assignment.identityservice.application.query.ExchangeCodeQuery;
import mss301.se1911.group.assignment.identityservice.application.query.RefreshTokenQuery;
import mss301.se1911.group.assignment.identityservice.application.query.ValidateTokenQuery;
import mss301.se1911.group.assignment.identityservice.application.usecase.*;
import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class IdentityController {

    private final RegisterUserUseCase registerUserUseCase;
    private final ExchangeCodeUseCase exchangeCodeUseCase;
    private final ValidateTokenUseCase validateTokenUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final GetLoginUrlUseCase getLoginUrlUseCase;
    private final LogoutUseCase logoutUseCase;

    @GetMapping("/login-url")
    public ResponseEntity<LoginUrlResponse> getLoginUrl() {
        return ResponseEntity.ok(getLoginUrlUseCase.execute());
    }

    @GetMapping("/me")
    public ResponseEntity<UserPrincipal> me(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userPrincipal);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.fullName(),
                request.phoneNumber(),
                request.email(),
                request.password(),
                request.role().toLowerCase()
        );

        Account registeredAccount = registerUserUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Đăng ký thành công! ID tài khoản: " + registeredAccount.getId().value());
    }

    @PostMapping("/exchange-code")
    public ResponseEntity<TokenResponse> exchangeCode(
            @RequestBody ExchangeCodeRequest request,
            HttpServletResponse httpResponse) {

        ExchangeCodeQuery query = ExchangeCodeQuery.builder()
                .code(request.code())
                .redirectUri(request.redirectUri())
                .build();

        TokenResponse response = exchangeCodeUseCase.execute(query);

        // 🎯 Đính kèm Refresh Token từ Keycloak vào HttpOnly Cookie
        if (response.refreshToken() != null && !response.refreshToken().isBlank()) {
            setRefreshCookie(httpResponse, response.refreshToken(), response.refreshExpiresIn());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserValidateResponse> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        String token;

        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = authHeader.substring(7).trim();
        } else {
            token = authHeader != null ? authHeader.trim() : "";
        }

        ValidateTokenQuery query = new ValidateTokenQuery(token);
        UserValidateResponse response = validateTokenUseCase.execute(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse httpResponse) { // 🎯 Tiêm HttpServletResponse để cập nhật xoay vòng Cookie

        // 🎯 1. Nếu F5 hoặc Refresh ngầm mà không mang kèm theo Cookie -> Chặn luôn từ vòng gửi xe bằng 401
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshTokenQuery query = RefreshTokenQuery.builder()
                .refreshToken(refreshToken.trim())
                .build();

        TokenResponse response = refreshTokenUseCase.execute(query);

        // 🎯 2. Đút mã Refresh Token mới (Nếu Keycloak kích hoạt Token Rotation) vào lại Cookie của trình duyệt
        if (response.refreshToken() != null && !response.refreshToken().isBlank()) {
            setRefreshCookie(httpResponse, response.refreshToken(), response.refreshExpiresIn());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse httpResponse) {

        logoutUseCase.execute(LogoutCommand.builder()
                .refreshToken(refreshToken)
                .build());

        setRefreshCookie(httpResponse, "", 0);
        return ResponseEntity.ok().build();
    }

    /**
     * Hàm trợ giúp (Helper) cấu hình thông số và đẩy Cookie xuống trình duyệt.
     */
    private void setRefreshCookie(HttpServletResponse response, String token, long maxAgeInSeconds) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .path("/")
                .secure(false) // Giữ nguyên false cho HTTP local giống code cũ của bạn
                .sameSite("Lax") // Hoặc "None" nếu bạn đổi .secure(true) và chạy HTTPS
                .maxAge(maxAgeInSeconds)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}