

# SimpleBluetoothLeTerminalUpdated

This is a fork of the simple bluetooth terminal app by kai morich we have updated the application to use with the adafruit circuit playground bluefruit microcontroller for the purpose of data collection and further development.  

The app is designed to facilitate data collection for the two projects that is working with behavioral authentication. It designed such a way that due to the similarities in our project the data collection would take much more time if we are going to do it separately.

 1. Authentication using Motion Sensors.
 2. Authentication using IR proximity sense.

The apps functionalities in general sense include
 1. Connecting to a BLE device and searching for various compatible ones.
 2. Ability to send and receive UART packets.
 3. It will be able to show the output in a general list form.
Added functionalities
 1. A few depreciated libraries have been improved to give support till android 13.
 2. It will be able to manage and save them into the data folder of the app in a shared folder according to the users choices.
 3. It will be able to save and separate UART data packets and save them as individual csv formats.

The master branch only has the fixes to fit android 13 The other branches are used for active development, will be updated only after the testing.

The code.py  manages the microcontroller code 
 circuit python is a extended distribution of python intended for microcontrollers.
 Both team uses Adafruit's circuitplayground Bluefruit microcontroller board. 
 
 1. This code in general collects the accelerometer and gyroscope data using I2C communication protocol and analog for proximity data
 2. The module mpu6050 and the opto switch is sg-2bc 
 3. Then It sends it through BLE as UART packets one after the other when a device is connected.
It needs these libraries for it to work
 - adafruit_ble
 - adafruit_bluefruit_connect
 - adafruit_bus_device
 - adafruit_circuitplayground
 - adafruit register
 - adafruit mpu6050.mpy

General components used for Hardware Design is
- adafruit circuitplayground bluefruit.

![image](https://user-images.githubusercontent.com/69628550/232348364-b1b08e9a-7ccf-43e3-bf85-d44b5ca4aabb.png)

- mpu 6050 6-axis IMU sensor board.

![image](https://user-images.githubusercontent.com/69628550/232348646-d2487d63-a854-48c7-90de-b8471fd84af9.png)

- Kodenshi SG-2BC opto switch.

![image](https://user-images.githubusercontent.com/69628550/232348731-03e235e8-3d43-4cf9-bd5b-d20d7a982566.png)

- An old headphone and earphone.

Complete device

![IMG_20230417_113316-removebg-preview(1)](https://user-images.githubusercontent.com/69628550/232400426-2ed90df1-c80d-47fd-b121-49e872cd82b0.png)


