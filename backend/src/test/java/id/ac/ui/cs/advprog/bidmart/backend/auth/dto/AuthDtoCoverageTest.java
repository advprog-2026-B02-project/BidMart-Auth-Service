package id.ac.ui.cs.advprog.bidmart.backend.auth.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthDtoCoverageTest {

    @Test
    void fieldOnlyDtosCanBeConstructedAndAssigned() {
        ChangePasswordRequestDTO changePassword = new ChangePasswordRequestDTO();
        changePassword.currentPassword = "old-password";
        changePassword.newPassword = "new-password";
        assertEquals("old-password", changePassword.currentPassword);

        TwoFactorSetupRequestDTO setup = new TwoFactorSetupRequestDTO();
        setup.method = "TOTP";
        assertEquals("TOTP", setup.method);

        VerifyEmailRequestDTO verifyEmail = new VerifyEmailRequestDTO();
        verifyEmail.token = "token";
        assertEquals("token", verifyEmail.token);

        TwoFactorConfirmRequestDTO confirm = new TwoFactorConfirmRequestDTO();
        confirm.code = "123456";
        assertEquals("123456", confirm.code);

        TwoFactorDisableRequestDTO disable = new TwoFactorDisableRequestDTO();
        disable.password = "password";
        assertEquals("password", disable.password);

        TwoFactorVerifyRequestDTO verify = new TwoFactorVerifyRequestDTO();
        verify.partialToken = "partial";
        verify.method = "TOTP";
        verify.code = "654321";
        assertEquals("partial", verify.partialToken);
    }

    @Test
    void constructorDtosExposeAssignedValues() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        SessionResponseDTO session = new SessionResponseDTO(id, "device", "127.0.0.1", now, true);
        assertEquals(id, session.id);
        assertEquals("device", session.device);
        assertEquals("127.0.0.1", session.ipAddress);
        assertEquals(now, session.lastActive);
        assertTrue(session.current);

        PartialLoginResponseDTO partial = new PartialLoginResponseDTO("partial", false, List.of("TOTP"), 300);
        assertEquals("partial", partial.partialToken);
        assertFalse(partial.requires2FA);
        assertEquals(List.of("TOTP"), partial.methods);
        assertEquals(300, partial.expiresIn);

        TwoFactorSetupResponseDTO setup = new TwoFactorSetupResponseDTO("secret", "qr", List.of("backup"));
        assertEquals("secret", setup.secret);
        assertEquals("qr", setup.qrCodeUri);
        assertEquals(List.of("backup"), setup.backupCodes);
    }
}
