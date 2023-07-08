# Project Title

Authentication Approach using Deep Learning

## Overview

This project aims to develop an active authentication approach that combines deep learning techniques with smartphone sensors for user authentication. The project consists of three main components: Android Development, Microcontroller, and Machine Learning. The Android app is used for data collection, the Microcontroller handles sensor data, and the Machine Learning component implements the GRU (Gated Recurrent Unit) algorithm for authentication.

## Repository Structure

The repository is structured as follows:

- Android_Development: Contains the Android app used for data collection.
- Microcontroller: Includes the `code.py` file for the microcontroller code written in CircuitPython.
- Machine_Learning: Contains the code for the GRU-based authentication algorithm.

## Android Development

The Android_Development folder contains the Android app responsible for collecting data from smartphone sensors. The app interacts with the user, collects sensor readings, and sends the data to the microcontroller via Bluetooth Low Energy (BLE). The collected data is used for training and testing the machine learning model.

## Microcontroller

The Microcontroller folder includes the `code.py` file, which manages the microcontroller code written in CircuitPython. The microcontroller communicates with the smartphone app over BLE, receives sensor data, and processes it before transmitting it to the machine learning component for authentication.

## Machine Learning

The Machine_Learning folder contains the code for the GRU-based authentication algorithm. This component utilizes the deep learning architecture of GRU to model and capture user behavioral patterns. The collected sensor data is preprocessed, and the GRU model is trained to accurately authenticate users based on their behavioral patterns. The folder also includes the evaluation metrics for the accelerometer and gyroscope sensors.

## Getting Started

To set up and run the project, follow these steps:

1. Clone the repository to your local machine.
2. Set up the Android development environment and install necessary dependencies.
3. Build and run the Android app from the Android_Development folder on your smartphone.
4. Connect the microcontroller (Adafruit Circuit Playground Bluefruit) to the smartphone app via BLE.
5. Upload the `code.py` file from the Microcontroller folder to the microcontroller board.
6. Run the GRU code from the Machine_Learning folder to train the model and perform authentication.

Please refer to the specific README files in each component folder for detailed instructions on setting up and running that particular component.

## Results

Extensive experiments were conducted using a real-world dataset collected from 17 users. The evaluation metrics for accelerometer and gyroscope sensors demonstrated an accuracy of 0.88414, precision of 0.88, recall of 0.88, F1-score of 0.88, and support of 4141.

## Conclusion

The project successfully implements an active authentication approach using deep learning and smartphone sensors. By leveraging behavioral patterns captured by the IMU sensors and employing a GRU model, accurate authentication is achieved. The Android app, microcontroller, and machine learning components work together to collect data, process it, and authenticate users based on their distinctive behavioral patterns.

## Future Work

Future work on this project may include:

- Exploring additional sensors for capturing behavioral patterns.
- Investigating the scalability of the authentication approach to larger user populations.
- Enhancing the user interface of the Android app for improved user experience.
- Conducting more comprehensive experiments with diverse datasets to further evaluate the system's performance.

Please refer to the specific component folders for further details and ongoing development work.

- Adafruit Circuit Playground Bluefruit microcontroller board.  
  ![Adafruit Circuit Playground Bluefruit](https://user-images.githubusercontent.com/69628550/232348364-b1b08e9a-7ccf-43e3-bf85-d44b5ca4aabb.png)

- MPU6050 6-axis IMU sensor board.  
  ![MPU6050 6-axis IMU Sensor Board](https://user-images.githubusercontent.com/69628550/232348646-d2487d63-a854-48c7-90de-b8471fd84af9.png)

- Kodenshi SG-2BC opto switch.  
  ![Kodenshi SG-2BC Opto Switch](https://user-images.githubusercontent.com/69628550/232348731-03e235e8-3d43-4cf9-bd5b-d20d7a982566.png)

- An old headphone and earphone.

## Complete Device
![Complete Device](https://user-images.githubusercontent.com/69628550/232400426-2ed90df1-c80d-47fd-b121-49e872cd82b0.png)
