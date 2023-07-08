
import numpy as np
import pandas as pd
from sklearn.preprocessing import MinMaxScaler,StandardScaler,RobustScaler
calibrated_data = pd.read_csv('updated_data.csv')

scaler = RobustScaler()

# Define a function to apply scaling
def scale_data(group):
    group[['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z','roll','pitch']] = scaler.fit_transform(
        group[['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z','roll','pitch']])
    return group

calibrated_data = calibrated_data.groupby('user_id', group_keys=False).apply(scale_data)

# Save the scaled data to a CSV file
calibrated_data.to_csv('final_data_scaled_per_user_with_roll_pitch.csv', index=False)