# Spirit interaction options

Right-click a living adult Spirit that is not already trading to open **Trade**, **Unfinished Business** and **Leave** buttons. Shift does not select a special path. Trade opens vanilla merchant trading with the existing expertise, prices, inventory and stock rules. Unfinished Business opens a quest offer with **Accept Quest**, **Decline Quest** and **Back**, shows active progress when a task is already accepted, or explains that no new offer is ready. Quest rewards and remote completion remain unchanged.

Chat offers and `/spiritquest` controls remain available for temporary Haunt encounters and checking progress without a permanent Spirit.

The server binds every open screen to a player, Spirit UUID, dimension, short-lived token and dialog mode. A choice consumes that token, validates the live adult/non-trading giver and interaction distance, and only permits actions from the displayed mode. Accepted quest buttons refer to the specific offer in that session. Replays, another player's token, arbitrary actions, removed givers, expired sessions and out-of-range actions cannot open trades or accept unseen quests. Subsequent screens receive fresh tokens.

The UI uses client-only vanilla Screen/Button classes; common networking carries native Component and string/integer stream codecs. No new stored data, menu inventory, registry items or mixins. Client/server must use the same mod version for these new payloads. Migration checks: custom payload registration, component stream codec, interaction reach, vanilla merchant opening and screen widgets.

Build and 118 required server tests pass. Tests cover main-menu choice before trading, native merchant payments, optional quest offer/accept/decline, active progress, repeated clicks, foreign/forged tokens, interaction distance, removed givers, dialog modes and payload round trips. Guide and generated resources remain valid. Screen layout and mouse interaction require client playtesting.
