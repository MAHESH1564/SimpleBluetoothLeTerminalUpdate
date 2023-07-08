import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import RobustScaler
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, roc_curve, auc
import tensorflow as tf
from tensorflow.keras.models import Sequential, save_model
from tensorflow.keras.layers import Dense, GRU, Dropout
from tensorflow.keras.optimizers import Adam, Adamax, Adafactor
from tensorflow.keras.regularizers import l2
from tensorflow.keras.callbacks import EarlyStopping
import matplotlib.pyplot as plt
from tensorflow.keras.models import load_model
import seaborn as sns

# Load the CSV file into a DataFrame
data = pd.read_csv('final_data_scaled_per_user_with_roll_pitch.csv')

# Separate the input features and the target variable
X = data[['acc_X', 'acc_Y', 'acc_Z', 'gyro_X', 'gyro_Y', 'gyro_Z','roll','pitch']]
#X = data[['roll','pitch']]
y = data['user_id']

# Define window size and stride
window_size = 150
stride = 25

# Apply windowing to the input data
X_windows = []
y_windows = []
for i in range(0, len(X) - window_size + 1, stride):
    X_window = X.iloc[i:i + window_size].values
    y_window = y.iloc[i:i + window_size].values[-1]

    X_windows.append(X_window)
    y_windows.append(y_window)

X_windows = np.array(X_windows)
y_windows = np.array(y_windows)

# Split the data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X_windows, y_windows, test_size=0.2, random_state=42)

# Build a GRU model with dropout and regularization
model = Sequential()
model.add(GRU(64, input_shape=(window_size, X.shape[1]), return_sequences=True, kernel_regularizer=l2(0.01)))
model.add(GRU(64, kernel_regularizer=l2(0.01)))
model.add(Dropout(0.2))
model.add(Dense(32, activation='relu'))
model.add(Dense(len(data['user_id'].unique()), activation='softmax'))

# Compile the model
learning_rate = 0.001
optimizer = Adam(learning_rate=learning_rate)
model.compile(optimizer=optimizer, loss='sparse_categorical_crossentropy', metrics=['accuracy'])

# Define early stopping callback
early_stopping = EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)

# Train the model
history = model.fit(X_train, y_train, epochs=10, batch_size=256, verbose=1,
                    validation_split=0.2, callbacks=[early_stopping])

# Make predictions on the testing set
y_pred_gru_prob = model.predict(X_test)
y_pred_gru = np.argmax(y_pred_gru_prob, axis=1)
accuracy_gru = accuracy_score(y_test, y_pred_gru)
print(f"Accuracy (GRU): {accuracy_gru}")
print(f"Classification Report (GRU):\n{classification_report(y_test, y_pred_gru)}")

# Save the trained model
save_model(model, 'gru_model_with_dropout.h5')
'''
# Plot the training and validation accuracy curves
plt.plot(history.history['accuracy'], label='Training Accuracy')
plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.title('Training and Validation Accuracy')
plt.legend()
plt.show()

# Plot the training and validation loss curves
plt.plot(history.history['loss'], label='Training Loss')
plt.plot(history.history['val_loss'], label='Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.title('Training and Validation Loss')
plt.legend()
plt.show()

# Calculate and display the confusion matrix
confusion_mat = confusion_matrix(y_test, y_pred_gru)
print("Confusion Matrix:")
print(confusion_mat)

plt.figure(figsize=(3, 3))
sns.heatmap(confusion_mat, annot=True, fmt="d", cmap="Blues")
plt.title("Confusion Matrix")
plt.xlabel("Predicted Label")
plt.ylabel("True Label")
plt.show()'''