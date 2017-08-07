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
    @Modifying
    @Query(value="INSERT INTO report (symbol, product, position, internal_close, internal_pnl, report_date, trader_id)\n" +
        "SELECT symbol, product, position, NULL, NULL, CURDATE(), trader_id FROM report WHERE DATE(report_date) = SUBDATE(CURDATE(),1) ORDER BY symbol;", nativeQuery = true)
    void persistReportOnStart();

    @Modifying
    @Query(value="UPDATE report r INNER JOIN\n" +
        "(SELECT t.symbol, SUM(t.jhi_share) AS position FROM transaction t WHERE DATE(t.trade_time) = CURDATE() GROUP BY t.symbol) j\n" +
        "ON j.symbol = r.symbol\n" +
        "LEFT JOIN (SELECT * FROM report WHERE DATE(report.report_date) = SUBDATE(CURDATE(), 1)) b\n" +
        "ON j.symbol = b.symbol\n" +
        "SET r.position = CASE WHEN b.internal_pnl IS NOT NULL THEN j.position + b.position ELSE j.position END\n" +
        "WHERE DATE(r.report_date) = CURDATE();", nativeQuery = true)
    void updateReportRows();

    @Modifying
    @Query(value="INSERT INTO report (symbol, product, position, report_date, trader_id)\n" +
        "SELECT t.symbol, t.product, SUM(t.jhi_share), CURDATE(), t.trader_id FROM transaction t \n" +
        "WHERE (t.symbol) NOT IN (SELECT DISTINCT report.symbol FROM report WHERE DATE(report.report_date) = CURDATE()) AND DATE(t.trade_time) = CURDATE()\n" +
        "GROUP BY t.symbol, t.product, t.trader_id ORDER BY t.symbol;", nativeQuery = true)
    void insertReportRows();

    @Modifying
    @Query(value="UPDATE report r INNER JOIN stock s \n" +
        "ON s.symbol = r.symbol AND DATE(report_date) = CURDATE() \n" +
        "SET r.internal_close = s.jhi_close;", nativeQuery = true)
    void updateClose();

    @Modifying
    @Query(value="UPDATE report a\n" +
        "LEFT JOIN (SELECT report.symbol, report.internal_pnl FROM report WHERE DATE(report_date) = SUBDATE(CURDATE(),1)) b\n" +
        "ON a.symbol = b.symbol\n" +
        "LEFT JOIN (SELECT t.symbol, SUM(t.cash) AS cash FROM transaction t GROUP BY t.symbol) c\n" +
        "ON a.symbol = c.symbol\n" +
        "SET a.internal_pnl = CASE WHEN (b.internal_pnl IS NOT NULL AND c.cash IS NOT NULL) THEN a.internal_close * a.position - b.internal_pnl + c.cash\n" +
        "WHEN (b.internal_pnl IS NULL AND c.cash IS NOT NULL) THEN a.internal_close * a.position + c.cash \n" +
        "WHEN (b.internal_pnl IS NOT NULL AND c.cash IS NULL) THEN a.internal_close * a.position - b.internal_pnl \n" +
        "WHEN (b.internal_pnl IS NULL AND c.cash IS NULL) THEN a.internal_close * a.position END\n" +
        "WHERE DATE(a.report_date) = CURDATE();", nativeQuery = true)
    void calculatePnl();

}
