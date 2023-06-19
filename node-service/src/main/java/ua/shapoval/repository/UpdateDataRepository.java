package ua.shapoval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.shapoval.entity.UpdateData;

@Repository
public interface UpdateDataRepository extends JpaRepository<UpdateData, Long> {
}
