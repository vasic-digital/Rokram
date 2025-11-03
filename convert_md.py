import markdown
import os

def convert_md_to_html(md_file, html_file, title):
    with open(md_file, 'r') as f:
        md_content = f.read()
    
    html_content = markdown.markdown(md_content)
    
    template = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yole - {title}</title>
    <link rel="stylesheet" href="../style.css">
</head>
<body>
    <nav>
        <a href="../index.html">Home</a> |
        <a href="README.html">Guides</a> |
        <a href="../README.html">README</a>
    </nav>

    <main>
        {html_content}
    </main>

    <footer>
        <p>&copy; 2025 Yole Project</p>
    </footer>
</body>
</html>"""
    
    with open(html_file, 'w') as f:
        f.write(template)

# List of MD files and titles
files = [
    ('doc/2023-06-02-csv-readme.md', 'docs/doc/2023-06-02-csv-readme.html', 'CSV Format Guide'),
    ('doc/2020-09-26-vimwiki-sync-plaintext-to-do-and-notes-todotxt-markdown.md', 'docs/doc/2020-09-26-vimwiki-sync-plaintext-to-do-and-notes-todotxt-markdown.html', 'VimWiki Sync Guide'),
    ('doc/2020-04-04-syncthing-file-sync-setup-how-to-use-with-markor.md', 'docs/doc/2020-04-04-syncthing-file-sync-setup-how-to-use-with-markor.html', 'Syncthing Setup'),
    ('doc/2019-07-16-using-markor-to-write-on-an-android-device-plaintextproject.md', 'docs/doc/2019-07-16-using-markor-to-write-on-an-android-device-plaintextproject.html', 'Android Writing Guide'),
    ('doc/2018-05-15-pandoc-vim-markdown-how-i-take-notes-vaughan.md', 'docs/doc/2018-05-15-pandoc-vim-markdown-how-i-take-notes-vaughan.html', 'Pandoc Integration')
]

for md, html, title in files:
    convert_md_to_html(md, html, title)