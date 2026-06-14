package mss301.se1911.group.assignment.identityservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonsecurity.dto.request.ExchangeCodeRequest;
import mss301.se1911.group.assignment.commonsecurity.dto.request.RefreshTokenRequest;
import mss301.se1911.group.assignment.commonsecurity.dto.response.TokenResponse;
import mss301.se1911.group.assignment.commonsecurity.dto.response.UserValidateResponse;
import mss301.se1911.group.assignment.identityservice.api.dto.request.UserRegisterRequest;
import mss301.se1911.group.assignment.identityservice.application.command.RegisterUserCommand;
import mss301.se1911.group.assignment.identityservice.application.query.ExchangeCodeQuery;
import mss301.se1911.group.assignment.identityservice.application.query.RefreshTokenQuery;
import mss301.se1911.group.assignment.identityservice.application.query.ValidateTokenQuery;
import mss301.se1911.group.assignment.identityservice.application.usecase.ExchangeCodeUseCase;
import mss301.se1911.group.assignment.identityservice.application.usecase.RefreshTokenUseCase;
import mss301.se1911.group.assignment.identityservice.application.usecase.RegisterUserUseCase;
import mss301.se1911.group.assignment.identityservice.application.usecase.ValidateTokenUseCase;
import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
        // Map từ REST DTO sang Application Command để đẩy vào UseCase
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
            @RequestBody ExchangeCodeRequest request) {
        ExchangeCodeQuery query = ExchangeCodeQuery.builder()
                .code(request.code())
                .redirectUri(request.redirectUri())
                .build();
        TokenResponse response = exchangeCodeUseCase.execute(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserValidateResponse> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        String token;

        // Kiểm tra an toàn xem header có hợp lệ và bắt đầu bằng Bearer không (không phân biệt hoa thường)
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            // Cắt bỏ 7 ký tự đầu ("Bearer ") và xóa hết khoảng trắng thừa hai đầu
            token = authHeader.substring(7).trim();
        } else {
            // Nếu không có Bearer hoặc rỗng, gán luôn authHeader ban đầu (hoặc xử lý ném lỗi tùy bạn)
            token = authHeader != null ? authHeader.trim() : "";
        }

        ValidateTokenQuery query = new ValidateTokenQuery(token);
        UserValidateResponse response = validateTokenUseCase.execute(query);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenQuery query = RefreshTokenQuery.builder()
                .refreshToken(refreshTokenRequest.refreshToken())
                .build();
        TokenResponse response = refreshTokenUseCase.execute(query);
        return ResponseEntity.ok(response);
    }
}