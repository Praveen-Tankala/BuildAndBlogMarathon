Building a Smart Storage System with Google Cloud Storage and Vertex AI Using JAVA


Ever struggled to manage a sea of old, unorganized files? What if AI could help you describe and categorize them for better clarity? In this blog, we’ll dive into building a smart storage system that utilizes Google Cloud Storage and Vertex AI to automatically generate summaries for uploaded files. Let’s learn how to bring order to chaos with a touch of AI magic!

![converted_image_2](https://github.com/user-attachments/assets/eaa32bd4-6f1b-478d-89d5-f8aa4fcff5c3)


Introduction

  Managing digital content becomes a daunting task as data piles up. Files are often stored without proper labels or context, leading to confusion down the line. Our solution leverages PostgreSQL, Google Cloud Storage and Vertex AI to generate descriptive summaries of file contents, allowing users to easily understand and retrieve old data.

Problem Statement

  Many users face challenges when trying to manage or make sense of a large volume of files stored over time. The lack of proper file descriptions makes it harder to locate and utilize stored content efficiently.

Target Audience

  This blog is aimed at developers and data enthusiasts who have a basic understanding of cloud platforms, particularly Google Cloud Platform (GCP). A familiarity with cloud storage, JAVA programming, and AI concepts will be beneficial.

Expected Outcome

By the end of this blog, you will learn how to build a system that:

  Automatically generates metadata and summaries for various file types.
  Provides a user-friendly way to search and retrieve old files based on AI-generated descriptions.
  Design

To provide a clear overview of the solution, let’s break down the architecture:

High-Level Architecture

    File Upload: Users upload files to Google Cloud Storage.
    JAVA Function Trigger: A JAVA Function is triggered on each file upload, updated the meta data to PostgreSQL Database.
    AI Analysis: The Cloud Function sends the file to Vertex AI for analysis.
    Result Storage: AI-generated descriptions are saved back to the Cloud Storage or a database for easy access.
    Design Choices
    Google Cloud Storage was selected due to its scalability and ease of integration with other Google Cloud services.
    Vertex AI offers pre-trained AI models, simplifying the task of content analysis without needing extensive AI knowledge.
    Using JAVA for robust development and thyme-leaf for frontend, cost-effective event-driven architecture.
    
Visualization
  ![App](https://github.com/user-attachments/assets/def14048-4bfe-44cf-9e90-83118d3b490a)
  The design choice prioritizes simplicity, scalability, and seamless integration between cloud components

Prerequisites
  Ensure you have the following tools and setup:
  
  Google Cloud Platform (GCP) Account: Sign up.
  Google Cloud SDK: Install the Google Cloud SDK for local setup and CLI interaction for PostgreSQL.
  Java Development Kit (JDK): Ensure you have JDK 17+ installed.
  Gradle: Use Gradle for dependency management.
  Google Cloud Storage Java SDK: Add dependency.
  Vertex AI Java SDK: Add the relevant dependency for interacting with Vertex AI (pre-trained models).

Result

By the end of this tutorial, your system will automatically generate AI-based summaries for files uploaded to Google Cloud Storage. You will see a summary file saved in your Cloud Storage bucket, making it easy to retrieve and understand your files.

What’s Next?
  To take this project further:
  
  Custom Models: Train your own model in Vertex AI to analyse more specific file types.
  Search Functionality: Implement a search function using the summaries to find files more efficiently.


