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

}
