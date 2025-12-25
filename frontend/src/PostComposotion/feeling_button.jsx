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
                padding: "8px 12px",
                borderRadius: "6px",
                background: "#f9fafb",
                border: "1px solid #d1d5db",
                fontSize: "14px",
                display: "flex",
                alignItems: "center",
                gap: "6px",
                cursor: "pointer",
              }}
            >
              <Smile size={16} />
              {feeling ? feeling : "Feeling"}
            </button>

            {/* DROPDOWN MENU */}
            {showFeelingsMenu && (
              <div
                style={{
                  position: "absolute",
                  top: "100%",
                  left: 0,
                  zIndex: 10,
                  background: "white",
                  borderRadius: "6px",
                  padding: "4px",
                  border: "1px solid #d1d5db",
                  maxHeight: "200px",
                  overflowY: "auto",
                  width: "160px",
                  boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
                }}
              >
                {feelingsList.map((f) => (
                  <div
                    key={f}
                    onClick={() => {
                      setFeeling(f.split(" ")[1]);
                      setShowFeelingsMenu(false);
                    }}
                    style={{
                      padding: "8px",
                      cursor: "pointer",
                      borderRadius: "4px",
                      fontSize: "14px",
                    }}
                    onMouseEnter={(e) =>
                      (e.target.style.background = "#f3f4f6")
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
