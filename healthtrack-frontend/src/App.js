import './App.css';
import MainPage from "./app/pages/MainPage";
import LoginPage from "./app/pages/LoginPage";
import AdminPage from "./app/pages/AdminPage";
import ClientPage from "./app/pages/ClientPage";
import React from "react";
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
function App() {
return (
      <Router>
        <Routes>
          <Route path='/' Component={MainPage}/>
          <Route path='/Login' Component={LoginPage}/>
          <Route path='/Admin' Component={AdminPage}/>
          <Route path='/Client' Component={ClientPage}/>
        </Routes>
      </Router>
  );
}

export default App;
