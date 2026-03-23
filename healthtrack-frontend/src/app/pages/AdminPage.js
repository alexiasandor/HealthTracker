import '../styles/AdminPage.css';
import React, {useState, useEffect, useRef} from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import validator from 'validator';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import ModalPage from './ModalPage';

import {USERS_HOST, DEVICES_HOST, MESSAGING_HOST, WEBSOCKET_MESSAGING_HOST, BROKER_MESSAGING_HOST} from "../assets/hosts";

function AdminPage() {
    //ENDPOINTS FOR MICROSERVICES
    const endpoint = {
        user_params: '/user/',
        user_no_params: '/user',
        device_params: '/device/',
        device_no_params: '/device',
        messaging1: '/conversationOf/',
        messaging2: '/conversation/',
        messaging3: '/messagesRead/',
        messaging4: '/message/',
        messaging5: '/unreadMessages/',
        websocketMessaging: '/topic/sendMessage',
        websocketMessagingSend: '/frontend/messageSent',
        websocketMessagingWrittingMessage: '/topic/isWritting'
    };


    //WEBSOCKET VARIABLES
    //MESSAGING
    const socketMessaging = new SockJS(WEBSOCKET_MESSAGING_HOST.backend_api);
    const stompClientMessaging = new Client({
        brokerURL: BROKER_MESSAGING_HOST.backend_api,
        connectHeaders: {},
        reconnectDelay: 5000,
        webSocketFactory: () => socketMessaging,
        debug: (msg) => console.log(msg)
    });


    //NAVIGATION, LOCATION AND OTHER VARIABLES
    const navigate = useNavigate();
    const location = useLocation();
    const successResponse = 200;
    const unauthorizedResponse = 401;
    const bearerName = 'Bearer ';
    const token = location.state === null ? "" : location.state.token;


    //STATE VARIABLES
    //ADMIN USER
    const [adminEmail, setAdminEmail] = useState([]);

    //SELECT WINDOW VISIBILITY FIELDS RELATED
    const [showUserTab, setShowUserTab] = useState(false);
    const [showDeviceTab, setShowDeviceTab] = useState(false);

    //USER LIST AND SELECTED USER RELATED
    const [userList, setUserList] = useState([]);
    const [selectedUserId, setSelectedUserId] = useState("-1");

    //USER WINDOW VISIBILITY FIELDS RELATED
    const [createUser, setCreateUser] = useState(false);
    const [updateUser, setUpdateUser] = useState(false);
    const [deleteUser, setDeleteUser] = useState(false);
    const [showUser, setShowUser] = useState(false);

    //USER TEXT FIELDS RELATED
    const [email, setEmail] = useState("");
    const [name, setEnteredName] = useState("");
    const [password, setEnteredPassword] = useState("");
    const [role, setEnteredRole] = useState("");

    //DEVICE LIST AND SELECTED DEVICE RELATED
    const [deviceList, setDeviceList] = useState([]);
    const [selectedDeviceId, setSelectedDeviceId] = useState("-1");

    //DEVICE WINDOW VISIBILITY FIELDS RELATED
    const [createDevice, setCreateDevice] = useState(false);
    const [updateDevice, setUpdateDevice] = useState(false);
    const [deleteDevice, setDeleteDevice] = useState(false);
    const [showDevice, setShowDevice] = useState(false);

    //DEVICE TEXT FIELDS RELATED
    const [address, setEnteredAddress] = useState("");
    const [description, setEnteredDescription] = useState("");
    const [mhec, setEnteredMhec] = useState("");
    const [userId, setEnteredUserId] = useState("-1");

    //MESSAGING RELATED RELATED
    const [user, setUser] = useState([]);
    const [userMessagingList, setUserMessagingList] = useState([]);
    const [messageHistory, setMessageHistory] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [enteredEmail, setEnteredEmail] = useState("");
    const [chatedUserList, setChatedUserList] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [messageWritten, setMessageWritten] = useState("");
    const [loading, setLoading] = useState(false);
    const [sendMessageFunction, setSendMessageFunction] = useState(null);
    const [userIsWrittingFunction, setUserIsWrittingFunction] = useState(null);
    const [messagesReceived, setMessagesReceived] = useState([]);
    const [usersWhoMessaged, setUsersWhoMessaged] = useState([]);
    const [hasUnreadMessages, setHasUnreadMessages] = useState(false);
    const [isUserWritting, setIsUserWritting] = useState(false);
    const selectedUserRef = useRef(selectedUser);


    //LOGOUT AND SETTING USER FUNCTIONS
    const logout = () => {
        stompClientMessaging.deactivate();
        navigate('/');
    }

    const searchUser = async (userId) => {
        try {
            const userObj = await axios.get(USERS_HOST.backend_api + endpoint.user_params + userId, {headers: {'Authorization': (bearerName + token)}});
            if(userObj.status === successResponse) {
                setUser(userObj.data);
            }
            else {
                alert("Could not load the resources needed! Redirecting back");
                navigate("/Login");
            }
        } catch(error) {
            alert("Could not load the resources needed! Redirecting back");
            navigate("/Login");
        }
    }


    //USER TEXT FIELDS HANDLERS
    function emailInputHandler(enteredEmailText) {
        setEmail(enteredEmailText.target.value);
    }

    function nameInputHandler(enteredNameText) {
        setEnteredName(enteredNameText.target.value);
    }

    function passwordInputHandler(enteredPasswordText) {
        setEnteredPassword(enteredPasswordText.target.value);
    }

    function roleInputHandler(enteredRoleText) {
        setEnteredRole(enteredRoleText.target.value);
    }


    //DEVICE TEXT FIELDS HANDLERS
    function addressInputHandler(enteredAddressText) {
        setEnteredAddress(enteredAddressText.target.value);
    }

    function descriptionInputHandler(enteredDescriptionText) {
        setEnteredDescription(enteredDescriptionText.target.value);
    }

    function mhecInputHandler(enteredMhecText) {
        setEnteredMhec(enteredMhecText.target.value);
    }

    function userIdInputHandler(enteredUserIdText) {
        setEnteredUserId(enteredUserIdText.target.value);
    }


    //USER FIELDS MANIPULATION
    const emptyUserFields = () => {
        setEmail("");
        setEnteredName("");
        setEnteredPassword("");
        setEnteredRole("");
    }

    const fillUserFields = (selectedUserIdValue) => {
        const newUserId = selectedUserIdValue.target.value;
        setSelectedUserId(newUserId);

        if(newUserId === "-1") {
            emptyUserFields();
        }
        else {
            for (var i = 0; i < userList.length; i += 1) {
                if(userList.at(i).userId === newUserId) {
                    setEmail(userList.at(i).email);
                    setEnteredName(userList.at(i).name);
                    setEnteredPassword(userList.at(i).password);
                    setEnteredRole(userList.at(i).role);
                    break;
                }
            }
        }
    }


    //DEVICE FIELDS MANIPULATION
    const emptyDeviceFields = () => {
        setEnteredAddress("");
        setEnteredDescription("");
        setEnteredMhec("");
        setEnteredUserId("-1");
    }

    const fillDeviceFields = (selectedDeviceIdValue) => {
        const newDeviceId = selectedDeviceIdValue.target.value;
        setSelectedDeviceId(newDeviceId);

        if(newDeviceId === "-1") {
            emptyDeviceFields();
        }
        else {
            for (var i = 0; i < deviceList.length; i += 1) {
                if(deviceList.at(i).deviceId === newDeviceId) {
                    setEnteredAddress(deviceList.at(i).address);
                    setEnteredDescription(deviceList.at(i).description);
                    setEnteredMhec(deviceList.at(i).maximumHourlyEnergyConsumption);
                    setEnteredUserId(deviceList.at(i).user === null?"-1":deviceList.at(i).user.userId);
                    break;
                }
            }
        }
    }


    //WINDOW OPTIONS CHANGE
    const changeShowUserTab = () => {
        if(showUserTab === true) {
            setShowUserTab(false);
        }
        else {
            setShowUserTab(true);
        }
        setShowDeviceTab(false);

        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        emptyUserFields();
        emptyDeviceFields();
        setSelectedUserId("-1");
        setSelectedDeviceId("-1");
    }

    const changeShowDeviceTab = () => {
        if(showDeviceTab === true) {
            setShowDeviceTab(false);
        }
        else {
            setShowDeviceTab(true);
        }
        setShowUserTab(false);

        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        emptyUserFields();
        emptyDeviceFields();
        setSelectedUserId("-1");
        setSelectedDeviceId("-1");
    }


    //USER OPTIONS WINDOW HANDLERS
    const handleCreateUser = () => {
        setCreateUser(true);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        emptyUserFields();
        setSelectedUserId("-1");
    }

    const handleUpdateUser = () => {
        setCreateUser(false);
        setUpdateUser(true);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        searchUserList();
        emptyUserFields();
        setSelectedUserId("-1");
    }

    const handleDeleteUser = () => {
        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(true);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        searchUserList();
        emptyUserFields();
        setSelectedUserId("-1");
    }

    const handleShowUser = () => {
        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(true);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        searchUserList();
        emptyUserFields();
        setSelectedUserId("-1");
    }


    //DEVICE OPTIONS WINDOW HANDLERS
    const handleCreateDevice = () => {
        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(true);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(false);

        emptyDeviceFields();
        setSelectedDeviceId("-1");
        searchUserList();
    }

    const handleUpdateDevice = () => {
        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(true);
        setDeleteDevice(false);
        setShowDevice(false);

        searchDeviceList();
        searchUserList();
        emptyDeviceFields();
        setSelectedDeviceId("-1");
    }

    const handleDeleteDevice = () => {
        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(true);
        setShowDevice(false);

        searchDeviceList();
        emptyDeviceFields();
        setSelectedDeviceId("-1");
    }

    const handleShowDevice = () => {
        setCreateUser(false);
        setUpdateUser(false);
        setDeleteUser(false);
        setShowUser(false);

        setCreateDevice(false);
        setUpdateDevice(false);
        setDeleteDevice(false);
        setShowDevice(true);

        searchDeviceList();
        emptyDeviceFields();
        setSelectedDeviceId("-1");
    }


    //SELECT CRUD OPERATION
    const performAction = () => {
        if(showUserTab === true) {
            if(createUser === true) {
                createNewUser();
            }
            else {
                if(updateUser === true) {
                    updateExistingUser();
                }
                else {
                    if(deleteUser === true) {
                        deleteExistingUser();
                    }
                }
            }
        }
        else {
            if(createDevice === true) {
                createNewDevice();
            }
            else {
                if(updateDevice === true) {
                    updateExistingDevice();
                }
                else {
                    deleteExistingDevice();
                }
            }
        }
    }


    //USER CRUD OPERATIONS
    const createNewUser = async () => {
        if(email === "" || name === "" || password === "" || role === "Placeholder") {
            alert("Complete all fields in order to create an user!");
            return;
        }
        if(password.length < 8) {
            alert("Password must be at least 8 characters long!");
            return;
        }
        if(!validator.isEmail(email)) {
            alert("Wrong email format!")
            return;
        }
        if(role !== "admin" && role !== "client") {
            alert("Choose a valid role!")
            return;
        }

        try {
            await axios.post(USERS_HOST.backend_api + endpoint.user_no_params, {
                email: email,
                name: name,
                password: password,
                role: role
            },
            {headers: {'Authorization': (bearerName + token)}});

            alert("User created successfully!");

            emptyUserFields();
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong while creating the user!\n" + error.message);
            }
        }
    }

    const updateExistingUser = async () => {
        if(selectedUserId === "-1") {
            alert("Select an User before performing an update!");
            return;
        }
        if(email === "" || name === "" || password === "" || role === "Placeholder") {
            alert("Complete all fields in order to create an user!");
            return;
        }
        if(password.length < 8) {
            alert("Password must be at least 8 characters long!");
            return;
        }
        if(!validator.isEmail(email)) {
            alert("Wrong email format!")
            return;
        }

        try {
            let response;
            if(email === adminEmail) {
                response = window.confirm("You are about to change your information. You will be logged out. Are you sure?");
                if(response === false)  {
                    searchUserList();
                    emptyUserFields();
                    setSelectedUserId("-1");
                    return;
                }
            }
            
            await axios.put(USERS_HOST.backend_api + endpoint.user_params + selectedUserId, {
                userId: selectedUserId,
                email: email,
                name: name,
                password: password,
                role: role
            },
            {headers: {'Authorization': (bearerName + token)}});

            alert("User updated successfully!");

            searchUserList();
            emptyUserFields();
            setSelectedUserId("-1");

            if(email === adminEmail) {
                navigate('/Login');
            }
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong while updating the user!\n" + error.message);
            }
        }
    }

    const deleteExistingUser = async () => {
        if(selectedUserId === "-1") {
            alert("Select an User before performing a delete!");
            return;
        }

        try {
            await axios.delete(USERS_HOST.backend_api + endpoint.user_params + selectedUserId, {headers: {'Authorization': (bearerName + token)}});

            alert("User deleted successfully!");

            searchUserList();
            emptyUserFields();
            setSelectedUserId("-1");
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong while deleting the user!\n" + error.message);
            }
        }
    }

    const searchUserList = async () => {
        try {
            const userList = await axios.get(USERS_HOST.backend_api + endpoint.user_no_params, {headers: {'Authorization': (bearerName + token)}});
            if(userList.status === successResponse) {
                setUserList(userList.data);
            }
            else {
                alert("Something went wrong when loading users!");
            }
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong when loading users!");
            }
        }
    }


    //DEVICE CRUD OPERATIONS
    const createNewDevice = async () => {
        if(address === "" || description === "" || mhec === "") {
            alert("Complete all fields in order to create an user!");
            return;
        }
        if(parseFloat(mhec) < 0) {
            alert("The energy consumption can not be negative!");
            return;
        }

        try {
            if(userId === "-1") {
                await axios.post(DEVICES_HOST.backend_api + endpoint.device_no_params, {
                    address: address,
                    description: description,
                    maximumHourlyEnergyConsumption: parseFloat(mhec).toFixed(2),
                    user: null
                },
                {headers: {'Authorization': (bearerName + token)}});
            }
            else {
                await axios.post(DEVICES_HOST.backend_api + endpoint.device_no_params, {
                    address: address,
                    description: description,
                    maximumHourlyEnergyConsumption: parseFloat(mhec).toFixed(2),
                    user: {
                        userId: userId,
                    }
                },
                {headers: {'Authorization': (bearerName + token)}});
            }

            alert("Device created successfully!");

            emptyDeviceFields();
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong while creating the device!\n" + error.message);
            }
        }
    }

    const updateExistingDevice = async () => {
        if(selectedDeviceId === "-1") {
            alert("Select a Device before performing an update!");
            return;
        }
        if(address === "" || description === "" || mhec === "") {
            alert("Complete all fields in order to create an user!");
            return;
        }
        if(parseFloat(mhec) < 0) {
            alert("The energy consumption can not be negative!");
            return;
        }

        let user;
        for (var i = 0; i < deviceList.length; i += 1) {
            if(deviceList.at(i).deviceId === selectedDeviceId) {
                user = deviceList.at(i).user;
                break;
            }
        }

        try {
            if(userId === "-1") {
                await axios.put(DEVICES_HOST.backend_api + endpoint.device_params + selectedDeviceId, {
                    deviceId: selectedDeviceId,
                    address: address,
                    description: description,
                    maximumHourlyEnergyConsumption: parseFloat(mhec).toFixed(2),
                    user: null
                },
                {headers: {'Authorization': (bearerName + token)}});
            }
            else {
                await axios.put(DEVICES_HOST.backend_api + endpoint.device_params + selectedDeviceId, {
                    deviceId: selectedDeviceId,
                    address: address,
                    description: description,
                    maximumHourlyEnergyConsumption: parseFloat(mhec).toFixed(2),
                    user: {
                        userId: userId,
                        devices: (user === null? []:user.devices)
                    }
                },
                {headers: {'Authorization': (bearerName + token)}});
            }

            alert("Device updated successfully!");

            searchDeviceList();
            searchUserList();
            emptyDeviceFields();
            setSelectedDeviceId("-1");
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong while updating the device!\n" + error.message);
            }
        }
    }

    const deleteExistingDevice = async () => {
        if(selectedDeviceId === "-1") {
            alert("Select a Device before performing a delete!");
            return;
        }

        try {
            await axios.delete(DEVICES_HOST.backend_api + endpoint.device_params + selectedDeviceId, {headers: {'Authorization': (bearerName + token)}});

            alert("Device deleted successfully!");

            searchDeviceList();
            searchUserList();
            emptyDeviceFields();
            setSelectedDeviceId("-1");
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong while deleting the device!\n" + error.message);
            }
        }
    }

    const searchDeviceList = async () => {
        try {
            const deviceList = await axios.get(DEVICES_HOST.backend_api + endpoint.device_no_params, {headers: {'Authorization': (bearerName + token)}});
            if(deviceList.status === successResponse) {
                setDeviceList(deviceList.data);
            }
            else {
                alert("Something went wrong when loading devices!");
            }
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Something went wrong when loading devices!");
            }
        }
    }


    //MESSAGING FUNCTIONS
    const retrieveMessages = async (receiverId, senderId) => {
        try {
            const messagesList = await axios.get(MESSAGING_HOST.backend_api + endpoint.messaging2 + receiverId + '/' + senderId);
            if(messagesList.status === successResponse) {
                return messagesList.data;
            }
            else {
                return [];
            }
        }
        catch(error) {
            return [];
        }
    }

    const existUnreadMessages = (receivedMessages) => {
        if(receivedMessages.length > 0) {
            setHasUnreadMessages(true);
        }
        else {
            setHasUnreadMessages(false);
        }
    }

    const searchReceivedMessages = async (userId) => {
        try {
            const response = await axios.get(MESSAGING_HOST.backend_api + endpoint.messaging5 + userId);
            if(response.status === successResponse) {
                setMessagesReceived(response.data);
                existUnreadMessages(response.data);
            }
            else {
                setMessagesReceived([]);
            }
        }
        catch(error) {
            setMessagesReceived([]);
        }
    }

    const searchUsersWhoMessaged = (receivedMessages) => {
        let users = [];
        for(var i = 0; i < userMessagingList.length; i += 1) {
            for(var j = 0; j < receivedMessages.length; j += 1) {
                if(userMessagingList.at(i).userId === receivedMessages.at(j).senderId) {
                    users.push(userMessagingList.at(i));
                }
            }
        }

        setUsersWhoMessaged(users);
    }

    const removeReceivedMessages = (receiverId, senderId) => {
        const filteredMessages = messagesReceived.filter(message => !(message.receiverId === receiverId && message.senderId === senderId));
        setMessagesReceived(filteredMessages);
        existUnreadMessages(filteredMessages);
        searchUsersWhoMessaged(filteredMessages);
    }

    const markMessagesAsRead = async (receiverId, senderId) => {
        try {
            const response = await axios.get(MESSAGING_HOST.backend_api + endpoint.messaging3 + receiverId + '/' + senderId);
            if(response.status === successResponse) {
                console.log(response.data);
            }
            else {
                console.log('Messages could not be read!');
            }
            removeReceivedMessages(receiverId, senderId);
        }
        catch(error) {
            console.log('Messages could not be read or no messages received from ' + senderId);
        }
    }

    const searchUserMessagingList = async () => {
        try {
            const response = await axios.get(USERS_HOST.backend_api + endpoint.user_no_params, {headers: {'Authorization': (bearerName + token)}});
            if(response.status === successResponse) {
                setUserMessagingList(response.data);
            }
            else {
                setUserMessagingList([]);
                alert('Error! The chat is unavailable at the moment');
            }
        }
        catch(error) {
            setUserMessagingList([]);
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert('Error! The chat is unavailable at the moment');
            }
        }
    }

    const createStompClientMessaging = (userId) => {
        stompClientMessaging.onConnect = (frame) => {
            console.log('Messaging Websocket\n' + 'Connected succesfull: ' + frame);

            stompClientMessaging.subscribe(endpoint.websocketMessaging, async (message) => {
                const messageReceived = JSON.parse(message.body);
                const receiverId = messageReceived.receiverId;
                const senderId = messageReceived.senderId;
                const selectedUserId = (selectedUserRef.current === null) ? "-1" : selectedUserRef.current.userId;

                if((userId === receiverId && selectedUserId === senderId) || (selectedUserId === receiverId && userId === senderId)) {
                    setLoading(true);
                    if(receiverId === userId) {
                        await markMessagesAsRead(receiverId, senderId);
                    }
                    const messagesList = await retrieveMessages(receiverId, senderId);
                    setMessageHistory(messagesList);
                    await searchUserMessagingList();
                    setLoading(false);
                }
                else {
                    setMessagesReceived([...messagesReceived, messageReceived]);
                    setHasUnreadMessages(true);
                }
            });

            stompClientMessaging.subscribe(endpoint.websocketMessagingWrittingMessage, (message) => {
                const messageReceived = JSON.parse(message.body);
                const receiverId = messageReceived.receiverId;
                const senderId = messageReceived.senderId;
                const selectedUserId = (selectedUserRef.current === null) ? "-1" : selectedUserRef.current.userId;

                if(userId === receiverId && selectedUserId === senderId) {
                    setIsUserWritting(messageReceived.writting);
                }
            });

            setSendMessageFunction(() => (idSender, idReceiver, message) => {
                const timestamp = Date.now();
                stompClientMessaging.publish({
                    destination: endpoint.websocketMessagingSend,
                    body: JSON.stringify({
                        messageId: null,
                        senderId: idSender,
                        receiverId: idReceiver,
                        messageContent: message,
                        timestamp: timestamp,
                        messageRead: false
                    })
                });
            });

            setUserIsWrittingFunction(() => (idSender, idReceiver, isWritting) => {
                stompClientMessaging.publish({
                    destination: endpoint.websocketMessagingWrittingMessage,
                    body: JSON.stringify({
                        senderId: idSender,
                        receiverId: idReceiver,
                        writting: isWritting
                    })
                })
            });

            stompClientMessaging.onStompError = (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            };
        };
    }

    const searchUserWithEmail = () => {
        let found = false;
        let user;

        for(var i = 0; i < userMessagingList.length; i += 1) {
            if(userMessagingList.at(i).email === enteredEmail) {
                found = true;
                user = userMessagingList.at(i);
                break;
            }
        }

        if(found) {
            setSelectedUser(user);
        }
        else {
            alert('No user with email \"' + enteredEmail + '\" was found!');
        }
    }

    const findUsersHistory = async () => {
        try {
            const response = await axios.get(MESSAGING_HOST.backend_api + endpoint.messaging1 + user.userId);
            if(response.status === successResponse) {
                const userIds = await response.data;
                let users = [];

                for(var i =0; i < userIds.length; i += 1) {
                    for(var j = 0; j < userMessagingList.length; j += 1) {
                        if(userMessagingList.at(j).userId === userIds.at(i)) {
                            users.push(userMessagingList.at(j));
                        }
                    }
                }

                setChatedUserList(users);
            }
            else {
                setChatedUserList([]);
            }

        }
        catch(error) {
            setChatedUserList([]);
        }
    }


    //MODAL FUNCTIONS
    const handleOpenModal = async () => {
        setShowModal(true);
        setLoading(true);

        try {
            await findUsersHistory();
            searchUsersWhoMessaged(messagesReceived);
        }
        catch(error) {
            console.error("Error in opening modal:", error);
            alert("Some error occured while searching you conversation history!");
        }
        finally {
            setLoading(false);
        }
    };
    
    const handleCloseModal = () => {
        setShowModal(false);
        setSelectedUser(null);
    };

    const emailInputHandlerMessaging = (enteredEmailText) => {
        setEnteredEmail(enteredEmailText.target.value);
    }

    const messageWrittenInputHandler = (messageWritten) => {
        if(messageWritten.target.value === "") {
            userIsWrittingFunction(user.userId, selectedUser.userId, false);
        }
        else {
            userIsWrittingFunction(user.userId, selectedUser.userId, true);
        }
        setMessageWritten(messageWritten.target.value);
    }

    const handleUserClick = async (userSelected) => {
        setLoading(true);
        setSelectedUser(userSelected);
        await markMessagesAsRead(user.userId, userSelected.userId);
        const messages = await retrieveMessages(userSelected.userId, user.userId);
        setMessageHistory(messages);
        setLoading(false);
    };
    
    const handleBackToUserList = () => {
        setSelectedUser(null);
        findUsersHistory();
        setMessageHistory([]);
        searchUsersWhoMessaged(messagesReceived);
    };

    const sendMessage = () => {
        if(messageWritten === "") {
            return;
        }

        if(sendMessageFunction) {
            sendMessageFunction(user.userId, selectedUser.userId, messageWritten);
            setMessageWritten("");
            userIsWrittingFunction(user.userId, selectedUser.userId, false);
        }
        else {
            alert('Message could not be sent! No connection available');
        }
    }


    //USEEFFECT FUNCTIONS
    useEffect(() => {
        if(location.state === null) {
            navigate("/");
        }
        else {
            if(location.state.userId === null || location.state.email === null || location.state.token === null) {
                navigate("/");
            }
            else {
                setAdminEmail(location.state.email);
                searchUser(location.state.userId);
                searchUserMessagingList();
                searchReceivedMessages(location.state.userId);

                createStompClientMessaging(location.state.userId);
                if(!stompClientMessaging.active) {
                    stompClientMessaging.activate();
                }
            }
        }
    }, []);

    useEffect(() => {
        selectedUserRef.current = selectedUser;
    }, [selectedUser]);


    return (
        <div>
            {/* Logout and Chat Button */}
            <div className='AdminDivTopButtons'>
                <button className='AdminPageBackButton' onClick={logout}><b>Logout</b></button>
                <button className='AdminChatButton' onClick={handleOpenModal}>
                    {hasUnreadMessages && <span className='AdminNotificationBubble'></span>}
                    <b>Chat</b>
                </button>
                <ModalPage 
                    show={showModal} 
                    onClose={handleCloseModal} 
                    enteredEmail={enteredEmail} 
                    emailInputHandler={emailInputHandlerMessaging} 
                    searchUserWithEmail={searchUserWithEmail} 
                    userList={chatedUserList}
                    handleUserClick={handleUserClick}
                    selectedUser={selectedUser}
                    currentUser={user}
                    handleBackToUserList={handleBackToUserList}
                    messageHistory={messageHistory}
                    loading={loading}
                    messageWritten={messageWritten}
                    messageWrittenInputHandler={messageWrittenInputHandler}
                    sendMessage={sendMessage}
                    usersWhoMessaged={usersWhoMessaged}
                    isWritting={isUserWritting}
                    allUsersList={userMessagingList}
                />
            </div>

            {/* Main Options Buttons */}
            <div className='AdminPageCenterRow'>
                <button className='AdminPageOptionButton' onClick={changeShowUserTab}>Manage users</button>
                <button className='AdminPageOptionButton' onClick={changeShowDeviceTab}>Manage devices</button>
            </div>

            {/* Manage User Buttons */}
            <div className='AdminPageSecondOptionsWrapper'  style={{display: showUserTab ? "flex" : "none"}}>
                <div className='AdminPageSecondOptionsWindow'>
                    <button className='AdminPageOptionButton' onClick={handleCreateUser}>Create User</button>
                    <button className='AdminPageOptionButton' onClick={handleUpdateUser}>Edit User</button>
                    <button className='AdminPageOptionButton' onClick={handleDeleteUser}>Delete User</button>
                    <button className='AdminPageOptionButton' onClick={handleShowUser}>See all Users</button>
                </div>

            </div>
            
            {/* Manage Device Buttons */}
            <div className='AdminPageSecondOptionsWrapper' style={{display: showDeviceTab ? "flex" : "none"}}>
                <div className='AdminPageSecondOptionsWindow'>
                    <button className='AdminPageOptionButton' onClick={handleCreateDevice}>Create Device</button>
                    <button className='AdminPageOptionButton' onClick={handleUpdateDevice}>Edit Device</button>
                    <button className='AdminPageOptionButton' onClick={handleDeleteDevice}>Delete Device</button>
                    <button className='AdminPageOptionButton' onClick={handleShowDevice}>See all Device</button>
                </div>
            </div>
            
            {/* Create, Update and Delete for Users */}
            <div className='AdminPageFieldsWrapper' style={{display: createUser || updateUser || deleteUser ? "flex" : "none"}}>
                <div className='AdminPageFieldsWindow'>
                    <label  style={{display: (updateUser || deleteUser) ? "block" : "none", paddingLeft: '30px'}}>
                        <b>Id: </b>
                        <span>
                            <select className='AdminPageTextField' value={selectedUserId} onChange={fillUserFields}>
                                <option value={"-1"}>Select an User</option>
                                {userList.map((user) => (<option value={user.userId} >{user.email}</option>))}
                            </select>
                        </span>
                    </label>

                    <label>
                        <b>Email:</b> <span><input className='AdminPageTextField' style={{color: deleteUser ? 'red' : 'black'}} value={email} placeholder={deleteUser ? 'Email' : 'Enter an email'} disabled={deleteUser ? "disabled" : ""} onChange={emailInputHandler}></input></span>
                    </label>

                    <label>
                        <b>Name:</b> <span><input className='AdminPageTextField' style={{color: deleteUser ? 'red' : 'black'}} value={name} placeholder={deleteUser ? 'Name' : 'Enter a name'} disabled={deleteUser ? "disabled" : ""} onChange={nameInputHandler}></input></span>
                    </label>

                    <label style={{paddingRight: '30px'}}>
                        <b>Password:</b> <span><input className='AdminPageTextField' style={{color: deleteUser ? 'red' : 'black'}} value={password} placeholder={deleteUser ? 'Password' : 'Enter a password'} disabled={deleteUser ? "disabled" : ""} onChange={passwordInputHandler}></input></span>
                    </label>

                    <label style={{display: (createUser || updateUser) ? "block" : "none", paddingLeft: '20px'}}>
                        <b>Role: </b>
                            <span>
                                <select className='AdminPageTextField' value={role} onChange={roleInputHandler}>
                                    <option value={"Placeholder"}>Select a role</option>
                                    <option value={"admin"}>admin</option>
                                    <option value={"client"}>client</option>
                                </select>
                            </span>
                    </label>

                    <label style={{display: deleteUser ? "block" : "none"}}>
                        <b>Role: </b> <span><input className='AdminPageTextField' style={{color: 'red'}} value={role} placeholder='Role' disabled onChange={roleInputHandler}></input></span>
                    </label>

                    <button className='AdminPageOptionButton' style={{marginLeft: '70px'}} onClick={performAction}>
                        {createUser && <span>Create</span>} {updateUser && <span>Update</span>} {deleteUser && <span>Delete</span>} User
                    </button>
                </div>
            </div>

            {/* Get Users Table */}
            <div className='AdminPageTable' style={{display: showUser ? "flex" : "none"}}>
                <table border="1" cellPadding="10" cellSpacing="0" className='AdminPageTableFormat'>
                    <thead className='AdminPageTableHead'>
                        <tr>
                            <th className='AdminPageTableData'>Id</th>
                            <th className='AdminPageTableData'>Email</th>
                            <th className='AdminPageTableData'>Name</th>
                            <th className='AdminPageTableData'>Password</th>
                            <th className='AdminPageTableData'>Role</th>
                        </tr>
                    </thead>
                    <tbody>
                        {userList.map((user) => 
                            (<tr>
                                <td className='AdminPageTableData'>{user.userId}</td>
                                <td className='AdminPageTableData'>{user.email}</td>
                                <td className='AdminPageTableData'>{user.name}</td>
                                <td className='AdminPageTableData'>{user.password}</td>
                                <td className='AdminPageTableData'>{user.role}</td>
                            </tr>))}
                    </tbody>
                </table>
            </div>
            
            {/* Create, Update and Delete for Devices */}
            <div className='AdminPageFieldsWrapper' style={{display: createDevice || updateDevice || deleteDevice ? "flex" : "none"}}>
                <div className='AdminPageFieldsWindow'>
                    <label  style={{display: (updateDevice || deleteDevice) ? "block" : "none", paddingLeft: '30px'}}>
                        <b>Id: </b>
                        <span>
                            <select className='AdminPageTextField' value={selectedDeviceId} onChange={fillDeviceFields}>
                                <option value={"-1"}>Select a Device</option>
                                {deviceList.map((device) => (<option value={device.deviceId} >{device.address}</option>))}
                            </select>
                        </span>
                    </label>

                    <label style={{paddingRight: '10px'}}>
                        <b>Address:</b> <span><input className='AdminPageTextField' style={{color: deleteDevice ? 'red' : 'black'}} value={address} placeholder={deleteDevice ? 'Address' : 'Enter an address'} disabled={deleteDevice ? "disabled" : ""} onChange={addressInputHandler}></input></span>
                    </label>

                    <label style={{paddingRight: '35px'}}>
                        <b>Description:</b> <span><input className='AdminPageTextField' style={{color: deleteDevice ? 'red' : 'black'}} value={description} placeholder={deleteDevice ? 'Description' : 'Enter a description'} disabled={deleteDevice ? "disabled" : ""} onChange={descriptionInputHandler}></input></span>
                    </label>

                    <label style={{paddingRight: '90px'}}>
                        <b>Max hour en cons :</b> <span><input className='AdminPageTextField' type='number' style={{color: deleteDevice ? 'red' : 'black'}} value={mhec} placeholder={deleteDevice ? 'Max hour en cons' : 'Enter a mhec'} disabled={deleteDevice ? "disabled" : ""} onChange={mhecInputHandler}></input></span>
                    </label>

                    <label style={{display: (createDevice || updateDevice) ? "block" : "none"}}>
                        <b>User Id: </b>
                            <span>
                                <select className='AdminPageTextField' value={userId} onChange={e => setEnteredUserId(e.target.value)}>
                                    <option value={"-1"}>Select an User</option>
                                    {userList.map((user) => (<option value={user.userId} >{user.email}</option>))}
                                </select>
                            </span>
                    </label>

                    <label style={{display: deleteDevice ? "block" : "none"}}>
                        <b>User Id: </b> <span><input className='AdminPageTextField' style={{color: 'red'}} value={userId} placeholder='Role' disabled onChange={userIdInputHandler}></input></span>
                    </label>

                    <button className='AdminPageOptionButton' style={{marginLeft: '70px'}} onClick={performAction}>
                        {createDevice && <span>Create</span>} {updateDevice && <span>Update</span>} {deleteDevice && <span>Delete</span>} Device
                    </button>
                </div>
            </div>

            {/* Get Devices Table */}
            <div className='AdminPageTable' style={{display: showDevice ? "flex" : "none"}}>
                <table border="1" cellPadding="10" cellSpacing="0" className='AdminPageTableFormat'>
                    <thead className='AdminPageTableHead'>
                        <tr>
                            <th className='AdminPageTableData'>Id</th>
                            <th className='AdminPageTableData'>Address</th>
                            <th className='AdminPageTableData'>Description</th>
                            <th className='AdminPageTableData'>Maximum hourly energy consumption</th>
                            <th className='AdminPageTableData'>User Email</th>
                        </tr>
                    </thead>
                    <tbody>
                        {deviceList.map((device) => 
                            (<tr>
                                <td className='AdminPageTableData'>{device.deviceId}</td>
                                <td className='AdminPageTableData'>{device.address}</td>
                                <td className='AdminPageTableData'>{device.description}</td>
                                <td className='AdminPageTableData'>{device.maximumHourlyEnergyConsumption}</td>
                                <td className='AdminPageTableData'>{(device.user === null ? "no user":device.user.email)}</td>
                            </tr>))}
                    </tbody>
                </table>
            </div>
        </div>
        
    );
}

export default AdminPage;