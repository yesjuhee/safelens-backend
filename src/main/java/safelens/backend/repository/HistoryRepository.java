package safelens.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safelens.backend.domain.History;
import safelens.backend.domain.Member;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByMemberOrderByCreatedAtDesc(Member member);
}
