import sys
import os
from dotenv import load_dotenv
from exa_py import Exa

load_dotenv()

EXA_API_KEY = os.getenv('EXA_API_KEY')

if not EXA_API_KEY:
    print("Eroare: Variabila de mediu EXA_API_KEY nu este setată în fișierul .env!", file=sys.stderr)
    sys.exit(1)

def run_search(query):
    try:
        exa = Exa(EXA_API_KEY)

        response = exa.search(
            query, 
            num_results=10, 
            type='keyword', 
            include_domains=['https://www.tiktok.com']
        )

        print(f"Interogare: {query}")
        
        if not response.results:
            print("Niciun rezultat gasit")
            return

        for i, result in enumerate(response.results):
            print(f"[{i+1}] Titlu: {result.title}")
            print(f"    URL: {result.url}")
            print("-------------------------")

    except Exception as e:
        print(f"eroare la executia Exa: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) > 1:
        search_query = sys.argv[1]
        run_search(search_query)
    else:
        print("eroare: niciun termen de cautare furnizat. Scriptul trebuie rulat cu un argument.", file=sys.stderr)
        sys.exit(1)