import google.generativeai as genai
import json
from models.analysisModels import AnalysisResult

class CVAnalyzer:
    def __init__(self, api_key:str):
        genai.configure(api_key=api_key)
        self.model = genai.GenerativeModel('gemini-2.5-flash')

    
    def analyze_cv(self, cvText):
        prompt = f"""
        Analyze this CV/Resume and provide detailed feedback. Respond ONLY with valid JSON in this exact format:
        {{
            "strong_points": ["point1", "point2", "point3"],
            "weak_points": ["point1", "point2", "point3"],
            "improvements_needed": ["improvement1", "improvement2", "improvement3"],
            "overall_score": 1-10,
            "summary": "Brief assessment in 2-3 sentences."
        }}
        CV Text:
        {cvText}

        Focus on: skills relevance, experience quality, formatting, completeness, and professional presentation.
        Rate the overall_score from 1-10 based on the actual quality of the CV. Be honest and vary the score based on the CV's strengths and weaknesses.
        """
        
        try:
            response = self.model.generate_content(prompt)
            print(f"response: {response.text}")  
            responseText = response.text.strip()
            if responseText.startswith('```json'):
                responseText = responseText[7:-3].strip()
            elif responseText.startswith('```'):
                responseText = responseText[3:-3].strip()
            result = json.loads(responseText)
            return AnalysisResult(**result)
        except Exception as e:
            raise e