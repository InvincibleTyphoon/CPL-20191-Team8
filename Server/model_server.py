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

            _userData = json.loads(args['data'])
            pprint(_userData)

            # parsing
            _userMagnetic = _userData['Magnetic']
            _userWifiInfo = _userData['WifiInfo']
            _userX = _userData['x']
            _userY = _userData['y']

            sampleMagnetic = dict()
            sampleMagnetic[u'x'] = _userMagnetic[0]
            sampleMagnetic[u'y'] = _userMagnetic[1]
            sampleMagnetic[u'z'] = _userMagnetic[2]
            
            sampleWifi = dict()
            for i, data in enumerate(_userWifiInfo):
                sampleWifi[str(i)] = data

            pprint(sampleMagnetic)
            pprint(sampleWifi)

            return {
                "status": "success",
                "XCoord": 0.0,
                "YCoord": 0.0
            }
        except Exception as e:
            print("Error:", str(e))
            return {
                'status': 'error',
                'error': str(e)
            }


api.add_resource(ModelServer, '/model')

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)

