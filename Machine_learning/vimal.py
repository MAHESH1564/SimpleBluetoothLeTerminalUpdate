import pandas as pd
import numpy as np

# Assuming 'data' is your DataFrame containing accelerometer and gyroscope measurements

# Constants for complementary filter
alpha = 0.98  # Weighting factor for accelerometer data
dt = 0.01  # Time step (adjust as per your data)

# Initialize roll and pitch
data['roll'] = 0.0
data['pitch'] = 0.0

# Iterate through the data to calculate roll and pitch
for i in range(1, len(data)):
    # Accelerometer-based pitch and roll calculation
    acc_roll = np.arctan2(data['acc_Y'][i], data['acc_Z'][i]) * 180 / np.pi
    acc_pitch = np.arctan2(-data['acc_X'][i], np.sqrt(data['acc_Y'][i]**2 + data['acc_Z'][i]**2)) * 180 / np.pi

    # Gyroscope-based roll and pitch calculation
    gyro_roll = data['roll'][i-1] + data['gyro_X'][i] * dt
    gyro_pitch = data['pitch'][i-1] + data['gyro_Y'][i] * dt

    # Complementary filter
    data['roll'][i] = alpha * gyro_roll + (1 - alpha) * acc_roll
    data['pitch'][i] = alpha * gyro_pitch + (1 - alpha) * acc_pitch

# Print the updated DataFrame
print(data.head())


'''
# Assuming fs is the sampling rate in Hz
fs = 100  # Adjust as per your data

# Assuming desired time constant in seconds
time_constant = 1.0  # Adjust as per your requirements

# Calculate alpha based on sampling rate and time constant
alpha = 1 - np.exp(-1 / (fs * time_constant))

print(alpha)
'''