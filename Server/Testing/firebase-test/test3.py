import DBConn

# json 키파일로 디비랑 연결
db = DBConn.DBConn('silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json') 
db.regiData("it4")  # 등록 하세요
# print(db.getData("it3",1,1))  # db.getData(data,x,y) data: 건물 / x,y: 상대적인 위치 x,y(ex1.1)

# magnetic = {
#     u'x':'2',
#     u'y':'2',
#     u'z':'2'

# }

# wifiscan = {
# u'0':{u'ssid':u'a',
#     u'level':1},
# u'1': {u'ssid':u'aa',
#     u'level':2},
# u'2':{u'ssid':u'aaa',
#     u'level':3}
# }
# point = DBConn.Point(x=1,y=1 ,magnetic=magnetic,wifiscan=wifiscan)
# db.setData('it3',point.to_dict())