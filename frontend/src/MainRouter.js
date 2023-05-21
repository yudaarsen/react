import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Appeal from "./pages/Appeal";
import Workspace from "./pages/Workspace";
import Login from "./pages/Login";
import AppealView from "./pages/AppealView";

export default function MainRouter() {
    return (
        <Router>
            <Routes>
                <Route path='/' element={ <Appeal /> } />
                <Route path='/workspace' element={ <Workspace /> } />
                <Route path='/login' element={ <Login /> } />
                <Route exact path='/workspace/:id' element={ <AppealView /> } />
            </Routes>
        </Router>
    );
}