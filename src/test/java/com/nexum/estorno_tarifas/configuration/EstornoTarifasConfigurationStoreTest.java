package com.nexum.estorno_tarifas.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EstornoTarifasConfigurationStoreTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void createsConfigurationWithPddDefaults() throws Exception {
        Path file = temporaryDirectory.resolve("configuration.properties");
        EstornoTarifasConfigurationStore store = new EstornoTarifasConfigurationStore(file);

        EstornoTarifasConfiguration configuration = store.load();

        assertTrue(file.toFile().isFile());
        assertEquals("5042", configuration.cooperativa());
        assertEquals(1, configuration.primeiroDiaUtil());
        assertEquals(5, configuration.ultimoDiaUtil());
        assertEquals("Conta Corrente", configuration.moduloSisbr());
        assertEquals("1", configuration.documentoPadrao());
        assertTrue(configuration.usarDataAtual());
        assertTrue(configuration.marcarEstornoTarifa());
    }

    @Test
    void savesAndLoadsEveryEditableValue() throws Exception {
        EstornoTarifasConfigurationStore store = new EstornoTarifasConfigurationStore(
                temporaryDirectory.resolve("configuration.properties"));
        EstornoTarifasConfiguration expected = new EstornoTarifasConfiguration(
                "9999", false, "robo", "segredo", false, 2, 4,
                "C:\\bases\\estornos.xlsx", "Modulo", "Menu", "Submenu", "Rotina",
                "7", false, false);

        store.save(expected);

        assertEquals(expected, store.load());
        String persisted = Files.readString(store.getPath());
        assertTrue(persisted.contains("senha.sisbr=dpapi:"));
        assertTrue(!persisted.contains("senha.sisbr=segredo"));
    }
}
