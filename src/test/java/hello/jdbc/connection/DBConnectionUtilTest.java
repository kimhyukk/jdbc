package hello.jdbc.connection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionUtilTest {

    @Test
    void getConnection() throws SQLException {

        Connection connection = DBConnectionUtil.getConnection();

        Assertions.assertThat(connection).isNotNull();
    }
}