package ua.shapoval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.shapoval.entity.AppPhoto;

@Repository
public interface AppPhotoRepository extends JpaRepository<AppPhoto, Long> {
}
