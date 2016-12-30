from twilio.rest import TwilioRestClient 
 
# put your own credentials here 
ACCOUNT_SID = "AC2abd7327e4b8b6477688439de121a92f" 
AUTH_TOKEN = "0c0e72d7c696fdfa2c6bf2c0e5459d49" 
 
client = TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN) 
def send_sms(msg,number): 
	client.messages.create(to=number, from_="+15636075781",  body=msg,)
