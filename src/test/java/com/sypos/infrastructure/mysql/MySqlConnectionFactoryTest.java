package com.sypos.infrastructure.mysql;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MySqlConnectionFactoryTest {

    @Test
    void throwsSQLExceptionWhenConnectionDetailsAreInvalid() {
        MySqlConnectionFactory factory =
                new MySqlConnectionFactory(
                        "jdbc:mysql://invalidhost:3306/doesnotexist",
                        "wrong",
                        "wrong"
                );

        assertThrows(SQLException.class, factory::getConnection);
    }
}
