# Sample: Introducing the IBM Decision Intelligence Designer MCP Server

This tutorial demonstrates how to use the MCP server provided with Decision Intelligence to create and test a decision service.

## Description

## Learning objectives
- Configure the MCP server with IBM Bob
- Use the MCP server to create and test a decision service

## Audience

This sample is designed for business users who want to use AI-powered tools to create and test decision artifacts in Decision Intelligence.

## Time required

20 minutes

## Prerequisites
- Decision Intelligence: A platform that helps you design, automate, and execute business decisions at scale. It leverages AI-powered rule creation, a comprehensive development environment, and runtime APIs to support end-to-end decision management.
- IBM Bob: An AI agent designed to automate and orchestrate end-to-end software development workflows across the entire software development lifecycle.

# Task 1: Configuring the MCP Server
**About this task**

In this task, you configure IBM Bob to connect to the MCP server.

**Procedure**

1. Sign in to Decision Intelligence by using your instance credentials
2. Follow the steps described in [Creating API keys]( https://www.ibm.com/docs/en/saas-console?topic=usca-granting-access-through-service-ids-api-keys-from-saas-console#creating_APIkeys) to create a personal API key.
3. Follow the steps described in [Connecting to IBM Bob]( https://www.ibm.com/docs/en/decision-intelligence?topic=designer-interacting-decision-through-mcp-server#con_mcp_server__title__2) to configure the MCP server.


# Task 2: Using the MCP server

**About this task**

In this task, you use the MCP server to create and test a decision service.

## Step 1: Creating a decision automation and a decision service
It is recommended to review the [Telecom Offer Recommendation Policy](./policy/TelecomOfferPolicy.md), as it provides the key rules needed to understand the decision logic. You will then use the MCP server to create a decision automation and a decision service based on this policy.

**Procedure**

1. Sign in to IBM Bob by using your instance credentials.
2. Click the mode selector and switch to Ask mode.
3. Ask Bob to read the [Telecom Offer Recommendation Policy](./policy/TelecomOfferPolicy.md).
4. Switch back to Advanced mode.
5. Ask Bob to create a decision automation named "Customer services". Verify that the automation was created in your Decision Intelligence instance.
6. Ask Bob to create an empty decision service for the [policy](./policy/TelecomOfferPolicy.md). Verify that it was created in your instance.

## Step 2: Creating a data model
You use the MCP server to create a data model based on a provided policy.

**Procedure**

1. Ask Bob to create a data model based on this [policy](./policy/TelecomOfferPolicy.md). Use the following prompt:

    **Prompt**
    ```
    Create a data model that complies with the Telecom Offer Recommendation Policy
    ```
2. Verify that the data model is correctly created by reviewing all data types and ask Bob to update the data model if it does not meet the policy requirements.
3. Share your changes in Decision Designer to persist them, enable version history, and roll back if needed.

## Step 3: Creating a decision model and defining the logic
You use the MCP server to create a decision model and implement the decision logic for all decision nodes based on the provided policy.

**Procedure**
1. Ask Bob to create a decision model based on the policy. The following example shows a prompt to perform this step:

    **Prompt**
    ```
    Create a decision model based on the Telecom Offer Recommendation Policy
    ```

2. Verify the generated decision diagram in Decision Designer. If it follows the flow described in the policy, proceed to the next step; otherwise, ask Bob to update it.
3. Ask Bob to define the decision logic for all decision nodes.

   **Prompt**
    ```
    Define the logic of each node 
    ```
4.  Share the changes in Decision Designer.

You can cancel the process at any time and request updates from Bob. You also have the option to review and decline any changes proposed by Bob. 


## Step 5: Creating an operation and testing the decision service
You use the MCP server to create a decision operation and test the generated decision service with different test data sets.


**Procedure**
1. Ask Bob to create a decision operation for the decision model.

    **Prompt**
    ```
    Create a decision operation for the decision model
    ```
2. Ask Bob to test the decision service with different test data sets.

    **Prompt**
    ```
    Create 4 test data sets to run the decision model.
    ```

3. Go to Decision Designer and run the decision model with the generated test data sets. Make sure that the results are consistent with the policy and accurately reflect the defined logic.
4. Share the changes in Decision Designer.

## Conclusion

This sample demonstrates how to use the Model Context Protocol (MCP) server provided with IBM Decision Intelligence to manage and interact with Decision Designer in a structured and scalable way. By simplifying integration, MCP helps improve the governance of decision logic across applications.
While AI can accelerate development, its outputs should always be carefully reviewed. Users are responsible for validating the generated logic and ensuring it aligns with business requirements, in order to deliver accurate and reliable results.
