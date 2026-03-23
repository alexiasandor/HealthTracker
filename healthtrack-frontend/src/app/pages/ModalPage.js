import React from "react";
import '../styles/ModalPage.css'

function ModalPage({ 
    show, onClose, enteredEmail, emailInputHandler, searchUserWithEmail, userList, allUsersList, handleUserClick, 
    selectedUser, currentUser, handleBackToUserList, messageHistory, loading, messageWritten, messageWrittenInputHandler, 
    sendMessage, usersWhoMessaged, isWritting}) {

        if (!show) {
            return null;
        }
        
          return (
                <div className="ModalOverlay">
                    <div className="ModalContent">
                        {selectedUser ? (
                                <div className="ModalMessageHistoryContainer">
                                    <button className="ModalBackToUserListButton" onClick={handleBackToUserList}>Back</button>
                                    <h3>Messages with {selectedUser.name} {selectedUser.role === 'admin' ? ('(Admin)') : ('')} {selectedUser.userId === currentUser.userId ? '(Me)' : ''}</h3>
                                    <div className="ModalMessageHistory">
                                        {messageHistory.map((message) => (
                                            <div 
                                                key={message.messageId}
                                                className={`ModalMessageItem ${message.senderId === currentUser.userId ? 'ModalMessageRight' : 'ModalMessageLeft'}`}
                                            >
                                                <p className='ModalMessageText'>{message.messageContent}</p>

                                                <div className="ModalMessageStatus">
                                                    {message.senderId === currentUser.userId ? (
                                                            message.messageRead ? (
                                                                <img src="MessageSeenIcon.png" alt="Read" />
                                                            ) : (
                                                                <img src="MessageNotSeenIcon.png" alt="Unread" />
                                                            )
                                                        ) : null
                                                    }
                                                </div>
                                            </div>
                                        ))}
                                    </div>

                                    <div className="ModalUserWrittingMessage">
                                        {isWritting ? (
                                                <div>
                                                    <img src="UserIsWrittingAMessage.png" alt="Typing..."/>
                                                </div>
                                            ) : <div></div> 
                                        }
                                    </div>

                                    <div className="ModalMessageInputContainer">
                                        <input
                                            type="text"
                                            value={messageWritten}
                                            onChange={messageWrittenInputHandler}
                                            placeholder="Type your message"
                                            className="ModalMessageInputField"
                                        />
                                        <button className="ModalSendMessageButton" onClick={sendMessage}>
                                            Send
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                loading ? (
                                    <p>Loading...</p>
                                    ) :
                                        <>
                                        {
                                            currentUser.role === 'admin' ? (
                                                <div className="ModalHeader">
                                                    <select className="ModalSearchField" onChange={emailInputHandler}>
                                                        <option value="">Select an user</option>
                                                        {allUsersList.map((user) => (
                                                            <option key={user.userId} value={user.email}>
                                                                {user.email}
                                                            </option>
                                                        ))}
                                                    </select>
                                                    <button className='ModalSearchButton' onClick={searchUserWithEmail}>Search</button>
                                                </div>
                                            ) : (
                                                <div className="ModalHeader">
                                                    <input className='ModalSearchField' type="text" onChange={emailInputHandler} value={enteredEmail} placeholder="Enter the user email you want to speak with" />
                                                    <button className='ModalSearchButton' onClick={searchUserWithEmail}>Search</button>
                                                </div>
                                            )
                                        }
                                            

                                            {
                                                userList.length === 0 ? (
                                                    <p>You didn't speak with someone yet! Start a conversation by searching their email</p>
                                                ) :
                                                    <div className="ModalUserListContainer">
                                                        {userList.map((user) => (
                                                            <div key={user.userId} className="ModalUserItem" onClick={() => handleUserClick(user)}>
                                                            <p style={usersWhoMessaged.includes(user) ? {'color':'red'} : {'color':'black'}}>{user.name}<b>{user.role === 'admin' ? ' *Admin*' : ""}</b>{user.userId === currentUser.userId ? '(Me)' : ''}</p>
                                                            <p style={usersWhoMessaged.includes(user) ? {'color':'red'} : {'color':'black'}}>{user.email}</p>
                                                            </div>
                                                        ))}
                                                    </div>
                                            }
                                        </>
                            )
                        }
                        <button className="ModalCloseButton" onClick={onClose}>
                            Close
                        </button>
                    </div>
                </div>
            );
}

export default ModalPage;