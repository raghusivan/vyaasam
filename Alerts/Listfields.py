Below is a Python script that lists all fields and their types from a specific Elasticsearch index. This script uses Python 3.6 and the elasticsearch library.


---

Python Script: List All Fields and Types

from elasticsearch import Elasticsearch

def list_index_fields(es_host, index_name):
    """
    Connects to Elasticsearch and lists all fields and their types in the given index.
    :param es_host: Elasticsearch host URL
    :param index_name: Index name to query
    """
    try:
        # Connect to Elasticsearch
        es = Elasticsearch([es_host])
        if not es.ping():
            print(f"Unable to connect to Elasticsearch at {es_host}")
            return

        # Get the mapping of the index
        mapping = es.indices.get_mapping(index=index_name)

        # Parse the mapping to extract fields and their types
        print(f"\nFields in the index '{index_name}':")
        fields = extract_fields(mapping[index_name]["mappings"]["properties"])
        for field, field_type in fields.items():
            print(f"{field}: {field_type}")

    except Exception as e:
        print(f"Error: {e}")


def extract_fields(properties, parent_key=''):
    """
    Recursively extracts fields and their types from the mapping.
    :param properties: Properties section of the mapping
    :param parent_key: Parent key for nested fields
    :return: Dictionary of fields and their types
    """
    fields = {}
    for key, value in properties.items():
        field_name = f"{parent_key}.{key}" if parent_key else key
        if "properties" in value:
            # Handle nested fields
            fields.update(extract_fields(value["properties"], field_name))
        else:
            fields[field_name] = value.get("type", "object/nested")
    return fields


if __name__ == "__main__":
    # Hardcoded Elasticsearch host and index name
    es_host = "http://localhost:9200"
    index_name = "your_index_name"

    # List fields in the specified index
    list_index_fields(es_host, index_name)


---

How It Works

1. Connect to Elasticsearch:

The script connects to an Elasticsearch cluster using the provided host URL (es_host).



2. Fetch Index Mapping:

It retrieves the mapping of the specified index (index_name) to understand its data structure.



3. Recursive Field Extraction:

The extract_fields function parses the mapping to extract all fields, including nested fields, along with their data types.



4. Output:

Prints a list of all fields and their corresponding types in the specified index.





---

Example Output

For an index with the following mapping:

{
  "your_index_name": {
    "mappings": {
      "properties": {
        "name": { "type": "text" },
        "age": { "type": "integer" },
        "address": {
          "type": "object",
          "properties": {
            "street": { "type": "text" },
            "city": { "type": "text" }
          }
        }
      }
    }
  }
}

The script will output:

Fields in the index 'your_index_name':
name: text
age: integer
address.street: text
address.city: text


---

Features

1. Recursive Parsing:

Handles nested and object fields gracefully, appending parent keys to nested field names.



2. Dynamic Output:

Adapts to any index structure, listing all fields and their data types.



3. Ready for Queries:

The field names can be directly used in Elasticsearch queries.





---

Run the Script

1. Save the script as list_index_fields.py.


2. Replace your_index_name with the name of your Elasticsearch index.


3. Run the script:

python3.6 list_index_fields.py




---

Next Steps

Use the field names output by this script to create custom queries for analyzing or visualizing data.

If the index has many fields, consider exporting the results to a JSON file for easier analysis. For example:

import json
with open("fields.json", "w") as f:
    json.dump(fields, f, indent=4)


Let me know if you need further enhancements!

