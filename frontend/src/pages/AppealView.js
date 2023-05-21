import { Link, useNavigate, useParams } from 'react-router-dom';
import '../styles/AppealView.css';
import { renewToken, formatDateTime } from './utils/Utils';
import { useEffect, useRef, useState } from 'react';


export default function AppealView() {
    const params = useParams();
    const navigate = useNavigate();
    const statusRef = useRef();
    const empRef = useRef();
    const employees = useRef([]);
    const categories = useRef([]);
    const category = useRef();

    const [appealData, setAppealData] = useState({status: { nextStatuses: [] }, attachments : [], employee: { id : null }});

    function getAppeal(id, lang) {
        fetch(process.env.REACT_APP_API_HOST + '/appeal?id=' + id + '&lang=' + lang, {
                method: 'GET',
                credentials: "include"
            }
        ).then((response) => {
            if(response.ok) {
                return response.json();
            } else {
                renewToken(navigate, '/workspace/' + id);
            }
        }).then((data) => {
            if(data != null)
                setAppealData(data)
        })
        .catch((error) => { 
            navigate('/workspace');
        })
    }

    function getEmployees() {
        fetch(process.env.REACT_APP_API_HOST + '/employees', {
            method: 'GET',
            credentials: "include"
        }).then((response) => response.json())
        .then((data) => employees.current = data);
    }

    function getCategories() {
        fetch(process.env.REACT_APP_API_HOST + '/categories', {
            method: 'GET',
            credentials: "include"
        }).then((response) => response.json())
        .then((data) => categories.current = data);
    }

    function handleSave() {
        let data = {};
        if(category.current.value > 0)
            data['category_id'] = category.current.value;
        if(empRef.current.value > 0)
            data['employee_id'] = empRef.current.value;
        data['status_id'] = statusRef.current.value;

        fetch(process.env.REACT_APP_API_HOST + '/appeal/' + params.id, {
            method: 'PATCH',
            credentials: "include",
            headers: new Headers({'content-type': 'text/plain'}),
            body: JSON.stringify(data)
        }).then(() => window.location.reload());
    }
    
    useEffect(() => { 
        getAppeal(params.id, 'RU');
        getEmployees();
        getCategories();
    }, []);

    return (
        <>
            <div className='container'>
                <h1>Обращение №{appealData.id}</h1>
                <div className='header'>
                    <table>
                        <tbody>
                            <tr>
                                <td>Заголовок: {appealData.title}</td>
                            </tr>
                            <tr>
                                <td>Имя: {appealData.firstName}</td>
                            </tr>
                            <tr>
                                <td>Фамилия: {appealData.lastName}</td>
                                </tr>
                            <tr>
                                <td>Отчество: {appealData.middleName}</td>
                            </tr>
                            <tr>
                                <td>Адрес электронной почты: {appealData.email}</td>
                            </tr>
                            <tr>
                                <td>Дата создания: {formatDateTime(appealData.createDate)}</td>
                            </tr>
                            <tr>
                                <td>Срок выполнения: {formatDateTime(appealData.deadLine)}</td>
                            </tr>
                            <tr>
                                <td>
                                    Категория: <select ref={category}>
                                        <option value="0">Выберите категорию</option>
                                        {
                                            categories.current.map((val) => {
                                                return (
                                                    <option key={val.code} value={val.code} selected={appealData.category != null && appealData.category.code == val.code}>
                                                        {val.name}
                                                    </option>
                                                );
                                            })
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Ответственный сотрудник: <select ref={empRef}>
                                        <option value="0">Выберите сотрудника</option>
                                        {
                                            employees.current.map((val) => {
                                                return (
                                                    <option key={val.id} value={val.id} selected={appealData.employee != null && appealData.employee.id == val.id}>
                                                        {val.lastName + ' ' + val.firstName + ' ' + val.middleName}
                                                    </option>
                                                );
                                            })
                                        }
                                    </select>
                                </td>
                            </tr>
                            
                            <tr>
                                <td>
                                    Статус: <select ref={statusRef} defaultValue={appealData.status.code}>
                                        <option value={appealData.status.code}>{appealData.status.name}</option>
                                        {
                                            appealData.status.nextStatuses.map((val) => {
                                                return <option key={val.code} value={val.code}>{val.name}</option>
                                            })
                                        }
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                <p>Приложения: </p>
                                {
                                    appealData.attachments.map((val) => {
                                        return (
                                            <div className='attachments' key={val.name} >
                                                <a href={process.env.REACT_APP_API_HOST + '/appeal/' + params.id + '/attachment/' + val.name}>
                                                    {val.name}
                                                </a>
                                            </div>
                                        )
                                    })
                                }
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <textarea value={appealData.textBody} disabled style={{width : '600px', height : '200px'}}>
                                    </textarea>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <button onClick={handleSave}>Сохранить обращение</button>
                    <button style={{marginLeft: '10px'}}>
                        <Link to={'/workspace'}>Назад</Link>
                    </button>
                </div>
            </div>
        </>
    );
}