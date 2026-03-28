# Inicia todas as automacoes configuradas no projeto carregado.

for automation in ModbusPal.getAutomations():
    automation.start()
