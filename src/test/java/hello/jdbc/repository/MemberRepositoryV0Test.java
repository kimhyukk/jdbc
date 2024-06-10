package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {


    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member memberV0 = new Member("memberV5", 10000);
        repository.save(memberV0);

        //findById
        Member findMember = repository.findById(memberV0.getMemberId());
        log.info("findMember = {}", findMember);
        Assertions.assertThat(findMember).isEqualTo(memberV0);


        repository.update(memberV0.getMemberId(), 20000);
        Member updateMember = repository.findById(memberV0.getMemberId());
        Assertions.assertThat(updateMember.getMoney()).isEqualTo(20000);

        repository.delete(memberV0.getMemberId());

        Assertions.assertThatThrownBy(() -> repository.findById(memberV0.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}