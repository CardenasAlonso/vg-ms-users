package pe.edu.vallegrande.sigrc.users.domain.exceptions;

public class NotFoundException extends DomainException{
    public NotFoundException(String entity, String id) {
        super("NOT_FOUND", entity + " con id '" + id + "' no encontrado");
    }
}
