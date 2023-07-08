import pandas as pd
import numpy as np
import os
from scipy.stats import entropy
from sklearn.preprocessing import MinMaxScaler, StandardScaler, RobustScaler

sort_key = lambda s: (len(s), s)
accel_headers = ['time', 'acc_X', 'acc_Y', 'acc_Z']
gyro_headers = ['time', 'gyro_X', 'gyro_Y', 'gyro_Z']
ir_headers = ['time', 'prox']
final_headers = ['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z', 'ir_data', 'user_id']

finaldata = pd.DataFrame()

user_id = 0
data_counts = []  # Stores data counts for each user
user_info = []  # Stores directory name and user ID

for dirs in next(os.walk('.'))[1]:
    sorted_dirs = os.listdir(dirs)
    sorted_dirs.sort(key=sort_key)
    print(sorted_dirs)
    num_files = int(len(sorted_dirs) / 3)
    user_data = []

    for i in range(0, num_files):
        accel_csv = (
            pd.read_csv(dirs + '/accel_data' + str(i) + '.csv', names=accel_headers)
            .drop('time', axis=1)
            .to_numpy()
        )
        gyro_csv = (
            pd.read_csv(dirs + '/gyro_data' + str(i) + '.csv', names=gyro_headers)
            .drop('time', axis=1)
            .to_numpy()
        )
        ir_csv = (
            pd.read_csv(dirs + '/ir_data' + str(i) + '.csv', names=ir_headers)
            .drop('time', axis=1)
            .to_numpy()
        )
        user_data.append(np.hstack((accel_csv, gyro_csv, ir_csv)))

    data_count = min(len(data) for data in user_data)
    data_counts.append(data_count)  # Store data count for the current user
    print(data_count)

    user_data_truncated = [data[:data_count] for data in user_data]  # Truncate user_data arrays to minimum data count
    if dirs == 'binu':
        userdata = np.concatenate(user_data_truncated + [np.full((data_count, 1), 5)], axis=1)
    else:
        userdata = np.concatenate(user_data_truncated + [np.full((data_count, 1), user_id)], axis=1)

    user_df = pd.DataFrame(userdata, columns=final_headers)

    # Perform Min-Max scaling on selected columns

    finaldata = pd.concat([finaldata, user_df])

    user_info.append((dirs, user_id))  # Save directory name and user ID
    if dirs!='binu':
        user_id += 1

min_data_count = np.median(data_counts)  # Find median data count among all users

finaldata = finaldata.groupby('user_id').head(int(min_data_count))  # Truncate data for each user

# Save user information to a CSV file
user_info_df = pd.DataFrame(user_info, columns=['Directory', 'User_ID'])
user_info_df.to_csv('user_info.csv', index=False)

# Save the processed data to a CSV file
finaldata.to_csv('final_combine_scaled_Robust.csv', index=False)
