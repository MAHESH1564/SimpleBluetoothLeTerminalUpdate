import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

# Read the CSV file containing the raw sensor data
raw_data = pd.read_csv('final_combine_scaled_Robust.csv')

# Read the CSV file containing the calibrated sensor data
calibrated_data = pd.read_csv('unscaled_corrected_data.csv')

# Extract the user IDs from the data
users = raw_data['user_id'].unique()

# Loop over each user and visualize the data before and after calibration
for user in users:
    # Filter raw data for the current user
    raw_user_data = raw_data[raw_data['user_id'] == user]

    # Filter calibrated data for the current user
    calibrated_user_data = calibrated_data[calibrated_data['user_id'] == user]

    # Extract the accelerometer and gyroscope data for the current user
    accel_raw = raw_user_data[['acc_X', 'acc_Y', 'acc_Z']].values
    accel_calibrated = calibrated_user_data[['acc_X', 'acc_Y', 'acc_Z']].values

    gyro_raw = raw_user_data[['gyro_X', 'gyro_Y', 'gyro_Z']].values
    gyro_calibrated = calibrated_user_data[['gyro_X', 'gyro_Y', 'gyro_Z']].values

    # Create subplots for accelerometer and gyroscope data
    fig, axs = plt.subplots(2, 2, figsize=(12, 8))
    fig.suptitle(f"Data Visualization for User {user}")

    # Plot raw accelerometer data
    axs[0, 0].plot(accel_raw)
    axs[0, 0].set_title('Raw Accelerometer Data')
    axs[0, 0].set_xlabel('Time')
    axs[0, 0].set_ylabel('Acceleration')

    # Plot calibrated accelerometer data
    axs[0, 1].plot(accel_calibrated)
    axs[0, 1].set_title('Calibrated Accelerometer Data')
    axs[0, 1].set_xlabel('Time')
    axs[0, 1].set_ylabel('Calibrated Acceleration')

    # Plot raw gyroscope data
    axs[1, 0].plot(gyro_raw)
    axs[1, 0].set_title('Raw Gyroscope Data')
    axs[1, 0].set_xlabel('Time')
    axs[1, 0].set_ylabel('Angular Velocity')

    # Plot calibrated gyroscope data
    axs[1, 1].plot(gyro_calibrated)
    axs[1, 1].set_title('Calibrated Gyroscope Data')
    axs[1, 1].set_xlabel('Time')
    axs[1, 1].set_ylabel('Calibrated Angular Velocity')

    # Adjust the layout of subplots
    plt.tight_layout()

    # Show the plot
    plt.show()
