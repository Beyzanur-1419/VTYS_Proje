from fastapi import FastAPI, UploadFile, File, HTTPException
import uvicorn
import torch
import torch.nn as nn
from torchvision import transforms
from PIL import Image
import io
import os

app = FastAPI(title="Glowmance AI Service")

# --- Configuration ---
MODEL_DIR = "."
DISEASE_MODEL_PATH = os.path.join(MODEL_DIR, "skin_disease_model.pth")
TYPE_MODEL_PATH = os.path.join(MODEL_DIR, "skin_type_model.pth")

# Label mapping
DISEASE_LABELS = {
    0: "Akne",
    1: "Egzama",
    2: "Normal", 
    3: "Rozase",
    4: "Leke",
    5: "Siğil"
}

TYPE_LABELS = {
    0: "Kuru",
    1: "Yağlı",
    2: "Normal",
    3: "Karma"
}

# --- Model Loading ---
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
from torchvision import models

def load_model(path, model_name, num_classes):
    if not os.path.exists(path):
        print(f"Warning: {model_name} file not found at {path}")
        return None

    try:
        # Load state dict
        state_dict = torch.load(path, map_location=device)
        
        # Assume ResNet18 (Standard for these keys)
        if "conv1.weight" in state_dict:
             print(f"DEBUG: {model_name} detected as ResNet-like.")
             model = models.resnet18(pretrained=False)
             num_ftrs = model.fc.in_features
             model.fc = nn.Linear(num_ftrs, num_classes)
             
             try:
                 model.load_state_dict(state_dict)
             except RuntimeError as e:
                 print(f"ResNet18 load failed: {e}")
                 print("Trying ResNet50...")
                 model = models.resnet50(pretrained=False)
                 num_ftrs = model.fc.in_features
                 model.fc = nn.Linear(num_ftrs, num_classes)
                 model.load_state_dict(state_dict)

             model = model.to(device)
             model.eval()
             print(f"{model_name} loaded successfully (ResNet Architecture).")
             return model

        # Fallback for full model or other types
        if hasattr(state_dict, 'eval'):
             state_dict.eval()
             return state_dict
             
        print(f"Error: {model_name} format not recognized/supported.")
        return None
             
    except Exception as e:
        print(f"Error loading {model_name}: {e}")
        return None

@app.on_event("startup")
async def startup_event():
    global disease_model, type_model
    # Disease: 4 detected (Assuming: Akne, Egzama, Normal, Rozase)
    disease_model = load_model(DISEASE_MODEL_PATH, "Disease Model", num_classes=4)
    # Type: 4 classes (Kuru, Yağlı, Normal, Karma)
    type_model = load_model(TYPE_MODEL_PATH, "Type Model", num_classes=4)

# --- Preprocessing ---
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    # Read and transform image once
    try:
        image_data = await file.read()
        image = Image.open(io.BytesIO(image_data)).convert('RGB')
        input_tensor = transform(image).unsqueeze(0).to(device)
        
        result = {
            "success": True,
            "data": {}
        }

        # 1. Disease Prediction
        if disease_model:
            try:
                with torch.no_grad():
                    outputs = disease_model(input_tensor)
                    probabilities = torch.nn.functional.softmax(outputs, dim=1)
                    print(f"DEBUG: Raw Probabilities: {probabilities}")
                    _, predicted = torch.max(outputs, 1)
                    idx = predicted.item()
                    label = DISEASE_LABELS.get(idx, "Bilinmiyor")
                    print(f"DEBUG: Predicted Class: {label} (Idx: {idx})")
                    
                    result["data"]["disease_prediction"] = label
                    result["data"]["hasAcne"] = (label == "Akne")
                    result["data"]["acneLevel"] = "Orta" if label == "Akne" else "Yok"
                    result["data"]["hasEczema"] = (label == "Egzama")
                    result["data"]["eczemaLevel"] = "Hafif" if label == "Egzama" else "Yok"
                    result["data"]["hasRosacea"] = (label == "Rozase")
            except Exception as e:
                print(f"Disease Inference Error: {e}")
                result["data"]["error_disease"] = str(e)
        else:
            result["data"]["disease_warning"] = "Model loaded değil"

        # 2. Type Prediction
        if type_model:
            try:
                with torch.no_grad():
                    outputs = type_model(input_tensor)
                    _, predicted = torch.max(outputs, 1)
                    idx = predicted.item()
                    label = TYPE_LABELS.get(idx, "Normal")
                    
                    result["data"]["skinType"] = label
            except Exception as e:
                print(f"Type Inference Error: {e}")
                result["data"]["error_type"] = str(e)
        else:
             result["data"]["type_warning"] = "Model loaded değil"

        return result
        
    except Exception as e:
        print(f"Prediction General Error: {e}")
        return {"success": False, "error": str(e)}

@app.get("/")
def home():
    return {
        "message": "AI Service running", 
        "disease_model": disease_model is not None, 
        "type_model": type_model is not None
    }

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
