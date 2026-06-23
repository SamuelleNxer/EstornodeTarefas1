package com.nexum.estorno_tarifas.directories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SisbrLauncher {
    private SisbrLauncher() {
    }

    public static Process open(String executableDirectory) throws IOException {
        if (executableDirectory == null || executableDirectory.isBlank()) {
            throw new IOException(
                    "Caminho do SISBR nao configurado. Abra Configuracoes e selecione o executavel.");
        }

        Path executable = Path.of(executableDirectory).toAbsolutePath().normalize();
        if (!Files.isRegularFile(executable)) {
            throw new IOException("Executavel do SISBR nao encontrado: " + executable);
        }
        if (!executable.getFileName().toString().toLowerCase().endsWith(".exe")) {
            throw new IOException("O caminho configurado para o SISBR deve apontar para um arquivo .exe.");
        }

        System.out.println("Abrindo SISBR: " + executable);
        return new ProcessBuilder(executable.toString())
                .directory(executable.getParent().toFile())
                .start();
    }
}
