import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# Assuming 'data' is your DataFrame containing accelerometer and gyroscope measurements
data = pd.read_csv('unscaled_corrected_data.csv')

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
    # Accelerometer-based roll and pitch calculation
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


import pandas as pd
import numpy as np

# Assuming 'data' is your DataFrame containing accelerometer and gyroscope measurements

# Calculate Euler angles
data['roll'] = np.arctan2(data['acc_Y'], data['acc_Z']) * 180 / np.pi
data['pitch'] = np.arctan2(-data['acc_X'], np.sqrt(data['acc_Y']**2 + data['acc_Z']**2)) * 180 / np.pi

# Save the DataFrame with pitch and roll to a file
data.to_csv('updated_data.csv', index=False)
