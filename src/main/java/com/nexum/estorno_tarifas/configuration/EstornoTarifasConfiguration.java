package com.nexum.estorno_tarifas.configuration;

public record EstornoTarifasConfiguration(
        String cooperativa,
        boolean loginManual,
        String usuarioSisbr,
        String senhaSisbr,
        boolean schedulerHabilitado,
        int primeiroDiaUtil,
        int ultimoDiaUtil,
        String caminhoPlanilha,
        String executavelSisbr,
        String moduloSisbr,
        String menuSisbr,
        String submenuSisbr,
        String rotinaSisbr,
        String documentoPadrao,
        boolean usarDataAtual,
        boolean marcarEstornoTarifa) {

    public static EstornoTarifasConfiguration defaults() {
        return new EstornoTarifasConfiguration(
                "5042",
                true,
                "",
                "",
                true,
                1,
                5,
                "",
                "",
                "Conta Corrente",
                "Movimentacao",
                "Tarifas",
                "Lancamento de Tarifas",
                "1",
                true,
                true);
    }
}
