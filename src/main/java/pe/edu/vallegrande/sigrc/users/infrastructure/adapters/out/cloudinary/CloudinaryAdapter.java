package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.out.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IImageStoragePort;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryAdapter implements IImageStoragePort {

    private final Cloudinary cloudinary;

    @Override
    public Mono<String> uploadImage(FilePart filePart, String userId) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .flatMap(bytes -> Mono.fromCallable(() -> {
                    Map<String, Object> options = ObjectUtils.asMap(
                            "folder", "users_profiles",
                            "public_id", "user_" + userId
                    );
                    Map uploadResult = cloudinary.uploader().upload(bytes, options);
                    return uploadResult.get("secure_url").toString();
                }));
    }

    @Override
    public Mono<Void> deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return Mono.empty();
        }
        return Mono.fromCallable(() -> {
            try {
                // Extract public_id from the secure_url
                // Example URL: https://res.cloudinary.com/cloud_name/image/upload/v1234/users_profiles/user_12345.jpg
                String[] parts = imageUrl.split("/");
                String folderAndFile = parts[parts.length - 2] + "/" + parts[parts.length - 1];
                String publicId = folderAndFile.substring(0, folderAndFile.lastIndexOf('.'));
                
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                log.error("Error deleting image from Cloudinary: {}", e.getMessage());
            }
            return null;
        }).then();
    }
}
