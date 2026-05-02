package id.ac.ui.cs.advprog.bidmart.backend.auth.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class InternalUserResponseDTO {

    public UUID id;
    public String email;
    public String displayName;
    public String avatarUrl;
    public boolean emailVerified;
    public List<String> roles;
    public String status;
    public Instant createdAt;
    public Instant updatedAt;

    public InternalUserResponseDTO(UUID id,
                                   String email,
                                   String displayName,
                                   String avatarUrl,
                                   boolean emailVerified,
                                   List<String> roles,
                                   String status,
                                   Instant createdAt,
                                   Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.emailVerified = emailVerified;
        this.roles = roles;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}