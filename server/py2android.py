import socketserver
import threading
from code.bot_trainer import BotTrainer
import requests
import json
#class ThreadedTCPRequestHandler(socketserver.BaseRequestHandler):
from code.twilio_test import *
from code.f_base import *
class ThreadedTCPRequestHandler(socketserver.BaseRequestHandler):


    t=BotTrainer()
    def handle(self):
        print "connected"
        data2 = str(self.request.recv(1024))
        #chatbot = ChatBot('Job bot')
        #chatbot.set_trainer(ChatterBotCorpusTrainer)

        #chatbot.train("chatterbot.corpus.english")
        #response=chatbot.get_response(data2)
#        if("tweet" in data2)
        print data2[2:]
        d=json.loads(data2[2:])
        print type(d)      
        print d['uid']
        try:
            response = ThreadedTCPRequestHandler.t.respond(data2)
        except:
            response="hello"
        #print("Server: {}".format(data2));
        data2=d['parameter']
        print "-------"+data2
        data2=data2.lower()
        data2=data2.strip()  
        words=data2.split()
#        print words[0]
        response = bytes(str(response))
        data2=data2.lower()

        print("Server: {}".format(data2));
        if "send" in data2:
            #execfile('twilio_test.py')
            url=get_url()
            response = bytes(str(url))
        elif("my tweet" in data2):
            
            verifier=d['verifier']
            tweet=d['tweet']
            print verifier+"++++++++@@@"+tweet
            s_tweet(verifier,tweet)    
            response = bytes(str("tweeted successfully"))
        elif "transaction" in data2:
            response = bytes(str("Please, enter the receiver's account number"))
        elif("account_no" in data2):
            response = bytes(str("Please, enter the amount you wish to transfer"))
        elif("amount" in data2):
            url = 'http://api108448live.gateway.akana.com:80/customers/1151515151'
            response = requests.get(url)
            j=response.json()
            title=j['title']
            first_name=j['firstName']
            last_name=j['lastName']
            response = bytes(str("Congratulations, {0} transferred to {1} {2} {3}".format(words[1],title,first_name,last_name)) )        
            number=get_number()
            send_sms("You have sucessfully transferred the {0} to {1} {2} {3}".format(words[1],title,first_name,last_name),number)
        elif("balance" in data2):
            response = bytes(str("Your account balance is seven thousand rupees"))
        else :
            response = bytes(str(response))    

        self.request.sendall(response)

class ThreadedTCPServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    pass

if __name__ == "__main__":
    # Port 0 means to select an arbitrary unused port
    HOST=""
    PORT =  5000

    tcpserver = ThreadedTCPServer((HOST, PORT-1), ThreadedTCPRequestHandler)
    server_thread = threading.Thread(target=tcpserver.serve_forever)
    server_thread.daemon = True
    server_thread.start()
    print("TCP serving at port", PORT-1)

    while True:
        pass
    tcpserver.shutdown()
