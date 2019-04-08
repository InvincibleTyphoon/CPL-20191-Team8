# -*- encoding: utf-8 -*-

# 포스트 예제

from flask import Flask, make_response
from flask import request
from flask_restful import Resource, Api
from flask_restful import reqparse
from pprint import pprint 
import json

app = Flask(__name__)
api = Api(app)

class CreateUser(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('data', type=str)
            args = parser.parse_args()

            _userData = json.loads(args['data'])

            # test logs
            pprint(args)
            pprint(type(_userData))
            pprint(_userData)

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