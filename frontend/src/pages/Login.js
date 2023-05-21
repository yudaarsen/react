import { useRef, useState } from 'react';
import '../styles/Login.css';
import { validateEmail } from './utils/Utils';
import Modal from './utils/Modal';
import { useLocation, useNavigate } from 'react-router-dom';
import sjcl from 'sjcl';


function validateInput(input) {
    let messages = [];
    if(!validateEmail(input.email))
        messages.push('- Адрес электронной почты');
    if(input.password.length == 0)
        messages.push('- Пароль');
    return messages.length == 0 ? null : (
        <>
            Заполните поля:<br/><br/>
            <>
                {messages.map((val) => <>{val}<br/></>)}
            </>
        </>
    );
}

export default function Login() {
    const email = useRef();
    const password = useRef();
    const messages = useRef();
    const location = useLocation();

    const navigate = useNavigate();

    const [modalActive, setModalActive] = useState(false);

    let destination = location.state;
    if(destination == null)
        destination = '/workspace';
    
    destination = destination.destination;

    function postLogin(data) {
        fetch(process.env.REACT_APP_API_HOST + '/access', {
            method: 'POST',
            credentials: "include",
            headers: new Headers({'content-type': 'application/json'}),
            body: JSON.stringify(data)
        })
        .then((response) => response.json())
        .then((response) => {
            localStorage.setItem('refreshToken', response['refresh_token']);
            navigate(destination);
        }).catch(() => {
            messages.current = 'Ошибка аутентификации!';
            setModalActive(true);
        });
    }

    function handleButton() {
        var formData = {
            email : email.current.value,
            password : sjcl.codec.hex.fromBits(sjcl.hash.sha256.hash(password.current.value))
        };

        messages.current = validateInput(formData);
        if(messages.current != null) {
            setModalActive(true);
        } else {
            postLogin(formData);
        }
    }


    return (
        <>
            <Modal isOpen={ modalActive } onClose={() => {
                setModalActive(false);
                messages.current = null;
            }}>
                { messages.current }
            </Modal>
            <div className="container">
                <div className='pane'>
                    <div className='cred'>
                        <input ref={email} className='login_el' type="text" placeholder="Адрес электронной почты" />
                        <input ref={password} className='login_el' type="password" placeholder="Пароль" />
                        <button className='submitButton' onClick={handleButton}>Вход в систему</button>
                    </div>
                </div>
            </div>
        </>
    );
}