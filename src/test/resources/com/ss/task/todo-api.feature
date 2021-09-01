Feature: Create and manage todo items using Todo service

  Scenario Outline: todo item can be updated
    Given todo item is created using "valid-post-request.json" at "/todo/items"
    When a "PATCH" request is received by the todo service on the endpoint "<url>" with "<requestBody>"
    Then the response status is <status>
    And response body is equal to "<response>"

    Examples:
      | url             | requestBody                | status | response           |
      | /todo/items/1   | valid-patch-request.json   | 200    | patch-success.json |
      | /todo/items/999 | valid-patch-request.json   | 404    | not-found.json     |
      | /todo/items/1   | invalid-patch-request.json | 400    | bad-request.json   |


  Scenario Outline: todo item can be fetched
    Given todo item is created using "valid-post-request.json" at "/todo/items"
    When a "GET" request is received by the todo service on the endpoint "<url>"
    Then the response status is <status>
    And response body is equal to "<response>"

    Examples:
      | url             | status | response         |
      | /todo/items/1   | 200    | get-success.json |
      | /todo/items/999 | 404    | not-found.json   |

  Scenario Outline: todo item can be added
    When a "POST" request is received by the todo service on the endpoint "<url>" with "<requestBody>"
    Then the response status is <status>
    And response body is equal to "<response>"

    Examples:
      | url         | requestBody               | status | response          |
      | /todo/items | valid-post-request.json   | 200    | post-success.json |
      | /todo/items | invalid-post-request.json | 400    | past-request.json |