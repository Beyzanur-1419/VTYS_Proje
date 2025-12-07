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


@app.post("/analyze/skin-type")
async def analyze_skin_type(image: UploadFile = File(...)):
    """
    Analyze skin type from image
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
            
            logger.info(f"Processing skin type analysis: {image.filename}, Size: {img_size}")
        except Exception as e:
            raise HTTPException(
                status_code=400,
                detail=f"Invalid image file: {str(e)}"
            )
        
        # Mock analysis results for Skin Type
        analysis_results = {
            "skin_type": "Combination",
            "confidence": 0.92,
            "details": {
                "oily_zone": "T-Zone",
                "dry_zone": "Cheeks"
            },
            "metadata": {
                "processed_at": datetime.now().isoformat()
            }
        }
        
        return JSONResponse(
            status_code=200,
            content=analysis_results
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing image: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Internal server error: {str(e)}"
        )


@app.post("/analyze/disease")
async def analyze_disease(image: UploadFile = File(...)):
    """
    Analyze skin diseases from image
    """
    try:
        # Validate file type
        allowed_types = ["image/jpeg", "image/jpg", "image/png", "image/webp"]
        if image.content_type not in allowed_types:
            raise HTTPException(
                status_code=400,
                detail=f"Invalid file type. Allowed types: {', '.join(allowed_types)}"
            )
        
        contents = await image.read()
        
        # Mock analysis results for Disease
        analysis_results = {
            "disease": "Acne",
            "severity": "Moderate",
            "confidence": 0.88,
            "conditions": ["Papules", "Pustules"],
            "recommendations": [
                "Salicylic Acid Cleanser",
                "Oil-free Moisturizer"
            ],
            "metadata": {
                "processed_at": datetime.now().isoformat()
            }
        }
        
        return JSONResponse(
            status_code=200,
            content=analysis_results
        )
        
    except Exception as e:
        logger.error(f"Error processing disease analysis: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Internal server error: {str(e)}"
        )


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )

