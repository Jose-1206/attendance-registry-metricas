# 00 — Git Conventions

This document defines the Git conventions used in the Attendance documentation
repository to keep changes organized, traceable, and reviewable.

## Branch strategy

The `main` branch contains the integrated project documentation.

Documentation changes are created in short-lived branches and proposed through
pull requests before they are integrated into `main`.

Each branch should focus on one clear documentation task.

## Branch naming

Documentation branches follow this format:

```text
docs/<short-description-in-kebab-case>
