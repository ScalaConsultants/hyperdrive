# hyperdrive

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

4. Update resource.

    Request:

    `PUT http://localhost:9080/foos/1`

    ```
    {
      "template": {
        "data": [
          {
            "name": "x",
            "value": "some new text"
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

    200 Ok
