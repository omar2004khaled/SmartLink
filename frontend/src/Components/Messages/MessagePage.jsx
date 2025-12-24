import React, {useState } from "react";
import MessagingApp from "../../MessagingApp";
import CompanyNavbar from "../CompanyNavbar";
import Navbar from "../Navbar";

const MessagePage = () => {
    const [userId] = useState(() => {
        const idStr = localStorage.getItem('userId');
        return idStr ? parseInt(idStr, 10) : null;
    });
    const [userType] = useState(() => localStorage.getItem('userType'));

    return (
        <div>
            {userType === 'COMPANY' ? <CompanyNavbar /> : <Navbar />}
            <MessagingApp userId={userId} />
        </div>
    );
};

export default MessagePage;