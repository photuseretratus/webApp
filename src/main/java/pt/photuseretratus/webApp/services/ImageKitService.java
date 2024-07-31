package pt.photuseretratus.webApp.services;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.exceptions.UnknownException;
import io.imagekit.sdk.models.BaseFile;
import io.imagekit.sdk.models.CreateFolderRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.ResultEmptyBlock;
import io.imagekit.sdk.models.results.ResultList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.photuseretratus.webApp.configuration.AppConfiguration;
import pt.photuseretratus.webApp.dtos.RequestToken;
import pt.photuseretratus.webApp.exceptions.ImageKitApiLevelException;
import pt.photuseretratus.webApp.utils.Security;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class ImageKitService {

    ImageKit imageKit;
    Configuration configuration;
    AppConfiguration appConfiguration;
    Security security;

    private final static String PATH = "path";
    private final static String TRANSFORMATION = "transformation";
    private final static String AR = "ar";


    public ImageKitService(AppConfiguration appConfiguration) {

        this.appConfiguration = appConfiguration;
        this.imageKit = io.imagekit.sdk.ImageKit.getInstance();
        configuration = new Configuration();
        configuration.setPrivateKey(appConfiguration.getImagekit().get("private"));
        configuration.setPublicKey(appConfiguration.getImagekit().get("public"));
        configuration.setUrlEndpoint(appConfiguration.getImagekit().get("url"));
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

    public List<String> getFolder(RequestToken requestToken) {

        try {
            List<String> URLs = new ArrayList<>();

            GetFileListRequest fileListRequest = new GetFileListRequest();
            fileListRequest.setPath(requestToken.getPath());
            ResultList resultList = imageKit.getFileList(fileListRequest);

            List<Map<String, String>> transformation = List.of(requestToken.getTransformation());

            Map<String, Object> urlOptions = new HashMap<>(Map.of(PATH, "", TRANSFORMATION, transformation));

            Comparator<BaseFile> mapComparator = Comparator.comparing(BaseFile::getName);

            resultList.getResults().stream().sorted(mapComparator).forEach(file -> {
                urlOptions.replace(PATH, file.getFilePath());
                if (Float.parseFloat(String.valueOf(file.getWidth())) < Float.parseFloat(String.valueOf(file.getHeight())))
                    transformation.get(0).replace(AR, "9-16");
                else
                    transformation.get(0).replace(AR, "16-9");
                URLs.add(imageKit.getUrl(urlOptions));
            });
            return URLs;

        } catch (Exception e) {
            throw new ImageKitApiLevelException("Problema a aceder ás fotos do utilizador", e);
        }
    }


    public List<String> getFoldersNames(RequestToken requestToken) {

        try {
            requestToken.setPath("Reportagens/" + security.getTokenSubject(requestToken.getToken()) + "/");

            List<String> URLs = new ArrayList<>();

            GetFileListRequest fileListRequest = new GetFileListRequest();
            fileListRequest.setPath(requestToken.getPath());
            fileListRequest.setType("folder");

            ResultList resultList = imageKit.getFileList(fileListRequest);

            URLs.add(requestToken.getPath());

            for (int i = 0; i < resultList.getResults().size(); i++) {
                URLs.add(resultList.getResults().get(i).getName());
            }

            return URLs;
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "Erro a validar autorização do utilizador");
        }
    }

    public void createFolder(RequestToken requestToken) throws UnknownException {

        CreateFolderRequest createFolderRequest = new CreateFolderRequest();
        createFolderRequest.setFolderName(requestToken.getUser());
        createFolderRequest.setParentFolderPath("/Reportagens");
        ResultEmptyBlock resultEmptyBlock = imageKit.createFolder(createFolderRequest);
        if(resultEmptyBlock.getResponseMetaData().getHttpStatusCode() != 201)
            throw new ImageKitApiLevelException("Erro a criar pasta");
    }
}