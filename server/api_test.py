import requests
import json
url = 'http://api108448live.gateway.akana.com:80/customers/1151515151'
#data = '{"query":{"bool":{"must":[{"text":{"record.document":"SOME_JOURNAL"}},{"text":{"record.articleTitle":"farmers"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}'
response = requests.get(url)
#print response.text
#print response.encoding
j=response.json()
#j=json.load(j)
#print type(j)
#j['id']
title=j['title']
first_name=j['firstName']
last_name=j['lastName']


