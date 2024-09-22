package ru.practicum.shareit.testbuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@With
@NoArgsConstructor(staticName = "anItem")
@AllArgsConstructor
public class ItemTestBuilder implements TestBuilder<Item> {
    private User owner;
    private String name = "item";
    private String description = "item description";
    private Boolean available = Boolean.TRUE;
    private ItemRequest request;


    @Override
    public Item build() {
        final var item = new Item();
        item.setOwner(owner);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setRequest(request);
        return item;
    }
}
