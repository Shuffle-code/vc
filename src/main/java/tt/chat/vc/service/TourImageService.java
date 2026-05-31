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

import java.awt.*;
import java.io.ByteArrayOutputStream;
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
    private static final String THUMBNAIL_SUFFIX = "_thumb";

    private static final int THUMBNAIL_WIDTH = 150;
    private static final int THUMBNAIL_HEIGHT = 150;
    private final String startImage = "TOUR.JPG";
    private final String startImageThumb = "TOUR_thumb.JPG";
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
    public Tour saveTourImage(Long tourId, MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            Tour tour = tourDao.getReferenceById(tourId);
            String pathToSavedFile = save(multipartFile);
            String existingThumbnail = getThumbnailPathByTourId(tourId);
            if (existingThumbnail.equals("TOUR_thumb.JPG") || existingThumbnail == null) {
                createAndSaveThumbnail(tourId);
            }
            String thumbnailPath = getThumbnailPathByTourId(tourId);
            TourImage tourImage = TourImage.builder()
                    .path(pathToSavedFile)
                    .thumbnailPath(thumbnailPath)
                    .tour(tour)
                    .build();
            tour.addImage(tourImage);
            Tour savedTour = tourDao.save(tour);
            deleteStartImage(tourImage);
            return savedTour;
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

//    public void deleteImageTour(Long idImage) {
//        if (idImage != null){
//            tourImageDao.deleteById(idImage);
//        }
//    }


    public void deleteImageTour(Long idImage) {
        if (idImage != null) {
            // Удаляем миниатюру
            TourImage tourImage = tourImageDao.findById(idImage).orElse(null);
            if (tourImage != null && tourImage.getThumbnailPath() != null) {
                try {
                    Files.deleteIfExists(rootLocation.resolve(path).resolve(tourImage.getThumbnailPath()));
                } catch (IOException e) {
                    log.error("Failed to delete thumbnail: {}", e.getMessage());
                }
            }
            tourImageDao.deleteById(idImage);
        }
    }
    public void deleteStartImage(TourImage tourImage){
        Long idTour = tourImage.getTour().getId();
        TourImage image = tourImageDao.findFirstByTourId(idTour);
        if (tourImageDao.count(tourImage.getTour().getId()) > 1 && image.getPath().equals(startImage)){
            log.info(tourImageDao.count(tourImage.getTour().getId()).toString());
            log.info(image.getPath());
            tourImageDao.delete(image);
        }
    }
    public void addStartImage(Tour tour){
        TourImage tourImage = new TourImage();
        tourImage.setPath(startImage);
        tourImage.setThumbnailPath(startImageThumb);
        tourImage.setTour(tour);
        tourImageDao.save(tourImage);
    }

    public List <Long> getAllIdImagesByTourId(Long tourId){
        return tourImageDao.findAllIdImagesByTourId(tourId);
    }


    public Long getTourIdByImageId(Long id) {
        return tourImageDao.findTourIdByImageId(id);
    }


    /**
     * Изменяет размер изображения
     */
    private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = resized.createGraphics();

        // Включаем сглаживание для лучшего качества
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resized;
    }
    /**
     * Загружает миниатюру по имени файла
     */
    public Resource loadThumbnail(String filename) {
        String baseName = filename.substring(0, filename.lastIndexOf('.'));
        String extension = filename.substring(filename.lastIndexOf('.'));
        String thumbnailName = baseName + THUMBNAIL_SUFFIX + extension;
        return loadAsResource(thumbnailName);
    }

    /**
     * Загружает миниатюру по ID тура
     */
    public Resource loadThumbnailByTourId(Long tourId) {
        String originalImageName = uploadMultipleFilesByTourId(tourId);
        if (originalImageName != null) {
            return loadThumbnail(originalImageName);
        }
        return null;
    }

    /**
     * Загружает миниатюру по ID изображения
     */
    public Resource loadThumbnailByImageId(Long imageId) {
        String originalImageName = uploadMultipleFilesByImageId(imageId);
        if (originalImageName != null) {
            return loadThumbnail(originalImageName);
        }
        return null;
    }

    /**
     * Получить путь к миниатюре по ID тура
     */
    public String getThumbnailPathByTourId(Long tourId) {
        TourImage tourImage = tourImageDao.findFirstByTourId(tourId);
        return tourImage != null ? tourImage.getThumbnailPath() : null;
    }

    /**
     * Создать и сохранить миниатюру для тура
     */
    public byte[] createAndSaveThumbnail(Long tourId) throws IOException {
        String originalImageName = uploadMultipleFilesByTourId(tourId);
        if (originalImageName == null) {
            return null;
        }

        BufferedImage original = loadFileAsImage(tourId);
        if (original == null) {
            return null;
        }
        BufferedImage thumbnail = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, original.getType());
        Graphics2D g = thumbnail.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0,THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
        g.dispose();

        // Сохраняем файл
        String thumbnailName = originalImageName.replace(".", "_thumb.");
        Path thumbnailPath = rootLocation.resolve(path).resolve(thumbnailName);
        ImageIO.write(thumbnail, "png", thumbnailPath.toFile());

        // ✅ СОХРАНЯЕМ В БД
        TourImage tourImage = tourImageDao.findFirstByTourId(tourId);
        if (tourImage != null) {
            tourImage.setThumbnailPath(thumbnailName);
            tourImageDao.save(tourImage);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "png", baos);
        return baos.toByteArray();
    }
    public Long getThumbnailImageIdByTourId(Long tourId) {
        TourImage tourImage = tourImageDao.findFirstByTourId(tourId);
        if (tourImage != null && tourImage.getThumbnailPath() != null) {
            // Ищем TourImage с путем = thumbnailPath
            return tourImageDao.findIdByPath(tourImage.getThumbnailPath());
        }
        return null;
    }

}
