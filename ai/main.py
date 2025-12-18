from fastapi import FastAPI, UploadFile, File, HTTPException
import uvicorn
import torch
import torch.nn as nn
from torchvision import transforms
from PIL import Image
import io
import os
import glob

app = FastAPI(title="Glowmance AI Service")

# --- Configuration ---
MODEL_DIR = "."
# Automatically find a .pth file in the current directory
model_files = glob.glob(os.path.join(MODEL_DIR, "*.pth"))
MODEL_PATH = model_files[0] if model_files else "model.pth"

# Label mapping (You might need to adjust this based on your training)
# Assuming typical categorical output. Specify your actual classes here!
# Example: 0: Normal, 1: Acne, 2: Eczema, etc.
LABELS = {
    0: "Normal",
    1: "Akne",
    2: "Egzama",
    3: "Rozase",
    4: "Leke"
}

# --- Model Loading ---
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = None

def load_model():
    global model
    if not os.path.exists(MODEL_PATH):
        print(f"Warning: Model file not found at {MODEL_PATH}")
        return

    try:
        # 1. Try loading entire model (if saved with save(model))
        # model = torch.load(MODEL_PATH, map_location=device)
        
        # 2. OR Try loading state dict (if saved with save(model.state_dict()))
        # You need to define the Architecture class here matching your training!
        # For now, let's assume it's a full model load or we'll wrap it later.
        # This is a PLACEHOLDER. User needs to confirm model architecture.
        model = torch.load(MODEL_PATH, map_location=device)
        model.eval()
        print(f"Model loaded successfully from {MODEL_PATH}")
    except Exception as e:
        print(f"Error loading model: {e}")

@app.on_event("startup")
async def startup_event():
    load_model()

# --- Preprocessing ---
transform = transforms.Compose([
    transforms.Resize((224, 224)), # Adjust to your model's input size
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    if model is None:
        # Mock response if model is missing, to keep flow working
        return {
            "success": True,
            "mock": True,
            "data": {
                "hasAcne": True,
                "acneLevel": "Orta",
                "hasEczema": False,
                "score": 0.85
            }
        }
        # raise HTTPException(status_code=503, detail="Model not loaded")

    try:
        # Read and transform image
        image_data = await file.read()
        image = Image.open(io.BytesIO(image_data)).convert('RGB')
        input_tensor = transform(image).unsqueeze(0).to(device)

        # Inference
        with torch.no_grad():
            outputs = model(input_tensor)
            _, predicted = torch.max(outputs, 1)
            class_idx = predicted.item()
            
        result_label = LABELS.get(class_idx, "Bilinmiyor")

        # Basic logic to map single label to our complex app structure
        result = {
            "success": True,
            "prediction_class": class_idx,
            "prediction_label": result_label,
            # Mapping logic (adjust based on your actual 5-class or N-class output)
            "data": {
                "hasAcne": result_label == "Akne",
                "acneLevel": "Yüksek" if result_label == "Akne" else "Yok",
                "hasEczema": result_label == "Egzama",
                "eczemaLevel": "Orta" if result_label == "Egzama" else "Yok",
                "isNormal": result_label == "Normal"
            }
        }
        return result

    except Exception as e:
        print(f"Prediction error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/")
def home():
    return {"message": "AI Service is running", "model_found": model is not None, "model_path": MODEL_PATH}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
