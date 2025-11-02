"""
FastAPI ML Service for Skin Analysis
This service receives images from the backend and returns analysis results
"""

from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import uvicorn
from typing import List, Dict
import base64
from PIL import Image
import io
from datetime import datetime
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="GLOWMANCE ML Service",
    description="AI-powered skin analysis service",
    version="1.0.0"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify exact origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
async def root():
    """Health check endpoint"""
    return {
        "status": "ok",
        "service": "GLOWMANCE ML Service",
        "version": "1.0.0",
        "timestamp": datetime.now().isoformat()
    }


@app.get("/health")
async def health():
    """Health check endpoint"""
    return {
        "status": "healthy",
        "service": "ML Service",
        "timestamp": datetime.now().isoformat()
    }


@app.post("/analyze")
async def analyze_skin(image: UploadFile = File(...)):
    """
    Analyze skin image
    
    Args:
        image: Image file (JPEG, PNG, WebP)
    
    Returns:
        Analysis results with confidence, conditions, and recommendations
    """
    try:
        # Validate file type
        allowed_types = ["image/jpeg", "image/jpg", "image/png", "image/webp"]
        if image.content_type not in allowed_types:
            raise HTTPException(
                status_code=400,
                detail=f"Invalid file type. Allowed types: {', '.join(allowed_types)}"
            )
        
        # Read image
        contents = await image.read()
        
        # Validate image size (max 5MB)
        max_size = 5 * 1024 * 1024  # 5MB
        if len(contents) > max_size:
            raise HTTPException(
                status_code=400,
                detail=f"Image size exceeds 5MB limit. Current size: {len(contents)} bytes"
            )
        
        # Load image with PIL
        try:
            img = Image.open(io.BytesIO(contents))
            img_format = img.format
            img_size = img.size
            
            logger.info(f"Processing image: {image.filename}, Size: {img_size}, Format: {img_format}")
        except Exception as e:
            raise HTTPException(
                status_code=400,
                detail=f"Invalid image file: {str(e)}"
            )
        
        # TODO: Here you would call your actual ML model
        # For now, we return a mock response with realistic structure
        
        # Mock analysis results
        analysis_results = {
            "confidence": 0.92,
            "conditions": [
                "Akne",
                "Kuruluk"
            ],
            "recommendations": [
                "Nemlendirici",
                "Akne Kremi"
            ],
            "metadata": {
                "image_format": img_format,
                "image_size": img_size,
                "file_size": len(contents),
                "processed_at": datetime.now().isoformat()
            }
        }
        
        # TODO: Replace with actual ML model inference
        # Example:
        # model = load_model("skin_analysis_model.h5")
        # predictions = model.predict(preprocessed_image)
        # analysis_results = format_predictions(predictions)
        
        return JSONResponse(
            status_code=200,
            content={
                "status": "success",
                "data": analysis_results
            }
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing image: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Internal server error: {str(e)}"
        )


@app.post("/analyze/base64")
async def analyze_skin_base64(data: Dict):
    """
    Analyze skin image from base64 encoded string
    
    Args:
        data: Dict with 'image' key containing base64 encoded image
    
    Returns:
        Analysis results
    """
    try:
        if "image" not in data:
            raise HTTPException(status_code=400, detail="Missing 'image' field in request")
        
        # Decode base64 image
        try:
            image_data = base64.b64decode(data["image"])
        except Exception as e:
            raise HTTPException(status_code=400, detail=f"Invalid base64 image: {str(e)}")
        
        # Validate image size
        max_size = 5 * 1024 * 1024  # 5MB
        if len(image_data) > max_size:
            raise HTTPException(
                status_code=400,
                detail=f"Image size exceeds 5MB limit"
            )
        
        # Load image
        try:
            img = Image.open(io.BytesIO(image_data))
            img_format = img.format
            img_size = img.size
        except Exception as e:
            raise HTTPException(status_code=400, detail=f"Invalid image file: {str(e)}")
        
        # Mock analysis results
        analysis_results = {
            "confidence": 0.90,
            "conditions": ["Kuruluk", "Pigmentasyon"],
            "recommendations": ["Nemlendirici", "Güneş Koruyucu"],
            "metadata": {
                "image_format": img_format,
                "image_size": img_size,
                "processed_at": datetime.now().isoformat()
            }
        }
        
        return JSONResponse(
            status_code=200,
            content={
                "status": "success",
                "data": analysis_results
            }
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing base64 image: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )

