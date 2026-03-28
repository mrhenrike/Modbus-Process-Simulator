# Handoff — Modbus Slave Simulator v1.7.5

## Objetivo da sessão

- Corrigir comportamento dos botões:
  - `Automation`: **Start all** / **Stop all**
  - `Modbus slaves`: **Enable all** / **Disable all**
- Gerar nova release com os ajustes.

## Alterações principais

### Correções aplicadas

- `src/modbuspal/automation/AutomationPanel.java`:
  - removida recursão indevida em callbacks:
    - `automationHasStarted()` **não** chama mais `automation.start()`
    - `automationHasEnded()` **não** chama mais `automation.stop()`
  - atualização de UI desses callbacks movida para EDT com `SwingUtilities.invokeLater(...)`.

- `src/modbuspal/slave/ModbusSlavePanel.java`:
  - registrado listener do modelo no painel:
    - `modbusSlave.addModbusSlaveListener(this)` no construtor.
  - removido listener no ciclo de vida:
    - `delete()` e `ancestorRemoved()`.
  - atualizações de UI (`enableToggleButton`, `nameTextField`) agora no EDT.

### Versionamento e release

- `src/modbuspal/main/ModbusPalPane.java`:
  - `APP_VERSION = "1.7.5"`.
- `build.xml`:
  - `standalone.basename = ModbusSlaveSimulator-v1.7.5-standalone`.

## Comandos executados

- `ant clean jar`

## Artefato final

- `releases/ModbusSlaveSimulator-v1.7.5-standalone.jar`

## Status final

- Correções dos botões aplicadas na camada de modelo + UI (EDT).
- Nova versão gerada com sucesso.

---

# Handoff — Modbus Process Plant Simulator v1.8.0

## Objetivo da sessão

- Executar o plano completo de UX/I18n e robustez operacional:
  - novo branding global;
  - menu superior (`File`, `Project`, `Tools`, `Language`, `About`);
  - internacionalização `pt-BR`, `en-US`, `es-ES`;
  - help multilíngue com instruções didáticas;
  - melhoria de mensagens de erro para TCP/Serial/Replay;
  - nova release standalone.

## Alterações aplicadas

- `src/modbuspal/main/LanguageManager.java` (novo):
  - gerenciamento central de idioma com `ResourceBundle`.
  - suporte aos locales `pt-BR`, `en-US`, `es-ES`.
  - persistência do idioma escolhido.

- `src/modbuspal/i18n/messages_pt_BR.properties` (novo)
- `src/modbuspal/i18n/messages_en_US.properties` (novo)
- `src/modbuspal/i18n/messages_es_ES.properties` (novo)
  - textos da UI principal, menus, títulos e mensagens de erro.

- `src/modbuspal/main/WorkspacePreferences.java`:
  - nova persistência de idioma (`language_tag`) além do último projeto.

- `src/modbuspal/main/ModbusPalPane.java`:
  - branding atualizado para `Modbus Process Plant Simulator`.
  - versão atualizada para `1.8.0`.
  - ativado `installCommPorts()` no startup (antes comentado).
  - nova rotina `applyLocalization()` para labels, botões, tabs e bordas.
  - nova API pública para ações via menu (`openProjectFromMenu`, etc.).
  - persistência de link por chave estável (`tcpip`, `serial`, `replay`) para evitar quebra com tradução.
  - tratamento robusto de erro serial:
    - valida ausência de portas antes de iniciar;
    - evita `IndexOutOfBoundsException` por seleção inválida;
    - mensagens objetivas no `ErrorMessage`.
  - mensagens de erro TCP e Replay melhoradas e localizadas.
  - tooltip explicando por que `ASCII` está desativado.
  - help recarregado conforme idioma ativo.

- `src/modbuspal/main/ModbusPalGui.java`:
  - inicialização de idioma no boot.
  - criação de menu superior com:
    - `File` (Exit),
    - `Project` (Load/Save/Save as/Clear),
    - `Tools` (Master/Scripts/Help/Console),
    - `Language` (pt-BR/en-US/es-ES),
    - `About`.
  - troca de idioma em runtime com atualização de UI/menu.
  - menu também habilitado no `JInternalFrame`.

- `src/modbuspal/help/HelpViewer.java`:
  - carregamento de help por idioma;
  - fallback para inglês;
  - método `reloadLocalizedIndex()`.

- novos guias de ajuda:
  - `src/modbuspal/help/pt_BR/index.html`
  - `src/modbuspal/help/en_US/index.html`
  - `src/modbuspal/help/es_ES/index.html`

- `src/modbuspal/master/ModbusMasterDialog.java`:
  - título do diálogo master com i18n.

- build/release:
  - `build.xml`: `standalone.basename=ModbusProcessPlantSimulator-v1.8.0-standalone`.
  - `nbproject/project.properties`: novo `application.title` e `dist.jar`.

## Comandos executados

- `ant compile`
- `ant clean jar`

## Erros observados e correções

- Cenário alvo corrigido: erro em serial por lista vazia de portas (`IndexOutOfBoundsException`).
  - Correção aplicada com validação prévia de portas + mensagem clara.
- Mensagens de bind em TCP/Serial estavam genéricas.
  - Correção aplicada com exibição explícita de tipo da exceção e mensagem.

## Artefatos gerados

- `releases/ModbusProcessPlantSimulator-v1.8.0-standalone.jar`
- logs de execução em `.log/terminal-output.log` (append).

## Status final

- Build e empacotamento standalone concluídos com sucesso.
- Plano executado no escopo de UI principal, idioma, help e tratamento de erro operacional.
- Compatibilidade de persistência `.xmpp` mantida (incluindo link persistido por chave estável).

---

# Handoff — Modbus Process Plant Simulator v1.8.1

## Objetivo da sessão

- Executar fase 1 do plano v1.8.1:
  - corrigir ruído/falha RXTX em runtime moderno;
  - simplificar UI (menus como principal fluxo);
  - remover menu `File` e manter `Sair`;
  - adicionar checker de dependências (modo seguro);
  - redesenhar help trilíngue com fluxo e troubleshooting;
  - gerar nova release.

## Alterações aplicadas

- `src/modbuspal/link/ModbusSerialLink.java`
  - adicionado guard para runtime legado RXTX:
    - novo método `isLegacyExtDirsAvailable()`.
  - `enumerate()` agora:
    - limpa lista antes da enumeração;
    - aborta de forma segura quando `java.ext.dirs` não existe;
    - captura `Throwable` e registra motivo sem derrubar execução TCP/Replay.

- `src/modbuspal/main/ModbusPalPane.java`
  - `verifyRXTX()` com `Class.forName(..., false, ...)` e validação de runtime legado.
  - `installCommPorts()` desativa aba serial quando enumeração falha.
  - nova simplificação visual: `configureMenuDrivenLayout()` oculta painéis redundantes `Project`/`Tools`.
  - nova ação de menu: `openDependencyCheckFromMenu()`.
  - versão atualizada: `APP_VERSION = "1.8.1"`.

- `src/modbuspal/main/ModbusPalGui.java`
  - removido menu `File`.
  - adicionado menu direto `Sair` (no frame e internal frame).
  - menu `Tools` ganhou item `Check dependencies`.

- `src/modbuspal/main/DependencyCheckDialog.java` (novo)
  - diagnóstico de requisitos/dependências:
    - SO/JVM;
    - classes no classpath (Jython/JFreeChart/RXTX);
    - presença de jars em `lib/`;
    - status serial legado.
  - ação de correção segura:
    - baixa somente jars de projeto em `lib/`;
    - não instala nada no sistema operacional.

- internacionalização atualizada:
  - `src/modbuspal/i18n/messages_pt_BR.properties`
  - `src/modbuspal/i18n/messages_en_US.properties`
  - `src/modbuspal/i18n/messages_es_ES.properties`
  - novas chaves para `menu.exit`, `menu.tools.dependencies` e strings do checker.

- help trilíngue fase 1:
  - índices redesenhados:
    - `src/modbuspal/help/pt_BR/index.html`
    - `src/modbuspal/help/en_US/index.html`
    - `src/modbuspal/help/es_ES/index.html`
  - novas páginas:
    - `src/modbuspal/help/pt_BR/workflow.html`
    - `src/modbuspal/help/pt_BR/troubleshooting.html`
    - `src/modbuspal/help/en_US/workflow.html`
    - `src/modbuspal/help/en_US/troubleshooting.html`
    - `src/modbuspal/help/es_ES/workflow.html`
    - `src/modbuspal/help/es_ES/troubleshooting.html`
  - referências a screenshots da pasta `Docs/`.

- release:
  - `build.xml`: `standalone.basename=ModbusProcessPlantSimulator-v1.8.1-standalone`.

## Comandos executados

- `ant compile`
- `ant clean jar` (falhou por lock em artefato antigo no `dist/`)
- `ant jar` (fallback bem-sucedido)
- validação de abertura com `-loadFile` do projeto `PlantaCeluloseCompleta.xmpp` (processo iniciado e encerrado após inspeção de bootstrap)

## Erros observados e correções

- `ant clean jar`:
  - erro: arquivo `dist/ModbusProcessPlantSimulator-v1.8.0-standalone.jar` bloqueado para deleção.
  - mitigação: build via `ant jar` sem clean, com geração correta do artefato v1.8.1.

## Artefatos gerados

- `releases/ModbusProcessPlantSimulator-v1.8.1-standalone.jar`
- logs append em `.log/terminal-output.log`

## Status final

- Fase 1 v1.8.1 implementada conforme plano.
- Build/release concluídos.
- Fluxo principal orientado por menus, checker seguro de dependências disponível e help trilíngue expandido.

---

# Handoff — ajuste UX execução/abas

## Objetivo da sessão

- Corrigir comportamento reportado:
  - indicador (bola) sem feedback visual ao clicar em Executar;
  - abas `Serial`/`Replay` ficando cinza ao iniciar execução;
  - esclarecer estado do botão `ASCII`.

## Alterações aplicadas

- `src/modbuspal/main/ModbusPalPane.java`
  - removido bloqueio global de `linksTabbedPane` no `startLink()`/`stopLink()`.
  - adicionado `setRunningState(true/false)` no `TiltLabel` para feedback contínuo durante execução.

- `src/modbuspal/main/TiltLabel.java`
  - novo estado `runningState`.
  - novo método `setRunningState(boolean)`.
  - comportamento ajustado:
    - em execução sem tráfego: exibe amarelo contínuo;
    - com tráfego: mantém flashes por evento (verde/amarelo/vermelho);
    - parado: retorna cinza.

## Comando executado

- `ant compile`

## Status final

- Build compilou com sucesso.
- Feedback visual de execução corrigido.
- Abas de link não são mais desativadas ao iniciar `Executar`.

---

# Handoff — ASCII + modernização UI/menu

## Objetivo da sessão

- Ativar operação ASCII em serial.
- Remover ruído RXTX em JVM moderna.
- Melhorar UI/UX com estética mais moderna/gamificada.
- Ajustar menus (Ajuda fora de Tools) e reduzir textos legados em inglês.

## Alterações aplicadas

- `src/modbuspal/main/ModbusPalGui.java`
  - adicionada preparação de compatibilidade RXTX: `prepareLegacySerialRuntime()` define `java.ext.dirs` vazio quando ausente.
  - menu `Help/Ajuda` promovido para menu top-level (não mais item de `Tools`).
  - internal frame também recebeu menu top-level de ajuda.

- `src/modbuspal/link/ModbusSerialLink.java`
  - adicionado suporte a modo ASCII:
    - novo construtor com flag `ascii`.
    - implementação de `LRC` (`computeLRC`), parsing/encoding de frame ASCII (`:...\\r\\n`).
    - `run()` agora processa RTU ou ASCII conforme modo selecionado.
    - `execute()` (master serial) também suporta ASCII.
  - ajuste de compatibilidade `isLegacyExtDirsAvailable()` para runtime preparado pelo bootstrap.

- `src/modbuspal/main/ModbusPalPane.java`
  - `ASCII` habilitado como toggle funcional (`configureAsciiToggle()`).
  - `startSerialLink()` passa a flag ASCII para `ModbusSerialLink`.
  - `applyLocalization()` atualiza tooltip conforme estado ASCII ativo/inativo.
  - novo styling moderno (`installVisualStyle()`):
    - paleta escura;
    - botões de ação com destaque de cor;
    - fonte em negrito e feedback visual mais forte.

- `src/modbuspal/script/ScriptManagerDialog.java`
  - adicionado `refreshLocalization()` com título/aba/botão localizados.
  - chamado no construtor e ao trocar idioma via painel principal.

- i18n atualizado:
  - `src/modbuspal/i18n/messages_pt_BR.properties`
  - `src/modbuspal/i18n/messages_en_US.properties`
  - `src/modbuspal/i18n/messages_es_ES.properties`
  - novas chaves para:
    - `menu.help`
    - `pane.ascii.enabled_tooltip`
    - textos do Script Manager
    - labels Run/Learn/Record com estilo visual gamificado.

- help atualizado para refletir o novo menu:
  - `src/modbuspal/help/pt_BR/index.html`
  - `src/modbuspal/help/en_US/index.html`
  - `src/modbuspal/help/es_ES/index.html`
  - documentação agora indica `Help/Ajuda` como menu dedicado e ASCII funcional.

## Comandos executados

- `ant compile`
- `java ... ModbusPalGui -loadFile=...PlantaCeluloseCompleta.xmpp` (validação rápida de bootstrap)
- `ant jar`

## Resultados de validação

- Build: sucesso (`ant compile`, `ant jar`).
- Release atualizada/copiada:
  - `releases/ModbusProcessPlantSimulator-v1.8.1-standalone.jar`
- Bootstrap não exibiu mais o NPE de `ext_dirs` no carregamento.

## Status final

- ASCII em serial implementado no link de comunicação.
- UX/UI aprimorada com tema visual moderno e botões de ação mais expressivos.
- Help promovido a menu dedicado e script manager com localização aplicada.

---

# Handoff — correção de encerramento

## Objetivo da sessão

- Corrigir `X` e `Sair` para encerrar totalmente o programa e processos associados.

## Alterações aplicadas

- `src/modbuspal/main/ModbusPalPane.java`
  - novo controle `shutdownInProgress`.
  - novo método `shutdownApplication()`:
    - persiste workspace;
    - para link + automações (`exit()` já cobre isso);
    - fecha/dispõe diálogos (Master, Scripts, Console, Help, Dependency Check);
    - dispõe todas as janelas abertas;
    - finaliza JVM com `System.exit(0)`.

- `src/modbuspal/main/ModbusPalGui.java`
  - `windowClosing` do frame principal chama `modbusPal.shutdownApplication()`.
  - `internalFrameClosing` também chama `shutdownApplication()`.
  - ação do menu `Sair` chama `shutdownApplication()` diretamente.

## Comando executado

- `ant compile`

## Status final

- Encerramento global passa a ser explícito e completo via `X` e menu `Sair`.

---

# Handoff — correções de versão/UI/tradução

## Objetivo da sessão

- Eliminar NPE `ext_dirs` reportado no bootstrap RXTX.
- Garantir remoção dos botões `Projeto/Ferramentas` da área principal (somente menu).
- Revisar traduções em áreas críticas de UI (paridade/stop bits/scripts/master tasks).
- Confirmar release atualizada.

## Alterações aplicadas

- `src/modbuspal/link/ModbusSerialLink.java`
  - `enumerate()` agora força `java.ext.dirs=""` quando a propriedade estiver ausente, antes de tocar no driver RXTX.

- `src/modbuspal/main/ModbusPalPane.java`
  - `configureMenuDrivenLayout()` passou a remover fisicamente `projectPanel` e `toolsPanel` do `settingsPanel`.
  - `applyLocalization()` agora localiza:
    - status serial desabilitado + botão "Por quê?";
    - lista de paridade;
    - lista de stop bits.
  - diálogo de scripts desabilitados passou para i18n.
  - atualização de idioma também refresca `ModbusMasterDialog`.

- `src/modbuspal/master/ModbusMasterDialog.java`
  - adicionado `refreshLocalization()`.
  - labels de botões `Add/Remove/Modify` e contexto de `Add` (task/target/request) localizados por i18n.

- `src/modbuspal/master/ModbusMasterRoot.java`
  - raiz da árvore `Tasks` agora usa i18n (`master.tasks_root`).

- i18n atualizado:
  - `src/modbuspal/i18n/messages_pt_BR.properties`
  - `src/modbuspal/i18n/messages_en_US.properties`
  - `src/modbuspal/i18n/messages_es_ES.properties`
  - novas chaves: serial labels, mensagens scripts disabled, master task labels.

## Comandos executados

- `ant compile`
- `ant jar`

## Artefato validado

- `releases/ModbusProcessPlantSimulator-v1.8.1-standalone.jar`

## Status final

- Correções aplicadas para NPE RXTX no ponto de uso, ocultação de botões redundantes e tradução de partes críticas da UI (incluindo master/tasks).

---

# Handoff — correção final de versão exibida

## Objetivo da sessão

- Resolver discrepância: JAR `v1.8.1` exibindo título/ajuda `v1.8.0`.

## Causa raiz

- Constante `APP_STRING` de `ModbusPalPane` estava sendo inlined em classes já compiladas (`ModbusPalGui`) durante empacotamento incremental.
- O JAR anterior foi montado sem recompilação completa de todas as classes dependentes.

## Ação aplicada

- Build completo forçado:
  - `ant clean jar`
- Reempacotamento standalone com recompilação total de fontes.

## Verificação objetiva

- Comando:
  - `java -cp releases/ModbusProcessPlantSimulator-v1.8.1-standalone.jar modbuspal.main.ModbusPalGui -help`
- Resultado:
  - banner agora mostra `Modbus Process Plant Simulator v1.8.1`.

## Status final

- Versão exibida corrigida para `1.8.1` no artefato standalone atual.

---

# Handoff — Modbus Process Simulator v1.9.0

## Objetivo da sessão

- Renomear identidade do produto para `Modbus Process Simulator`, ajustar versão/artefato e preparar publicação no GitHub.

## Alterações aplicadas

- Branding/versionamento:
  - `src/modbuspal/main/ModbusPalPane.java`
    - `APP_NAME`: `Modbus Process Simulator`
    - `APP_VERSION`: `1.9.0`
  - `src/modbuspal/main/ModbusPalGui.java`
    - banner `-help` atualizado para `(process simulator)`.
- Internacionalização:
  - `src/modbuspal/i18n/messages_pt_BR.properties`
  - `src/modbuspal/i18n/messages_en_US.properties`
  - `src/modbuspal/i18n/messages_es_ES.properties`
  - `app.name` e `menu.about.message` alinhados ao novo nome.
- Build/release:
  - `build.xml`
    - `standalone.basename=ModbusProcessSimulator-v1.9.0-standalone`
    - correção de empacotamento para usar somente `${dist.jar}` (evita classes antigas por incluir `dist/*.jar`).
  - `nbproject/project.properties`
    - `application.title=Modbus Process Simulator`
    - `dist.jar=${dist.dir}/ModbusProcessSimulator.jar`

## Comandos executados

- `ant clean jar`
- `ant jar`
- `java -cp "releases/ModbusProcessSimulator-v1.9.0-standalone.jar" modbuspal.main.ModbusPalGui -help`

## Erros encontrados e correções

- Erro: banner de help ainda saía como `(process plant simulator)` após ajuste.
  - Causa: empacotamento usando `dist/*.jar` incluía artefatos antigos no fat JAR.
  - Correção: alterar `build.xml` para agrupar apenas `${dist.jar}` e `dist/lib/*.jar`.

## Artefato gerado

- `releases/ModbusProcessSimulator-v1.9.0-standalone.jar`

## Status final

- Build e validação concluídos com sucesso para `v1.9.0`.
- Banner validado: `Modbus Process Simulator v1.9.0 (process simulator)`.
- Renomeação física da pasta do projeto ainda pendente por bloqueio de acesso no diretório durante a sessão (`Access denied` em rename local).
