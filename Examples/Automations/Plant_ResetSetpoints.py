# Restaura setpoints operacionais padrao da planta didatica.

for slave in ModbusPal.getModbusSlaves():
    if slave is None:
        continue

    name = slave.getName()
    regs = slave.getHoldingRegisters()
    coils = slave.getCoils()

    if name == "SubestacaoEletrica":
        regs.setValue(1, 400)   # SP_Voltage
        regs.setValue(6, 900)   # SP_LoadLimit
        coils.setValue(0, 1)    # Comando liga alimentacao
        coils.setValue(2, 0)
        coils.setValue(3, 0)
        coils.setValue(4, 0)

    elif name == "CentrifugaLinhaA":
        regs.setValue(1, 5500)  # SP_RPM
        regs.setValue(3, 25)    # SP_VibrationMax
        regs.setValue(5, 95)    # SP_TemperatureMax
        coils.setValue(0, 1)    # Motor enable
        coils.setValue(1, 0)    # ESD reset
        coils.setValue(2, 0)
        coils.setValue(3, 0)

    elif name == "DigestorPolpa":
        regs.setValue(1, 172)   # SP_Temperature
        regs.setValue(3, 8)     # SP_Pressure
        regs.setValue(5, 70)    # SP_Level
        coils.setValue(0, 1)    # Feed pump enable
        coils.setValue(1, 1)    # Steam valve enable
        coils.setValue(2, 0)
        coils.setValue(3, 0)
