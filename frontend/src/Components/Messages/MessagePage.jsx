import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import MessagingApp from "./MessagingApp";
import CompanyNavbar from "../CompanyNavbar";
import Navbar from "../Navbar";

const MessagePage = () => {
    const location = useLocation();
    const [userId] = useState(() => {
        const idStr = localStorage.getItem('userId');
        return idStr ? parseInt(idStr, 10) : null;
    });
    const [userType] = useState(() => localStorage.getItem('userType'));
    const openChatWith = location.state?.openChatWith;

    return (
        <div>
            {userType === 'COMPANY' ? <CompanyNavbar /> : <Navbar />}
            <MessagingApp userId={userId} openChatWith={openChatWith} />
        </div>
    );
};

export default MessagePage;