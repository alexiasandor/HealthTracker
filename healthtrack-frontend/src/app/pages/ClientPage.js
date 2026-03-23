import '../styles/ClientPage.css';
import React, {useState, useEffect, useRef} from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import ModalPage from './ModalPage';

import {USERS_HOST, DEVICES_HOST, MONITORING_HOST, WEBSOCKET_MONITORING_HOST, MESSAGING_HOST, WEBSOCKET_MESSAGING_HOST, BROKER_MESSAGING_HOST} from "../assets/hosts";


function ClientPage() {
    //ENDPOINTS FOR MICROSERVICES
    const endpoint = {
        user1: '/user/',
        user2 : '/user',
        device: '/device',
        monitoring: '/consumption/',
        messaging1: '/conversationOf/',
        messaging2: '/conversation/',
        messaging3: '/messagesRead/',
        messaging4: '/message/',
        messaging5: '/unreadMessages/',
        websocketMonitoring: '/topic/notify',
        websocketMessaging: '/topic/sendMessage',
        websocketMessagingSend: '/frontend/messageSent',
        websocketMessagingWrittingMessage: '/topic/isWritting'
    };


    //CHART DATA
    const [charData, setCharData] = useState([
        {hour: 0, consumption: 0}
    ]);


    //WEBSOCKET VARIABLES
    //MONITORING
    const socketMonitoring = new SockJS(WEBSOCKET_MONITORING_HOST.backend_api);
    const stompClientMonitoring = new Client({
        webSocketFactory: () => socketMonitoring,
        reconnectDelay: 5000
    });

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
    //USER RELATED
    const [user, setUser] = useState([]);
    const [deviceList, setDeviceList] = useState([]);

    //MONITORING RELATED
    const [seeChart, setSeeChart] = useState(false);
    const [displayChart, setDisplayChart] = useState(false);
    const [selectedDeviceId, setSelectedDeviceId] = useState("-1");
    const [selectedDate, setSelectedDate] = useState("");

    //MESSAGING RELATED
    const [messageHistory, setMessageHistory] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [enteredEmail, setEnteredEmail] = useState("");
    const [userList, setUserList] = useState([]);
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


    //USER AND MONITORING RELATED FUNCTIONS
    const logout = () => {
        stompClientMonitoring.deactivate();
        stompClientMessaging.deactivate();
        navigate('/');
    }

    const retrieveUserDevicesList = async (userId) => {
        try {
            const userDeviceList = await axios.get(DEVICES_HOST.backend_api + endpoint.device + endpoint.user1 + userId, {headers: {'Authorization': (bearerName + token)}});
            if(userDeviceList.status === successResponse) {
                return userDeviceList.data
            }
            else {
                return [];
            }
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            return [];
        }
    }

    const createStompClientMonitoring = (userId) => {
        stompClientMonitoring.onConnect = (frame) => {
            console.log('Monitoring Websocket\n' + 'Connected succesfull: ' + frame);

            stompClientMonitoring.subscribe(endpoint.websocketMonitoring, async (message) => {
                const messageReceived = message.body;
                const deviceIdBegin = messageReceived.indexOf("Id:");
                const deviceIdWithExceededConsumption = messageReceived.substring((deviceIdBegin + 3));
                let found = false;

                const userDevicesList = await retrieveUserDevicesList(userId);
                
                for(var i = 0; i < userDevicesList.length; i += 1) {
                    if(userDevicesList.at(i).deviceId === deviceIdWithExceededConsumption) {
                        found = true;
                        break;
                    }
                }

                if(found) {
                    const alertMessage = messageReceived.slice(0, deviceIdBegin);
                    alert(alertMessage);
                    found = false;
                }
            });
        };

        stompClientMonitoring.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };
    }

    const searchUser = async (userId) => {
        try {
            const userObj = await axios.get(USERS_HOST.backend_api + endpoint.user1 + userId, {headers: {'Authorization': (bearerName + token)}});
            if(userObj.status === successResponse) {
                setUser(userObj.data);
            }
            else {
                alert("Could not load the resources needed! Redirecting back...");
                navigate("/Login");
            }
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
            }
            else {
                alert("Could not load the resources needed! Redirecting back...");
            }
            navigate("/Login");
        }
    }

    const searchDevicesByUserId = async (userId) => {
        try {
            const userDeviceList = await axios.get(DEVICES_HOST.backend_api + endpoint.device + endpoint.user1 + userId, {headers: {'Authorization': (bearerName + token)}});
            if(userDeviceList.status === successResponse) {
                setDeviceList(userDeviceList.data);
            }
            else {
                alert("Could not load the devices! Try reloading the page.");
            }
        } catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Could not load the devices! Try reloading the page.");
            }
        }
    }

    const seeChartOrTable = () => {
        setSeeChart(!seeChart);
        setDisplayChart(false);
    }

    const showChart = async () => {
        if(selectedDate === "" || selectedDeviceId === "-1") {
            setDisplayChart(false);
            alert("Fill all the fields in order to see the chart!!!");
            return;
        }
        
        try {
            const consumptionList = await axios.get(MONITORING_HOST.backend_api + endpoint.monitoring + selectedDeviceId + '/' + selectedDate, {headers: {'Authorization': (bearerName + token)}});
            if(consumptionList.status === successResponse) {
                const hourlyConsumptions = consumptionList.data;
                
                const formattedData = hourlyConsumptions.map((value, index) => ({
                    hour: index,
                    consumption: parseFloat(value).toFixed(2)
                }));
    
                setCharData(formattedData);
                setDisplayChart(true);
            }
            else {
                alert("Could not retrive data for the chart!");
            }
        }
        catch(error) {
            if(error.status === unauthorizedResponse) {
                alert('Your session has expired. You will be logged out.');
                navigate('/');
            }
            else {
                alert("Could not retrive data for the chart!");
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
        for(var i = 0; i < userList.length; i += 1) {
            for(var j = 0; j < receivedMessages.length; j += 1) {
                if(userList.at(i).userId === receivedMessages.at(j).senderId) {
                    users.push(userList.at(i));
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

    const searchUserList = async () => {
        try {
            const response = await axios.get(USERS_HOST.backend_api + endpoint.user2, {headers: {'Authorization': (bearerName + token)}});
            if(response.status === successResponse) {
                setUserList(response.data);
            }
            else {
                setUserList([]);
                alert('Error! The chat is unavailable at the moment');
            }
        }
        catch(error) {
            setUserList([]);
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
                if(message.body === null) {
                    alert('Your session has expired. You will be logged out.');
                    navigate('/');
                }
                else {
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
                        await searchUserList();
                        setLoading(false);
                    }
                    else {
                        setMessagesReceived([...messagesReceived, messageReceived]);
                        setHasUnreadMessages(true);
                    }
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

        for(var i = 0; i < userList.length; i += 1) {
            if(userList.at(i).email === enteredEmail) {
                found = true;
                user = userList.at(i);
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
                    for(var j = 0; j < userList.length; j += 1) {
                        if(userList.at(j).userId === userIds.at(i)) {
                            users.push(userList.at(j));
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

    const emailInputHandler = (enteredEmailText) => {
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
            if(location.state.userId === null || location.state.token === null) {
                navigate("/");
            }
            else {
                searchUser(location.state.userId);
                searchDevicesByUserId(location.state.userId);
                searchUserList();
                searchReceivedMessages(location.state.userId);
    
                createStompClientMonitoring(location.state.userId);
                createStompClientMessaging(location.state.userId);
    
                if(!stompClientMonitoring.active) {
                    stompClientMonitoring.activate();
                }
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
            <div className='ClientDivTopButtons'>
                <button className='ClientPageBackButton' onClick={logout}><b>Logout</b></button>
                <button className='ClientChatButton' onClick={handleOpenModal}>
                    {hasUnreadMessages && <span className='ClientNotificationBubble'></span>}
                    <b>Chat</b>
                </button>
                <ModalPage 
                    show={showModal} 
                    onClose={handleCloseModal} 
                    enteredEmail={enteredEmail} 
                    emailInputHandler={emailInputHandler} 
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
                    allUsersList={[]}
                />
            </div>

            {/* Page header 1 */}
            <div className='ClientPageHeader1Wrapper'>
                <header className='ClientPageHeader1Window'><b>Hello, {user.name}!</b></header>
            </div>

            {/* Page header 2 */}
            <div className='ClientPageHeader2Wrapper'>
                <header className='ClientPageHeader2Window'><b className='ClientPageBoldHeaderText'>Down bellow are your devices and their maximum consumption</b></header>
            </div>

            {/* Button for chartic */}
            <div className='ClientCenterChartDiv'>
                <button className='ClientChartButton' onClick={seeChartOrTable}>{displayChart ? ('See table') : ('See chart')}</button>
            </div>

            <div>
            <div className='ClientPageCenterChartAndTable'>
                {/* Get Devices Table of an User*/}
                <div className='ClientPageTable' style={{display: seeChart ? "none" : "flex"}}>
                    <table border="1" cellPadding="10" cellSpacing="0" className='ClientPageTableFormat'>
                        <thead className='ClientPageTableHead'>
                            <tr>
                                <th className='ClientPageTableData'>Id</th>
                                <th className='ClientPageTableData'>Address</th>
                                <th className='ClientPageTableData'>Description</th>
                                <th className='ClientPageTableData'>Maximum hourly energy consumption</th>
                            </tr>
                        </thead>
                        <tbody>
                            {deviceList.map((device) => 
                                (<tr>
                                    <td className='ClientPageTableData'>{device.deviceId}</td>
                                    <td className='ClientPageTableData'>{device.address}</td>
                                    <td className='ClientPageTableData'>{device.description}</td>
                                    <td className='ClientPageTableData'>{device.maximumHourlyEnergyConsumption}</td>
                                </tr>))}
                        </tbody>
                    </table>
                </div>

                {/* See the chart*/}
                <div className='ClientPageChartCenterElements' style={{display: seeChart ? "flex" : "none"}}>
                    <div>
                        <select className='ClientPageTextField' value={selectedDeviceId} onChange={(e) => {setSelectedDeviceId(e.target.value)}}>
                            <option value={"-1"}>Select a Device</option>
                            {deviceList.map((device) => (<option value={device.deviceId} >{device.deviceId}</option>))}
                        </select>

                        <input className='ClientPageDateField' type="date" onChange={(e) => {setSelectedDate(e.target.value)}}/>

                        <button className='ClientSeeConsumptionButton' onClick={showChart}>See consumption</button>
                    </div>

                    
                </div>
            </div>
            </div>
                {
                    seeChart && displayChart &&
                    <ResponsiveContainer width="95%" height={400}>
                        <LineChart data={charData}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="hour"  tickFormatter={(tick) => (tick < 10 ? `0${tick}` : tick)} tick={{ fill: '#000' }}/>
                            <YAxis tick={{ fill: '#000' }}/>
                            <Tooltip />
                            <Legend />
                            <Line type="monotone" dataKey="consumption" stroke="#000" activeDot={{ r: 8 }} />
                        </LineChart>
                    </ResponsiveContainer>
                }
        </div>
    );
}

export default ClientPage;