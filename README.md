# AndroidEltechatAdminApp
Chat for Eltech university students with admins functionality


## Server API
### Responses

Message from user: 
```JSON
{"flag":"message", "messageId":0, "name":"megalok", "message":"text"}
```

(Admin) Delete message:
```JSON
{"flag":"deleteMessage", "messageId":0}
```

Login successfully:
```JSON
{"flag":"loginSuccess"}
```

Login failure because of nickname:
```JSON
{"flag":"loginFailureNickname"}
```

Login failure because of server internal error:
```JSON
{"flag":"loginServerFailure"}
```
Another user login:
```JSON
{"flag":"newUserConnect", "name":"Komdosh", "online":1}
```
Another user logout:
```JSON
{"flag":"userDisconnect", "name":"Komdosh", "online":2}
```

(Admin) Kick user:
```JSON
{"flag":"kick", "name":"Komdosh"}
```

(Admin) Mute user: 
```JSON
{"flag":"mute", "name":"megalok"}
```

### Requests 

Message: 
```JSON
{"flag":"message", "name":"megalok",  "message":"text"} 
```

(Admin) Delete user: (field "sec" current is not implemented)
```JSON
{"flag":"deleteMessage", "messageId":0, "sec":"1234"}
```

(Admin) Kick user: (field "sec" current is not implemented)
```JSON
{"flag":"kick", "name":"Komdosh", "sec":"g134f"}
```

(Admin) Mute user: 
```JSON
{"flag":"mute", "name":"megalok"}
```

(Admin) Unmute user: 
```JSON
{"flag":"unmute", "name":"megalok"}
```
