class ChatClient {
    constructor(streamId) {
        this.streamId = streamId;
        this.stompClient = null;
        this.messageCallbacks = [];
        this.connectCallbacks = [];
        this.pendingMessages = [];
        this.isFirstConnection = true;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 10;
    }

    connect() {
        const socket = new SockJS('/websocket');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({},
            (frame) => this.onConnected(frame),
            (error) => this.onError(error)
        );
    }

    onConnected(frame) {
        console.log('✅ Connected to WebSocket', frame);
        console.log('📡 Subscribing to /topic/streams/' + this.streamId);
        this.isConnected = true;
        this.reconnectAttempts = 0;
        // Основная подписка
        this.stompClient.subscribe(
            `/topic/streams/${this.streamId}`,
            (message) => this.onMessageReceived(message)
        );
        this.loadHistory().then(() => {
            this.historyLoaded = true;
            const msg = this.pendingMessages.shift();
            this.messageCallbacks.forEach(cb => cb(msg));
        });
        this.connectCallbacks.forEach(cb => cb(true, null));
    }

    onMessageReceived(message) {
        try {
            const chatMessage = JSON.parse(message.body);

            if (Array.isArray(chatMessage)) {
                // История
                chatMessage.reverse().forEach(msg => {
                    this.messageCallbacks.forEach(cb => cb(msg));
                });
            } else {
                // Новые сообщения
                if (!this.historyLoaded) {
                    // Если история еще не загружена, сохраняем сообщение
                    this.pendingMessages.push(chatMessage);
                } else {
                    this.messageCallbacks.forEach(cb => cb(chatMessage));
                }
            }
        } catch (e) {
            console.error('❌ Error parsing message:', e);
        }
    }

    // onSystemMessage(message) {
    //     try {
    //         const systemMessage = JSON.parse(message.body);
    //         console.log('🔔 System message:', systemMessage);
    //         this.messageCallbacks.forEach(cb => cb({
    //             ...systemMessage,
    //             isSystem: true
    //         }));
    //     } catch (e) {
    //         console.error('❌ Error parsing system message:', e);
    //     }
    // }

    onError(error) {
        console.error('❌ WebSocket error:', error);

        this.reconnectAttempts++;

        if (this.reconnectAttempts <= this.maxReconnectAttempts) {
            console.log(`🔄 Reconnecting... Attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
            setTimeout(() => this.connect(), 5000);
        } else {
            console.error('❌ Max reconnection attempts reached');
            this.connectCallbacks.forEach(cb => cb(false, error));
        }
    }

    sendMessage(content) {
        if (!this.stompClient || !this.stompClient.connected) {
            console.error('❌ Not connected to WebSocket');
            return false;
        }

        if (!content || content.trim() === '') {
            console.warn('⚠️ Cannot send empty message');
            return false;
        }

        console.log('📤 Sending message:', content);

        this.stompClient.send(
            `/app/streams/${this.streamId}/message`,
            {},
            content.trim()  // Просто текст
        );

        return true;
    }

    loadHistory(limit = 50) {
        const url = `/api/streams/${this.streamId}/chat/messages?limit=${limit}`;

        console.log('📚 Loading history from:', url);

        return fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }
                return response.json();
            })
            .then(messages => {
                console.log(`📚 Loaded ${messages.length} messages`);
                messages.forEach(msg => {
                    this.messageCallbacks.forEach(cb => cb(msg));
                });
                return messages;
            })
            .catch(err => {
                console.error('❌ Failed to load history:', err);
                throw err;
            });
    }

    onMessage(callback) {
        if (typeof callback === 'function') {
            this.messageCallbacks.push(callback);
        }
    }

    onConnectionChange(callback) {
        if (typeof callback === 'function') {
            this.connectCallbacks.push(callback);
        }
    }
    // disconnect() {
    //     if (this.stompClient && this.stompClient.connected) {
    //         this.stompClient.disconnect(() => {
    //             console.log('🔌 Disconnected from WebSocket');
    //         });
    //     }
    // }
    //
    // isConnected() {
    //     return this.stompClient && this.stompClient.connected;
    // }
}