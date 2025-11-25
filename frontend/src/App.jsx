import { useState } from 'react'
import CreatePost from './PostComposotion/PostComposotion'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
       <CreatePost></CreatePost>
    </>
  )
}

export default App
