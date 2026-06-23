package com.nexum.estorno_tarifas.directories;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class SisbrLauncherTest {

    @Test
    void rejectsEmptyConfiguration() {
        IOException exception = assertThrows(IOException.class, () -> SisbrLauncher.open(""));
        assertTrue(exception.getMessage().contains("nao configurado"));
    }

    @Test
    void rejectsMissingExecutable() {
        IOException exception = assertThrows(
                IOException.class,
                () -> SisbrLauncher.open("C:\\arquivo-que-nao-existe\\Sisbr 2.0.exe"));
        assertTrue(exception.getMessage().contains("nao encontrado"));
    }
}
