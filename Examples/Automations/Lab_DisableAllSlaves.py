# Desativa todos os escravos Modbus do projeto corrente.

for slave in ModbusPal.getModbusSlaves():
    if slave is not None:
        slave.setEnabled(False)
