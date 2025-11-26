package com.easy.stazy.shared.common.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MessageConstants {
    public final static String AUTHENTICATION_FAILED = "Authentication Failed";
    public final static String ACCESS_DENIED = "Access Denied";
    public final static String UNSUPPORTED_REQUEST_BODY = "Unsupported Request Body";
    public final static String VALIDATION_FAILED = "Validation Failed";
    public final static String NO_TRACE_ID = "no-trace-id";
    public final static String ERROR_GETTING_TRACE_ID = "error-getting-trace-id";
    public final static String NOT_FOUND = "%s not found with %s : '%s'";
    public final static String ERROR_SENDING_EMAIL = "Error sending email with template: %s";
    public final static String FAILED_TO_SEND_EMAIL = "Failed to send email to %s: %s";
    public final static String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    public final static String ALREADY_EXISTS =  "%s with %s '%s' already exists.";
    public final static String VERIFICATION_EMAIL_SUBJECT = "Please verify your Email address";
    public final static String USER_NOT_FOUND = "User Not Found";
    public final static String INVALID_PASSWORD = "Invalid password";
    public final static String  ACCOUNT_NOT_ACTIVATED = "Account is not activated.";
    public final static String ACCOUNT_NOT_VERIFIED = "Account is not verified. Please check your email to verify your account.";
    public final static String USER_ALREADY_EXISTS = "User Already Exists";
    public final static String USER_CREATED_SUCCESSFULLY = "User Created Successfully";
    public final static String LOGIN_SUCCESSFULLY ="login successfully with id: %s and username: %s ";
    public final static String INVALID_TOKEN = "Invalid token";
    public final static String USER_ALREADY_VERIFIED = "User already verified";
    public final static String USER_VERIFIED_SUCCESSFULLY = "User verified successfully with id: %s and username: %s ";
    public final static String ACTIVATE_ACCOUNT_MESSAGE = "Click this link to activate your account: %s/v1/users/verify?token=%s\nThis link expires in 24 hours.";
    public final static String ACTIVATE_ACCOUNT_SUBJECT = "Activate Your Dine-Ease Account";
    public final static String SENDING_ACCOUNT_ACTIVATION_EMAIL = "Sending account activation email to user: %s";
    public final static String RESET_PASSWORD_SUBJECT = "Reset Your Dine-Ease Password";
    public final static String RESET_PASSWORD_TOKEN_SENT = "Reset password token sent to your email";
    public final static String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully";
    public final static String VERIFICATION_EMAIL_WITH_PASSWORD = """
            <html>
            <body>
            <h3>Welcome to Our Dine-Ease Platform!</h3>
            <p>We're excited to have you on board. Your account has been created successfully.</p>
            <p><strong>Here is your temporary password: %s</strong></p>
            <p>To complete your registration, please click on the link below: </p>
            <a href="%s" style = "display: inline-block; padding: 10px 20px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 5px;">Verify Email</a>
            <p style = "margin-top: 20px;">After your first login, we strongly recommend you to change your password for security reasons.</p>
            <p>Thank you for choosing Dine-Ease! If you have any questions or need assistance, please don't hesitate to contact our support team.<br>The Dine-Ease Team</p>
            </body>
            </html>
            """;
    public final static String RESET_PASSWORD_MESSAGE = """
            Hi %s,
            
            You requested a password reset.
            
            Click the link below to reset your password:
            https://dine-ease-domain.com/reset-password?token=%s
            
            If you did not request this, please ignore this email.
            
            Thanks,
            The Dine-Ease Team
            """;

}
