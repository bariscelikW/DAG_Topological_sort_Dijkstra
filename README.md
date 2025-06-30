## 🚀 Overview

(Please read the description PDF for a full understanding.)

This project contains two main parts:

### 🛠️ Part I – Club Fair Setup Planning

Build a scheduling tool to compute the **earliest possible start and end times** for booth setup tasks during the Hacettepe University Club Fair. Each task has a duration and a list of dependency tasks that must finish before it can begin.

- **Input:** XML file with project name, tasks, durations, dependencies  
- **Output:** Formatted task schedule in topological order, including:
  - Task ID
  - Description
  - Start and End times
  - Total duration

### 🗺️ Part II – Campus Navigator App

Implement a campus navigation assistant that helps visitors find the **fastest route** across campus using a mix of walking and golf cart travel.

- **Input:** Loosely formatted `.dat` file with:
  - Start/destination coordinates
  - Golf cart station locations
  - Average cart speed
- **Output:** Step-by-step directions with:
  - Mode of travel
  - Duration (to 2 decimal places)
  - Total time (rounded to nearest minute)

---

## 🧩 Features

### ✅ Club Fair Setup
- Parse XML project and task data
- Build a directed acyclic graph of dependencies
- Topological sort for task scheduling
- Calculate earliest start/end times
- Print formatted schedule and critical path

### ✅ Campus Navigator
- Use regular expressions to read `.dat` input
- Construct weighted graph of all walking + cart routes
- Use Dijkstra’s algorithm to find shortest time
- Print detailed route directions with timing
