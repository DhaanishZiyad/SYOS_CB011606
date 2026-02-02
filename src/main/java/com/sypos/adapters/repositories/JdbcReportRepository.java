package com.sypos.adapters.repositories;

import com.sypos.application.dto.reports.ReorderLine;
import com.sypos.application.dto.reports.ReshelveLine;
import com.sypos.application.dto.reports.SalesLine;
import com.sypos.application.dto.reports.StockBatchLine;
import com.sypos.application.ports.ReportRepository;
import com.sypos.domain.valueobjects.Money;
import com.sypos.infrastructure.mysql.MySqlConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcReportRepository implements ReportRepository {

    private final MySqlConnectionFactory connectionFactory;

    // For reshelve report: target shelf level (simple + defendable)
    private final int targetShelfLevel;

    public JdbcReportRepository(MySqlConnectionFactory connectionFactory, int targetShelfLevel) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
        this.targetShelfLevel = targetShelfLevel;
    }

    @Override
    public List<SalesLine> fetchDailySales(LocalDate date) {
        String sql = """
            SELECT bi.item_code,
                   bi.item_name,
                   SUM(bi.quantity) AS total_qty,
                   SUM(bi.line_total) AS total_revenue
            FROM bills b
            JOIN bill_items bi ON bi.bill_serial_no = b.serial_no
            WHERE b.bill_date = ?
            GROUP BY bi.item_code, bi.item_name
            ORDER BY bi.item_code
            """;

        List<SalesLine> lines = new ArrayList<>();

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("item_code");
                    String name = rs.getString("item_name");
                    int qty = rs.getInt("total_qty");
                    var revenue = rs.getBigDecimal("total_revenue");

                    lines.add(new SalesLine(code, name, qty, new Money(revenue)));
                }
            }

            return lines;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch daily sales for: " + date, e);
        }
    }

    @Override
    public List<ReshelveLine> fetchReshelveItems() {
        // Definition (simple): bring shelf up to targetShelfLevel.
        // reshelveQty = max(0, targetShelfLevel - currentShelfQty)
        String sql = """
            SELECT i.code, i.name, COALESCE(s.quantity, 0) AS shelf_qty
            FROM items i
            LEFT JOIN shelf_stock s ON s.item_code = i.code
            WHERE COALESCE(s.quantity, 0) < ?
            ORDER BY i.code
            """;

        List<ReshelveLine> lines = new ArrayList<>();

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, targetShelfLevel);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("code");
                    String name = rs.getString("name");
                    int shelfQty = rs.getInt("shelf_qty");
                    int toReshelve = targetShelfLevel - shelfQty;

                    lines.add(new ReshelveLine(code, name, toReshelve));
                }
            }

            return lines;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch reshelve items", e);
        }
    }

    @Override
    public List<ReorderLine> fetchReorderItems(int threshold) {
        // Interpretation: reorder based on total remaining store stock (sum of remaining_qty across batches).
        String sql = """
            SELECT i.code, i.name, COALESCE(SUM(sb.remaining_qty), 0) AS store_stock
            FROM items i
            LEFT JOIN stock_batches sb ON sb.item_code = i.code
            GROUP BY i.code, i.name
            HAVING COALESCE(SUM(sb.remaining_qty), 0) < ?
            ORDER BY i.code
            """;

        List<ReorderLine> lines = new ArrayList<>();

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("code");
                    String name = rs.getString("name");
                    int stock = rs.getInt("store_stock");

                    lines.add(new ReorderLine(code, name, stock));
                }
            }

            return lines;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch reorder items with threshold: " + threshold, e);
        }
    }

    @Override
    public List<StockBatchLine> fetchStockBatchReport() {
        String sql = """
            SELECT id, item_code, purchase_date, expiry_date, received_qty, remaining_qty
            FROM stock_batches
            ORDER BY item_code, purchase_date, expiry_date, id
            """;

        List<StockBatchLine> lines = new ArrayList<>();

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String itemCode = rs.getString("item_code");
                var purchase = rs.getDate("purchase_date").toLocalDate();
                var expiry = rs.getDate("expiry_date").toLocalDate();
                int received = rs.getInt("received_qty");
                int remaining = rs.getInt("remaining_qty");

                lines.add(new StockBatchLine(itemCode, purchase, received, remaining, expiry));
            }

            return lines;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch stock batch report", e);
        }
    }
}
