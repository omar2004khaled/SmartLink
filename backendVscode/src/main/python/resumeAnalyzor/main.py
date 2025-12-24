from fastapi import FastAPI, UploadFile, File, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware
from services.textExtractor import extractText
from services.cvAnalyzer import CVAnalyzer
from models.analysisModels import AnalysisResult
import os
from dotenv import load_dotenv

load_dotenv() 

app = FastAPI(title="CV Analyzer API")
app.add_middleware(
    CORSMiddleware,allow_origins=["*"],allow_credentials=True,allow_methods=["*"],allow_headers=["*"],
)

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if not GEMINI_API_KEY:
    raise ValueError("GEMINI_API_KEY environment variable is required")
cv_analyzer = CVAnalyzer(GEMINI_API_KEY)

@app.post("/analyze-cv", response_model=AnalysisResult)
async def analyze_cv(file: UploadFile = File(...), position: str = Form(...)):
    if not file.filename.endswith('.pdf'):
        raise HTTPException(status_code=400, detail="enter pdf file only")
    
    try:
        cvText = extractText(file)
        if not cvText.strip():
            raise HTTPException(status_code=400,detail="No text found")
        analysis = cv_analyzer.analyze_cv(cvText, position)
        return analysis
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")

@app.get("/health")
async def health_check():
    return {"status": "healthy"}