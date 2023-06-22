package ua.shapoval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.shapoval.entity.AppDocument;

public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
