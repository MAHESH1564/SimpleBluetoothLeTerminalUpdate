
import board
import adafruit_mpu6050
import time
from analogio import AnalogIn
from adafruit_ble import BLERadio
from adafruit_ble.advertising.standard import ProvideServicesAdvertisement
from adafruit_ble.services.nordic import UARTService

ble = BLERadio()
uart_server = UARTService()
advertisement = ProvideServicesAdvertisement(uart_server)
i2c = board.I2C()
mpu = adafruit_mpu6050.MPU6050(i2c)
analog_in = AnalogIn(board.A1)


def get_voltage(pin):
    return (pin.value * 3.3) / 65536
    # return pin.value

def get_val(pin):
    return (pin.value)
while True:
    # Advertise when not connected.
    ble.start_advertising(advertisement)
    while not ble.connected:
        pass
    ble.stop_advertising()
    while ble.connected:
        px = get_val(analog_in)
        ax, ay, az = mpu.acceleration
        gx, gy, gz = mpu.gyro
        #print(((get_voltage(analog_in),)))
        # print("accelerometer,{},{},{}\n".format(ax, ay, az))
        # print("gyro,{},{},{}\n".format(gx, gy, gz))
        # print("prox,{}\n".format(px))
        uart_server.write("{},{},{}\n".format(ax, ay, az))
        uart_server.write("{},{},{}\n".format(gx, gy, gz))
        uart_server.write("{}\n".format(px))
        time.sleep(.5)
