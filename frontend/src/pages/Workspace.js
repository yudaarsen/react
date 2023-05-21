import { useEffect, useRef, useState } from "react";
import { formatDateTime, renewToken } from "./utils/Utils";
import { Link, useNavigate } from "react-router-dom";


export default function Workspace() {
    const [appeals, setAppels] = useState([]);
    const [page, setPage] = useState(0);
    const navigate = useNavigate();

    function loadAppeals(start) {
        fetch(process.env.REACT_APP_API_HOST + '/appeals?start=' + start, {
            method: 'GET',
            credentials: 'include'
        }).then((response) => response.json())
        .then((data) => setAppels(data))
        .catch(() => renewToken(navigate, '/workspace'));
    }

    useEffect(() => {
        loadAppeals(page);
    }, []);
    
    function prevPage() {
        if(page >= 10) {
            setPage(page - 10);
            loadAppeals(page - 10);
        }
    }

    function nextPage() {
        if(appeals != null && appeals.length > 0) {
            setPage(page + 10);
            loadAppeals(page + 10);
        }
    }

    return (
        <div style={{paddingTop: '50px', width: '100%'}}>
            <h1>Список обращений</h1>
            <table width="100%" style={{padding: '5px'}} border="1">
                <tbody>
                    <tr>
                        <th>Номер</th>
                        <th>Заголовок</th>
                        <th>Статус</th>
                        <th>Клиент</th>
                        <th>Дата создания</th>
                        <th>Срок выполнения</th>
                    </tr>
                    {
                        appeals.map((val) => {
                            return (
                                <tr key={val.id}>
                                    <td>{val.id}</td>
                                    <Link to={'/workspace/' + val.id}>
                                        <td>{val.title}</td>
                                    </Link>
                                    <td>{val.status.name}</td>
                                    <td>{val.lastName + ' ' + val.firstName + ' ' + val.middleName}</td>
                                    <td>{formatDateTime(val.createDate)}</td>
                                    <td>{formatDateTime(val.deadLine)}</td>
                                </tr>
                            )
                        })
                    }
                </tbody>
            </table>
            <button onClick={prevPage}>Предыдущая страница</button>
            <button onClick={nextPage} style={{marginLeft: '10px'}}>Следующая страница</button>
        </div>
    );
}