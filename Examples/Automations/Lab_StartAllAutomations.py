# Inicia todas as automacoes do projeto (Jython no simulador).
# A variavel ModbusPal e injetada pelo ScriptRunner, nao usar import ModbusPal.

for automation in ModbusPal.getAutomations():
    automation.start()
