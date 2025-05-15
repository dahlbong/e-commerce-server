package kr.hhplus.be.server.domain.stock;

public interface StockRepository {

    Stock save(Stock stock);

    Stock findByProductId(Long productId);

    Stock findByProductIdWithLock(Long productId);
}