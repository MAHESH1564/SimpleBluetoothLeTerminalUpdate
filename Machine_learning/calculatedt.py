import pandas as pd

# Assuming 'data' is your DataFrame containing the data, including 'timestamp' column

# Convert the 'timestamp' column to datetime type
data['timestamp'] = pd.to_datetime(data['timestamp'])

# Calculate the time difference between consecutive timestamps
data['dt'] = (data['timestamp'] - data['timestamp'].shift()).fillna(pd.Timedelta(seconds=0))

# Convert the time difference to seconds
data['dt'] = data['dt'].apply(lambda x: x.total_seconds())

# Print the unique values of 'dt' to identify the time step
print(data['dt'].unique())

