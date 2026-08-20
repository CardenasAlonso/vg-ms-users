package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigrc.users.application.dto.common.ApiResponse;
import pe.edu.vallegrande.sigrc.users.application.dto.request.UpdateUsersRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UsersResponse;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IProfileUseCase;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "Operaciones para gestion del perfil propio del usuario")
public class ProfileController {

    private final IProfileUseCase profileUseCase;

    private String getUsernameFromJwt(Jwt jwt) {
        if (jwt.hasClaim("preferred_username")) {
            return jwt.getClaimAsString("preferred_username");
        }
        return jwt.getSubject(); // Fallback si preferred_username no esta configurado
    }

    @GetMapping
    public Mono<ApiResponse<UsersResponse>> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String username = getUsernameFromJwt(jwt);
        return profileUseCase.getMyProfile(username)
                .map(user -> ApiResponse.ok("Perfil obtenido exitosamente", user));
    }

    @PutMapping
    public Mono<ApiResponse<UsersResponse>> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUsersRequest request) {
        String username = getUsernameFromJwt(jwt);
        return profileUseCase.updateMyProfile(username, request)
                .map(user -> ApiResponse.ok("Perfil actualizado exitosamente", user));
    }

    @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ApiResponse<UsersResponse>> updateMyAvatar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("file") FilePart filePart) {
        String username = getUsernameFromJwt(jwt);
        return profileUseCase.updateMyAvatar(username, filePart)
                .map(user -> ApiResponse.ok("Avatar actualizado exitosamente", user));
    }

    @DeleteMapping("/avatar")
    public Mono<ApiResponse<UsersResponse>> deleteMyAvatar(@AuthenticationPrincipal Jwt jwt) {
        String username = getUsernameFromJwt(jwt);
        return profileUseCase.deleteMyAvatar(username)
                .map(user -> ApiResponse.ok("Avatar eliminado exitosamente", user));
    }
}
