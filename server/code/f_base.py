from firebase import firebase
import tweepy
firebase = firebase.FirebaseApplication('https://barclaysapp-4f1ef.firebaseio.com', None)
result = firebase.get('/users',None)
print result
print type(result)
#{'1': 'John Doe'}

#token=result['7NlM7mFvAVdHqeOXzOm8jHzWkl23']['twitter_access_token']
#print token
#token='2188886478-TDh4lcO7b3KMoMHjg7YoIACxhsHtkZfgnd2VKlo'
consumer_key='7Q0gW7rGv4Eu20W0ZWRRsLedQ'
consumer_secret='nQYbudQDKDlUTqYJetv4DGMBEUBA1928EDkT1859IWpGABa4UE'

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
def get_url():
    global auth
    url=auth.get_authorization_url()
    return url
def s_tweet(v,msg):
    global auth
    print "@@@@@@@@@@@@@@@@@@@@@2"
    auth.get_access_token(v)

    api=tweepy.API(auth)
    print "appppppppppppppppii"
    api.update_status(msg)
    
    
#auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
#api = tweepy.API(auth)
#api.update_status("Hello twitter")
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
key='746581061707038721-pkhH8spTdU0jRBa3kdkSsRiIj24J60b'
secret='nEEoO61ZqIKP8KJAxyTWqw4FZz46ptWUHeW5RTQ5byhdn'
auth.set_access_token(key, secret)
api = tweepy.API(auth)
def send_tweet(msg):
    api.update_status(msg)

def get_number():
    global num
    num=result['7NlM7mFvAVdHqeOXzOm8jHzWkl23']['phone_no']
    print num
    return num


