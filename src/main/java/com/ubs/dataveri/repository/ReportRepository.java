package com.ubs.dataveri.repository;

import com.ubs.dataveri.domain.Report;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Report entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    @Query(value="insert into report (symbol, product, position, internal_close, internal_pnl, report_date, trader_id) select symbol, product, position, null, null, CURDATE(), trader_id from report where report_date = SUBDATE(CURDATE(),1)", nativeQuery = true)
    void persistReportOnStart();

    @Query(value="SET SQL_SAFE_UPDATES = 0;" +
        "DELETE FROM report WHERE report_date = CURDATE();" +
        "SET SQL_SAFE_UPDATES = 1;" +
        "INSERT INTO report (symbol, product, position, internal_close, internal_pnl, report_date, trader_id)" +
        "select t.symbol, t.product, sum(t.jhi_share), s.jhi_close, sum(t.cash), CURDATE(), t.trader_id from transaction t" +
        "left join stock s on t.symbol = s.symbol" +
        "where date(t.trade_time) = CURDATE()" +
        "group by t.symbol, t.product, s.jhi_close, t.trader_id;", nativeQuery = true)
    void updateReport();

}
