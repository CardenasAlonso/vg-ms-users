package pe.edu.vallegrande.sigrc.users.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.application.mappers.UsersMapper;
import pe.edu.vallegrande.sigrc.users.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IProfileUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IImageStoragePort;
import pe.edu.vallegrande.sigrc.users.domain.ports.out.IUsersRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileUseCaseImpl implements IProfileUseCase {

    private final IUsersRepository repository;
    private final IImageStoragePort imageStoragePort;

    @Override
    public Mono<UsersResponse> getMyProfile(String username) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado en la base de datos.")))
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> updateMyProfile(String username, UpdateUsersRequest request) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado.")))
                .flatMap(user -> {
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setPhone(request.getPhone());
                    // we might need more complex logic to check email uniqueness
                    user.setEmail(request.getEmail());
                    user.setDocumentType(request.getDocumentType());
                    user.setDocumentNumber(request.getDocumentNumber());
                    user.setUpdatedAt(LocalDateTime.now());
                    
                    return repository.save(user);
                })
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> updateMyAvatar(String username, FilePart filePart) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado.")))
                .flatMap(user -> imageStoragePort.uploadImage(filePart, user.getUserId())
                        .flatMap(imageUrl -> {
                            user.setProfileImagePath(imageUrl);
                            user.setUpdatedAt(LocalDateTime.now());
                            return repository.save(user);
                        }))
                .map(UsersMapper::toResponse);
    }

    @Override
    public Mono<UsersResponse> deleteMyAvatar(String username) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new DomainException("USER_NOT_FOUND", "Usuario no encontrado.")))
                .flatMap(user -> {
                    if (user.getProfileImagePath() == null || user.getProfileImagePath().isBlank()) {
                        return Mono.just(user);
                    }
                    return imageStoragePort.deleteImage(user.getProfileImagePath())
                            .then(Mono.defer(() -> {
                                user.setProfileImagePath(null);
                                user.setUpdatedAt(LocalDateTime.now());
                                return repository.save(user);
                            }));
                })
                .map(UsersMapper::toResponse);
    }
}
