import sys
import os
from dotenv import load_dotenv
from exa_py import Exa

sys.stdout.reconfigure(encoding='utf-8')
sys.stderr.reconfigure(encoding='utf-8')

load_dotenv()

EXA_API_KEY = os.getenv('EXA_API_KEY')

if not EXA_API_KEY:
    print("Eroare: Variabila de mediu EXA_API_KEY nu este setată în fișierul .env!", file=sys.stderr)
    sys.exit(1)

def run_search(query):
    try:
        exa = Exa(EXA_API_KEY)

        response = exa.search_and_contents(
            query, 
            num_results=20, 
            type='auto', 
            summary= True,
        )

        print(f"Searching for: {query}")
        
        if not response.results:
            print("No results found")
            return

        for i, result in enumerate(response.results):
            snippet = result.summary if result.summary else ""
            print(f"{result.title}|{result.url}|{snippet}")

    except Exception as e:
        print(f"Exa execution error: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) > 1:
        search_query = sys.argv[1]
        run_search(search_query)
    else:
        print("Error: no term used", file=sys.stderr)
        sys.exit(1)