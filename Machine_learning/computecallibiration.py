import numpy as np
import pandas as pd
from sklearn.preprocessing import MinMaxScaler,StandardScaler,RobustScaler

# MPU-6050 calibration offsets (if available)
ACCELEROMETER_OFFSETS = np.array([-0.6333274573740695, 4.3505573778522475, -3.150580189949322])
GYROSCOPE_OFFSETS = np.array([0.019826311717331294, 0.0046459383401432726, -0.03814255467533131])
Accelerometer_Scale_Factors = np.array([0.04022058, 0.03882201, 0.09327457])
Gyroscope_Scale_Factors = np.array([0.00559548, 0.00447905, 0.00295827])

# Read the CSV file
data = pd.read_csv('final_combine_scaled_Robust.csv')

accel_data = data[['acc_X', 'acc_Y', 'acc_Z']].values
gyro_data = data[['gyro_X', 'gyro_Y', 'gyro_Z']].values

calibrated_accel_data = (accel_data - ACCELEROMETER_OFFSETS) * Accelerometer_Scale_Factors
calibrated_gyro_data = (gyro_data - GYROSCOPE_OFFSETS) * Gyroscope_Scale_Factors

calibrated_data = pd.DataFrame({
    'acc_X': calibrated_accel_data[:, 0],
    'acc_Y': calibrated_accel_data[:, 1],
    'acc_Z': calibrated_accel_data[:, 2],
    'gyro_X': calibrated_gyro_data[:, 0],
    'gyro_Y': calibrated_gyro_data[:, 1],
    'gyro_Z': calibrated_gyro_data[:, 2],
    'ir_data': data['ir_data'].values,
    'user_id': data['user_id'].values
})
calibrated_data.to_csv('unscaled_corrected_data.csv',index=False)
# Scale the data per user
scaler = RobustScaler()

# Define a function to apply scaling
def scale_data(group):
    group[['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z']] = scaler.fit_transform(
        group[['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z']])
    return group

calibrated_data = calibrated_data.groupby('user_id', group_keys=False).apply(scale_data)

# Save the scaled data to a CSV file
calibrated_data.to_csv('final_data_scaled_per_user.csv', index=False)
