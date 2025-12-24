import React, { useState } from 'react';
import { Upload, FileText, Download, Loader } from 'lucide-react';
import Navbar from './Navbar';
import jsPDF from 'jspdf';

const CVAnalysis = () => {
  const [file, setFile] = useState(null);
  const [position, setPosition] = useState('');
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile.type === 'application/pdf') {
      setFile(selectedFile);
      setError('');
    } else {
      setError('Please select a PDF file');
    }
  };

  const handleAnalyze = async () => {
    if (!file || !position.trim()) {
      setError('Please select a PDF file and enter a position');
      return;
    }

    setLoading(true);
    setError('');

    const formData = new FormData();
    formData.append('file', file);
    formData.append('position', position);

    try {
      const response = await fetch('http://localhost:8000/analyze-cv', {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Analysis failed');
      }

      const result = await response.json();
      setAnalysis(result);
    } catch (err) {
      setError('Failed to analyze CV. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const exportToPDF = () => {
    if (!analysis) return;

    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.width;
    let yPosition = 20;

    // Title
    doc.setFontSize(20);
    doc.text('CV Analysis Report', pageWidth / 2, yPosition, { align: 'center' });
    yPosition += 20;

    // Position
    doc.setFontSize(14);
    doc.text(`Position: ${position}`, 20, yPosition);
    yPosition += 15;

    // Overall Score
    doc.setFontSize(16);
    doc.text(`Overall Score: ${analysis.overall_score}/10`, 20, yPosition);
    yPosition += 20;

    // Strong Points
    doc.setFontSize(14);
    doc.text('Strong Points:', 20, yPosition);
    yPosition += 10;
    analysis.strong_points.forEach((point, index) => {
      doc.setFontSize(12);
      doc.text(`• ${point}`, 25, yPosition);
      yPosition += 8;
    });
    yPosition += 10;

    // Weak Points
    doc.setFontSize(14);
    doc.text('Weak Points:', 20, yPosition);
    yPosition += 10;
    analysis.weak_points.forEach((point, index) => {
      doc.setFontSize(12);
      doc.text(`• ${point}`, 25, yPosition);
      yPosition += 8;
    });
    yPosition += 10;

    // Improvements
    doc.setFontSize(14);
    doc.text('Improvements Needed:', 20, yPosition);
    yPosition += 10;
    analysis.improvements_needed.forEach((improvement, index) => {
      doc.setFontSize(12);
      doc.text(`• ${improvement}`, 25, yPosition);
      yPosition += 8;
    });
    yPosition += 10;

    // Summary
    doc.setFontSize(14);
    doc.text('Summary:', 20, yPosition);
    yPosition += 10;
    doc.setFontSize(12);
    const splitSummary = doc.splitTextToSize(analysis.summary, pageWidth - 40);
    doc.text(splitSummary, 20, yPosition);

    doc.save('cv-analysis-report.pdf');
  };

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gray-50 pt-20">
        <div className="max-w-4xl mx-auto p-6">
          <h1 className="text-3xl font-bold text-gray-900 mb-8">CV Analysis</h1>

          {/* Upload Section */}
          <div className="bg-white rounded-lg shadow-md p-6 mb-6">
            <h2 className="text-xl font-semibold mb-4">Upload Your CV</h2>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Position Applied For
                </label>
                <input
                  type="text"
                  value={position}
                  onChange={(e) => setPosition(e.target.value)}
                  placeholder="e.g., Software Engineer, Marketing Manager"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  CV File (PDF only)
                </label>
                <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                  <Upload className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                  <input
                    type="file"
                    accept=".pdf"
                    onChange={handleFileChange}
                    className="hidden"
                    id="cv-upload"
                  />
                  <label
                    htmlFor="cv-upload"
                    className="cursor-pointer text-blue-600 hover:text-blue-500"
                  >
                    Click to upload PDF
                  </label>
                  {file && (
                    <p className="mt-2 text-sm text-gray-600">
                      Selected: {file.name}
                    </p>
                  )}
                </div>
              </div>

              {error && (
                <div className="text-red-600 text-sm">{error}</div>
              )}

              <button
                onClick={handleAnalyze}
                disabled={loading || !file || !position.trim()}
                className="w-full bg-blue-600 text-white py-3 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
              >
                {loading ? (
                  <>
                    <Loader className="animate-spin mr-2" size={20} />
                    Analyzing...
                  </>
                ) : (
                  <>
                    <FileText className="mr-2" size={20} />
                    Analyze CV
                  </>
                )}
              </button>
            </div>
          </div>

          {/* Analysis Results */}
          {analysis && (
            <div className="bg-white rounded-lg shadow-md p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Analysis Results</h2>
                <button
                  onClick={exportToPDF}
                  className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 flex items-center"
                >
                  <Download className="mr-2" size={16} />
                  Export PDF
                </button>
              </div>

              {/* Overall Score */}
              <div className="mb-8 text-center">
                <div className="text-6xl font-bold text-blue-600 mb-2">
                  {analysis.overall_score}/10
                </div>
                <div className="text-lg text-gray-600">Overall Score</div>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                {/* Strong Points */}
                <div className="bg-green-50 p-6 rounded-lg">
                  <h3 className="text-xl font-bold text-green-800 mb-4">Strong Points</h3>
                  <ul className="space-y-2">
                    {analysis.strong_points.map((point, index) => (
                      <li key={index} className="flex items-start">
                        <span className="text-green-600 mr-2">✓</span>
                        <span className="text-green-700">{point}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                {/* Weak Points */}
                <div className="bg-red-50 p-6 rounded-lg">
                  <h3 className="text-xl font-bold text-red-800 mb-4">Weak Points</h3>
                  <ul className="space-y-2">
                    {analysis.weak_points.map((point, index) => (
                      <li key={index} className="flex items-start">
                        <span className="text-red-600 mr-2">✗</span>
                        <span className="text-red-700">{point}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>

              {/* Improvements */}
              <div className="mt-6 bg-yellow-50 p-6 rounded-lg">
                <h3 className="text-xl font-bold text-yellow-800 mb-4">Improvements Needed</h3>
                <ul className="space-y-2">
                  {analysis.improvements_needed.map((improvement, index) => (
                    <li key={index} className="flex items-start">
                      <span className="text-yellow-600 mr-2">→</span>
                      <span className="text-yellow-700">{improvement}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Summary */}
              <div className="mt-6 bg-blue-50 p-6 rounded-lg">
                <h3 className="text-xl font-bold text-blue-800 mb-4">Summary</h3>
                <p className="text-blue-700 leading-relaxed">{analysis.summary}</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default CVAnalysis;