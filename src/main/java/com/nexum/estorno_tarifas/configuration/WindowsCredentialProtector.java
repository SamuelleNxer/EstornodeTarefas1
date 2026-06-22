package com.nexum.estorno_tarifas.configuration;

import com.sun.jna.platform.win32.Crypt32Util;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

final class WindowsCredentialProtector {
    private static final String PREFIX = "dpapi:";

    private WindowsCredentialProtector() {
    }

    static String protect(String value) {
        if (value == null || value.isEmpty() || value.startsWith(PREFIX)) {
            return value == null ? "" : value;
        }
        requireWindows();
        byte[] encrypted = Crypt32Util.cryptProtectData(value.getBytes(StandardCharsets.UTF_8));
        return PREFIX + Base64.getEncoder().encodeToString(encrypted);
    }

    static String unprotect(String value) {
        if (value == null || value.isEmpty() || !value.startsWith(PREFIX)) {
            return value == null ? "" : value;
        }
        requireWindows();
        byte[] encrypted = Base64.getDecoder().decode(value.substring(PREFIX.length()));
        return new String(Crypt32Util.cryptUnprotectData(encrypted), StandardCharsets.UTF_8);
    }

    private static void requireWindows() {
        if (!System.getProperty("os.name", "").startsWith("Windows")) {
            throw new IllegalStateException("A protecao de credenciais DPAPI requer Windows.");
        }
    }
}
