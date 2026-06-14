package mss301.se1911.group.assignment.identityservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.identityservice.api.dto.request.UserRegisterRequest;
import mss301.se1911.group.assignment.identityservice.application.command.RegisterUserCommand;
import mss301.se1911.group.assignment.identityservice.application.usecase.RegisterUserUseCase;
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
}