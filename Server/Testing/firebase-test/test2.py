import DBConn

# json 키파일로 디비랑 연결
db = DBConn.DBConn('silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json') 
print(db.getData("it2","1.1"))  # db.getData(data,Point) data: 건물 / point: 상대적인 위치 x.y(ex1.1)
 
key = "1.1" # 포인트 겸 테이블 키 랑 같은값 으로 설정 ..  변경예정

magnetic = DBConn.Magnetic(x=0,y=0,z=0) # 전자기 값  
wifiscan = list()
wifiscan = DBConn.Wifiscan(bbsid='',ssid='',level='') # 와이파이 데이터 필요시 배열로 구현 해드림
point = DBConn.Point(key=key ,magnetic=magnetic.to_dict(),wifiscan=wifiscan.to_dict())
db.setData('it2','1.1',point.to_dict())