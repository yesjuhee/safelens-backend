package safelens.backend.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safelens.backend.history.domain.History;
import safelens.backend.member.domain.Member;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByMemberOrderByCreatedAtDesc(Member member);
}
