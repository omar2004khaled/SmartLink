import { useState } from 'react'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import CreatePost from './PostComposotion/PostComposotion'
import PostCard from './PostCard/PostCard'

import './App.css'

function App() {
  

  return (
    <BrowserRouter>
      <Routes>
          <Route path="/PostComposation" element={<CreatePost></CreatePost>}></Route>
          <Route path='/Feed' element={<PostCard></PostCard>}></Route>   
      </Routes>
    </BrowserRouter>
  )
}

export default App
