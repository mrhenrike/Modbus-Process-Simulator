# Simula alteracao operacional de turno: sobe setpoints de carga/producao.

for slave in ModbusPal.getModbusSlaves():
    if slave is None:
        continue

    name = slave.getName()
    regs = slave.getHoldingRegisters()

    if name == "SubestacaoEletrica":
        regs.setValue(6, 980)   # SP_LoadLimit
    elif name == "CentrifugaLinhaA":
        regs.setValue(1, 5650)  # SP_RPM
    elif name == "DigestorPolpa":
        regs.setValue(5, 78)    # SP_Level
