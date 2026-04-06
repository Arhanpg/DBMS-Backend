# Vercel serverless entry point — imports the FastAPI app from main.py
import sys
import os

# Make sure the project root is on the path so `from routers import ...` works
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from main import app  # noqa: F401  — Vercel picks up the `app` object
