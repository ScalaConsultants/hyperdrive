# hyperdrive

Generic implementation of Collection+JSON hypermedia-type as defined in the [documentation](http://amundsen.com/media-types/collection/).

This is work in progress - not intended for anything other than learning and experimenting at this point.

### Parts more or less ready:

1. Get list of resources.
2. Get one resource.
3. Add resource.

### Parts definitely not ready:

1. Search.
2. Update resource.

### Example:

`sbt "project example" run`

1. Get list of resources.

    Request:

    `GET http://localhost:9080/foos`

    Response:

    200 Ok

    ```
    {
      "collection": {
        "queries": [],
        "items": [
          {
            "href": "http://localhost:9080/foos/1",
            "data": [
              {
                "name": "id",
                "value": 1
              },
              {
                "name": "x",
                "value": "one"
              },
              {
                "name": "y",
                "value": 11
              }
            ],
            "links": []
          },
          {
            "href": "http://localhost:9080/foos/2",
            "data": [
              {
                "name": "id",
                "value": 2
              },
              {
                "name": "x",
                "value": "two"
              },
              {
                "name": "y",
                "value": 22
              }
            ],
            "links": []
          }
        ],
        "version": "1.0",
        "links": [],
        "template": {
          "data": [
            {
              "name": "x"
            },
            {
              "name": "y"
            }
          ]
        },
        "href": "http://localhost:9080/foos"
      }
    }
    ```

2. Get one resource.

    Request:

    `GET http://localhost:9080/foos/1`

    Response:

    200 Ok

    ```
    {
      "collection": {
        "queries": [],
        "items": [
          {
            "href": "http://localhost:9080/foos/1",
            "data": [
              {
                "name": "id",
                "value": 1
              },
              {
                "name": "x",
                "value": "one"
              },
              {
                "name": "y",
                "value": 11
              }
                ],
            "links": []
          }
        ],
        "version": "1.0",
        "links": [],
        "template": {
          "data": [
            {
              "name": "x"
            },
            {
              "name": "y"
            }
          ]
        },
        "href": "http://localhost:9080/foos"
      }
    }
    ```

3. Add new resource.

    Request:

    `POST http://localhost:9080/foos`

    ```
    {
      "template": {
        "data": [
          {
            "name": "x",
            "value": "some text"
          },
          {
            "name": "y",
            "value": 10
          }
        ]
      }
    }
    ```

    Response:

    201 Created

    `Location: http://localhost:9080/foos/4`
