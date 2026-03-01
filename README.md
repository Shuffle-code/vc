1. Клиент загружает страницу:
   → GET /api/streams/123/chat/messages?limit=50
   ← [история сообщений]

2. Клиент подключается к WebSocket:
   → CONNECT /ws-chat
   ← CONNECTED

3. Клиент подписывается на топик:
   → SUBSCRIBE /topic/streams/123
   ← SUBSCRIBED
   (сервер отправляет JOIN уведомление)

4. Клиент отправляет сообщение:
   → SEND /app/streams/123/message
     body: {"content": "Привет!"}
   
   Сервер:
   - проверяет права
   - сохраняет в БД
   - рассылает всем:
     ← MESSAGE /topic/streams/123
       body: {"id": "...", "username": "...", "content": "Привет!", ...}
