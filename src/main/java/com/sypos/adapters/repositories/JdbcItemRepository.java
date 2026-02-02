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
}
