package pt.photuseretratus.webApp.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties("config")
@PropertySource(value = "classpath:application.yaml")
@Getter
public class AppConfiguration {

    private final Map<String, String> imagekit = new HashMap<>();

    private final Map<String, String> security = new HashMap<>();

    private final Map<String, String> database = new HashMap<>();


}
