package pt.photuseretratus.webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.utils.Utils;

import java.io.IOException;


@SpringBootApplication
public class WebAppApplication {
    public static void main(String[] args) throws IOException {

        SpringApplication.run(WebAppApplication.class, args);

    }
}
