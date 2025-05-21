package kr.hhplus.be.server.infra.rank;

import kr.hhplus.be.server.domain.rank.Rank;
import kr.hhplus.be.server.domain.rank.enums.RankType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {
    List<Rank> findByRankTypeAndRankDate(RankType rankType, LocalDate rankDate);
}
