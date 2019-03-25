#pip install --upgrade firebase-admin 으로 설치

import firebase_admin  
from firebase_admin import credentials
from firebase_admin import firestore

# Use a service account
cred = credentials.Certificate('silnaewichi-2c66f-firebase-adminsdk-t6ob2-a6dfec601d.json') #json 파일에 DB정보가 있어서 연결해줌
firebase_admin.initialize_app(cred)

db = firestore.client()   # db 를 선언

doc_ref = db.collection(u'users').document(u'alovelace')  #'users'에 컬렉션 이름을  'alovelace'는 문서이름이다.
doc_ref.set({ #해당 필드가 다큐먼트 아래에 적혀진다.
    u'first': u'Ada',
    u'last': u'Lovelace',
    u'born': 1815
})

doc_ref = db.collection(u'users').document(u'aturing')
doc_ref.set({
    u'first': u'Alan',
    u'middle': u'Mathison',
    u'last': u'Turing',
    u'born': 1912
})


users_ref = db.collection(u'users')  #users 컬렉션 아래 다큐먼트를 전부 읽어 온다.
docs = users_ref.get()

for doc in docs: #반복 출력
    print(u'{} => {}'.format(doc.id, doc.to_dict())) 
