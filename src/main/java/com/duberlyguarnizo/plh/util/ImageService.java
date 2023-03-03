package com.duberlyguarnizo.plh.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
@Slf4j
public class ImageService {
    private final Path root = Paths.get("uploads");
    private final Path profilePics = Paths.get("uploads/profilePics");
    private final Path evidencePics = Paths.get("uploads/evidencePics");

    public void init() {
        try {
            Files.createDirectories(root);
            Files.createDirectories(profilePics);
            Files.createDirectories(evidencePics);
            log.warn("ImageService: init(): Creating directory");
            log.warn(root.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public void saveEvidenceImage(String uploadDir, String fileName,
                                  MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public boolean saveProfilePicture(MultipartFile file, String username) {
        try {
            log.warn("ImageService: saveProfilePicture(): Saving profile picture in service layer");
            log.warn("ImageService: saving to resolved URI route: " + this.root.resolve("profilePics/" + username + ".jpg").toAbsolutePath().toUri());
            InputStream inputStream = file.getInputStream();
            log.warn("ImageService: Input stream is: " + inputStream);
            //TODO: fix only jpg images allowed.
            Files.copy(inputStream,
                    this.root.resolve("profilePics/" + username + ".jpg"),
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (DirectoryNotEmptyException e) {
            log.warn("ImageService: the directory is not empty!");
            return false;
        } catch (FileAlreadyExistsException e) {
            log.warn("ImageService: the file already exists!");
            return false;
        } catch (IOException e) {
            log.warn("ImageService: there was a IO exception!");
            e.printStackTrace();
            return false;
        } catch (UnsupportedOperationException e) {
            log.warn("ImageService: the operation is not supported!");
            return false;
        } catch (SecurityException e) {
            log.warn("ImageService: the security exception!");
            return false;
        }
    }
}
