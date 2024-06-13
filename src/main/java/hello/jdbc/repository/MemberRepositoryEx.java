package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryEx {

    Member save() throws SQLException;

    Member findById() throws SQLException;

    void update() throws SQLException;

    void delete() throws SQLException;

}
