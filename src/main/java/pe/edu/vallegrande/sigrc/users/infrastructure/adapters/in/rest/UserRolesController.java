package pe.edu.vallegrande.sigrc.users.infrastructure.adapters.in.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigrc.users.application.dto.common.ApiResponse;
import pe.edu.vallegrande.sigrc.users.application.dto.request.AssignRoleRequest;
import pe.edu.vallegrande.sigrc.users.application.dto.response.UserWithRolesResponse;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IAssignRoleUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IGetUserRolesUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IRevokeRoleUseCase;
import pe.edu.vallegrande.sigrc.users.domain.ports.in.IUpdateUserRoleUseCase;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-roles")
@Tag(name = "User Roles", description = "Operaciones para asignar y revocar roles de usuarios")
public class UserRolesController {
    private final IGetUserRolesUseCase getUseCase;
    private final IAssignRoleUseCase assignUseCase;
    private final IUpdateUserRoleUseCase updateUseCase;
    private final IRevokeRoleUseCase revokeUseCase;

    @GetMapping("/unassigned-users")
    public Mono<ApiResponse<List<UserWithRolesResponse>>> getUnassignedUsers() {
        return getUseCase.getUnassignedUsers()
                .collectList()
                .map(users -> ApiResponse.ok("Usuarios sin rol obtenidos", users));
    }

    @GetMapping("/all-users")
    public Mono<ApiResponse<List<UserWithRolesResponse>>> getAllUsersWithRoles() {
        return getUseCase.getAllUsersWithRoles()
                .collectList()
                .map(users -> ApiResponse.ok("Usuarios con roles obtenidos", users));
    }

    @GetMapping("/user/{userId}")
    public Mono<ApiResponse<UserWithRolesResponse>> getByUser(@PathVariable String userId) {
        return getUseCase.getByUserId(userId)
                .map(user -> ApiResponse.ok("Roles del usuario obtenidos", user));
    }

    @GetMapping("/role/{roleId}")
    public Mono<ApiResponse<List<UserWithRolesResponse>>> getByRole(@PathVariable String roleId) {
        return getUseCase.getByRoleId(roleId)
                .collectList()
                .map(users -> ApiResponse.ok("Usuarios con rol obtenidos", users));
    }

    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<UserWithRolesResponse>> assign(@Valid @RequestBody AssignRoleRequest request) {
        return assignUseCase.assign(request)
                .map(user -> ApiResponse.ok("Rol asignado exitosamente", user));
    }

    @PutMapping("/update")
    public Mono<ApiResponse<UserWithRolesResponse>> update(@Valid @RequestBody AssignRoleRequest request) {
        return updateUseCase.update(request)
                .map(user -> ApiResponse.ok("Rol de usuario actualizado exitosamente", user));
    }

    @DeleteMapping("/revoke/{userId}/{roleId}")
    public Mono<ApiResponse<Void>> revoke(@PathVariable String userId, @PathVariable String roleId) {
        return revokeUseCase.revoke(userId, roleId)
                .then(Mono.just(ApiResponse.ok("Rol revocado", null)));
    }
}
