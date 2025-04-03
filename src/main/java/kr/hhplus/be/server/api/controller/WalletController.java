package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.dto.WalletRequest;
import kr.hhplus.be.server.domain.model.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    // 더미 데이터: 예시 사용자(user1)의 잔액
    private static Wallet dummyWallet = new Wallet("user1", 500.0);

    // 잔액 조회: GET /api/wallet/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String userId) {
        if(dummyWallet.getUserId().equals(userId)) {
            return ResponseEntity.ok(dummyWallet);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 잔액 충전: POST /api/wallet/topup
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