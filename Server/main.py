# -*- encoding: utf-8 -*-

# 포스트 예제

from flask import Flask, make_response
from flask import request, Request
from flask_restful import Resource, Api
from flask_restful import reqparse
from pprint import pprint 
from db import DBConn
import json

db = DBConn.DBConn('config/silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json')
# print(db.getData("it2", 10, 10))

app = Flask(__name__)
api = Api(app)


# the test of reading payload (JSON)
class PostTest(Resource):
    def post(self):
        try:
            # reads request
            parser = reqparse.RequestParser()
            parser.add_argument('data', type=str)
            args = parser.parse_args()

            _userData = json.loads(args['data'])
            pprint(_userData)
            # print("data,", request.data)         # read all payload
            # print("get_data(),", request.get_data())   # read data
            # print("get_json(),", request.get_json())   # read only json
            # print("stream.read(),", request.stream.read())
            
            # parsing to commit db
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

            # commit db
            point = DBConn.Point(x=_userX, y=_userY ,magnetic=sampleMagnetic,wifiscan=sampleWifi)
            db.setData('it5',point.to_dict())
            pprint('done')

            return {
                "status": "success"
            }
        except Exception as e:
            print("Error:", str(e))
            return {'error': str(e)}



class CreateUser(Resource):
    def post(self):
        try:
            # reads request
            parser = reqparse.RequestParser()
            parser.add_argument('data', type=str)
            args = parser.parse_args()

            _userData = json.loads(args['data'])
            pprint(_userData)
            
            # parsing to commit db
            _userMagnetic = _userData['Magnetic']
            _userWifiInfo = _userData['WifiInfo']

            sampleMagnetic = dict()
            sampleMagnetic[u'x'] = _userMagnetic[0]
            sampleMagnetic[u'y'] = _userMagnetic[1]
            sampleMagnetic[u'z'] = _userMagnetic[2]
            
            sampleWifi = dict()
            for i, data in enumerate(_userWifiInfo):
                sampleWifi[str(i)] = data
            
            pprint(sampleMagnetic)
            pprint(sampleWifi)
            
            # commit db
            point = DBConn.Point(x=1, y=1 ,magnetic=sampleMagnetic,wifiscan=sampleWifi)
            db.setData('it2',point.to_dict())
            pprint('done')

            return {
                "status": "success"
            }
            '''
            return {
                'Email': _userEmail,
                'UserName': _userName,
                'Password': _userPassword
            }
            '''
        except Exception as e:
            return {'error': str(e)}


api.add_resource(CreateUser, '/')
api.add_resource(PostTest, '/test')


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
