package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;


    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

    }

    @Test
    void sqlExceptionErrorCode() {

        String sql = "select bad grammar";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);
            int errorCode = e.getErrorCode();
            log.info("errorCode = {}", errorCode);
            log.info("error", e);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "select bad grammar";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);
            SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = translator.translate("select ", sql, e);
            log.info("resultEx", resultEx);
            Assertions.assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }
}
