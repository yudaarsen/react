import { useRef, useState } from "react";
import '../styles/Appeal.css';
import Modal from "./utils/Modal";
import { validateEmail } from './utils/Utils';

function validateInput(input) {
    var messages = []
    if(input['title'].length == 0)
        messages.push('- Заголовок обращения');
    if(input['text_body'].length == 0) 
        messages.push('- Текст обращения');
    if(input['first_name'].length == 0)
        messages.push('- Имя');
    if(input['last_name'].length == 0)
        messages.push('- Фамилия');
    if(!validateEmail(input['email']))
        messages.push('- Адрес электронной почты');
    if(messages.length > 0) {
        return (
            <>
                Необходимо правильно заполнить поля:<br/><br/>
                <>
                    {messages.map(val => (<>{val}<br/></>))}
                </>
            </>
        );
    }
    return null;
}

export default function Appeal() {
    const title = useRef();
    const textBody = useRef();
    const firstName = useRef();
    const lastName = useRef();
    const midName = useRef();
    const email = useRef();

    const [modalActive, setModalActive] = useState(false);
    const messages = useRef();

    function postAppeal(appeal) {
        fetch(process.env.REACT_APP_API_HOST + '/appeal', {
            method: 'POST',
            body: JSON.stringify(appeal),
        }).then(response => response.json())
        .then(data => uploadFiles(data.appeal_id))
        .catch(() => {
            messages.current = <>Ошибка при создании обращения!</>;
            setModalActive(true);
        });
    }
    
    function uploadFiles(appealId) {
        var data = new FormData();
        var el = document.getElementById('file');
        for(var file of el.files) {
            data.append('files', file);
        }
        fetch(process.env.REACT_APP_API_HOST + '/appeal/' + appealId + '/attachment', {
            method: 'POST',
            body: data
        }).then(response => {
            title.current.value = "";
            textBody.current.value = "";
            messages.current = <>Обращение успешно создано!<br/><br/>№{appealId}</>;
            setModalActive(true);
        }).catch(() => {
            messages.current = <>Произошла ошибка при загрузке приложений!<br/><br/>Обращение №{appealId}</>;
            setModalActive(true);
        });
    }

    function handleButton() {
        const appeal = {
            "title" : title.current.value,
            "text_body" : textBody.current.value,
            "first_name" : firstName.current.value,
            "last_name" : lastName.current.value,
            "middle_name" : midName.current.value,
            "email" : email.current.value
        }

        messages.current = validateInput(appeal);

        if(messages.current != null) {
            setModalActive(true);
        } else {
            postAppeal(appeal);
        }
    }

    return (
        <div>
            <Modal isOpen={ modalActive } onClose={() => { 
                setModalActive(false);
                messages.current = null;
             }}>
                { messages.current }
            </Modal>
            <div className="container">
                <div className="pane">
                    <input className="el" type="email" placeholder="Адрес электронной почты *" ref={ email } required />
                    <input className="el" type="text" placeholder="Имя *" ref={ firstName } required />
                    <input className="el" type="text" placeholder="Фамилия *" ref={ lastName } required />
                    <input className="el" type="text" placeholder="Отчество" ref={ midName } />
                    <input className="el" type="text" placeholder="Заголовок обращения *" ref={ title } required />
                    <textarea className="el" maxLength="1000" type="text" placeholder="Введите текст обращения *" ref={ textBody } required />
                    <input id="file" className="file el" placeholder="Добавить приложение" type="file" multiple />
                    <button onClick={ handleButton } className="submitButton">Отправить</button>
                </div>
            </div>
        </div>
    );
}