import React, { useState, useRef } from 'react';
import { X } from 'lucide-react';
import PostComposotion from '../PostComposotion/PostComposotion';
import UserHeader from './UserHeader';
import Content from './Content';
import Attachment from './Attachment';
import './PostCard.css';
import Footer from './Footer';
export default function PostCard({ onClose }) {
  return (
    <div className="post-card">
      <UserHeader ></UserHeader>
      <Content></Content>
      <Attachment></Attachment>
      <Footer></Footer>
      
    </div>
  );
};