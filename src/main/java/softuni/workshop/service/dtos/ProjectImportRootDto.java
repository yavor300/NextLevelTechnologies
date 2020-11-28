package softuni.workshop.service.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "projects")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectImportRootDto {
    @XmlElement(name = "project")
    private List<ProjectImportDto> projects;

    public ProjectImportRootDto() {
    }

    public List<ProjectImportDto> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectImportDto> projects) {
        this.projects = projects;
    }
}
