package com.easy.stazy.shared.common.util;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

public class PhoneUtil {

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public static String normalizeToE164(String input, String defaultRegion) throws NumberParseException {
        PhoneNumber number = phoneUtil.parse(input, defaultRegion);
        if (!phoneUtil.isValidNumber(number)) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        return phoneUtil.format(number, PhoneNumberFormat.E164); // e.g., +917981344858
    }
}