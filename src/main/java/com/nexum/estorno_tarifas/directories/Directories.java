package com.nexum.estorno_tarifas.directories;

import com.nexum.estorno_tarifas.configuration.EstornoTarifasConfigurationStore;
import java.io.IOException;

public final class Directories {
    public static String SISBR_EXECUTABLE_DIRECTORY = "";

    private Directories() {
    }

    public static synchronized void reload() throws IOException {
        SISBR_EXECUTABLE_DIRECTORY = new EstornoTarifasConfigurationStore()
                .load()
                .executavelSisbr();
    }
}
