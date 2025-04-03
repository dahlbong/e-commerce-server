package kr.hhplus.be.server.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.dto.WalletRequest;
import kr.hhplus.be.server.domain.model.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@Tag(name = "Wallet API", description = "사용자 지갑(잔액) 관리 API")
public class WalletController {

    private static Wallet dummyWallet = new Wallet("user1", 500.0);

    @Operation(summary = "사용자 잔액 조회", description = "사용자의 현재 지갑 잔액을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"User not found\"}")))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        if(dummyWallet.getUserId().equals(userId)) {
            return ResponseEntity.ok(dummyWallet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "지갑 충전", description = "사용자의 지갑에 금액을 충전합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "충전 성공"),
            @ApiResponse(responseCode = "400", description = "충전 금액이 0 이하일 경우",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"Invalid amount\"}"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"User not found\"}")))
    })
    @PostMapping("/topup")
    public ResponseEntity<Wallet> topUpWallet(@RequestBody WalletRequest walletRequest) {
        if(walletRequest.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if(!dummyWallet.getUserId().equals(walletRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 충전 요청에 따라 잔액 업데이트 필요

        return ResponseEntity.ok(dummyWallet);
    }
}
