#!/bin/bash

# ML Service Start Script
echo "Starting GLOWMANCE ML Service..."

# Check if virtual environment exists
if [ ! -d "venv" ]; then
    echo "Creating virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
source venv/bin/activate

# Install dependencies
echo "Installing dependencies..."
pip install -r requirements.txt

# Start the service
echo "Starting FastAPI service on port 8000..."
uvicorn main:app --host 0.0.0.0 --port 8000 --reload

