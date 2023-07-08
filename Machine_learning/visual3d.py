import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Assuming 'data' is your DataFrame containing accelerometer and gyroscope measurements
# Calculate Euler angles

# Calculate Euler angles
roll = np.arctan2(data['acc_Y'], data['acc_Z'])
pitch = np.arctan2(-data['acc_X'], np.sqrt(data['acc_Y']**2 + data['acc_Z']**2))

# Create a 3D plot
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Plot the orientation
ax.scatter(roll, pitch, marker='o')
ax.set_xlabel('Roll')
ax.set_ylabel('Pitch')
ax.set_zlabel('Yaw')

plt.title('Orientation Visualization in 3D Space')
plt.show()
