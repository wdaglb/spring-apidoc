package cn.kecms.library.kapidoc;

import cn.kecms.library.kapidoc.models.Api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@SpringBootTest
public class KApiDocTest extends AbstractJUnit4SpringContextTests {

    @Test
    public void scan() throws Exception {
        KApiDoc kApiDoc = new KApiDoc("cn.kecms.library");
        kApiDoc.scan();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OpenAPI openAPI = kApiDoc.getOpenAPI();
        String apiString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);

        File file = new File("apidoc.json");
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(apiString.getBytes());
        outputStream.close();
    }
}