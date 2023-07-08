# BLE UART Service Application

This repository is a fork of the simple Bluetooth terminal app by Kai Morich. We have updated the application to work with the Adafruit Circuit Playground Bluefruit microcontroller for the purpose of data collection and further development. The app is designed to facilitate data collection for two projects related to behavioral authentication:

1. Authentication using Motion Sensors.
2. Authentication using IR proximity sense.

## Features

The application provides the following functionalities:

1. Connecting to a BLE device and searching for compatible devices.
2. Sending and receiving UART packets.
3. Displaying the output in a general list form.

In addition to the original functionalities, we have added the following features:

1. Improved compatibility with Android 13 by updating deprecated libraries.
2. Ability to manage and save collected data into the data folder of the app in a shared folder, based on the user's choices.
3. Ability to save and separate UART data packets as individual CSV files.

## Code Overview

The `code.py` file manages the microcontroller code written in CircuitPython, an extended distribution of Python intended for microcontrollers. The code performs the following tasks:

1. Collecting accelerometer and gyroscope data using I2C communication protocol.
2. Collecting proximity data using an analog opto switch (SG-2BC).
3. Sending the collected data as UART packets over BLE when a device is connected.

The code relies on the following libraries:

- `adafruit_ble`
- `adafruit_bluefruit_connect`
- `adafruit_bus_device`
- `adafruit_circuitplayground`
- `adafruit_register`
- `adafruit_mpu6050.mpy`

## Hardware Design Components

The hardware design includes the following components:

- Adafruit Circuit Playground Bluefruit microcontroller board.  
  ![Adafruit Circuit Playground Bluefruit](https://user-images.githubusercontent.com/69628550/232348364-b1b08e9a-7ccf-43e3-bf85-d44b5ca4aabb.png)

- MPU6050 6-axis IMU sensor board.  
  ![MPU6050 6-axis IMU Sensor Board](https://user-images.githubusercontent.com/69628550/232348646-d2487d63-a854-48c7-90de-b8471fd84af9.png)

- Kodenshi SG-2BC opto switch.  
  ![Kodenshi SG-2BC Opto Switch](https://user-images.githubusercontent.com/69628550/232348731-03e235e8-3d43-4cf9-bd5b-d20d7a982566.png)

- An old headphone and earphone.

## Complete Device
![Complete Device](https://user-images.githubusercontent.com/69628550/232400426-2ed90df1-c80d-47fd-b121-49e872cd82b0.png)

Please note that this is just a summary of the project and its components. For detailed implementation instructions and code, please refer to the repository's code files.
