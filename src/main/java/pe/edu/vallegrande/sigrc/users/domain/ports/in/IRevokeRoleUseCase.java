package pe.edu.vallegrande.sigrc.users.domain.ports.in;

import reactor.core.publisher.Mono;

public interface IRevokeRoleUseCase {
    Mono<Void> revoke(String userId, String roleId);
}
