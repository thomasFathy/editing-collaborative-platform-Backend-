# Collaborative Real-Time Document Editing Platform - Backend

## Overview

The backend powers a collaborative document editing platform where multiple users can create, edit, share, and synchronize documents in real time.

Built with:

* Spring Boot
* Spring Security
* JWT Authentication
* Spring WebSocket (STOMP)
* PostgreSQL
* JPA / Hibernate

---

## Features

### Authentication & Authorization

* User Registration
* User Login
* JWT-based Authentication
* Role-based Access Control
* Secure REST APIs
* Secure WebSocket Connections

### Document Management

Users can:

* Create Documents
* View Documents
* Delete Documents
* Update Document Metadata

Each document contains:

* Title
* Owner
* Creation Timestamp
* Last Updated Timestamp

---

### Real-Time Collaboration

Implemented using:

* STOMP WebSockets
* Topic-based broadcasting

Capabilities:

* Multiple users editing simultaneously
* Real-time text synchronization
* Cursor tracking
* Active collaborator presence

---

### Document Sharing

Document owners can share documents with:

* VIEWER Role
* EDITOR Role

Permission checks are enforced on:

* API endpoints
* WebSocket editing operations

---

### Versioning System

The platform stores:

* Current Document Content
* Historical Versions

Users can:

* Save document state
* Restore previous versions
* Track document evolution

---

### Optimized Persistence Strategy

Instead of persisting every keystroke:

Old Approach:

* INSERT operation stored per character

Problems:

* Massive database growth
* Poor performance
* Difficult cleanup

Current Approach:

* WebSocket synchronizes edits in memory
* Entire document saved explicitly
* Single database update per save action

Benefits:

* Better scalability
* Lower database load
* Cleaner version history

---

## Database Schema

### users

| Column   |
| -------- |
| id       |
| email    |
| password |

### documents

| Column     |
| ---------- |
| id         |
| title      |
| owner_id   |
| created_at |
| updated_at |

### document_contents

| Column      |
| ----------- |
| document_id |
| content     |
| version     |

### document_shares

| Column      |
| ----------- |
| id          |
| document_id |
| user_id     |
| role        |

### document_versions

| Column         |
| -------------- |
| id             |
| document_id    |
| content        |
| version_number |
| created_at     |

---

## REST API

### Authentication

POST /api/auth/register

POST /api/auth/login

---

### Documents

GET /documents

POST /documents

GET /documents/{id}

DELETE /documents/{id}

---

### Sharing

POST /documents/{id}/share

---

### Content

GET /document-contents/{documentId}

PUT /document-contents/{documentId}

---

## WebSocket Endpoints

Connection Endpoint:

/ws

Topics:

/topic/document/{documentId}

/topic/document/{documentId}/cursor

---

## Security

Implemented with:

* JWT Authentication
* Spring Security
* Endpoint Authorization
* WebSocket Authentication
* Ownership Verification
* Share Permission Validation

---

## Future Improvements

* Operational Transformation (OT)
* CRDT Support
* Redis Pub/Sub
* Multi-node Scaling
* Cursor Selections
* Rich Text Editor
* Audit Logging
* ElasticSearch Integration

---

## Architecture

Controller Layer

↓

Service Layer

↓

Repository Layer

↓

PostgreSQL

Real-Time Communication

Client

↔

WebSocket Broker

↔

Collaboration Service

↔

Connected Clients
