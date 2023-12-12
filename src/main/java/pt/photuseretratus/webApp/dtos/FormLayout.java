package pt.photuseretratus.webApp.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FormLayout {

    private String name;
    private String email;
    private String subject;
    private String otherSubject;
    private String text;
    private String eventType;
    private String pricingPlan;
    private MultipartFile[] files;
    private String size;
    private String type;
    private int phone;

}
