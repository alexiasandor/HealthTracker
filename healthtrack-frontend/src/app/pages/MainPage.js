import { useNavigate } from "react-router-dom";
import '../styles/MainPage.css';

function MainPage() {
    const navigate = useNavigate();

    return (
        <div className='MainPageCenter'>
            <button className='MainPageLoginButton' onClick={() => navigate('/Login')}><b>LOGIN</b></button>
            <button className='MainPageRegisterButton' onClick={() => navigate('/Register')}><b>REGISTER</b></button>
        </div>
        
    );
}

export default MainPage;