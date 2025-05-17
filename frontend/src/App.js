import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import UploadPage from './pages/UploadPage';
import UploadedPage from './pages/UploadedPage';
import AnonymizedPage from './pages/AnonymizedPage';
import LinkTablePage from './pages/LinkTablePage';
import LinkDetailsPage from "./pages/LinkDetailsPage";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/upload" element={<UploadPage />} />
                <Route path="/uploaded" element={<UploadedPage />} />
                <Route path="/anonymized" element={<AnonymizedPage />} />
                <Route path="/links" element={<LinkTablePage />} />
                <Route path="/link-details/:id" element={<LinkDetailsPage />} />
            </Routes>
        </Router>
    );
}

export default App;