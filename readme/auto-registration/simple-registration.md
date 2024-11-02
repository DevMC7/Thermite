# Simple Registration

To automatically register your mod's items, you need to do two things

1.  Make sure your class implements XRegister (where X is Item, Block etc)\


    ```java
    import net.devmc.thermite.lib.registration.registers.ItemRegister;

    public class ModItems implements ItemRegister {
        
    }
    ```
2.  Add your fields\


    ```java
    import net.devmc.thermite.lib.registration.registers.ItemRegister;
    import net.minecraft.item.Item;

    public class ModItems implements ItemRegister {

        public static final Item ITEM = new Item(new Item.Settings());
    }
    ```
3.  Then, register your class as a Registry

    ```java
    ItemRegistry.REGISTRY.register(ModItems);
    ```

You are done! Go to the next page to see how you can customize items and blocks
