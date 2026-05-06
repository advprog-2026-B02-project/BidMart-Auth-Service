package id.ac.ui.cs.advprog.bidmart.backend.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "mailEnabled", true);
    }

    @Test
    void sendVerificationEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendVerificationEmail("test@test.com", "http://link");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetPasswordEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendResetPasswordEmail("test@test.com", "http://link");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmailDisabledAndMailFailureDoNotThrow() {
        ReflectionTestUtils.setField(emailService, "mailEnabled", false);
        emailService.sendVerificationEmail("test@test.com", "http://link");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));

        ReflectionTestUtils.setField(emailService, "mailEnabled", true);
        doThrow(new MailException("boom") { }).when(mailSender).send(any(SimpleMailMessage.class));
        assertDoesNotThrow(() -> emailService.sendVerificationEmail("test@test.com", "http://link"));
    }

    @Test
    void sendResetPasswordEmailDisabledAndMailFailureDoNotThrow() {
        ReflectionTestUtils.setField(emailService, "mailEnabled", false);
        emailService.sendResetPasswordEmail("test@test.com", "http://link");

        ReflectionTestUtils.setField(emailService, "mailEnabled", true);
        doThrow(new MailException("boom") { }).when(mailSender).send(any(SimpleMailMessage.class));
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmail("test@test.com", "http://link"));
    }
}
