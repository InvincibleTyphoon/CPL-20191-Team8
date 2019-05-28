import DBConn as dbc
from pprint import pprint

db = dbc.DBConn('config/silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json') 

sample_1_1 = db.getData('it5', 0, 0)
print(type(sample_1_1))
pprint(sample_1_1)