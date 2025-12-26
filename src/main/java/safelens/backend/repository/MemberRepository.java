package safelens.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safelens.backend.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
