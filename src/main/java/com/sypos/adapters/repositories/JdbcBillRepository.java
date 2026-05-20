package com.sypos.adapters.repositories;

import com.sypos.application.ports.BillRepository;
import com.sypos.domain.entities.Bill;
import com.sypos.domain.entities.BillLineItem;
import com.sypos.infrastructure.mysql.MySqlConnectionFactory;
import com.sypos.domain.valueobjects.Money;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcBillRepository implements BillRepository {

    private final MySqlConnectionFactory connectionFactory;

    public JdbcBillRepository(MySqlConnectionFactory connectionFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory);
    }

    @Override
    public int nextSerialNumber() {
        String selectSql = "SELECT last_serial FROM bill_sequence WHERE stub = 'A' FOR UPDATE";
        String updateSql = "UPDATE bill_sequence SET last_serial = ? WHERE stub = 'A'";

        try (Connection con = connectionFactory.getConnection()) {
            con.setAutoCommit(false);

            int next;
            try (PreparedStatement select = con.prepareStatement(selectSql);
                 ResultSet rs = select.executeQuery()) {

                if (!rs.next()) {
                    throw new IllegalStateException("bill_sequence row missing. Insert stub='A' first.");
                }

                int last = rs.getInt(1);
                next = last + 1;
            }

            try (PreparedStatement update = con.prepareStatement(updateSql)) {
                update.setInt(1, next);
                update.executeUpdate();
            }

            con.commit();
            return next;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate next bill serial number", e);
        }
    }

    @Override
    public void save(Bill bill) {
        Objects.requireNonNull(bill);

        String insertBill = """
                INSERT INTO bills (serial_no, bill_date, full_total, discount, final_total, cash_tendered, change_amount, transaction_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'POS')
                """;

        String insertItem = """
                INSERT INTO bill_items (bill_serial_no, item_code, item_name, quantity, line_total)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = connectionFactory.getConnection()) {
            con.setAutoCommit(false);

            // Insert bill header
            try (PreparedStatement ps = con.prepareStatement(insertBill)) {
                ps.setInt(1, bill.getSerialNumber());
                ps.setDate(2, Date.valueOf(bill.getDate()));
                ps.setBigDecimal(3, bill.getTotal().getAmount());
                ps.setBigDecimal(4, bill.getDiscount().getAmount());
                ps.setBigDecimal(5, bill.getFinalTotal().getAmount());
                ps.setBigDecimal(6, bill.getCashTendered().getAmount());
                ps.setBigDecimal(7, bill.getChangeAmount().getAmount());
                ps.executeUpdate();
            }

            // Insert bill items
            try (PreparedStatement ps = con.prepareStatement(insertItem)) {
                for (BillLineItem li : bill.getItems()) {
                    ps.setInt(1, bill.getSerialNumber());
                    ps.setString(2, li.getItem().getCode().getValue());
                    ps.setString(3, li.getItem().getName());
                    ps.setInt(4, li.getQuantity().getValue());
                    ps.setBigDecimal(5, li.getLineTotal().getAmount());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save bill: " + bill.getSerialNumber(), e);
        }
    }

    @Override
    public Optional<Bill> findBySerial(int serialNumber) {

        String billSql = """
        SELECT serial_no, bill_date, full_total, discount, cash_tendered, change_amount
        FROM bills
        WHERE serial_no = ?
    """;

        String itemsSql = """
        SELECT item_code, item_name, quantity, line_total
        FROM bill_items
        WHERE bill_serial_no = ?
    """;

        try (Connection con = connectionFactory.getConnection()) {

            Bill bill = null;

            // 🔹 Load bill header
            try (PreparedStatement ps = con.prepareStatement(billSql)) {
                ps.setInt(1, serialNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bill = new Bill(
                                rs.getInt("serial_no"),
                                rs.getDate("bill_date").toLocalDate()
                        );

                        bill.setTotal(new Money(rs.getBigDecimal("full_total")));
                        bill.setDiscount(new Money(rs.getBigDecimal("discount")));

                        bill.recordPayment(
                                new Money(rs.getBigDecimal("cash_tendered")),
                                new Money(rs.getBigDecimal("change_amount"))
                        );
                    }
                }
            }

            if (bill == null) return Optional.empty();

            // 🔹 Load items
            try (PreparedStatement ps = con.prepareStatement(itemsSql)) {
                ps.setInt(1, serialNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {

                        var item = new com.sypos.domain.entities.Item(
                                new com.sypos.domain.valueobjects.ItemCode(rs.getString("item_code")),
                                rs.getString("item_name"),
                                new Money(rs.getBigDecimal("line_total")) // ⚠️ approximation
                        );

                        var qty = new com.sypos.domain.valueobjects.Quantity(rs.getInt("quantity"));

                        bill.addItem(new BillLineItem(item, qty));
                    }
                }
            }

            return Optional.of(bill);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch bill " + serialNumber, e);
        }
    }

    @Override
    public List<Bill> findByDate(LocalDate date) {
        // Implement later if you want bill report from this repository (or keep in ReportRepository).
        return List.of();
    }

    @Override
    public List<Bill> findAll() {
        List<Bill> bills = new java.util.ArrayList<>();

        String sql = """
        SELECT serial_no, bill_date, full_total, discount, final_total, cash_tendered, change_amount
        FROM bills
        ORDER BY serial_no DESC
    """;

        try (Connection con = connectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int serial = rs.getInt("serial_no");
                LocalDate date = rs.getDate("bill_date").toLocalDate();

                Bill bill = new Bill(serial, date);

                bill.setTotal(new Money(rs.getBigDecimal("full_total")));
                bill.setDiscount(new Money(rs.getBigDecimal("discount")));

                bill.recordPayment(
                        new Money(rs.getBigDecimal("cash_tendered")),
                        new Money(rs.getBigDecimal("change_amount"))
                );

                bills.add(bill);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all bills", e);
        }

        return bills;
    }
}