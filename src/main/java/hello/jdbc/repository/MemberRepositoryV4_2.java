package hello.jdbc.repository;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j

/**
 * 스프링 예외 변환기를 통해 SQLException 을 RuntimeException으로 변환하여 반환
 */
public class MemberRepositoryV4_2 implements MemberRepository{

    private final DataSource dataSource;

    private final SQLExceptionTranslator translator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {

        String sql = "insert into member(member_id, money) values (?,?)";

        Connection con = null;

        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw translator.translate("save ", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }


    @Override
    public Member findById(String memberId)   {

        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw translator.translate("find ", sql, e);
        } finally {
            close(con, pstmt, rs);
        }
    }


    @Override
    public void update(String memberId, int money)   {

        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}",resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw translator.translate("update ", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }


    @Override
    public void delete(String memberId)   {

        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("db error", e);
            throw translator.translate("delete ", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, PreparedStatement stmt, ResultSet rs)  {

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //트랜잭션 동기화를 위해 DataSourceUtils 사용
        DataSourceUtils.releaseConnection(con, dataSource);
//        JdbcUtils.closeConnection(con);


    }

    private  Connection getConnection() throws SQLException {

        //트랜잭션 동기화를 사용하기 위해 DataSourceUtils 사용
        Connection con = DataSourceUtils.getConnection(dataSource);
//        Connection con = dataSource.getConnection();
        log.info("get connection={}, class = {}", con, con.getClass());
        return con;
    }
}
