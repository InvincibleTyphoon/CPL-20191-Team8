import firebase_admin  
from firebase_admin import credentials
from firebase_admin import firestore

class DBConn:
    db = None
    def __init__(self,keyfile): # keyfile = 'silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json'
        cred = credentials.Certificate(keyfile) #json 파일에 DB정보가 있어서 연결해줌
        firebase_admin.initialize_app(cred)
        self.db = firestore.client()
    def setData(self,dataKey,point,data):
        doc_ref = self.db.collection('data').document(dataKey).collection('point').document(point)  #'users'에 컬렉션 이름을  'alovelace'는 문서이름이다.
        doc_ref.set(data)
                                
    def getData(self,dataKey,point):
        users_ref = self.db.collection('data').document(dataKey).collection('point').document(point)  #users 컬렉션 아래 다큐먼트를 전부 읽어 온다.
        doc = users_ref.get()
        return doc.to_dict()


class Magnetic:
    def __init__(self,x,y,z):
        self.x = x
        self.y= y
        self.z =z
        pass
    def from_dict(self,source):
        self.x = source[u'x']
        self.y= source[u'y']
        self.z= source[u'z']
    def to_dict(self):
        data = {
            u'x':self.x,
            u'y':self.y,
            u'z':self.z,
           
        }
        return data
    def __repr__(self):
        return u'Magnetic(x={}, y={}, z={})'.format(
            self.x, self.y, self.z)


class Wifiscan:
    def __init__(self,ssid,level):
        self.ssid = ssid
        self.level = level
        pass
        
    def from_dict(self,source):
        self.bbsid = source[u'bbsid']
        self.ssid= source[u'ssid']
        self.level= source[u'level']
    def to_dict(self):
        data = {
            u'ssid': self.ssid,
            u'level':self.level,
        }
        return data

    def __repr__(self):
        return u'Wifiscan(bbsid={}, ssid={}, level={})'.format(
            self.bbsid, self.ssid, self.level)

class Point:
    def __init__(self,key:str,magnetic:dict,wifiscan:dict):
        self.key = key
        self.magnetic = magnetic
        self.wifiscan = wifiscan
        pass

    def from_dict(self,source):
        self.key = source[u'key']
        self.magnetic= source[u'magnetic']
        self.wifiscan= source[u'wifiscan']

    def to_dict(self):
        data = {
            u'key' : self.key,
            u'magnetic':self.magnetic,
            u'wifiscan':self.wifiscan
        }
        return data

    def __repr__(self):
        return u'Point(key={},magnetic={},wifiscan={})'.format(
            self.key, self.magnetic,self.wifiscan)