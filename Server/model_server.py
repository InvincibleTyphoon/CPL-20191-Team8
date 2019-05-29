from flask import Flask, make_response
from flask import request, Request
from flask_restful import Resource, Api
from flask_restful import reqparse
from model.model import Model
from pprint import pprint 
import json


app = Flask(__name__)
api = Api(app)
model = Model()


class ModelServer(Resource):
    def get(self):
        try:
            # reads request
            parser = reqparse.RequestParser()
            parser.add_argument('data', type=str)
            args = parser.parse_args()

            # to json
            _userData = json.loads(args['data'])
            pprint(_userData)

            # parsing
            _userMagnetic = _userData['Magnetic']
            _userWifiInfo = _userData['WifiInfo']
            # _userX = _userData['x']
            # _userY = _userData['y']
            sampleMagnetic = dict()
            sampleMagnetic[u'x'] = _userMagnetic[0]
            sampleMagnetic[u'y'] = _userMagnetic[1]
            sampleMagnetic[u'z'] = _userMagnetic[2]
            
            # predict
            sample_np = model.parse_data(sampleMagnetic, _userWifiInfo)
            print("std sample:", sample_np)
            sample_predict = model.predict(sample_np)
            result = {
                "status": "success",
                "xCoord": float(sample_predict[0]),
                "yCoord": float(sample_predict[1])
            }
            print("Inference :")
            pprint(result)

            return result
        except Exception as e:
            print("Error:", str(e))
            return {
                'status': 'error',
                'exception': str(e)
            }


api.add_resource(ModelServer, '/model')

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)

