import pandas
from sklearn.model_selection import train_test_split
import MyRecommender as Recommenders
#This is made by Aldrago
import numpy
def train(userNumber):
    triplets_file='User.csv'
    songs_metadata_file='Songs.csv'
    
    song_df_1 = pandas.read_csv(triplets_file,header=None)
    song_df_1.columns = ['user_id', 'song_id', 'listen_count']
    
    #Read song  metadata
    song_df_2 =  pandas.read_csv(songs_metadata_file,header=None)
    song_df_2.columns = ['song_id', 'song_name','artist_name']
    
    #Merge the two dataframes above to create input dataframe for recommender systems
    song_df = pandas.merge(song_df_1, song_df_2.drop_duplicates(['song_id']), on="song_id", how="left") 
    
    #Merge song title and artist_name columns to make a merged column
    song_df['song_name'] = song_df['song_name'].map(str) + " - " + song_df['artist_name']
    song_grouped = song_df.groupby(['song_name']).agg({'listen_count': 'count'}).reset_index()
    grouped_sum = song_grouped['listen_count'].sum()
    song_grouped['percentage']  = song_grouped['listen_count'].div(grouped_sum)*100
    song_grouped.sort_values(['listen_count', 'song_name'], ascending = [0,1])
    
    #Recommender
    train_data, test_data = train_test_split(song_df, test_size = 0.20, random_state=0)
    
    #Popularity
    pm = Recommenders.popularity_recommender_py()
    pm.create(train_data, 'user_id', 'song_name')
    
    #Colloborative Filtering
    is_model = Recommenders.item_similarity_recommender_py()
    is_model.create(train_data, 'user_id', 'song_name')
    
    
    users = song_df['user_id'].unique()
    user_id=users[userNumber-1]
    ans1=pm.recommend(user_id)
    
    #Content based    
    ans1=ans1['song_name'].values.tolist()
    
    ans2=is_model.recommend(user_id)
    
    ans2=ans2['song'].values.tolist()
    
    song1=ans2[0]
    song2=ans2[1]
    song3=ans2[2]
    
    ans3=is_model.get_similar_items([song1])
    ans4=is_model.get_similar_items([song2])
    ans5=is_model.get_similar_items([song3])
    
    ans3=ans3['song'].values.tolist()
    ans4=ans4['song'].values.tolist()
    ans5=ans5['song'].values.tolist()
    
    
    res_list = ans1[:5] + ans2[:5] + ans3[:5] + ans4[:5] + ans5[:5]
    answer=set(res_list)
    
    return answer

def login(username, password):
    users = pandas.read_csv("UserDetails.csv",header=None)
    users.columns = ['userid', 'userName', 'password']
    users=users.values.tolist()
    for user in users:
        if(user[1]==username and user[2]==password):
            return user[0]
    return 'Not Found'

def play(userid,password):
    uid=login(userid,password)
    return train(int(uid[3:]))

def playSong(song):
    from pygame import mixer  # Load the popular external library
    mixer.init()
    mixer.music.load(song+'.wav')
    mixer.music.play()

import cv2
import io
import socket
import struct
import time
import pickle
import zlib
import time

server_socket = socket.socket()
print("connecting")
server_socket.bind(('',8000)) 
server_socket.listen(10)

connection,address= server_socket.accept()
print("connection has been established | ip "+address[0]+" port "+str(address[1]))
print("connected")
encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]
 
uap=connection.recv(200)
s=uap.decode()
userid,password=s.split(",")

print(userid,password)
song=play(userid,password)
#data = pickle.dumps(song,0)
time.sleep(1)
for i in song:
    i=i+"\n"
    s=i.encode()
    print(s)
    connection.send(s)
    a=connection.recv(2)
    print(a)
songname=connection.recv(100)
print(songname)
playSong(songname)
print("sent")

time.sleep(0.01)

connection.close()
