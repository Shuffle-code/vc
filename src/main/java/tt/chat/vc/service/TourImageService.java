package tt.chat.vc.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tt.chat.vc.dao.TourDao;
import tt.chat.vc.dao.TourImageDao;
import tt.chat.vc.entity.Tour;
import tt.chat.vc.entity.TourImage;
import tt.chat.vc.exception.StorageException;
import tt.chat.vc.exception.StorageFileNotFoundException;

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
public class TourImageService {
    private static final String path = "tours";

    @Value("${storage.location}")
    private String storagePath;
    private final TourImageDao tourImageDao;
    private final TourDao tourDao;
    private Path rootLocation;

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
    // сохроняем файл(картинку) из клиента в директорию на сервере с помощью и возвращаем сгененрированное новое имя картинки
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


    // сохроняем файл(картинку) из клиента в директорию на сервере с помощью - save(multipartFile), + сбилдили TourImage и сохранили данный турнир
    public Tour saveTourImage(Long tourId, MultipartFile multipartFile) {
        if (!multipartFile.isEmpty()) {
            Tour tour = tourDao.getReferenceById(tourId);
            String pathToSavedFile = save(multipartFile);
            TourImage tourImage = TourImage.builder()
                    .path(pathToSavedFile)
                    .tour(tour)
                    .build();
            tour.addImage(tourImage);
            return tourDao.save(tour);
        }
        return null;
    }


    public BufferedImage loadFileAsImage(Long id) throws IOException {
        String imageName = uploadMultipleFilesByTourId(id);
        Resource resource = loadAsResource(imageName);
        return ImageIO.read(resource.getFile());
    }

    public BufferedImage loadFileAsImageByIdImage(Long id) throws IOException {
        String imageName = uploadMultipleFilesByImageId(id);
        Resource resource = loadAsResource(imageName);
        return ImageIO.read(resource.getFile());
    }

    public String uploadMultipleFilesByTourId(Long id) {
        return tourImageDao.findImageNameByTourId(id);
    }

    public String uploadMultipleFilesByImageId(Long id) {
        return tourImageDao.findImageNameByImageId(id);
    }

    public List<Long> uploadMultipleFiles(Long id) {
        return tourImageDao.findAllIdImagesByTourId(id);
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

    public void deleteImageTour(Long idImage) {
        if (idImage != null){
            tourImageDao.deleteById(idImage);
        }
    }

    public Long getTourIdByImageId(Long id) {
        return tourImageDao.findTourIdByImageId(id);
    }
}
