package com.nexum.estorno_tarifas.directories;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SisbrLauncherTest {
    @TempDir
    Path temporaryDirectory;

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

    @Test
    void resolvesQuotedExecutablePath() throws Exception {
        Path executable = Files.createFile(temporaryDirectory.resolve("Sisbr 2.0.exe"));

        assertEquals(executable, SisbrLauncher.resolveExecutable("\"" + executable + "\""));
    }

    @Test
    void resolvesFileUrl() throws Exception {
        Path executable = Files.createFile(temporaryDirectory.resolve("Sisbr.exe"));

        assertEquals(executable, SisbrLauncher.resolveExecutable(executable.toUri().toString()));
    }

    @Test
    void findsSisbrExecutableInsideDirectory() throws Exception {
        Files.createFile(temporaryDirectory.resolve("Outro.exe"));
        Path executable = Files.createFile(temporaryDirectory.resolve("Sisbr 2.0.exe"));

        assertEquals(executable, SisbrLauncher.resolveExecutable(temporaryDirectory.toString()));
    }

    @Test
    void rejectsWebUrl() {
        IOException exception = assertThrows(
                IOException.class,
                () -> SisbrLauncher.resolveExecutable("https://example.com/Sisbr.exe"));

        assertTrue(exception.getMessage().contains("nao uma URL da internet"));
    }

    @Test
    void normalizesWindowsPathLikeFollowUp() {
        assertEquals("C:\\Sisbr\\Sisbr.exe", SisbrLauncher.normalizeExecutablePath("\"C:/Sisbr/Sisbr.exe\""));
        assertEquals("C:\\Sisbr\\Sisbr.exe", SisbrLauncher.normalizeExecutablePath("C:Sisbr\\Sisbr.exe"));
    }
}
