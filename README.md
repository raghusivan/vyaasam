Vyaasam projects and document maintenance


Vyaasam: A Comprehensive Text Analysis and Summarization Tool

curl -u "u:p" \
  --header "Content-Type: application/json" \
  --location --request GET "https://host:port/messages/_search" \
  --data-raw '{
    "query": {
      "wildcard": {
        "content": {
          "value": "Hel*",
          "boost": 1.0,
          "rewrite": "constant_score"
        }
      }
    }
  }'
