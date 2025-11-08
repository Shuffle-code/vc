package tt.chat.vc.service;

import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.ObserverImage;
import tt.chat.vc.exception.StorageException;
import tt.chat.vc.exception.StorageFileNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tt.chat.vc.dao.ObserverDao;
import tt.chat.vc.dao.ObserverImageDao;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObserverImageService {
    private static final String path = "Observer";
    private final String startImage = "image104-66.jpg";

    @Value("${storage.location}")
    private String storagePath;

    private final ObserverImageDao observerImageDao;
    private final ObserverDao observerDao;
    private Path rootLocation;

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long count() {
        return observerImageDao.count();
    }

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long countImagesOfObserver(Long id) {
        return observerImageDao.count(id);
    }

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(storagePath);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("Error while creating storage {}", rootLocation.toAbsolutePath());
            throw new StorageException(String.format("Error while creating storage %s", rootLocation.toAbsolutePath()));
        }
    }

    public String save(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        return this.save(file, filename);
    }

    public String save(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException(String.format("File %s is empty", filename));
            }
            if (filename.contains("..")) {
                throw new StorageException(String.format("Symbol '..' do not permit"));
            }
            Files.createDirectories(rootLocation.resolve(path));
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(rootLocation.resolve(path))) {
                for (Path child : dirStream) {
                    if (child.getFileName().toString().equals(filename)) {
                        throw new StorageException(String.format("File with name %s/%s already exists", rootLocation.resolve(path), filename));
                    }
                }
            } catch (IOException e) {
                throw new StorageException(String.format("Error while creating file %s", filename));
            }

        } catch (IOException e) {
            throw new StorageException("Error while creating storage");
        }
        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(path).resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(String.format("Error while saving file %s", filename));
        }
        return filename;
    }

    public Observer saveObserverImage(Long observerId, MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            Observer observer = observerDao.getReferenceById(observerId);
            String pathToSavedFile = save(multipartFile);
            ObserverImage observerImage = ObserverImage.builder()
                    .path(pathToSavedFile)
                    .observer(observer)
                    .build();
            observer.addImage(observerImage);
            Observer savePlayer = observerDao.save(observer);
            deleteStartImage(observerImage);
            return savePlayer;
        }
        return null;
    }

    public void deleteStartImage(ObserverImage observerImage){
        Long idObserver = observerImage.getObserver().getId();
        ObserverImage image = observerImageDao.findFirstByObserverId(idObserver);
        if (observerImageDao.count(observerImage.getObserver().getId()) > 1 && image.getPath().equals("image104-66.jpg")){
//            log.info(image.getPath());
            observerImageDao.delete(image);
        }
    }
    public void deleteImage(Long id){
        observerImageDao.deleteById(id);
    }
    public BufferedImage loadFileAsImage(Long id) throws IOException {
        String imageName = uploadMultipleFilesByObserverId(id);
        Resource resource = loadAsResource(imageName);
        return ImageIO.read(resource.getFile());
    }

    public BufferedImage loadFileAsImageByIdImage(Long id) throws IOException {
        String imageName = uploadMultipleFilesByImageId(id);
        Resource resource = loadAsResource(imageName);
        return ImageIO.read(resource.getFile());
    }

    public String uploadMultipleFilesByObserverId(Long id) {
        return observerImageDao.findImageNameByObserverId(id);
    }
    public String uploadMultipleFilesByImageId(Long id) {
        return observerImageDao.findImageNameByImageId(id);
    }
    public List<Long> uploadMultipleFiles(Long id) {
        return observerImageDao.findAllIdImagesByObserverId(id);
    }

    public Long getObserverIdByImageId(Long id){
        return observerImageDao.findObserverIdByImageId(id);
    }

    public Resource loadAsResource(String filename) {
        if (StringUtils.hasText(filename)) {
            try {
                Path file = rootLocation.resolve(path).resolve(filename);
//                private static final String path = "products";
//                8e6d4478-ee77-4d43-96ef-0d6df9fb1589_i.jpg
//                products/8e6d4478-ee77-4d43-96ef-0d6df9fb1589_i.jpg
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new StorageFileNotFoundException(String.format("File %s not found in directory %s", filename, path));
                }
            } catch (MalformedURLException e) {
                throw new StorageFileNotFoundException(String.format("File %s not found in directory %s", filename, path), e);
            }
        } else {
            throw new StorageFileNotFoundException(String.format("Filename cannot be empty: %s", filename));
        }
    }

    public void addStartImage(Observer observer){
        ObserverImage observerImage = new ObserverImage();
        observerImage.setPath(startImage);
        observerImage.setObserver(observer);
        observerImageDao.save(observerImage);
    }
}
