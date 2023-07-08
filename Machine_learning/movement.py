import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Assuming 'data' is your DataFrame containing accelerometer measurements
data = pd.read_csv('unscaled_corrected_data.csv')
# Calculate displacement based on accelerometer data
dt = 0.01  # Time step (adjust as per your data)
displacement = data[['acc_X', 'acc_Y', 'acc_Z']].cumsum() * dt

# Create a 3D plot
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Plot the movement
ax.plot(displacement['acc_X'], displacement['acc_Y'], displacement['acc_Z'])
ax.set_xlabel('X')
ax.set_ylabel('Y')
ax.set_zlabel('Z')

plt.title('Actual Movement Visualization')
plt.show()
