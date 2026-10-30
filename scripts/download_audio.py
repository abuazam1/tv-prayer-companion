"""Download the two upstream adhan recordings needed for a local build."""
from pathlib import Path
import hashlib,json,urllib.request
root=Path(__file__).resolve().parents[1]
dest=root/'app/src/main/assets'
dest.mkdir(parents=True,exist_ok=True)
for item in json.loads((root/'scripts/audio-sources.json').read_text(encoding='utf-8')):
    target=dest/item['file']
    if target.exists() and hashlib.sha256(target.read_bytes()).hexdigest()==item['sha256']:
        print('Verified',item['file']); continue
    data=urllib.request.urlopen(item['url'],timeout=60).read()
    if hashlib.sha256(data).hexdigest()!=item['sha256']:
        raise RuntimeError('Checksum mismatch: '+item['file'])
    target.write_bytes(data)
    print('Downloaded and verified',item['file'])
