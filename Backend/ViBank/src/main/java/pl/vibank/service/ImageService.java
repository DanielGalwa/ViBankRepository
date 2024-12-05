package pl.vibank.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageService {

    private final String imgLocation;

    public ImageService(@Value("${app.storage.location}") String storageLocation) {
        this.imgLocation = storageLocation + "/img/";
        Path imageStoragePath = Path.of(this.imgLocation);
        prepareStorageDirectory(imageStoragePath);
    }

    private void prepareStorageDirectory(Path imageStoragePath) {
        try {
            if (Files.notExists(imageStoragePath)) {
                Files.createDirectories(imageStoragePath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Nie udało się stworzyć folderu do przechowywania zdjęć", e);
        }
    }

    public byte[] getFile(String name) throws IOException {
        Path filePath = Path.of(this.imgLocation + name);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Plik nie został odnaleziony");
        }

        return Files.readAllBytes(filePath);
    }
}