package softuni.workshop.service.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.workshop.data.entities.Employee;
import softuni.workshop.data.entities.Project;
import softuni.workshop.data.repositories.EmployeeRepository;
import softuni.workshop.data.repositories.ProjectRepository;
import softuni.workshop.service.dtos.EmployeeImportDto;
import softuni.workshop.service.dtos.EmployeeImportRootDto;
import softuni.workshop.service.services.EmployeeService;
import softuni.workshop.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final String EMPLOYEES_XML = "src/main/resources/files/xmls/employees.xml";
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ProjectRepository projectRepository, XmlParser xmlParser, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
    }

    @Override
    public void importEmployees() throws JAXBException {
        EmployeeImportRootDto employeeImportRootDto = this.xmlParser.parseXml(EmployeeImportRootDto.class, EMPLOYEES_XML);
        for (EmployeeImportDto dto : employeeImportRootDto.getEmployeeImportDtos()) {
            Optional<Project> byNameAndCompanyName = this.projectRepository.findByNameAndCompanyName(dto.getProject().getName(), dto.getProject().getCompany().getName());

            if (byNameAndCompanyName.isPresent()) {
                Employee employee = this.modelMapper.map(dto, Employee.class);
                employee.setProject(byNameAndCompanyName.get());
                this.employeeRepository.saveAndFlush(employee);
            }
        }

    }

    @Override
    public boolean areImported() {
        return this.employeeRepository.count() > 0;
    }

    @Override
    public String readEmployeesXmlFile() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of(EMPLOYEES_XML)));
    }

    @Override
    public String exportEmployeesWithAgeAbove() {
        StringBuilder sb = new StringBuilder();
        List<Employee> allByAgeGreaterThan = this.employeeRepository.getAllByAgeGreaterThan(25);
        for (Employee employee : allByAgeGreaterThan) {
            sb.append(String.format("Name: %s %s%n\tAge: %d%n\tProject name: %s%n",
                    employee.getFirstName(), employee.getLastName(), employee.getAge(),
                    employee.getProject().getName()));
        }

        return sb.toString().trim();
    }
}
