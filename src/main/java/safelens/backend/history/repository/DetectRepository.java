package safelens.backend.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import safelens.backend.history.domain.Detect;

public interface DetectRepository extends JpaRepository<Detect, Long> {
}
