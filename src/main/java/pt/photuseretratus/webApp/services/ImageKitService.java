package pt.photuseretratus.webApp.services;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.results.ResultList;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.photuseretratus.webApp.configuration.AppConfiguration;
import pt.photuseretratus.webApp.dtos.RequestToken;

import java.util.*;

@Component
@AllArgsConstructor
public class ImageKitService {

    ImageKit imageKit;
    Configuration configuration;
    @Value("${config.imagekit.private}")
    private String privKey;
    @Value("${config.imagekit.public}")
    private String publicKey;
    @Value("${config.imagekit.url}")
    private String url;

    @Autowired
    AppConfiguration appConfiguration;

    public ImageKitService() {
        this.imageKit = io.imagekit.sdk.ImageKit.getInstance();
        configuration = new Configuration();
        configuration.setPrivateKey(privKey);
        configuration.setPublicKey(publicKey);
        configuration.setUrlEndpoint(url);
        this.imageKit.setConfig(configuration);
    }

    public String getURL(RequestToken RequestToken) {

        List<Map<String, String>> transformation = new ArrayList<>();
        transformation.add(RequestToken.getTransformation());

        Map<String, Object> options = new HashMap<>();
        options.put("path", RequestToken.getPath() + RequestToken.getName());
        options.put("transformation", transformation);

        return imageKit.getUrl(options);

    }

    public List<String> getFolder(RequestToken RequestToken) {

        List<String> URLs = new ArrayList<>();
        Map<String, String> options = new HashMap<>();
        options.put("path", RequestToken.getPath());

        Map<String, Object> urlOptions = new HashMap<>();
        ResultList resultList = imageKit.getFileList(options);

        List<Map<String, Object>> resultListMap = resultList.getMap();
        Comparator<Map<String, Object>> mapComparator = Comparator.comparing(m -> m.get("name").toString());

        resultListMap.sort(mapComparator);

        List<Map<String, String>> transformation = new ArrayList<>();
        transformation.add(RequestToken.getTransformation());
        urlOptions.put("path", resultList.getResults().get(0).getFilePath());
        urlOptions.put("transformation", transformation);

        for (Map<String, Object> i : resultListMap) {
            urlOptions.replace("path", i.get("filePath"));
            if (Float.parseFloat(i.get("width").toString()) < Float.parseFloat(i.get("height").toString()))
                transformation.get(0).replace("ar", "9-16");
            else
                transformation.get(0).replace("ar", "16-9");
            URLs.add(imageKit.getUrl(urlOptions));
        }

        return URLs;
    }


    public List<String> getFoldersNames(RequestToken requestToken) {

        List<String> URLs = new ArrayList<>();
        Map<String, String> options = new HashMap<>();

        options.put("path", requestToken.getPath());
        options.put("type", "folder");

        ResultList resultList = imageKit.getFileList(options);

        URLs.add(requestToken.getPath());

        for (int i = 0; i < resultList.getResults().size(); i++) {
            URLs.add(resultList.getResults().get(i).getName());
        }

        return URLs;
    }


}