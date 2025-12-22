from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from services.textExtractor import extractText
from services.cvAnalyzer import CVAnalyzer
from models.analysisModels import AnalysisResult

app = FastAPI(title="CV Analyzer API")
app.add_middleware(
    CORSMiddleware,allow_origins=["*"],allow_credentials=True,allow_methods=["*"],allow_headers=["*"],
)

GEMINI_API_KEY = "AIzaSyC66Ue0VF-f9ddNMe1n5LFw_EMvGe9P_so"
cv_analyzer = CVAnalyzer(GEMINI_API_KEY)

@app.post("/analyze-cv", response_model=AnalysisResult)
async def analyze_cv(file: UploadFile = File(...)):
    if not file.filename.endswith('.pdf'):
        raise HTTPException(status_code=400, detail="enter pdf file only")
    
    try:
        cv_text = extractText(file)
        if not cv_text.strip():
            raise HTTPException(status_code=400, detail="No text found")
        analysis = cv_analyzer.analyze_cv(cv_text)
        return analysis
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")

@app.get("/health")
async def health_check():
    return {"status": "healthy"}