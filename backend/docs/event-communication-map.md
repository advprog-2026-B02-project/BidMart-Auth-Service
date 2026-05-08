# Event Communication Map

BidMart uses Spring Application Events for in-process, asynchronous communication between modules. Auth publishes domain events; consuming modules listen to the event classes from `id.ac.ui.cs.advprog.bidmart.common.event`.

## Auth Events

| Publisher | Event | Trigger | Consumers | Consumer responsibility |
| --- | --- | --- | --- | --- |
| Auth | `UserSuspendedEvent` | Admin or internal service changes a user status to `SUSPENDED` | Auction, Wallet, Notification | Stop or reject user-owned auction activity, restrict wallet operations, notify affected parties |
| Auth | `UserRoleChangedEvent` | Admin or internal service updates user roles | All modules | Invalidate cached authorization/user-role data |

## Runtime Contract

- Events are published with Spring `ApplicationEventPublisher`.
- Event dispatch is asynchronous through the `applicationEventMulticaster` configured in Auth.
- Delivery is in-process, so listeners run only inside the same deployed application/runtime.
- Auth revokes active refresh tokens when a user is suspended.
- Auth keeps at most three active sessions per user by revoking the oldest active session before creating a new one.

## Listener Shape For Other Modules

```java
@Component
class AuctionUserEventListener {

    @EventListener
    public void onUserSuspended(UserSuspendedEvent event) {
        // Cancel, pause, or block auction actions for event.getUserId().
    }

    @EventListener
    public void onUserRoleChanged(UserRoleChangedEvent event) {
        // Invalidate cached authorization data for event.getUserId().
    }
}
```
