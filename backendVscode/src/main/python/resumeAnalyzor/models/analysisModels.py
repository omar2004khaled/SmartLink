from pydantic import BaseModel
from typing import List

class AnalysisResult(BaseModel):
    strong_points: List[str]
    weak_points: List[str]
    improvements_needed: List[str]
    overall_score: int 
    summary: str