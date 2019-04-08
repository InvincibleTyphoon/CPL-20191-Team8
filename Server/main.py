# -*- encoding: utf-8 -*-

# 포스트 예제

from flask import Flask
from flask_restful import Resource, Api
from flask_restful import reqparse

app = Flask(__name__)
api = Api(app)

class CreateUser(Resource):
    def post(self):
        try:
            parser = reqparse.RequestParser()
            parser.add_argument('email', type=str)
            parser.add_argument('user_name', type=str)
            parser.add_argument('password', type=str)
            args = parser.parse_args()

            _userEmail = args['email']
            _userName = args['user_name']
            _userPassword = args['password']

            return {
                'Email': _userEmail,
                'UserName': _userName,
                'Password': _userPassword
            }

        except Exception as e:
            return {'error': str(e)}
        

api.add_resource(CreateUser, '/user')


if __name__ == "__main__":
    app.run(debug=True)