from keras.models import Sequential
from keras.layers import Dense, Activation
from keras.optimizers import Adam
from sklearn.preprocessing import StandardScaler
from sklearn.externals import joblib
import pandas as pd
import numpy as np


class Model():
    def __init__(self):
        self.model = Sequential()
        self.scaler = joblib.load("./std_scaler.pickle")
        self.set_model()
        self.model.load_weights("./best_model.h5")

    def set_model(self):
        opt = Adam(lr=0.05)
        self.model.add(Dense(12, input_dim=12, activation='sigmoid'))
        self.model.add(Dense(6, activation='sigmoid'))
        self.model.add(Dense(2, activation="linear"))
        self.model.compile(optimizer=opt,
                    loss='mean_squared_error',
                    metrics=['mae'])


## Test code
if __name__=="__main__":
    test = Model()
    test.model.summary()
    print(test.scaler)
