package ua.shapoval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.shapoval.entity.BinaryContent;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
