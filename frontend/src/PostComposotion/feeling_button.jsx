import React, { useState, useRef } from 'react';
import { Upload, X, Image, Video, Smile } from 'lucide-react';

export default function FeelingButton({setFeeling,feeling}){
    const [showFeelingsMenu, setShowFeelingsMenu] = useState(false);
    const feelingsList = [
    "🚫 None",
    "😀 Happy",
    "😢 Sad",
    "😡 Angry",
    "😍 In Love",
    "😴 Tired",
    "🤩 Excited",
    "😎 Confident",
    "🤔 Thinking",
    "😭 Emotional",
    "😇 Blessed",
  ];
    return <div style={{ position: "relative" }}>
            <button
              onClick={() => setShowFeelingsMenu(!showFeelingsMenu)}
              style={{
                padding: ".9rem 1.2rem",
                borderRadius: "0.75rem",
                background: "#f0f9ff",
                border: "2px solid #bfdbfe",
                fontSize: "1rem",
                display: "flex",
                alignItems: "center",
                gap: ".5rem",
                cursor: "pointer",
                whiteSpace: "nowrap",
              }}
            >
              <Smile size={20} />
              {feeling ? `Feeling: ${feeling}` : "Add Feeling"}
            </button>

            {/* DROPDOWN MENU */}
            {showFeelingsMenu && (
              <div
                style={{
                  position: "absolute",
                  top: "110%",
                  left: 0,
                  zIndex: 10,
                  background: "white",
                  borderRadius: "0.75rem",
                  padding: ".5rem",
                  border: "1px solid #bfdbfe",
                  maxHeight: "200px",
                  overflowY: "auto",
                  width: "180px",
                }}
              >
                {feelingsList.map((f) => (
                  <div
                    key={f}
                    onClick={() => {
                      console.log(f.split(" ")[1])  
                      setFeeling(f.split(" ")[1]);
                    
                      setShowFeelingsMenu(false);
                    }}
                    style={{
                      padding: ".75rem",
                      cursor: "pointer",
                      borderRadius: ".5rem",
                    }}
                    onMouseEnter={(e) =>
                      (e.target.style.background = "#eff6ff")
                    }
                    onMouseLeave={(e) =>
                      (e.target.style.background = "white")
                    }
                  >
                    {f}
                  </div>
                ))}
              </div>
            )}
          </div>
}