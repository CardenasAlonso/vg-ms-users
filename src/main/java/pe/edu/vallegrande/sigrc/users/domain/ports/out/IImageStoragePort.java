package pe.edu.vallegrande.sigrc.users.domain.ports.out;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IImageStoragePort {
    Mono<String> uploadImage(FilePart filePart, String userId);
    Mono<Void> deleteImage(String imageUrl);
}
