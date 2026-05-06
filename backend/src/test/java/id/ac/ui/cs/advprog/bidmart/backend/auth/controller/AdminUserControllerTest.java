package id.ac.ui.cs.advprog.bidmart.backend.auth.controller;

import id.ac.ui.cs.advprog.bidmart.backend.auth.dto.UpdateUserRolesRequestDTO;
import id.ac.ui.cs.advprog.bidmart.backend.auth.dto.UpdateUserStatusRequestDTO;
import id.ac.ui.cs.advprog.bidmart.backend.auth.dto.UserResponseDTO;
import id.ac.ui.cs.advprog.bidmart.backend.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AdminUserController adminUserController;

    private UUID testUserId;
    private UserResponseDTO testUserResponse;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserResponse = new UserResponseDTO(
                testUserId,
                "test@example.com",
                "Test User",
                true,
                Instant.now(),
                Arrays.asList("ROLE_USER")
        );
    }

    @Test
    void testListUsersWithoutFilters() {
        List<UserResponseDTO> users = Arrays.asList(testUserResponse);
        when(authService.adminListUsers(null, null, null, 0, 20))
                .thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(0, 20, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(authService).adminListUsers(null, null, null, 0, 20);
    }

    @Test
    void testListUsersWithSearchFilter() {
        List<UserResponseDTO> users = Arrays.asList(testUserResponse);
        when(authService.adminListUsers("test", null, null, 0, 20))
                .thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(0, 20, "test", null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(authService).adminListUsers("test", null, null, 0, 20);
    }

    @Test
    void testListUsersWithRoleFilter() {
        List<UserResponseDTO> users = Arrays.asList(testUserResponse);
        when(authService.adminListUsers(null, "ROLE_USER", null, 0, 20))
                .thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(0, 20, null, "ROLE_USER", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(authService).adminListUsers(null, "ROLE_USER", null, 0, 20);
    }

    @Test
    void testListUsersWithStatusFilter() {
        List<UserResponseDTO> users = Arrays.asList(testUserResponse);
        when(authService.adminListUsers(null, null, "ACTIVE", 0, 20))
                .thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(0, 20, null, null, "ACTIVE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(authService).adminListUsers(null, null, "ACTIVE", 0, 20);
    }

    @Test
    void testListUsersWithPagination() {
        List<UserResponseDTO> users = Arrays.asList(testUserResponse);
        when(authService.adminListUsers(null, null, null, 2, 50))
                .thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(2, 50, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).adminListUsers(null, null, null, 2, 50);
    }

    @Test
    void testListUsersEmpty() {
        when(authService.adminListUsers(null, null, null, 0, 20))
                .thenReturn(Arrays.asList());

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(0, 20, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetUserSuccess() {
        when(authService.adminGetUser(testUserId)).thenReturn(testUserResponse);

        ResponseEntity<UserResponseDTO> response = adminUserController.getUser(testUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserResponse.email, response.getBody().email);
        verify(authService).adminGetUser(testUserId);
    }

    @Test
    void testGetUserNotFound() {
        when(authService.adminGetUser(testUserId))
                .thenThrow(new IllegalArgumentException("User not found"));

        try {
            adminUserController.getUser(testUserId);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(authService).adminGetUser(testUserId);
    }

    @Test
    void testUpdateUserStatusSuccess() {
        UserResponseDTO updatedUser = new UserResponseDTO(
                testUserId,
                "test@example.com",
                "Test User",
                true,
                Instant.now(),
                Arrays.asList("ROLE_USER")
        );
        when(authService.adminUpdateUserStatus(testUserId, "SUSPENDED", "Violation"))
                .thenReturn(updatedUser);

        UpdateUserStatusRequestDTO request = new UpdateUserStatusRequestDTO();
        request.status = "SUSPENDED";
        request.reason = "Violation";

        ResponseEntity<UserResponseDTO> response = adminUserController.updateStatus(testUserId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(authService).adminUpdateUserStatus(testUserId, "SUSPENDED", "Violation");
    }

    @Test
    void testUpdateUserStatusWithoutReason() {
        UserResponseDTO updatedUser = new UserResponseDTO(
                testUserId,
                "test@example.com",
                "Test User",
                true,
                Instant.now(),
                Arrays.asList("ROLE_USER")
        );
        when(authService.adminUpdateUserStatus(testUserId, "SUSPENDED", null))
                .thenReturn(updatedUser);

        UpdateUserStatusRequestDTO request = new UpdateUserStatusRequestDTO();
        request.status = "SUSPENDED";
        request.reason = null;

        ResponseEntity<UserResponseDTO> response = adminUserController.updateStatus(testUserId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).adminUpdateUserStatus(testUserId, "SUSPENDED", null);
    }

    @Test
    void testUpdateUserRolesSuccess() {
        UserResponseDTO updatedUser = new UserResponseDTO(
                testUserId,
                "test@example.com",
                "Test User",
                true,
                Instant.now(),
                Arrays.asList("ROLE_USER", "ROLE_ADMIN")
        );
        List<String> newRoles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        when(authService.adminUpdateUserRoles(testUserId, newRoles))
                .thenReturn(updatedUser);

        UpdateUserRolesRequestDTO request = new UpdateUserRolesRequestDTO();
        request.roles = newRoles;

        ResponseEntity<UserResponseDTO> response = adminUserController.updateRoles(testUserId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().roles.size());
        verify(authService).adminUpdateUserRoles(testUserId, newRoles);
    }

    @Test
    void testUpdateUserRolesSingleRole() {
        UserResponseDTO updatedUser = new UserResponseDTO(
                testUserId,
                "test@example.com",
                "Test User",
                true,
                Instant.now(),
                Arrays.asList("ROLE_ADMIN")
        );
        List<String> newRoles = Arrays.asList("ROLE_ADMIN");
        when(authService.adminUpdateUserRoles(testUserId, newRoles))
                .thenReturn(updatedUser);

        UpdateUserRolesRequestDTO request = new UpdateUserRolesRequestDTO();
        request.roles = newRoles;

        ResponseEntity<UserResponseDTO> response = adminUserController.updateRoles(testUserId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().roles.size());
        verify(authService).adminUpdateUserRoles(testUserId, newRoles);
    }

    @Test
    void testListUsersWithAllFilters() {
        List<UserResponseDTO> users = Arrays.asList(testUserResponse);
        when(authService.adminListUsers("search", "ROLE_USER", "ACTIVE", 1, 10))
                .thenReturn(users);

        ResponseEntity<List<UserResponseDTO>> response = adminUserController.listUsers(1, 10, "search", "ROLE_USER", "ACTIVE");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).adminListUsers("search", "ROLE_USER", "ACTIVE", 1, 10);
    }

    @Test
    void testGetUserReturnsCorrectData() {
        UserResponseDTO user = new UserResponseDTO(
                testUserId,
                "user@domain.com",
                "John Doe",
                false,
                Instant.now(),
                Arrays.asList("ROLE_USER")
        );
        when(authService.adminGetUser(testUserId)).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = adminUserController.getUser(testUserId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user@domain.com", response.getBody().email);
        assertEquals("John Doe", response.getBody().displayName);
        assertEquals(false, response.getBody().emailVerified);
    }
}
