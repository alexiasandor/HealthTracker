import React, { useState } from "react";
import '../styles/LoginPage.css';
import { useNavigate } from "react-router-dom";
import axios from "axios";

import {USERS_HOST} from "../assets/hosts";

function LoginPage() {
    /********************************************************************* endpoints *****************************************************************************************************/
    const endpoint = {
        user: '/user/',
        token_gen: 'token_generation/'
    };
    
    
    /******************************************************************** NAVIGATION AND VARIABLES ***************************************************************************/
    const navigate = useNavigate();
    const successResponse = 200;
    const notFoundResponse = 404;
    const serverErrorResponse = 500;
    const clientRole = "client";
    const adminRole = "admin";


    /********************************************************************* LOGIN FIELDS ****************************************************************************************************************/
    const [email, setEnteredEmail] = useState("");
    const [password, setEnteredPassword] = useState("");


    /******************************************************************** HANDLERS FOR LOGIN FIELDS ************************************************************************************************************/
    function emailInputHandler(enteredEmailText) {
        setEnteredEmail(enteredEmailText.target.value);
    }

    function passwordInputHandler(enteredPasswordText) {
        setEnteredPassword(enteredPasswordText.target.value);
    }


    /**************************************************************LOGIN FUNCTION ******************************************************************************************/
    const login = async () => {
        if(email === "" || password === "") {
            alert("Wrong email or password!!!");
        }
        else {
            try {
                const user = await axios.get(USERS_HOST.backend_api + endpoint.user + email + "/" + password);
                if(user.status === successResponse) {
                    const userRole = user.data.role;
    
                    const userId = user.data.userId;
                    const token = await axios.post(USERS_HOST.backend_api + endpoint.user + endpoint.token_gen + userId, {});
                    if(token.status === successResponse) {
                        if(userRole === clientRole) {
                            navigate("/Client", {state: {userId: userId, token: token.data}});
                        }
                        else {
                            if(userRole === adminRole) {
                                navigate("/Admin", {state: {email: email, userId: userId, token: token.data}});
                            }
                        }
                    }
                    else {
                        alert("The session could not be validated!!!");
                    }
                }
                else {
                    alert("Wrong email or password!!!");
                }
            } catch(error) {
                if(error.status === notFoundResponse) {
                    alert("Wrong email or password!!!");
                }
                else {
                    if(error.status === serverErrorResponse) {
                        alert("The session could not be validated!!!");
                    }
                    else {
                        alert("Something went wrong while trying to login...");
                    }
                }   
            }
        }
    }


    return (
        <div>
            <button className='LoginPageBackButton' onClick={() => navigate('/')}><b>Back</b></button>

            <div className='LoginPageCenter'>
                <label style={{paddingRight: '40px'}}>
                    <b>Email:</b> <span><input className='LoginPageTextField' placeholder='Enter your email' onChange={emailInputHandler}></input></span>
                </label >
                <label style={{paddingRight: '70px'}}>
                    <b>Password:</b> <span><input className='LoginPageTextField' placeholder='Enter your password' type="password" onChange={passwordInputHandler}></input></span>
                </label>

                <button className='LoginPageLoginButton' onClick={login}>LOGIN</button>
            </div>
        </div>
    );
}

export default LoginPage;