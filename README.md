# AndroidEltechatAdminApp
Chat for Eltech university students with admins functionality
## Сервер API
### Сообщения с сервера 

Простое сообщение: 
```JSON

{"flag":"message", "messageId":0, "name":"megalok", "message":"text"}
```

(АДМИН) Удаление сообщения:

```JSON

{"flag":"deleteMessage", "messageId":0}
```


Пользователь зашёл удачно:

```JSON

{"flag":"loginSuccess"}
```

Пользователь не смог зайти из-за ника:

```JSON

{"flag":"loginFailureNickname"}
```

Пользователь не смог зайти ошибка сервера:

```JSON

{"flag":"loginServerFailure"}
```

Другой пользователь зашёл:

```JSON

{"flag":"newUserConnect", "name":"Komdosh", "online":1}
```

Другой пользователь вышел:

```JSON

{"flag":"userDisconnect", "name":"Komdosh", "online":2}
```

(АДМИН) Кик пользователя:

```JSON

{"flag":"kick", "name":"Komdosh"}
```

(АДМИН | МОДЕРАТОР) Мут пользователя: 

```JSON

{"flag":"mute", "name":"megalok"}
```

### Сообщения на сервер 
Простое сообщение: 

```JSON

{"flag":"message", "name":"megalok",  "message":"text"} 
```

(АДМИН) Удаление сообщения: (поле "sec" временно отсутствует)

```JSON

{"flag":"deleteMessage", "messageId":0, "sec":"1234"}
```

(АДМИН) Кик пользователя: (поле "sec" временно отсутствует)

```JSON

{"flag":"kick", "name":"Komdosh", "sec":"g134f"}
```

(АДМИН | МОДЕРАТОР) Замутить пользователя: 

```JSON

{"flag":"mute", "name":"megalok"}
```

(АДМИН | МОДЕРАТОР) Размутить пользователя: 

```JSON

{"flag":"unmute", "name":"megalok"}
