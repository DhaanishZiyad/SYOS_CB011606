package com.sypos.adapters.repositories;

import com.sypos.application.ports.InventoryRepository;
import com.sypos.domain.entities.ShelfStock;
import com.sypos.domain.entities.StockBatch;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Quantity;
import com.sypos.infrastructure.mysql.MySqlConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcInventoryRepository implements InventoryRepository {

    private final MySqlConnectionFactory connectionFactory;

    public JdbcInventoryRepository(MySqlConnectionFactory connectionFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
    }

    @Override
    public Optional<ShelfStock> findShelfStock(ItemCode itemCode) {
        String sql = "SELECT item_code, quantity FROM shelf_stock WHERE item_code = ?";

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, itemCode.getValue());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                int qty = rs.getInt("quantity");
                return Optional.of(new ShelfStock(itemCode, new Quantity(qty)));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch shelf stock for: " + itemCode, e);
        }
    }

    @Override
    public void saveShelfStock(ShelfStock shelfStock) {
        // upsert pattern (insert or update)
        String sql = """
                INSERT INTO shelf_stock (item_code, quantity)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)
                """;

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, shelfStock.getItemCode().getValue());
            ps.setInt(2, shelfStock.getQuantity().getValue());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save shelf stock for: " + shelfStock.getItemCode(), e);
        }
    }

    @Override
    public List<StockBatch> findAvailableBatches(ItemCode itemCode) {
        String sql = """
                SELECT id, item_code, purchase_date, expiry_date, received_qty, remaining_qty
                FROM stock_batches
                WHERE item_code = ? AND remaining_qty > 0
                """;

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, itemCode.getValue());

            try (ResultSet rs = ps.executeQuery()) {
                java.util.ArrayList<StockBatch> batches = new java.util.ArrayList<>();
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String code = rs.getString("item_code");
                    var purchaseDate = rs.getDate("purchase_date").toLocalDate();
                    var expiryDate = rs.getDate("expiry_date").toLocalDate();
                    int remaining = rs.getInt("remaining_qty");

                    batches.add(new StockBatch(
                            id,
                            new ItemCode(code),
                            purchaseDate,
                            expiryDate,
                            new Quantity(remaining)
                    ));
                }
                return batches;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch batches for: " + itemCode, e);
        }
    }

    @Override
    public void saveBatches(List<StockBatch> modifiedBatches) {
        String sql = "UPDATE stock_batches SET remaining_qty = ? WHERE id = ?";

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (StockBatch b : modifiedBatches) {
                ps.setInt(1, b.getQuantity().getValue());
                ps.setLong(2, b.getId());
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save modified stock batches", e);
        }
    }
}
