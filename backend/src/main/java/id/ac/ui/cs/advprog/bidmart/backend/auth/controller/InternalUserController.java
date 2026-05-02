package id.ac.ui.cs.advprog.bidmart.backend.auth.controller;

import id.ac.ui.cs.advprog.bidmart.backend.auth.entity.User;
import id.ac.ui.cs.advprog.bidmart.backend.auth.entity.UserStatus;
import id.ac.ui.cs.advprog.bidmart.backend.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final AuthService authService;

    public InternalUserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Map<String, Object>> validateUser(@PathVariable UUID userId) {
        User user = authService.getUserById(userId);

        Map<String, Object> body = new LinkedHashMap<>();

        if (user.getStatus() == UserStatus.SUSPENDED) {
            body.put("valid", false);
            body.put("userId", user.getId());
            body.put("status", safeStatus(user));
            body.put("message", "User ini telah di-suspend.");
            return ResponseEntity.status(403).body(body);
        }

        body.put("valid", true);
        body.put("userId", user.getId());
        body.put("status", safeStatus(user));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable UUID userId) {
        User user = authService.getUserById(userId);
        return ResponseEntity.ok(toInternalUserMap(user));
    }

    @GetMapping("/by-email")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@RequestParam String email) {
        User user = authService.getUserByEmail(email);
        return ResponseEntity.ok(toInternalUserMap(user));
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<Map<String, Object>> getUserRoles(@PathVariable UUID userId) {
        User user = authService.getUserById(userId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", user.getId());
        body.put("roles", safeRoles(user));

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable UUID userId) {
        User user = authService.getUserById(userId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", user.getId());
        body.put("status", safeStatus(user));
        body.put("valid", user.getStatus() != UserStatus.SUSPENDED);

        return ResponseEntity.ok(body);
    }

    private Map<String, Object> toInternalUserMap(User user) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("id", user.getId());
        body.put("email", user.getEmail());
        body.put("displayName", user.getDisplayName() != null ? user.getDisplayName() : "Pengguna Baru");
        body.put("avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        body.put("emailVerified", user.isEmailVerified());
        body.put("roles", safeRoles(user));
        body.put("status", safeStatus(user));
        body.put("createdAt", user.getCreatedAt());
        body.put("updatedAt", user.getUpdatedAt());

        return body;
    }

    private List<String> safeRoles(User user) {
        return user.getRolesList() != null ? user.getRolesList() : List.of();
    }

    private String safeStatus(User user) {
        return user.getStatus() != null ? user.getStatus().name() : "ACTIVE";
    }
}