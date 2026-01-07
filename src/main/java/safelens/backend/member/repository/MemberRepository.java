package safelens.backend.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safelens.backend.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
