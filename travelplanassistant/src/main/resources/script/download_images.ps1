# PowerShell script to download images for travel plan assistant
# Downloads 50 travel images and 100 profile pictures

# Create directory if it doesn't exist
$uploadDir = "uploads\profile-pictures"
if (!(Test-Path $uploadDir)) {
    New-Item -ItemType Directory -Path $uploadDir -Force
    Write-Host "Created directory: $uploadDir"
}

function Download-Image {
    param(
        [string]$Url,
        [string]$FilePath,
        [int]$DelaySeconds = 1
    )
    
    try {
        Write-Host "Downloading: $FilePath"
        Invoke-WebRequest -Uri $Url -OutFile $FilePath -UserAgent "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        Start-Sleep -Seconds $DelaySeconds
        return $true
    }
    catch {
        Write-Host "Failed to download $Url : $_" -ForegroundColor Red
        return $false
    }
}

# Download travel images (50 images)
Write-Host "=== Downloading Travel Images ===" -ForegroundColor Green
for ($i = 1; $i -le 50; $i++) {
    $url = "https://picsum.photos/800/600?random=$($i + 1000)"
    $filename = "travel_image_$($i.ToString().PadLeft(2, '0')).jpg"
    $filepath = Join-Path $uploadDir $filename
    Download-Image -Url $url -FilePath $filepath -DelaySeconds 1
}

# Download profile pictures (100 images)
Write-Host "=== Downloading Profile Pictures ===" -ForegroundColor Green
for ($i = 1; $i -le 100; $i++) {
    $url = "https://picsum.photos/400/400?random=$($i + 2000)"
    $filename = "profile_$($i.ToString().PadLeft(3, '0')).jpg"
    $filepath = Join-Path $uploadDir $filename
    Download-Image -Url $url -FilePath $filepath -DelaySeconds 1
}

Write-Host "=== Download Complete ===" -ForegroundColor Green
Write-Host "Images saved to: $uploadDir"
Write-Host "Travel images: travel_image_01.jpg to travel_image_50.jpg"
Write-Host "Profile images: profile_001.jpg to profile_100.jpg"
