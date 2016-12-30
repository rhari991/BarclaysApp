import socketserver
import threading
from chatterbot import ChatBot
from textblob import TextBlob
from chatterbot.training.trainers import ChatterBotCorpusTrainer
from chatterbot.training.trainers import ListTrainer
##
##class Chatty:
##    def __init__(self,msg):
##        self.msg=msg
##        
##    chatbot = ChatBot('Job bot')
##    chatbot.set_trainer(ChatterBotCorpusTrainer)
##    chatbot.train("chatterbot.corpus.english")
##    rep=""
##    def reply():
##       response=chatbot.get_response(data2)
##       self.rep=str(response)
##       return self.rep
class ThreadedTCPRequestHandler(socketserver.BaseRequestHandler):
    conver= [
			"Hello",
			"Hi there!",
			"How are you?",
			"I am good.",
			"How are you doing?",
			"I'm doing great.",
			"Where do you live?",
			"I live in Mumbai",
			"Where are you from?",
			"I live in Navi Mumbai",
			"Where in navi mumbai?",
			"Cbd bealpur",
			"Are you robot or human?",
			"I am human ofcourse.",
			"That is good to hear",
			"Me too",
			"Thank you.",
			"You're welcome.",
			"give me a job",
			"That's great.Please, enter the position for which you want the job"    
   		]
    chatbot = ChatBot("Harsh Gupta")
    def __init__(self,a,b,c):
		chatbot = ThreadedTCPRequestHandler.chatbot
  		chatbot.set_trainer(ChatterBotCorpusTrainer)
  		chatbot.train("chatterbot.corpus.english")  		
		chatbot.set_trainer(ListTrainer)
		chatbot.train(ThreadedTCPRequestHandler.conver)
    def handle(self):
        #Connect to database
        print "Connected"
        data2 = str(self.request.recv(1024))
        #chatbot = ChatBot('Job bot')
        #chatbot.set_trainer(ChatterBotCorpusTrainer)
        #chatbot.train("chatterbot.corpus.english")
        response=self.chatbot.get_response(data2)
        print("Server: {}".format(data2));
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
