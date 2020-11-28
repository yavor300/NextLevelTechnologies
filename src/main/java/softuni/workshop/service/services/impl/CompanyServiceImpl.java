package softuni.workshop.service.services.impl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.workshop.data.entities.Company;
import softuni.workshop.data.repositories.CompanyRepository;
import softuni.workshop.service.dtos.CompanyImportDto;
import softuni.workshop.service.dtos.CompanyImportRootDto;
import softuni.workshop.service.services.CompanyService;
import softuni.workshop.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final static String COMPANIES_PATH = "src/main/resources/files/xmls/companies.xml";
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, ModelMapper modelMapper, XmlParser xmlParser) {
        this.companyRepository = companyRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }

    @Override
    public void importCompanies() throws JAXBException {
        CompanyImportRootDto companyImportRootDto = this.xmlParser.parseXml(CompanyImportRootDto.class, COMPANIES_PATH);
        for (CompanyImportDto dto : companyImportRootDto.getCompanyImportDtos()) {
            this.companyRepository.saveAndFlush(this.modelMapper.map(dto, Company.class));
        }

    }

    @Override
    public boolean areImported() {
        return this.companyRepository.count() > 0;
    }

    @Override
    public String readCompaniesXmlFile() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of(COMPANIES_PATH)));
    }
}
