import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, GRU
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score

def windowing(data, labels, window_size, stride):
    windows = []
    window_labels = []

    for i in range(0, len(data) - window_size + 1, stride):
        window = data[i:i+window_size]
        window_label = labels[i+window_size-1]
        windows.append(window)
        window_labels.append(window_label)

    windows = np.array(windows)
    window_labels = np.array(window_labels)

    return windows, window_labels

# Read the data from CSV file
data = pd.read_csv('final_data_scaled_per_user_with_roll_pitch.csv')

# Extract the desired columns as input features
X = data[['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z', 'roll', 'pitch']].values

# Extract the user_id column as the target variable
y = data['user_id'].values

# Define window size and stride
window_size = 150
stride = 25

# Apply windowing to the input data
X_windows, y_windows = windowing(X, y, window_size, stride)

# Convert user_id to categorical labels using one-hot encoding
num_classes = len(data['user_id'].unique())
y_encoded = tf.keras.utils.to_categorical(y_windows - 1, num_classes)

# Split the data into train and test sets
X_train, X_test, y_train, y_test = train_test_split(X_windows, y_encoded, test_size=0.2, random_state=42)

# Define the deep learning model
model = Sequential()
model.add(GRU(64, input_shape=(window_size, X.shape[1])))
model.add(Dense(32, activation='relu'))
model.add(Dense(len(data['user_id'].unique()), activation='softmax'))

# Define the optimizers to use

# Train the model with different optimizers
for optimizer_name, optimizer in optimizers.items():
    model.compile(loss='categorical_crossentropy', optimizer=optimizer, metrics=['accuracy'])
    model.fit(X_train, y_train, epochs=10, batch_size=256, validation_split=0.2)

    # Evaluate the model
    y_pred_prob = model.predict(X_test)
    y_pred = np.argmax(y_pred_prob, axis=1)
    accuracy = accuracy_score(np.argmax(y_test, axis=1), y_pred)
    print(f"Optimizer: {optimizer_name}")
    print(f"Accuracy: {accuracy}")
    print(f"Classification Report:\n{classification_report(np.argmax(y_test, axis=1), y_pred)}")
    print("------------------------------")
