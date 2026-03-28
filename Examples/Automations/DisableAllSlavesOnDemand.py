# Desativa todos os escravos (executar sob demanda no Script Manager).

for slave in ModbusPal.getModbusSlaves():
    if slave is not None:
        slave.setEnabled(False)
