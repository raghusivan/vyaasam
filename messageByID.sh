#!/bin/bash

# Check if the Elasticsearch host, index name, and message ID are provided
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]; then
  echo "Usage: $0 <es_host> <index_name> <message_id>"
  echo "Example: $0 https://your_elasticsearch_host:9200 ecomm_production_20200929_message_types_2023-11-19 MES-4bdad613948a126c-27d719800000"
  exit 1
fi

# Assign the Elasticsearch host, index name, and message ID from the arguments
ES_HOST="$1"
INDEX_NAME="$2"
MESSAGE_ID="$3"

# Replace these with your actual credentials
USERNAME="your_username"
PASSWORD="your_password"

# Create the query to match the given message_id
QUERY='{
  "query": {
    "term": {
      "message_id": "'"$MESSAGE_ID"'"
    }
  }
}'

# Execute the curl request to Elasticsearch to get the full JSON for the given message ID
RESPONSE=$(curl -u "$USERNAME:$PASSWORD" --header "Content-Type: application/json" --location --request GET "$ES_HOST/$INDEX_NAME/_search" --data-raw "$QUERY")

# Format the JSON output using 'jq' if installed; otherwise, print raw JSON
if command -v jq > /dev/null 2>&1; then
  echo "$RESPONSE" | jq .
else
  echo "jq is not installed. Showing raw JSON output:"
  echo "$RESPONSE"
fi
