# Write your code here :-)
import board
import adafruit_mpu6050
import time
from analogio import AnalogIn
from adafruit_ble import BLERadio
from adafruit_ble.advertising.standard import ProvideServicesAdvertisement
from adafruit_ble.services.nordic import UARTService

SEND_RATE = 0.0166667  # how often in seconds to send text

ble = BLERadio()
uart_server = UARTService()
advertisement = ProvideServicesAdvertisement(uart_server)
i2c = board.I2C()
mpu = adafruit_mpu6050.MPU6050(i2c)
analog_in = AnalogIn(board.A1)


def get_val(pin):
    return pin.value


while True:
    stop = False
    print("WAITING...")
    # Advertise when not connected.
    ble.start_advertising(advertisement)
    while not ble.connected:
        pass
    # Connected
    ble.stop_advertising()
    print("CONNECTED")

    # Loop and read packets
    last_send = time.monotonic()
    while ble.connected:
        # INCOMING (RX) check for incoming text
        if uart_server.in_waiting:
            raw_bytes = uart_server.read(uart_server.in_waiting)
            text = raw_bytes.decode().strip()
            if text == "stop":
                stop = False
                pass
            if text == "start":
                stop = True
                pass
        # OUTGOING (TX) periodically send text
        if time.monotonic() - last_send > SEND_RATE and stop:
            px = get_val(analog_in)
            ax, ay, az = mpu.acceleration
            gx, gy, gz = mpu.gyro
            # print("{},{},{},{}\n".format(time.monotonic_ns(), ax, ay, az))
            # print("{},{},{},{}\n".format(time.monotonic_ns(), gx, gy, gz))
            # print("{},{}\n".format(time.monotonic_ns(), px),)
            uart_server.write("{},{},{},{}\n".format(time.monotonic_ns(), ax, ay, az))
            uart_server.write("{},{},{},{}\n".format(time.monotonic_ns(), gx, gy, gz))
            uart_server.write("{},{}\n".format(time.monotonic_ns(), px))
            last_send = time.monotonic()
    # Disconnected
    print("DISCONNECTED")
