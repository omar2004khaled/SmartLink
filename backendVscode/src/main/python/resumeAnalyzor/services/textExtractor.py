import pdfplumber
from io import BytesIO 
from fastapi import UploadFile

def extractText(file):
    text = ""
    contents = file.file.read()

    with pdfplumber.open(BytesIO(contents)) as pdf:
        for page in pdf.pages:
            pageText = page.extract_text()
            if pageText:
                text += pageText+"\n"

    return text.strip()


# with open(r"C:\Users\DELL\Desktop\SmartLink\SmartLink\backendVscode\src\main\resources\Sheet 7 (1).pdf", "rb") as f:
#     fake_upload = UploadFile(
#         filename="Sheet 7 (1).pdf",
#         file=BytesIO(f.read())
#     )

# text = extractText(fake_upload)
# print(text)