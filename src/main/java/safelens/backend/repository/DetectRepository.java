package safelens.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safelens.backend.domain.Detect;

public interface DetectRepository extends JpaRepository<Detect, Long> {
}
