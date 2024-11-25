import requests
import json
import sys
from typing import Iterator, Dict, Any

def scroll_elasticsearch(
    es_host: str,
    index_name: str,
    search_text: str,
    username: str,
    password: str,
    batch_size: int = 1000,
    scroll_timeout: str = "5m"
) -> Iterator[Dict[str, Any]]:
    """
    Generator function that performs a scrolled search query on Elasticsearch.
    
    Args:
        es_host: Elasticsearch host URL
        index_name: Index name to search
        search_text: Text to search across fields
        username: Elasticsearch username
        password: Elasticsearch password
        batch_size: Number of results per batch
        scroll_timeout: How long Elasticsearch should keep the scroll context alive
    
    Yields:
        Dictionary containing the hits for each batch
    """
    try:
        # Initial search request
        search_url = f"{es_host}/{index_name}/_search"
        query = {
            "size": batch_size,
            "query": {
                "multi_match": {
                    "query": search_text,
                    "fields": ["*"]
                }
            },
            "sort": ["_doc"]  # For efficient scrolling
        }

        # Initialize scroll
        response = requests.post(
            f"{search_url}?scroll={scroll_timeout}",
            auth=(username, password),
            headers={"Content-Type": "application/json"},
            data=json.dumps(query)
        )

        if response.status_code != 200:
            raise Exception(f"Initial scroll failed: {response.text}")

        # Process the first batch
        result = response.json()
        scroll_id = result.get('_scroll_id')
        hits = result.get('hits', {}).get('hits', [])

        while hits:
            yield result

            # Get the next batch using the scroll ID
            scroll_response = requests.post(
                f"{es_host}/_search/scroll",
                auth=(username, password),
                headers={"Content-Type": "application/json"},
                data=json.dumps({
                    "scroll": scroll_timeout,
                    "scroll_id": scroll_id
                })
            )

            if scroll_response.status_code != 200:
                raise Exception(f"Scroll request failed: {scroll_response.text}")

            result = scroll_response.json()
            scroll_id = result.get('_scroll_id')
            hits = result.get('hits', {}).get('hits', [])

    except Exception as e:
        print(f"An error occurred: {e}")
        raise
    finally:
        # Clean up scroll context if we have a scroll_id
        if 'scroll_id' in locals():
            try:
                requests.delete(
                    f"{es_host}/_search/scroll",
                    auth=(username, password),
                    headers={"Content-Type": "application/json"},
                    data=json.dumps({"scroll_id": scroll_id})
                )
            except Exception as e:
                print(f"Warning: Failed to clear scroll context: {e}")

def main():
    """Main function to handle command line arguments and execute the search."""
    if len(sys.argv) != 6:
        print("Usage: python search_es_index.py <es_host> <index_name> <search_text> <username> <password>")
        sys.exit(1)

    es_host = sys.argv[1]
    index_name = sys.argv[2]
    search_text = sys.argv[3]
    username = sys.argv[4]
    password = sys.argv[5]

    total_hits = 0
    try:
        for batch in scroll_elasticsearch(es_host, index_name, search_text, username, password):
            hits = batch.get('hits', {})
            batch_hits = hits.get('hits', [])
            total_hits += len(batch_hits)
            
            # Print results in a formatted way
            for hit in batch_hits:
                print(json.dumps(hit, indent=2))
                print("-" * 80)  # Separator between results
            
            # Print progress
            print(f"Processed {total_hits} documents so far...")

        print(f"\nSearch completed. Total documents found: {total_hits}")

    except Exception as e:
        print(f"Error during search: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
