# achterhoek
Parser for Achterhoek civil and parish records for the purpose of genealogy research
## Purpose
PDF files on genealogiedomein.nl containing transcriptions of church and civil records are useful to a certain extent, but hard to search through.

The purpose of this project is to extract data from the PDFs and to make it searchable.
## How this project fits in the whole picture
 1. The original documents were handwritten hundreds of years ago.  Many are still available.
 2. The above were scanned and are available through genealogiedomein.nl
 3. The documents were already transcribed and are published as PDF files on the same website.
 4. To convert PDF files to text: pdftotext -enc UTF-8 -nopgbrk -raw file.pdf output.txt
 5. This project's parser converts the above output.txt into CSV data.
 6. A future tool normalizes and imports the above data into a database.
 7. A future web app makes the database data searchable.
