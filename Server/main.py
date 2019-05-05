# -*- encoding: utf-8 -*-

# 포스트 예제

from flask import Flask, make_response
from flask import request
from flask_restful import Resource, Api
from flask_restful import reqparse
from pprint import pprint 
from db import DBConn
import json

db = DBConn.DBConn('config/silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json')
print(db.getData("it2", "1.1"))

app = Flask(__name__)
api = Api(app)



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
            point = DBConn.Point(key='1.1' ,magnetic=sampleMagnetic,wifiscan=sampleWifi)
            db.setData('it2','1.1',point.to_dict())
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

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)