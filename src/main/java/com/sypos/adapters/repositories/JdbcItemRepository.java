package com.sypos.adapters.repositories;

import com.sypos.application.ports.ItemRepository;
import com.sypos.domain.entities.Item;
import com.sypos.domain.valueobjects.ItemCode;
import com.sypos.domain.valueobjects.Money;
import com.sypos.infrastructure.mysql.MySqlConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;

public class JdbcItemRepository implements ItemRepository {

    private final MySqlConnectionFactory connectionFactory;

    public JdbcItemRepository(MySqlConnectionFactory connectionFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
    }

    @Override
    public Optional<Item> findByCode(ItemCode code) {
        Objects.requireNonNull(code);

        String sql = "SELECT code, name, unit_price FROM items WHERE code = ?";

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code.getValue());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                String itemCode = rs.getString("code");
                String name = rs.getString("name");
                BigDecimal unitPrice = rs.getBigDecimal("unit_price");

                Item item = new Item(new ItemCode(itemCode), name, new Money(unitPrice));
                return Optional.of(item);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch item by code: " + code, e);
        }
    }

    @Override
    public java.util.List<Item> findAll() {

        String sql = """
            SELECT code, name, unit_price
            FROM items
            ORDER BY name
            """;

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            java.util.List<Item> items =
                    new java.util.ArrayList<>();

            while (rs.next()) {

                String code = rs.getString("code");
                String name = rs.getString("name");

                java.math.BigDecimal unitPrice =
                        rs.getBigDecimal("unit_price");

                items.add(
                        new Item(
                                new ItemCode(code),
                                name,
                                new Money(unitPrice)
                        )
                );
            }

            return items;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch all items",
                    e
            );
        }
    }

    @Override
    public void save(Item item) {

        String sql = """
            INSERT INTO items
            (code, name, unit_price)
            VALUES (?, ?, ?)
            """;

        try (
                Connection con =
                        connectionFactory.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    item.getCode().getValue()
            );

            ps.setString(
                    2,
                    item.getName()
            );

            ps.setBigDecimal(
                    3,
                    item.getUnitPrice().getAmount()
            );

            ps.executeUpdate();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save item",
                    e
            );
        }
    }
}
