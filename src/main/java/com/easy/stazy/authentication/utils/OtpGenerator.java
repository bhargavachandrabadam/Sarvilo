package com.easy.stazy.authentication.utils;

import com.easy.stazy.authentication.repository.OtpRepository;
import java.security.SecureRandom;

public class OtpGenerator {
    private static final int OTP_LENGTH = 4;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random 4-digit OTP as a String.
     */
    public static String generateOtp() {
        return String.format("%04d", random.nextInt(10000));
    }

    /**
     * Generates a unique random 4-digit OTP as a String.
     * Checks the database using OtpRepository to ensure uniqueness among active OTPs.
     *
     * @param otpRepository the repository to check for active OTPs
     * @return a unique 4-digit OTP
     */
    public static String generateUniqueOtp(OtpRepository otpRepository) {
        String otp;
        boolean exists;
        do {
            otp = generateOtp();
            exists = otpRepository.findByOtpAndIsActive(otp, true).isPresent();
        } while (exists);
        return otp;
    }
}
