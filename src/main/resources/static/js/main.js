'use strict';
const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const connectingElement = document.querySelector('.connectin');
const chatArea = document.querySelector('#chat-message');
const logout = document.querySelector('#logout');

let stompClient = null;
let username = null;
let fullname = null;
let selectedUserId = null;

async function fetchCurrentUser() {
    const response = await fetch('/current-user');
    const user = await response.json();
    username = user.username;
    fullname = user.fullname;
}

window.addEventListener('DOMContentLoaded', async () =>{
    await fetchCurrentUser();
    if (document.querySelector('#chat-page')){
        connect();
    }
})

function connect() {
    if (username&&fullname){
        const socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

function onConnected(){
    stompClient.subscribe(`/user/${username}/queue/message`, onMessageReceived);
    stompClient.subscribe('user/public', onMessageReceived);
    stompClient.send("/app/user.addUser", {}, JSON.stringify({username: username, fullname: fullname, status: "ONLINE"}));
    document.querySelector('#connected-user-fullname').textContent = fullname;
    chatPage.classList.remove('hidden');
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers(){
    const connectedUsersResponse = await fetch("/users");
    let connectedUsers = await connectedUsersResponse.json();

    connectedUsers = connectedUsers.filter(user => user.username !== username);
    const connectedUserList = document.getElementById('/connectedUsers');
    connectedUserList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUserList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1){
            const separator = document.createElement("li");
            separator.classList.add('separator');
            connectedUserList.appendChild(separator);
        }
        }
    )
}

function appendUserElement(user, connectedUsersList){
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.username;
    const userImage = document.createElement('img')
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.fullname;
    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullname;
    const receivedMsg = document.createElement('span');
    receivedMsg.textContent = '0';
    receivedMsg.classList.add('nbr-msg', 'hidden');
    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsg);
    listItem.addEventListener('click', userItemClick);
    connectedUsersList.appendChild(listItem);
}

function userItemClick(){
    document.querySelector('user-item').forEach(item => {
        item.classList.remove('active');
    })
    messageForm.classList.remove('hidden');
    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');
    selectedUserId = clickedUser.getAttribute('id');
    findAndDisplayConnectedUsers().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';
}

function displayMessage (senderId, content){
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === username){
        messageContainer.classList.add('sender');
    }else {
        messageContainer.classList.add('received');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayConnectedUsers(){
    const userChatResponse = await fetch(`/message/${username}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = "";
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    })
    chatArea.scrollTop = chatArea.scrollHeight;

}

function onError(){
    connectingElement.textContent = 'Could not connect to WebSocket';
    connectingElement.style.collor = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if(messageContent && stampClient) {
        const chatMessage = {
            senderID: username, recipientId: selectedUserId,
            content: messageInput.value.trim(),
            timeStamp: new Date()
        };
        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        displayMessage(username, messageInput.value.trim());
        messageInput.value = "";
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}
async function onMessageReceived(payload){
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderID){
        displayMessage(message.senderID, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }
    if (selectedUserId){
        document.querySelector('${selectedUserId}').classList.add('active');
    } else {
        messageForm.classList.add('hidden')
    }
    const notifiedUser = document.querySelector('${message.senderId}');
    if (notifiedUser && !notifiedUser.classList.contains('active')){
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}
let hasLoggedOut = false;
function onLogout(){
    if (hasLoggedOut)return;
    hasLoggedOut = true;
    if (stompClient && stompClient.connected){
        stompClient.send('/app/user.disconnectUser',{}, JSON.stringify({username: username, fullName: fullname, status: 'OFFLINE' }));
    }
    window.location.reload();
}
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.addEventListener('beforeunload', () => {
    const isReload = window.performance.getEntriesByType('navigation')[0]?.type === 'reload';
    if (!isReload){
        onLogout();
    }
})



// // Подключение к чату
// class ChatClient {
//     constructor(streamId) {
//         this.streamId = streamId;
//         this.stompClient = null;
//         this.messageCallbacks = [];
//     }
//
//     connect() {
//         const socket = new SockJS('/ws-chat');
//         this.stompClient = Stomp.over(socket);
//
//         this.stompClient.connect({},
//             (frame) => {
//                 console.log('Connected: ' + frame);
//
//                 // Подписка на топик стрима
//                 this.stompClient.subscribe(
//                     `/topic/streams/${this.streamId}`,
//                     (message) => {
//                         const chatMessage = JSON.parse(message.body);
//                         this.messageCallbacks.forEach(cb => cb(chatMessage));
//                     }
//                 );
//
//                 // Загружаем историю
//                 this.loadHistory();
//             },
//             (error) => {
//                 console.error('Connection error: ', error);
//             }
//         );
//     }
//
//     loadHistory(limit = 50, before = null) {
//         let url = `/api/streams/${this.streamId}/chat/messages?limit=${limit}`;
//         if (before) {
//             url += `&before=${before}`;
//         }
//
//         fetch(url)
//             .then(response => response.json())
//             .then(messages => {
//                 // Отображаем историю в обратном порядке
//                 messages.reverse().forEach(msg => this.displayMessage(msg));
//             })
//             .catch(err => console.error('Failed to load history:', err));
//     }
//
//     sendMessage(content) {
//         if (!this.stompClient || !this.stompClient.connected) {
//             console.error('Not connected');
//             return;
//         }
//
//         this.stompClient.send(
//             `/app/streams/${this.streamId}/message`,
//             {},
//             JSON.stringify({ content: content })
//         );
//     }
//
//     onMessage(callback) {
//         this.messageCallbacks.push(callback);
//     }
//
//     displayMessage(message) {
//         const container = document.getElementById('chat-messages');
//         const element = document.createElement('div');
//         element.className = `chat-message ${message.type.toLowerCase()}`;
//         element.innerHTML = `
//             <span class="time">${new Date(message.timestamp).toLocaleTimeString()}</span>
//             <span class="user">${message.username}:</span>
//             <span class="content">${message.content}</span>
//         `;
//         container.appendChild(element);
//         container.scrollTop = container.scrollHeight;
//     }
// }
//
// // Использование
// const chat = new ChatClient(123); // streamId = 123
// chat.connect();
//
// chat.onMessage((message) => {
//     chat.displayMessage(message);
// });
//
// // Отправка сообщения
// document.getElementById('send-btn').addEventListener('click', () => {
//     const input = document.getElementById('message-input');
//     chat.sendMessage(input.value);
//     input.value = '';
// });