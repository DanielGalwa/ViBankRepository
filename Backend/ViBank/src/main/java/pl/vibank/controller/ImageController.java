package pl.vibank.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.vibank.service.ImageService;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/photos")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{name}")
    public ResponseEntity<?> getByName(@PathVariable("name") String name){
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(imageService.getFile(name));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
