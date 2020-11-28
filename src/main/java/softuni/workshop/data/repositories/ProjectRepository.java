package softuni.workshop.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.workshop.data.entities.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Optional<Project> findByNameAndCompanyName(String projectName, String companyName);

    List<Project> getAllByFinishedIsTrue();
}
