package ru.practicum.shareit.testbuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "anItemRequest")
public class ItemRequestTestBuilder implements TestBuilder<ItemRequest> {
    private String description;
    private User requestor;
    private Instant created = Instant.now();

    @Override
    public ItemRequest build() {
        final var itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(created);
        return itemRequest;
    }
}
