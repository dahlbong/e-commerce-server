package kr.hhplus.be.server.infra.rank;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.rank.RankCommand;
import kr.hhplus.be.server.domain.rank.RankInfo;
import kr.hhplus.be.server.domain.rank.enums.RankType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.domain.rank.QRank.rank;

@Repository
@RequiredArgsConstructor
public class RankQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Deprecated
    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.PopularSellRank command) {
        return queryFactory.select(
                        Projections.constructor(
                                RankInfo.PopularProduct.class,
                                rank.productId,
                                rank.score.sum().as("totalScore")
                        )
                )
                .from(rank)
                .where(
                        rank.rankType.eq(RankType.SELL),
                        rank.rankDate.between(
                                command.getDate().minusDays(command.getDays()),
                                command.getDate()
                        )
                )
                .groupBy(rank.productId)
                .orderBy(rank.score.sum().desc())
                .limit(command.getTop())
                .fetch();
    }
}