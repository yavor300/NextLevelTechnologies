package softuni.workshop.service.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.workshop.data.entities.Project;
import softuni.workshop.data.repositories.CompanyRepository;
import softuni.workshop.data.repositories.ProjectRepository;
import softuni.workshop.service.dtos.ProjectImportDto;
import softuni.workshop.service.dtos.ProjectImportRootDto;
import softuni.workshop.service.services.ProjectService;
import softuni.workshop.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


@Service
public class ProjectServiceImpl implements ProjectService {
    private final static String PROJECTS_PATH = "src/main/resources/files/xmls/projects.xml";
    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, CompanyRepository companyRepository, XmlParser xmlParser, ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
    }

    @Override
    public void importProjects() throws JAXBException {
        ProjectImportRootDto projectImportRootDto = this.xmlParser.parseXml(ProjectImportRootDto.class, PROJECTS_PATH);
        for (ProjectImportDto project : projectImportRootDto.getProjects()) {
            if (this.companyRepository.findByName(project.getCompany().getName()).isPresent()) {
                Project projectToFlush = this.modelMapper.map(project, Project.class);
                projectToFlush.setCompany(this.companyRepository.findByName(project.getCompany().getName()).get());
                this.projectRepository.saveAndFlush(projectToFlush);
            }
        }

    }

    @Override
    public boolean areImported() {
        return this.projectRepository.count() > 0;
    }

    @Override
    public String readProjectsXmlFile() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of(PROJECTS_PATH)));
    }

    @Override
    public String exportFinishedProjects(){
        StringBuilder sb = new StringBuilder();
        List<Project> allByFinishedIsTrue = this.projectRepository.getAllByFinishedIsTrue();
        for (Project project : allByFinishedIsTrue) {
            sb.append(String.format("Project Name: %s%n\tDescription: %s%n\t%s%n",
                    project.getName(), project.getDescription(), project.getPayment()));
        }

        return sb.toString().trim();
    }
}
