# Variavel ModbusPal = projeto atual (injetada pelo ScriptRunner).
# Nao usar: from modbuspal.main import ModbusPal

for automation in ModbusPal.getAutomations():
    automation.start()
