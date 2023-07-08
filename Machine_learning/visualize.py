import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from scipy import stats

# Load the final dataset
finaldata = pd.read_csv('final_data_calibrated_all_scaled_Standard_1.csv')

# Summary statistics
print(finaldata.describe())

# Distribution of user IDs
user_counts = finaldata['user_id'].value_counts()
print(user_counts)

# Scatter plots of sensor variables for each user
sns.set_palette('colorblind')
for user_id in finaldata['user_id'].unique():
    from mpl_toolkits.mplot3d import Axes3D

# ...

# Scatter plots of sensor variables for each user
for user_id in finaldata['user_id'].unique():
    user_data = finaldata.loc[finaldata['user_id'] == user_id]
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.scatter(user_data['accel_x'], user_data['accel_y'], user_data['accel_z'] , label='accelerometer')
    ax.scatter(user_data['gyro_x'], user_data['gyro_y'], user_data['gyro_z'], label='Gyroscope')
    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    ax.set_zlabel('Z')
    plt.title(f'User {user_id} - Sensor Data')
    plt.legend()
    plt.savefig(f'user_{user_id}_scatterplot.png')
    plt.close()

# Correlation analysis
corr_matrix = finaldata[['accel_x', 'accel_y', 'accel_z', 'gyro_x', 'gyro_y', 'gyro_z']].corr()
sns.heatmap(corr_matrix, annot=True, cmap='coolwarm')
plt.title('Correlation Matrix')
plt.show()

# Statistical tests
for column in ['accel_x', 'accel_y', 'accel_z', 'gyro_x', 'gyro_y', 'gyro_z']:
    print(f'{column}:')
    for user_id in finaldata['user_id'].unique():
        user_data = finaldata.loc[finaldata['user_id'] == user_id]
        t_stat, p_value = stats.ttest_1samp(user_data[column], finaldata[column].mean())
        print(f'User {user_id}: t-statistic = {t_stat:.4f}, p-value = {p_value:.4f}')
