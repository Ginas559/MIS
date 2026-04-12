# Export Java packages to Word

Script: `export_java_packages_to_word.py`

## What it does
- Quet package goc `src/main/java/vn/iotstar/coolenglish`
- Lay tat ca package con truc tiep (muc cap 1)
- Tao `n` file `.docx` (n = so package con truc tiep)
- Moi file Word chua toan bo code `.java` trong package con do (gom ca package long ben duoi)

## Setup
```powershell
pip install -r requirements.txt
```

## Dry run
```powershell
python .\export_java_packages_to_word.py --dry-run
```

## Export Word files
```powershell
python .\export_java_packages_to_word.py
```

Mac dinh file duoc xuat vao thu muc `word_exports`.

## Optional arguments
```powershell
python .\export_java_packages_to_word.py --src src/main/java --out word_exports --package-root vn/iotstar/coolenglish
```

