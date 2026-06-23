# Estorno de Tarifas

Aplicacao independente para automatizar o estorno de tarifas no SISBR, criada a
partir dos requisitos do PDD e inspirada na estrutura visual do follow-up.

## Executar

Requisitos: Java 17 e Maven.

```powershell
mvn spring-boot:run
```

A tela `Configuracoes` permite editar os parametros operacionais levantados no
PDD: cooperativa, acesso ao SISBR, janela mensal, planilha, navegacao no modulo
Conta Corrente e valores padrao do lancamento. Os valores ficam no arquivo
externo `configuration.properties`, sem necessidade de recompilar a aplicacao.

O fluxo SISBR e a leitura da planilha serao implementados em componentes
separados.

Ao iniciar o processo, a aplicacao recarrega `sisbr.executavel` por meio de
`Directories.SISBR_EXECUTABLE_DIRECTORY`, valida o arquivo e abre o SISBR antes
de inicializar o contexto do Spring.
