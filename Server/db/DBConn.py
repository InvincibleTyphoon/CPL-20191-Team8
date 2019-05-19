import firebase_admin  
from firebase_admin import credentials
from firebase_admin import firestore
class DBConn:
    db = None
    
    def __init__(self,keyfile): # keyfile = 'silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json'
        cred = credentials.Certificate(keyfile) #json 파일에 DB정보가 있어서 연결해줌
        firebase_admin.initialize_app(cred)
        self.db = firestore.client()
    def setData(self,dataKey,data):
        doc_ref = self.db.collection('data').document(dataKey).collection('point').document()  #'users'에 컬렉션 이름을  'alovelace'는 문서이름이다.
        doc_ref.set({
            u'x':data['x'],
            u'y':data['y'],
            u'magnetic':data['magnetic']
        })
        doc_ref = doc_ref.collection('wifi')

        for wifiData in data['wifiscan'].values():
            # print(wifiData)
            doc_ref.add(wifiData)

    def getData(self,dataKey,x,y):
        index = 0 # 데이터 순서
        rst = dict() #린턴될 값
        users_ref = self.db.collection('data').document(dataKey).collection('point')  #users 컬렉션 아래 다큐먼트를 전부 읽어 온다.
        posDocs = users_ref.where(u'x',u'==',x).where(u'y',u'==',y).get()
        temp = dict()
        flag = False
        pointId = ''
        for posDoc in posDocs:
            #print(doc.to_dict())
            temp = posDoc.to_dict()
            pointId =posDoc.id

            rst[index] = temp #
            wifiLvDocs = self.db.collection('data').document(dataKey).collection('point').document(pointId).collection('wifi').get()
            wifitemp = dict()
            i = 0
            for wifiLvDoc in wifiLvDocs:
                wifitemp[i] = wifiLvDoc.to_dict()
                wifiPosDocs =  self.db.collection('data').document(dataKey).collection('wifi').where(u'SSID',u'==',wifitemp[i]['SSID']).get()
                temp = dict()
                flag = False
                for wifiPosDoc in wifiPosDocs:
                    # print(wifiDoc.to_dict())
                    temp = wifiPosDoc.to_dict()
                    flag = True
                if(flag):
                    wifitemp[i]['x'] = temp['x']
                    wifitemp[i]['y'] = temp['y']
                else:
                    wifitemp[i]['x'] = -1
                    wifitemp[i]['y'] = -1
                i+=1
            rst['wifi'] = wifitemp
            index+=1
        rst['n'] = index
        return rst
    def regiData(self,key): #건물의 키값을 등록 해야 함
        doc = self.db.collection('data').document(key)
        doc.set({u'key':key})



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
    def __init__(self,x:int,y:int,magnetic:dict,wifiscan:dict):
        self.x = x
        self.y = y
        self.magnetic = magnetic
        self.wifiscan = wifiscan
        pass

    def from_dict(self,source):
        self.x = source[u'x']
        self.y = source[u'y']
        self.magnetic= source[u'magnetic']
        self.wifiscan= source[u'wifiscan']

    def to_dict(self):
        data = {
            u'x' : self.x,
            u'y' : self.y,
            u'magnetic':self.magnetic,
            u'wifiscan':self.wifiscan
        }
        return data

    def __repr__(self):
        return u'Point(x={},y={},magnetic={},wifiscan={})'.format(
            self.x,self.y, self.magnetic,self.wifiscan)