package pt.photuseretratus.webApp;

import org.springframework.web.multipart.MultipartFile;

public class FormLayout {

    private String name;
    private String email;
    private String subject;
    private String otherSubject;
    private String text;
    private String eventType;
    private String pricingPlan;
    private MultipartFile[] ficheiro;
    private String size;
    private String type;
    private int phone;

    public String getOtherSubject() {
        return otherSubject;
    }

    public void setOtherSubject(String otherSubject) {
        this.otherSubject = otherSubject;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public MultipartFile[] getFicheiro() {
        return ficheiro;
    }

    public void setFicheiro(MultipartFile[] ficheiro) {
        this.ficheiro = ficheiro;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPricingPlan() {
        return pricingPlan;
    }

    public void setPricingPlan(String pricingPlan) {
        this.pricingPlan = pricingPlan;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
