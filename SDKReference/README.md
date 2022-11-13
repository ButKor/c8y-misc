# About

This project is a reference implementation on how an own SDK implementation against the Cumulocity API could look like. It specifically highlights the implementation against Cumulocitys OAuth Interface.

# Structure

The example consists of a minimal example of:
- A main controller utilizing the C8Y API
- An implementation of the Inventory- and Events-API (with one sample each)
- A component named 'c8yClient' which encapsulates the traffic between the SDK and Cumulocity. This client retrieves its context from environment variables and supports Basic Auth and OAuth Internal for authentication.

![Components](Components.png "Components")