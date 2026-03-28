# Avalia alarmes de processo com base em PVs e SPs.

for slave in ModbusPal.getModbusSlaves():
    if slave is None:
        continue

    name = slave.getName()
    regs = slave.getHoldingRegisters()
    coils = slave.getCoils()
    inputs = slave.getDiscreteInputs()

    if name == "SubestacaoEletrica":
        pv_voltage = regs.getValue(0)
        sp_voltage = regs.getValue(1)
        pv_load = regs.getValue(5)
        sp_load = regs.getValue(6)

        high_v = 1 if pv_voltage > (sp_voltage + 12) else 0
        low_v = 1 if pv_voltage < (sp_voltage - 12) else 0
        overload = 1 if pv_load > sp_load else 0
        alarm_active = 1 if (high_v or low_v or overload) else 0

        coils.setValue(2, high_v)
        coils.setValue(3, low_v)
        coils.setValue(4, overload)
        inputs.setValue(1, alarm_active)

    elif name == "CentrifugaLinhaA":
        pv_vib = regs.getValue(2)
        sp_vib = regs.getValue(3)
        pv_temp = regs.getValue(4)
        sp_temp = regs.getValue(5)
        rpm = regs.getValue(0)
        sp_rpm = regs.getValue(1)

        vib_alarm = 1 if pv_vib > sp_vib else 0
        temp_alarm = 1 if pv_temp > sp_temp else 0
        rpm_alarm = 1 if abs(rpm - sp_rpm) > 250 else 0
        alarm_active = 1 if (vib_alarm or temp_alarm or rpm_alarm) else 0

        coils.setValue(2, vib_alarm)
        coils.setValue(3, temp_alarm)
        inputs.setValue(1, alarm_active)

    elif name == "DigestorPolpa":
        pv_temp = regs.getValue(0)
        sp_temp = regs.getValue(1)
        pv_press = regs.getValue(2)
        sp_press = regs.getValue(3)
        pv_level = regs.getValue(4)
        sp_level = regs.getValue(5)

        temp_alarm = 1 if abs(pv_temp - sp_temp) > 8 else 0
        press_alarm = 1 if abs(pv_press - sp_press) > 2 else 0
        level_alarm = 1 if abs(pv_level - sp_level) > 12 else 0
        alarm_active = 1 if (temp_alarm or press_alarm or level_alarm) else 0

        coils.setValue(2, temp_alarm)
        coils.setValue(3, press_alarm)
        inputs.setValue(1, alarm_active)
