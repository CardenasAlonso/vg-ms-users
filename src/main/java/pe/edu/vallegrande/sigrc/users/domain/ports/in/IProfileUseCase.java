package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import org.springframework.http.codec.multipart.FilePart;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import reactor.core.publisher.Mono;

public interface IProfileUseCase {
    Mono<UsersResponse> getMyProfile(String keycloakId);
    Mono<UsersResponse> updateMyProfile(String keycloakId, UpdateUsersRequest request);
    Mono<UsersResponse> updateMyAvatar(String keycloakId, FilePart filePart);
    Mono<UsersResponse> deleteMyAvatar(String keycloakId);
}
