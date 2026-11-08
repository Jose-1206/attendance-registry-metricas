# QR Code — Screen

## Identification

| Field | Value |
|---|---|
| Screen | Class QR code |
| Serves | [`HU-08 — Generate a temporary QR code for a class session`](../04-requirements/user-stories.md) |
| Primary users | `TEACHER` |
| Purpose | Generate and display the temporary QR code used by students to register attendance for a class session. |

## Wireframe

```text
+----------------------------------+
| < Class sessions    Class QR     |
| Course: [selected course]        |
| Session: [date / start time]     |
|                                  |
|        +----------------+        |
|        |                |        |
|        |    QR CODE     |        |
|        |                |        |
|        +----------------+        |
|                                  |
| Valid for 15 minutes             |
| Expires at: [expiration time]    |
|                                  |
| [          Generate QR        ]  |
+----------------------------------+
```

## States

| State | What the user sees |
|---|---|
| Ready | Selected class-session context and Generate QR action. |
| Generating | Generate action disabled while the request is processed. |
| Active | Generated QR plus expiration information. |
| Expired | `QR code expired.` and the code is no longer presented as valid. |
| Error | Human-readable error and Generate again or Retry when safe. |

## QR data

The `QRCode` domain entity contains:

- session reference;
- unique token;
- expiration time;
- active state.

The QR belongs to one `ClassSession`.

## Display rules

- The QR must be visibly associated with the selected class session.
- The documented default validity period is **15 minutes**.
- Expiration must be shown using text.
- An expired QR must not continue to appear as valid.
- QR tokens must not be exposed in documentation screenshots or logs.

## Actions and behavior

- **Generate QR** requests a QR for the selected class session.
- The UI reports the QR as active only after successful backend generation.
- When expiration is reached, the UI changes the QR state to expired.
- QR generation is a teacher action.

## Business-rule connection

- **BR-05:** a QR code is valid only for the class session for which it was
  generated and before its expiration time.

## Accessibility notes

- Class-session and expiration information is available as readable text.
- QR state changes are announced as status messages.
- The teacher can determine whether the code is active or expired without
  depending only on color.

---

**Related:** [`./class-sessions-screen.md`](./class-sessions-screen.md) · [`./scan-qr-screen.md`](./scan-qr-screen.md) · [`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md)