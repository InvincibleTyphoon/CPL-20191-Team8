from keras.models import Sequential
from keras.layers import Dense, Activation
from keras.optimizers import Adam
from sklearn.preprocessing import StandardScaler
from sklearn.externals import joblib
import pandas as pd
import numpy as np

_columns = [
    '28:c2:dd:70:6f:51',    
    '28:c2:dd:70:70:4b',   
    '28:c2:dd:70:70:4f',   
    '28:c2:dd:70:70:51',   
    '28:c2:dd:70:93:0d',   
    '68:ca:e4:47:51:80',   
    '68:ca:e4:47:51:81',   
    '68:ca:e4:47:51:8e',   
    '68:ca:e4:47:51:8f',   
    'magnetic_x',
    'magnetic_y',
    'magnetic_z'
]
_columns_pos = {
    key: i for i, key in enumerate(_columns)
}

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

    def parse_data(self, a_magnetics:dict, a_wifi:list):
        results = [-100.0 for _ in range(len(_columns))]

        # parsing magnetic info.
        for k, v in a_magnetics.items():
            results[_columns_pos[f"magnetic_{k}"]] = v

        # parsing wifi bssid info.
        for item in a_wifi:
            if item['BSSID'] in _columns:
                results[_columns_pos[item['BSSID']]] = item['WifiLevel']

        results = np.array(results, dtype=np.float64)
        return self.scaler.transform(results)

    def predict(self, data):
        return self.model.predict(data, verbose=0)


## Test code
if __name__=="__main__":
    test = Model()
    test.model.summary()
    print(test.scaler)
