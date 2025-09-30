#!/usr/bin/env python3
"""
Script to download images for the travel plan assistant application.
Downloads 50 travel/landscape images and 100 profile pictures.
"""

import os
import requests
import time
from urllib.parse import urlparse
import random

def create_directories():
    """Create necessary directories if they don't exist."""
    os.makedirs('uploads/profile-pictures', exist_ok=True)
    print("Created directories: uploads/profile-pictures")

def download_image(url, filepath, delay=1):
    """Download an image from URL to filepath with rate limiting."""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        
        with open(filepath, 'wb') as f:
            f.write(response.content)
        print(f"Downloaded: {filepath}")
        time.sleep(delay)  # Rate limiting
        return True
    except Exception as e:
        print(f"Failed to download {url}: {e}")
        return False

def download_travel_images():
    """Download 50 travel/landscape images."""
    print("\n=== Downloading Travel Images ===")
    
    # Unsplash API for free high-quality images
    base_urls = [
        # Landscapes
        "https://picsum.photos/800/600?random=",
        # You can also use Unsplash Source API (free, no API key needed)
        # "https://source.unsplash.com/800x600/?landscape,",
        # "https://source.unsplash.com/800x600/?architecture,",
        # "https://source.unsplash.com/800x600/?travel,",
    ]
    
    # Travel/landscape keywords for variety
    categories = ['landscape', 'architecture', 'travel', 'nature', 'city', 'mountains', 
                 'beach', 'forest', 'sunset', 'buildings', 'temple', 'bridge',
                 'castle', 'park', 'lake', 'ocean', 'desert', 'winter', 'spring',
                 'summer', 'festival', 'culture', 'street', 'plaza', 'garden']
    
    for i in range(1, 51):
        # Use different sources for variety
        if i <= 50:
            # Lorem Picsum for basic landscapes
            url = f"https://picsum.photos/800/600?random={i + 1000}"
            filename = f"travel_image_{i:02d}.jpg"
        else:
            # Unsplash for themed images
            category = categories[(i-26) % len(categories)]
            url = f"https://source.unsplash.com/800x600/?{category}"
            filename = f"travel_image_{i:02d}.jpg"
        
        filepath = f"uploads/profile-pictures/{filename}"
        download_image(url, filepath, delay=0.5)

def download_profile_images():
    """Download 100 profile pictures."""
    print("\n=== Downloading Profile Pictures ===")
    
    # This Person Does Not Exist API for generated faces
    # Alternative: Use diverse stock photo APIs
    
    for i in range(1, 101):
        # Generated faces (ethically sourced)
        url = f"https://thispersondoesnotexist.com/image"
        filename = f"profile_{i:03d}.jpg"
        filepath = f"uploads/profile-pictures/{filename}"
        
        # Add more delay for this API as it's rate-limited
        if download_image(url, filepath, delay=2):
            pass
        else:
            # Fallback to placeholder service
            fallback_url = f"https://picsum.photos/400/400?random={i + 2000}"
            download_image(fallback_url, filepath, delay=0.5)

def main():
    """Main function to orchestrate the download process."""
    print("Starting image download process...")
    print("This will download:")
    print("- 50 travel/landscape images (800x600)")
    print("- 100 profile pictures (400x400)")
    print("\nEstimated time: 5-10 minutes")
    
    response = input("\nProceed with download? (y/n): ")
    if response.lower() != 'y':
        print("Download cancelled.")
        return
    
    create_directories()
    
    try:
        download_travel_images()
        download_profile_images()
        print("\n=== Download Complete ===")
        print("Images saved to: uploads/profile-pictures/")
        print("Travel images: travel_image_01.jpg to travel_image_50.jpg")
        print("Profile images: profile_001.jpg to profile_100.jpg")
        
    except KeyboardInterrupt:
        print("\nDownload interrupted by user.")
    except Exception as e:
        print(f"\nError during download: {e}")

if __name__ == "__main__":
    main()
