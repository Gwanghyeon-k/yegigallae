package com.umc.yeogi_gal_lae;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class CheckServerStatusController {

    @Operation(description = "Docker 컨테이너 가동 시 헬스 체킹을 위한 컨트롤러")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running");
    }

//    @GetMapping("/")
//    public ResponseEntity<String> rootEndpoint() {
//        return ResponseEntity.ok("Welcome to API Server");
//    }
}
